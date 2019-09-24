package ru.api.moviepark.service.dbclient;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.api.moviepark.data.entities.HallsEntity;
import ru.api.moviepark.data.entities.SeancePlacesEntity;
import ru.api.moviepark.data.repositories.HallsRepo;
import ru.api.moviepark.data.repositories.SeancesPlacesRepo;
import ru.api.moviepark.data.valueobjects.BlockUnblockPlaceInput;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;
import ru.api.moviepark.data.valueobjects.MainScheduleViewEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootConfiguration
@EnableJpaRepositories
@ComponentScan("ru.api.moviepark")
@PropertySource("/application-test.properties")
public class RemoteDatabaseClientImplTest {

    @Autowired
    private RemoteDatabaseClientImpl remoteDatabaseClient;

    @Autowired
    private HallsRepo hallsRepo;

    @Autowired
    private SeancesPlacesRepo seancesPlacesRepo;

    private LocalDate testDate = LocalDate.of(2030, 1, 1);

    @Before
    public void createSeanceInputTemplate() {

    }

    @Test
    public void changeCacheLifeTime() {
    }

    @Test
    public void Should_GetSeanceById_When_ItIsGiven() {
        MainScheduleViewEntity result = remoteDatabaseClient.getSeanceById(1);
        MainScheduleViewEntity expected = MainScheduleViewEntity.builder()
                .seanceId(1)
                .seanceDate(testDate)
                .startTime(LocalTime.of(9,0,0))
                .endTime(LocalTime.of(11,0,0))
                .movieParkId(1)
                .movieParkName("CinemaPark1")
                .movieId(1)
                .movieName("Film1")
                .hallId(101)
                .basePrice(100)
                .vipPrice(200)
                .build();

        assertEquals(expected, result);
    }

    @Test
    public void Should_GetAllSeancesByDate_When_ItIsGiven() {
        List<MainScheduleViewEntity> resultList = remoteDatabaseClient.getAllSeancesByDate(testDate);
        assertEquals(24, resultList.size());
    }

    @Test
    public void Should_GetAllSeancesByPeriod_When_ItIsGiven() {
        List<MainScheduleViewEntity> resultList = remoteDatabaseClient.getAllSeancesByPeriod(testDate, testDate.plusDays(1));
        assertEquals(47, resultList.size());
    }

    @Test
    public void Should_GetAllMoviesByDate_When_ItIsGiven() {
        Map<Integer, String> result = remoteDatabaseClient.getAllMoviesByDate(testDate);
        assertTrue(result.containsValue("Film1"));
        assertTrue(result.containsValue("Film2"));
        assertFalse(result.containsValue("Film3"));

        result = remoteDatabaseClient.getAllMoviesByDate(testDate.plusDays(1));
        assertTrue(result.containsValue("Film1"));
        assertTrue(result.containsValue("Film2"));
        assertTrue(result.containsValue("Film3"));

        result = remoteDatabaseClient.getAllMoviesByDate(testDate.plusDays(10));
        assertTrue(result.isEmpty());
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

    @Test
    @Transactional
    public void Should_CreateNewSeance_When_InputInfoIsGiven() {
        CreateSeanceInput seanceInput = CreateSeanceInput.builder()
                .date(testDate.plusDays(5))
                .startTime(LocalTime.of(7,30))
                .endTime(LocalTime.of(8,30))
                .movieParkId(1)
                .movieId(1)
                .hallId(101)
                .basePrice(100)
                .vipPrice(200)
                .build();

        List<MainScheduleViewEntity> result = remoteDatabaseClient.getAllSeancesByDate(testDate.plusDays(5));
        assertEquals(0, result.size());
        remoteDatabaseClient.createNewSeance(seanceInput);

        result = remoteDatabaseClient.getAllSeancesByDate(testDate.plusDays(5));
        assertEquals(1, result.size());
    }

    @Test
    public void updateScheduleTable() {
    }

    @Test
    public void Should_GetHallPlacesInfo_When_HallIdIsGiven() {
        List<HallsEntity> result = hallsRepo.findAllByHallId(101).get();
        assertEquals(3, result.size());

        result = hallsRepo.findAllByHallId(202).get();
        assertEquals(5, result.size());

        result = hallsRepo.findAllByHallId(303).get();
        assertEquals(7, result.size());
    }

    @Test
    public void Should_GetSeancePlacesInfo_When_SeanceIdIsGiven() {
        List<SeancePlacesEntity> result = seancesPlacesRepo.findAllBySeanceId(1);
        assertEquals(3, result.size());

        result = seancesPlacesRepo.findAllBySeanceId(6);
        assertEquals(5, result.size());

        result = seancesPlacesRepo.findAllBySeanceId(35);
        assertEquals(7, result.size());
    }

    @Test
    @Ignore
    public void Should_BlockOrUnblockPlaceOnSeance_WhenInputIsGiven() {
        List<SeancePlacesEntity> result1 = remoteDatabaseClient.getSeancePlacesInfo(93);

        BlockUnblockPlaceInput input = BlockUnblockPlaceInput.builder()
                .seanceId(93)
                .blocked(true)
                .placeIdList(Arrays.asList(101, 102))
                .build();

        remoteDatabaseClient.blockOrUnblockPlaceOnSeance(input);

        List<SeancePlacesEntity> result2 = remoteDatabaseClient.getSeancePlacesInfo(93);
        int sum = 0;
        for (SeancePlacesEntity entity : result2) {
            if (entity.getBlocked()) {
                sum ++;
            }
        }

        assertEquals(2, sum);
    }
}
