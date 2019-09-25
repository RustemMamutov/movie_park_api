package ru.api.moviepark.service.dbclient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;
import ru.api.moviepark.data.valueobjects.MainScheduleViewEntity;
import ru.api.moviepark.service.cache.SeanceInfoTtlCache;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootConfiguration
@EnableJpaRepositories
@ComponentScan("ru.api.moviepark")
@PropertySource("/application-test.properties")
public class SeanceInfoTtlCacheTest {

    @Autowired
    private RemoteDatabaseClientImpl remoteDatabaseClient;

    private LocalDate testDate = LocalDate.of(2030, 1, 1);

    @Test
    public void Should_GetSeanceById_When_ItIsGiven() {
        assertFalse(SeanceInfoTtlCache.checkCacheContainsElementByDate(testDate));
        remoteDatabaseClient.getSeanceById(1);
        assertTrue(SeanceInfoTtlCache.checkCacheContainsElementByDate(testDate));

        List<MainScheduleViewEntity> result = SeanceInfoTtlCache.getSeancesListByDateFromCache(testDate);
        assertEquals(24, result.size());
    }

    @Test
    public void Should_CacheAllSeancesByDate_When_ResponseWasReturned() {
        assertFalse(SeanceInfoTtlCache.checkCacheContainsElementByDate(testDate));
        remoteDatabaseClient.getAllSeancesByDate(testDate);
        assertTrue(SeanceInfoTtlCache.checkCacheContainsElementByDate(testDate));

        List<MainScheduleViewEntity> result = SeanceInfoTtlCache.getSeancesListByDateFromCache(testDate);
        assertEquals(24, result.size());
    }

    @Test
    public void Should_CacheAllSeancesByPeriod_When_ResponseWasReturned() {
        assertFalse(SeanceInfoTtlCache.checkCacheContainsElementByDate(testDate));
        assertFalse(SeanceInfoTtlCache.checkCacheContainsElementByDate(testDate.plusDays(1)));

        remoteDatabaseClient.getAllSeancesByPeriod(testDate, testDate.plusDays(1));

        assertTrue(SeanceInfoTtlCache.checkCacheContainsElementByDate(testDate));
        assertTrue(SeanceInfoTtlCache.checkCacheContainsElementByDate(testDate.plusDays(1)));

        List<MainScheduleViewEntity> result = SeanceInfoTtlCache.getSeancesListByDateFromCache(testDate);
        assertEquals(24, result.size());
        result = SeanceInfoTtlCache.getSeancesListByDateFromCache(testDate.plusDays(1));
        assertEquals(24, result.size());
    }
}
