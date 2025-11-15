package ru.job4j;

import org.apache.log4j.Logger;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.service.Config;
import ru.job4j.grabber.service.SchedulerManager;
import ru.job4j.grabber.service.SuperJobGrab;
import ru.job4j.grabber.stores.JdbcStore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        var config = new Config();
        config.load("application.properties");
        try (Connection connection = DriverManager.getConnection(
                config.get("db.url"),
                config.get("db.username"),
                config.get("db.password")
        );
             var scheduler = new SchedulerManager()) {
            var store = new JdbcStore(connection);
            var post = new Post();
            post.setTitle("Super Java Job");
            post.setDescription("Test description");
            post.setLink("http://example.com");
            post.setTime(System.currentTimeMillis());
            store.save(post);

            scheduler.init();
            scheduler.load(
                    Integer.parseInt(config.get("rabbit.interval")),
                    SuperJobGrab.class,
                    store);

            Thread.sleep(10000);

        } catch (SQLException e) {
            LOG.error("When create a connection", e);
        } catch (InterruptedException e) {
            LOG.error("Interrupted exception", e);
            Thread.currentThread().interrupt();
        }
    }
}