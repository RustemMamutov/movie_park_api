package ru.api.moviepark;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.api.moviepark.data.remote.RemoteDatabaseClient;
import ru.api.moviepark.data.valueobjects.AllSeancesView;
import ru.api.moviepark.data.valueobjects.BlockPlaceInput;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MovieParkApplicationTests {

    @Autowired
    private RemoteDatabaseClient service;

    @Test
    public void test() {
//		List<PlaceInHallInfo> res = service.getSeanceFullInfo(145);
//		List<AllSeancesView> res1 = service.getAllSeances();
        List<AllSeancesView> res2 = service.getAllSeancesForDate(LocalDate.now().plusDays(1));
        System.out.println();
    }

    //	@Test
    public void test1() {
        BlockPlaceInput input = BlockPlaceInput.builder()
                .seanceId(145)
                .row(10)
                .place(10)
                .blocked(true)
                .build();
        service.blockOrUnblockPlaceOnSeance(input);
    }

    //	@Test
    public void test2() {
        CreateSeanceInput inputJson = CreateSeanceInput.builder()
                .date(LocalDate.of(2019, 1, 5))
                .startTime(LocalTime.of(7, 0))
                .endTime(LocalTime.of(8, 50))
                .movieId(1)
                .hallId(1)
                .basePrice(100)
                .build();
        service.createNewSeance(inputJson);
    }
}

