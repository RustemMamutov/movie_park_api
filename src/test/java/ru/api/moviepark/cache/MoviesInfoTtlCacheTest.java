package ru.api.moviepark.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.api.moviepark.service.MovieParkClient;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class MoviesInfoTtlCacheTest {

    @Autowired
    private MovieParkClient movieParkClient;

    private LocalDate testDate = LocalDate.of(2030, 1, 1);

    @Test
    public void Should_GetAllMoviesByDate_When_ItIsGiven() {
        assertFalse(MoviesInfoTtlCache.containsElementByDate(testDate));
        Map<Integer, String> result = movieParkClient.getAllMoviesByDate(testDate);
        assertTrue(MoviesInfoTtlCache.containsElementByDate(testDate));
    }

    @Test
    public void Should_GetAllMoviesByPeriod_When_ItIsGiven() {
        Map<LocalDate, Map<Integer, String>> result = movieParkClient.getAllMoviesByPeriod(testDate, testDate);
        Map<Integer, String> allMoviesByDate = movieParkClient.getAllMoviesByDate(testDate);
        Map<LocalDate, Map<Integer, String>> expected = new HashMap<>();
        expected.put(testDate, allMoviesByDate);

        assertEquals(expected, result);

        result = movieParkClient.getAllMoviesByPeriod(testDate, testDate.plusDays(1));
        Map<Integer, String> allMoviesByNextDate = movieParkClient.getAllMoviesByDate(testDate.plusDays(1));
        expected.put(testDate.plusDays(1), allMoviesByNextDate);

        assertEquals(expected, result);
    }

    @Test
    public void getAllSeancesByMovieAndDateGroupByMoviePark() {

    }
}
