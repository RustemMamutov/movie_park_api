package ru.api.moviepark.cache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.api.moviepark.service.MovieParkClient;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static ru.api.moviepark.config.CacheConfig.MOVIES_INFO_CACHE;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class MoviesInfoTtlCacheTest {

    @Autowired
    private MovieParkClient movieParkClient;

    private LocalDate testDate = LocalDate.of(2030, 1, 1);

    @Autowired
    private ApplicationContext context;

    private GuavaCache moviesInfoCache;

    @Before
    public void init(){
        CacheManager local = (CacheManager) context.getBean("cacheManager");
        moviesInfoCache = (GuavaCache) local.getCache(MOVIES_INFO_CACHE);
    }

    @Test
    public void Should_CacheAllMoviesByDate_When_ItIsGiven() {
        assertNull(moviesInfoCache.get(testDate));

        Map<Integer, String> result = movieParkClient.getAllMoviesByDate(testDate);
        assertFalse(result.isEmpty());

        Map<Integer, String> cache = getFromCache(testDate);
        assertNotNull(cache);
        assertEquals(result, cache);
    }

    @Test
    public void Should_CacheAllMoviesByPeriod_When_ItIsGiven() {
        Map<LocalDate, Map<Integer, String>> expected = new HashMap<>();
        Map<LocalDate, Map<Integer, String>> result =
                movieParkClient.getAllMoviesByPeriod(testDate, testDate.plusDays(1));

        Map<Integer, String> allMoviesByDate = getFromCache(testDate);
        Map<Integer, String> allMoviesByNextDate = getFromCache(testDate.plusDays(1));

        expected.put(testDate, allMoviesByDate);
        expected.put(testDate.plusDays(1), allMoviesByNextDate);

        assertEquals(expected, result);
    }

    private Map<Integer, String> getFromCache(LocalDate date) {
        return (Map<Integer, String>) moviesInfoCache.get(date).get();
    }

    @Test
    public void getAllSeancesByMovieAndDateGroupByMoviePark() {

    }
}
