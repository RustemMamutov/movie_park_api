package ru.api.moviepark.service.dbclient;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@EnableJpaRepositories
@ComponentScan("ru.api.moviepark")
@PropertySource("/application-test.yaml")
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
                .startTime(LocalTime.of(8,0,0))
                .endTime(LocalTime.of(9,50,0))
                .movieParkId(1)
                .movieParkName("CinemaPark1")
                .movieId(1)
                .movieName("Movie1")
                .hallId(101)
                .basePrice(100)
                .vipPrice(200)
                .build();

        assertEquals(expected, result);
    }

    @Test
    public void Should_GetAllSeancesByDate_When_ItIsGiven() {
        List<MainScheduleViewEntity> resultList = remoteDatabaseClient.getAllSeancesByDate(testDate);
        assertEquals(21, resultList.size());
    }

    @Test
    public void Should_GetAllSeancesByPeriod_When_ItIsGiven() {
        List<MainScheduleViewEntity> resultList = remoteDatabaseClient.getAllSeancesByPeriod(testDate, testDate.plusDays(1));
        assertEquals(43, resultList.size());
    }

    @Test
    public void Should_GetAllMoviesByDate_When_ItIsGiven() {
        Map<Integer, String> result = remoteDatabaseClient.getAllMoviesByDate(testDate);
        assertTrue(result.containsValue("Movie1"));

        result = remoteDatabaseClient.getAllMoviesByDate(testDate.plusDays(2));
        assertTrue(result.containsValue("Movie1"));
        assertTrue(result.containsValue("Movie2"));

        result = remoteDatabaseClient.getAllMoviesByDate(testDate.plusDays(4));
        assertTrue(result.containsValue("Movie2"));
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
    @Ignore
    public void Should_CreateNewSeance_When_InputInfoIsGiven() {
        CreateSeanceInput seanceInput = CreateSeanceInput.builder()
                .date(testDate.plusDays(4))
                .startTime(LocalTime.of(7,30))
                .endTime(LocalTime.of(8,30))
                .movieParkId(1)
                .movieId(1)
                .hallId(101)
                .basePrice(100)
                .vipPrice(200)
                .build();

        List<MainScheduleViewEntity> result = remoteDatabaseClient.getAllSeancesByDate(testDate.plusDays(4));
        assertEquals(1, result.size());
        remoteDatabaseClient.createNewSeance(seanceInput);

        List<MainScheduleViewEntity> result2 = remoteDatabaseClient.getAllSeancesByDate(testDate.plusDays(4));
        assertEquals(2, result2.size());
    }

    @Test
    public void Should_GetHallPlacesInfo_When_HallIdIsGiven() {
        List<HallsEntity> result = hallsRepo.findAllByHallId(101).get();
        assertEquals(9, result.size());

        result = hallsRepo.findAllByHallId(102).get();
        assertEquals(10, result.size());

        result = hallsRepo.findAllByHallId(201).get();
        assertEquals(6, result.size());

        result = hallsRepo.findAllByHallId(202).get();
        assertEquals(14, result.size());
    }

    @Test
    public void Should_GetSeancePlacesInfo_When_SeanceIdIsGiven() {
        List<SeancePlacesEntity> result = seancesPlacesRepo.findAllBySeanceId(1);
        assertEquals(9, result.size());
    }

    @Test
    @Ignore
    public void Should_BlockOrUnblockPlaceOnSeance_WhenInputIsGiven() {
        List<SeancePlacesEntity> result1 = remoteDatabaseClient.getSeancePlacesInfo(1);

        BlockUnblockPlaceInput input = BlockUnblockPlaceInput.builder()
                .seanceId(1)
                .blocked(true)
                .placeIdList(Arrays.asList(101, 102))
                .build();

        remoteDatabaseClient.blockOrUnblockPlaceOnSeance(input);

        List<SeancePlacesEntity> result2 = remoteDatabaseClient.getSeancePlacesInfo(1);
        int sum = 0;
        for (SeancePlacesEntity entity : result2) {
            if (entity.getBlocked()) {
                sum ++;
            }
        }

        assertEquals(2, sum);
    }
}
