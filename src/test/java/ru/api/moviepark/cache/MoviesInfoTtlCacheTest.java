package ru.api.moviepark.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;
import ru.api.moviepark.service.cache.MoviesInfoTtlCache;
import ru.api.moviepark.service.dbclient.RemoteDatabaseClientImpl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@EnableJpaRepositories
@ComponentScan("ru.api.moviepark")
@PropertySource("/application-test.yaml")
public class MoviesInfoTtlCacheTest {

    @Autowired
    private RemoteDatabaseClientImpl remoteDatabaseClient;

    private LocalDate testDate = LocalDate.of(2030, 1, 1);

    @Test
    public void Should_GetAllMoviesByDate_When_ItIsGiven() {
        assertFalse(MoviesInfoTtlCache.containsElement(testDate));
        Map<Integer, String> result = remoteDatabaseClient.getAllMoviesByDate(testDate);
        assertTrue(MoviesInfoTtlCache.containsElement(testDate));
    }

    @Test
    public void Should_GetAllMoviesByPeriod_When_ItIsGiven() {
        Map<LocalDate, Map<Integer, String>> result = remoteDatabaseClient.getAllMoviesByPeriod(testDate, testDate);
        Map<Integer, String> allMoviesByDate = remoteDatabaseClient.getAllMoviesByDate(testDate);
        Map<LocalDate, Map<Integer, String>> expected = new HashMap<>();
        expected.put(testDate, allMoviesByDate);

        assertEquals(expected, result);

        result = remoteDatabaseClient.getAllMoviesByPeriod(testDate, testDate.plusDays(1));
        Map<Integer, String> allMoviesByNextDate = remoteDatabaseClient.getAllMoviesByDate(testDate.plusDays(1));
        expected.put(testDate.plusDays(1), allMoviesByNextDate);

        assertEquals(expected, result);
    }

    @Test
    public void getAllSeancesByMovieAndDateGroupByMoviePark() {

    }
}
