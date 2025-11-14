package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HabrCareerDateTimeParserTest {

    @Test
    void whenParseISO8601ThenReturnLocalDateTime() {
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();

        String datetime = "2025-11-14T12:34:00+03:00";
        LocalDateTime expected = LocalDateTime.of(2025, 11, 14, 12, 34, 0);

        LocalDateTime actual = parser.parse(datetime);

        assertEquals(expected, actual);
    }

    @Test
    void whenParseAnotherDateThenCorrect() {
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();

        String datetime = "2024-01-01T00:00:00+00:00";
        LocalDateTime expected = LocalDateTime.of(2024, 1, 1, 0, 0, 0);

        LocalDateTime actual = parser.parse(datetime);

        assertEquals(expected, actual);
    }
}