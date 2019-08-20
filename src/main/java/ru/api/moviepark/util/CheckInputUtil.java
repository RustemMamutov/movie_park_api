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
        if (inputDate.isBefore(LocalDate.now())) {
            return INVALID_DATE;
        }

        if (!moviesRepo.checkIdExists(inputJson.getMovieId()).orElse(false)) {
            return INVALID_MOVIE;
        }

        if (!hallsRepo.checkIdExists(inputJson.getHallId()).orElse(false)) {
            return INVALID_HALL;
        }

        if (inputJson.getBasePrice() <= 0) {
            return INVALID_PRICE;
        }

        int inputHallId = inputJson.getHallId();
        LocalTime inputStartTime = inputJson.getStartTime();
        LocalTime inputEndTime = inputJson.getEndTime();

        List<MainScheduleEntity> allSeancesForDate = mainScheduleRepo.findSeancesEntityBySeanceDateAndHallId(inputDate, inputHallId);
        for (MainScheduleEntity entity : allSeancesForDate) {
            LocalTime localStartTime = entity.getStartTime();
            LocalTime localEndTime = entity.getEndTime();
            boolean startChecker = localStartTime.isBefore(inputStartTime) && inputStartTime.isBefore(localEndTime);
            boolean endChecker = localStartTime.isBefore(inputEndTime) && inputEndTime.isBefore(localEndTime);
            if (startChecker || endChecker) {
                return INVALID_TIME_PERIOD;
            }
        }

        return VALID_DATA;
    }
}
