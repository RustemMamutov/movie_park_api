package ru.api.moviepark.util;

import ru.api.moviepark.data.entities.MainScheduleEntity;
import ru.api.moviepark.data.repositories.HallsRepo;
import ru.api.moviepark.data.repositories.MainScheduleRepo;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;
import ru.api.moviepark.exceptions.MyInvalidInputException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.api.moviepark.web.CustomResponse.INVALID_DATE;
import static ru.api.moviepark.web.CustomResponse.INVALID_HALL;
import static ru.api.moviepark.web.CustomResponse.INVALID_PRICE;
import static ru.api.moviepark.web.CustomResponse.INVALID_SEANCE_ID;
import static ru.api.moviepark.web.CustomResponse.INVALID_START_END_TIME;
import static ru.api.moviepark.web.CustomResponse.INVALID_TIME_PERIOD;

public class InputPreconditionsUtil {

    private static MainScheduleRepo mainScheduleRepo;
    private static HallsRepo hallsRepo;

    public static void setMainScheduleRepo(MainScheduleRepo repo) {
        mainScheduleRepo = repo;
    }

    public static void setHallsRepo(HallsRepo hallsRepo) {
        InputPreconditionsUtil.hallsRepo = hallsRepo;
    }

    public static void checkSeanceIdExists(int seanceId) {
        if (mainScheduleRepo.checkSeanceIdExists(seanceId)) {
            return;
        }
        throw new MyInvalidInputException(INVALID_SEANCE_ID);
    }

    public static void checkHallIdExists(int hallId) {
        if (hallsRepo.checkHallIdExists(hallId)) {
            return;
        }
        throw new MyInvalidInputException(INVALID_HALL);
    }

    public static void checkPrice(int price) {
        if (price <= 0) {
            throw new MyInvalidInputException(INVALID_PRICE);
        }
    }

    public static void checkInputDate(LocalDate inputDate) {
        if (inputDate.isBefore(LocalDate.now())) {
            throw new MyInvalidInputException(INVALID_DATE);
        }
    }

    public static void checkInputTimePeriod(LocalTime inputStartTime, LocalTime inputEndTime) {
        if (inputEndTime.isBefore(inputStartTime)) {
            throw new MyInvalidInputException(INVALID_START_END_TIME);
        }
    }

    public static void checkCreateSeanceInput(CreateSeanceInput inputJson) {
        LocalDate inputDate = inputJson.getDate();
        int inputHallId = inputJson.getHallId();

        checkInputDate(inputJson.getDate());
        checkHallIdExists(inputJson.getHallId());
        checkPrice(inputJson.getBasePrice());
        checkPrice(inputJson.getVipPrice());

        LocalTime inputStartTime = inputJson.getStartTime();
        LocalTime inputEndTime = inputJson.getEndTime();

        checkInputTimePeriod(inputStartTime, inputEndTime);

        List<MainScheduleEntity> allSeancesForDate =
                mainScheduleRepo.findSeancesEntityBySeanceDateAndHallId(inputDate, inputHallId);

        long crossingByTimeCount = allSeancesForDate.stream()
                .filter(seance ->
                        checkTimeInSeanceInterval(seance, inputStartTime) ||
                                checkTimeInSeanceInterval(seance, inputEndTime))
                .count();

        if (crossingByTimeCount > 0) {
            throw new MyInvalidInputException(INVALID_TIME_PERIOD);
        }
    }

    private static boolean checkTimeInSeanceInterval(MainScheduleEntity seance, LocalTime testTime) {
        return !(testTime.isBefore(seance.getStartTime()) || testTime.isAfter(seance.getEndTime()));
    }
}
