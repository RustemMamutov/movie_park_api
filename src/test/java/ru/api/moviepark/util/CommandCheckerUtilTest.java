package ru.api.moviepark.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;
import ru.api.moviepark.controller.CommonResponse;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@EnableJpaRepositories
@ComponentScan("ru.api.moviepark")
@PropertySource("/application-test.yaml")
public class CommandCheckerUtilTest {

    private CreateSeanceInput seanceInput;

    private LocalDate testDate = LocalDate.of(2030, 1, 1);

    @Before
    public void createSeanceInputTemplate() {
        seanceInput = CreateSeanceInput.builder()
                .date(testDate)
                .startTime(LocalTime.of(7,30))
                .endTime(LocalTime.of(8,30))
                .movieParkId(1)
                .movieId(1)
                .hallId(101)
                .basePrice(100)
                .vipPrice(200)
                .build();
    }

    @Test
    public void Should_Return_InvalidTimePeriod_When_InvalidPeriodIsGiven() {
        setTimePeriod(seanceInput, LocalTime.of(8,30), LocalTime.of(9,10));
        assertEquals(CommonResponse.INVALID_TIME_PERIOD, CheckInputUtil.checkCreateSeanceInput(seanceInput));

        setTimePeriod(seanceInput, LocalTime.of(9,10), LocalTime.of(10,30));
        assertEquals(CommonResponse.INVALID_TIME_PERIOD, CheckInputUtil.checkCreateSeanceInput(seanceInput));

        setTimePeriod(seanceInput, LocalTime.of(10,30), LocalTime.of(10,50));
        assertEquals(CommonResponse.INVALID_TIME_PERIOD, CheckInputUtil.checkCreateSeanceInput(seanceInput));

        setTimePeriod(seanceInput, LocalTime.of(8,50), LocalTime.of(10,50));
        assertEquals(CommonResponse.INVALID_TIME_PERIOD, CheckInputUtil.checkCreateSeanceInput(seanceInput));
    }

    @Test
    public void Should_Return_InvalidDate_When_DateIsBeforeToday() {
        seanceInput.setDate(LocalDate.of(2000, 1, 1));
        assertEquals(CommonResponse.INVALID_DATE, CheckInputUtil.checkCreateSeanceInput(seanceInput));
    }

    @Test
    public void Should_Return_InvalidHall_When_IncorrectHallIdIsGiven() {
        seanceInput.setHallId(9999);
        assertEquals(CommonResponse.INVALID_HALL, CheckInputUtil.checkCreateSeanceInput(seanceInput));
    }

    @Test
    public void Should_Return_InvalidPrice_When_IncorrectPriceIsGiven() {
        seanceInput.setBasePrice(-100);
        assertEquals(CommonResponse.INVALID_PRICE, CheckInputUtil.checkCreateSeanceInput(seanceInput));

        seanceInput.setBasePrice(0);
        assertEquals(CommonResponse.INVALID_PRICE, CheckInputUtil.checkCreateSeanceInput(seanceInput));
    }

    @Test
    public void Should_Return_Valid_When_CorrectInput() {
        assertEquals(CommonResponse.VALID_DATA, CheckInputUtil.checkCreateSeanceInput(seanceInput));
    }

    private void setTimePeriod(CreateSeanceInput seanceInput, LocalTime start, LocalTime finish) {
        seanceInput.setStartTime(start);
        seanceInput.setEndTime(finish);
    }
}
