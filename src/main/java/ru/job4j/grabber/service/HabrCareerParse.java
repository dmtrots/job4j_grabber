package ru.job4j.grabber.service;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {
    private static final Logger LOG = Logger.getLogger(HabrCareerParse.class);
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PREFIX = "/vacancies?page=";
    private static final String SUFFIX = "&q=Java%20developer&type=all";
    private static final int PAGES_TO_PARSE = 5;

    @Override
    public List<Post> fetch() {
        var result = new ArrayList<Post>();
        try {
            //int pageNumber = 1;
            for (int pageNumber = 1; pageNumber <= PAGES_TO_PARSE; pageNumber++) {
                String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, pageNumber, SUFFIX);
                var connection = Jsoup.connect(fullLink);
                var document = connection.get();
                var rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    var titleElement = row.select(".vacancy-card__title").first();
                    var linkElement = titleElement.child(0);
                    String vacancyName = titleElement.text();
                    String link = String.format("%s%s", SOURCE_LINK,
                            linkElement.attr("href"));

                    var datetimeElement = row.select(".vacancy-card__date").first().child(0);
                    String datetime = datetimeElement.attr("datetime");
                    Long dateMillis = java.time.OffsetDateTime.parse(datetime)
                            .toInstant().toEpochMilli();

                    String description = retrieveDescription(link);
                    var post = new Post();
                    post.setTitle(vacancyName);
                    post.setLink(link);
                    post.setTime(dateMillis);
                    post.setDescription(description);
                    result.add(post);
                });
            }
        } catch (IOException e) {
            LOG.error("When load page", e);
        }
        return result;
    }

    private String retrieveDescription(String link) {
        try {
            var document = Jsoup.connect(link).get();
            var descriptionElement = document.select(".vacancy-description__text").first();

            if (descriptionElement != null) {
                return descriptionElement.text();
            }
        } catch (IOException e) {
            LOG.error("Error retrieving description from: " + link, e);
        }
        return "";
    }

    public static void main(String[] args) {
        HabrCareerParse parser = new HabrCareerParse();
        List<Post> posts = parser.fetch();

        for (Post post : posts) {
            System.out.println("Title: " + post.getTitle());
            System.out.println("Link: " + post.getLink());
            System.out.println("Description: " + post.getDescription());
            var instant = java.time.Instant.ofEpochMilli(post.getTime());
            var dateTime = java.time.ZonedDateTime.ofInstant(instant, java.time.ZoneId.systemDefault());
            System.out.println("Time: " + dateTime.format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.println("-------------------------");
        }
    }
}