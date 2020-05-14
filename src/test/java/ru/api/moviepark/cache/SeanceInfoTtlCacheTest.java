package ru.api.moviepark.cache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.api.moviepark.data.dto.MainScheduleDTO;
import ru.api.moviepark.service.MovieParkClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class SeanceInfoTtlCacheTest {

    @Autowired
    private MovieParkClient movieParkClient;

    private LocalDate testDate = LocalDate.of(2030, 1, 1);

    @Before
    public void clearCache() {
        SeanceInfoTtlCache.clearAllCache();
    }


    @Test
    public void Should_GetSeanceById_When_ItIsGiven() {
        assertFalse(SeanceInfoTtlCache.containsElementByDate(testDate));
        movieParkClient.getSeanceById(1);
        assertTrue(SeanceInfoTtlCache.containsElementByDate(testDate));

        List<MainScheduleDTO> result = SeanceInfoTtlCache.getSeancesListByDate(testDate);
        assertEquals(21, result.size());
    }

    @Test
    public void Should_CacheAllSeancesByDate_When_ResponseWasReturned() {
        assertFalse(SeanceInfoTtlCache.containsElementByDate(testDate));
        movieParkClient.getAllSeancesByDate(testDate);
        assertTrue(SeanceInfoTtlCache.containsElementByDate(testDate));

        List<MainScheduleDTO> result = SeanceInfoTtlCache.getSeancesListByDate(testDate);
        assertEquals(21, result.size());
    }

    @Test
    public void Should_CacheAllSeancesByPeriod_When_ResponseWasReturned() {
        assertFalse(SeanceInfoTtlCache.containsElementByDate(testDate));
        assertFalse(SeanceInfoTtlCache.containsElementByDate(testDate.plusDays(1)));

        movieParkClient.getAllSeancesByPeriod(testDate, testDate.plusDays(1));

        assertTrue(SeanceInfoTtlCache.containsElementByDate(testDate));
        assertTrue(SeanceInfoTtlCache.containsElementByDate(testDate.plusDays(1)));

        assertEquals(21, SeanceInfoTtlCache.getSeancesListByDate(testDate).size());
        assertEquals(22, SeanceInfoTtlCache.getSeancesListByDate(testDate.plusDays(1)).size());
    }
}
