package ru.api.moviepark.util;

import ru.api.moviepark.controller.CommonResponse;
import ru.api.moviepark.data.entities.MainScheduleEntity;
import ru.api.moviepark.data.repositories.HallsRepo;
import ru.api.moviepark.data.repositories.MainScheduleRepo;
import ru.api.moviepark.data.repositories.MoviesRepo;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.api.moviepark.controller.CommonResponse.INVALID_DATE;
import static ru.api.moviepark.controller.CommonResponse.INVALID_HALL;
import static ru.api.moviepark.controller.CommonResponse.INVALID_MOVIE;
import static ru.api.moviepark.controller.CommonResponse.INVALID_PRICE;
import static ru.api.moviepark.controller.CommonResponse.INVALID_TIME_PERIOD;
import static ru.api.moviepark.controller.CommonResponse.VALID_DATA;

public class CheckInputUtil {

    private static MainScheduleRepo mainScheduleRepo;
    private static MoviesRepo moviesRepo;
    private static HallsRepo hallsRepo;

    public CheckInputUtil() {
    }

    public static void setMainScheduleRepo(MainScheduleRepo repo) {
        mainScheduleRepo = repo;
    }

    public static void setMoviesRepo(MoviesRepo moviesRepo) {
        CheckInputUtil.moviesRepo = moviesRepo;
    }

    public static void setHallsRepo(HallsRepo hallsRepo) {
        CheckInputUtil.hallsRepo = hallsRepo;
    }

    public static CommonResponse checkCreateSeanceInput(CreateSeanceInput inputJson) {
        LocalDate inputDate = inputJson.getDate();
        int inputHallId = inputJson.getHallId();

        if (inputDate.isBefore(LocalDate.now())) {
            return INVALID_DATE;
        }

        if (!moviesRepo.checkIdExists(inputJson.getMovieId()).orElse(false)) {
            return INVALID_MOVIE;
        }

        if (!hallsRepo.checkIdExists(inputHallId).orElse(false)) {
            return INVALID_HALL;
        }

        if (inputJson.getBasePrice() <= 0) {
            return INVALID_PRICE;
        }

        LocalTime inputStartTime = inputJson.getStartTime();
        LocalTime inputEndTime = inputJson.getEndTime();

        if (inputEndTime.isBefore(inputStartTime)) {
            return INVALID_TIME_PERIOD;
        }

        List<MainScheduleEntity> allSeancesForDate = mainScheduleRepo.findSeancesEntityBySeanceDateAndHallId(inputDate, inputHallId);
        for (MainScheduleEntity currentSeance : allSeancesForDate) {
            LocalTime currentStartTime = currentSeance.getStartTime();
            LocalTime currentEndTime = currentSeance.getEndTime();

            if (inputStartTime.isAfter(currentEndTime) || inputEndTime.isBefore(currentStartTime)) {
                continue;
            }

            return INVALID_TIME_PERIOD;
        }

        return VALID_DATA;
    }
}
