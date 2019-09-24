package ru.api.moviepark.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;
import ru.api.moviepark.controller.CommonResponse;
import ru.api.moviepark.data.repositories.HallsRepo;
import ru.api.moviepark.data.repositories.MainScheduleRepo;
import ru.api.moviepark.data.repositories.MoviesRepo;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootConfiguration
@EnableJpaRepositories
@ComponentScan("ru.api.moviepark")
@PropertySource("/application-test.properties")
public class CommandCheckerUtilTest {

    @Autowired
    private MainScheduleRepo mainScheduleRepo;

    @Autowired
    private MoviesRepo moviesRepo;

    @Autowired
    private HallsRepo hallsRepo;

    private LocalDate testDate = LocalDate.of(2030, 1, 1);

    @Before
    public void createSeanceInputTemplate() {
        CheckInputUtil.setMainScheduleRepo(mainScheduleRepo);
        CheckInputUtil.setMoviesRepo(moviesRepo);
        CheckInputUtil.setHallsRepo(hallsRepo);
    }

    @Test
    public void Should_checkCreateSeancePostQueryInput() {
        CreateSeanceInput seanceInput = CreateSeanceInput.builder()
                .date(testDate)
                .movieParkId(1)
                .movieId(1)
                .hallId(101)
                .basePrice(100)
                .vipPrice(200)
                .build();

        setTimePeriod(seanceInput, LocalTime.of(8,30), LocalTime.of(9,10));
        assertEquals(CommonResponse.INVALID_TIME_PERIOD, CheckInputUtil.checkCreateSeanceInput(seanceInput));

        setTimePeriod(seanceInput, LocalTime.of(9,10), LocalTime.of(10,30));
        assertEquals(CommonResponse.INVALID_TIME_PERIOD, CheckInputUtil.checkCreateSeanceInput(seanceInput));

        setTimePeriod(seanceInput, LocalTime.of(10,30), LocalTime.of(10,50));
        assertEquals(CommonResponse.INVALID_TIME_PERIOD, CheckInputUtil.checkCreateSeanceInput(seanceInput));

        setTimePeriod(seanceInput, LocalTime.of(8,50), LocalTime.of(10,50));
        assertEquals(CommonResponse.INVALID_TIME_PERIOD, CheckInputUtil.checkCreateSeanceInput(seanceInput));

        setTimePeriod(seanceInput, LocalTime.of(7,10), LocalTime.of(8,50));
        assertEquals(CommonResponse.VALID_DATA, CheckInputUtil.checkCreateSeanceInput(seanceInput));
    }

    private void setTimePeriod(CreateSeanceInput seanceInput, LocalTime start, LocalTime finish) {
        seanceInput.setStartTime(start);
        seanceInput.setEndTime(finish);
    }
}
