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
import ru.api.moviepark.data.entities.HallsEntity;
import ru.api.moviepark.data.repositories.HallsRepo;
import ru.api.moviepark.data.repositories.MainScheduleRepo;
import ru.api.moviepark.data.repositories.MoviesRepo;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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

    private CreateSeanceInput seanceInput;

    @Before
    public void createSeanceInputTemplate() {
        CheckInputUtil.setMainScheduleRepo(mainScheduleRepo);
        CheckInputUtil.setMoviesRepo(moviesRepo);
        CheckInputUtil.setHallsRepo(hallsRepo);
        seanceInput = CreateSeanceInput.builder()
                .date(LocalDate.now())
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .movieParkId(1)
                .movieId(1)
                .hallId(101)
                .basePrice(100)
                .vipPrice(200)
                .build();
    }

    @Test
    public void Should_checkPostQuery() {
        Optional<List<HallsEntity>> resultOpt = hallsRepo.findAllByHallId(101);
        List<HallsEntity> result = resultOpt.get();

        seanceInput.setStartTime(LocalTime.of(7,0));
        seanceInput.setEndTime(LocalTime.of(8,0));
        CommonResponse checkInputResult = CheckInputUtil.checkCreateSeanceInput(seanceInput);
        assertEquals(CommonResponse.VALID_DATA, checkInputResult);
    }
}
