package ru.api.moviepark.service.dbclient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.api.moviepark.data.dto.MainScheduleDTO;
import ru.api.moviepark.data.entities.HallsEntity;
import ru.api.moviepark.data.entities.SeancePlacesEntity;
import ru.api.moviepark.data.repositories.HallsRepo;
import ru.api.moviepark.data.repositories.SeancesPlacesRepo;
import ru.api.moviepark.data.valueobjects.BlockUnblockPlaceInput;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;
import ru.api.moviepark.service.MovieParkClient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class MovieParkClientImplTest {

    private final LocalDate testDate = LocalDate.of(2030, 1, 1);
    @Autowired
    private MovieParkClient movieParkClient;
    @Autowired
    private HallsRepo hallsRepo;
    @Autowired
    private SeancesPlacesRepo seancesPlacesRepo;

    @Before
    public void createSeanceInputTemplate() {

    }

    @Test
    public void changeCacheLifeTime() {
    }

    @Test
    public void Should_GetSeanceById_When_ItIsGiven() {
        MainScheduleDTO result = movieParkClient.getSeanceById(1);
        MainScheduleDTO expected = MainScheduleDTO.builder()
                .seanceId(1)
                .seanceDate(testDate)
                .startTime(LocalTime.of(8, 0, 0))
                .endTime(LocalTime.of(9, 50, 0))
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
        List<MainScheduleDTO> resultList = movieParkClient.getAllSeancesByDate(testDate);
        assertEquals(21, resultList.size());
    }

    @Test
    public void Should_GetAllSeancesByPeriod_When_ItIsGiven() {
        List<MainScheduleDTO> resultList = movieParkClient.getAllSeancesByPeriod(testDate, testDate.plusDays(1));
        assertEquals(43, resultList.size());
    }

    @Test
    public void Should_GetAllMoviesByDate_When_ItIsGiven() {
        Map<Integer, String> result = movieParkClient.getAllMoviesByDate(testDate);
        assertTrue(result.containsValue("Movie1"));

        result = movieParkClient.getAllMoviesByDate(testDate.plusDays(2));
        assertTrue(result.containsValue("Movie1"));
        assertTrue(result.containsValue("Movie2"));

        result = movieParkClient.getAllMoviesByDate(testDate.plusDays(4));
        assertTrue(result.containsValue("Movie2"));
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

    @Test
    public void Should_CreateNewSeance_When_InputInfoIsGiven() {
        CreateSeanceInput seanceInput = CreateSeanceInput.builder()
                .date(testDate.plusDays(4))
                .startTime(LocalTime.of(7, 30))
                .endTime(LocalTime.of(8, 30))
                .movieParkId(1)
                .movieId(1)
                .hallId(101)
                .basePrice(100)
                .vipPrice(200)
                .build();

        List<MainScheduleDTO> result = movieParkClient.getAllSeancesByDate(testDate.plusDays(4));
        assertEquals(1, result.size());
        movieParkClient.createNewSeance(seanceInput);

        List<MainScheduleDTO> result2 = movieParkClient.getAllSeancesByDate(testDate.plusDays(4));
        assertEquals(2, result2.size());
    }

    @Test
    public void Should_GetHallPlacesInfo_When_HallIdIsGiven() {
        Optional<List<HallsEntity>> result = hallsRepo.findAllByHallId(101);
        if (!result.isPresent()) {
            throw new RuntimeException();
        } else {
            assertEquals(9, result.get().size());
        }

        result = hallsRepo.findAllByHallId(102);
        if (!result.isPresent()) {
            throw new RuntimeException();
        } else {
            assertEquals(10, result.get().size());
        }

        result = hallsRepo.findAllByHallId(201);
        if (!result.isPresent()) {
            throw new RuntimeException();
        } else {
            assertEquals(6, result.get().size());
        }

        result = hallsRepo.findAllByHallId(202);
        if (!result.isPresent()) {
            throw new RuntimeException();
        } else {
            assertEquals(14, result.get().size());
        }
    }

    @Test
    public void Should_GetSeancePlacesInfo_When_SeanceIdIsGiven() {
        List<SeancePlacesEntity> result = seancesPlacesRepo.findAllBySeanceId(1);
        assertEquals(9, result.size());
    }

    @Test
    public void Should_BlockOrUnblockPlaceOnSeance_WhenInputIsGiven() {
        List<SeancePlacesEntity> result1 = movieParkClient.getSeancePlacesInfo(1);

        BlockUnblockPlaceInput input = BlockUnblockPlaceInput.builder()
                .seanceId(1)
                .placeIdList(Arrays.asList(101, 102))
                .build();

        movieParkClient.blockOrUnblockPlaceOnSeance(input.getSeanceId(), input.getPlaceIdList(), true);

        List<SeancePlacesEntity> result2 = movieParkClient.getSeancePlacesInfo(1);
        long sum = result2.stream().filter(SeancePlacesEntity::getBlocked).count();

        assertEquals(2, sum);
    }
}
