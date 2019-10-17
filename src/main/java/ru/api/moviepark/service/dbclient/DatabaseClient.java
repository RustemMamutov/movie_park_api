package ru.api.moviepark.service.dbclient;

import ru.api.moviepark.controller.CommonResponse;
import ru.api.moviepark.data.entities.HallsEntity;
import ru.api.moviepark.data.entities.SeancePlacesEntity;
import ru.api.moviepark.data.valueobjects.BlockUnblockPlaceInput;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;
import ru.api.moviepark.data.valueobjects.MainScheduleViewEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DatabaseClient {

    void changeCacheLifeTime(long cacheLifeTime);

    MainScheduleViewEntity getSeanceById(int seanceId);

    List<MainScheduleViewEntity> getAllSeancesByPeriod(LocalDate periodStart, LocalDate periodEnd);

    Map<Integer, String> getAllMoviesByDate(LocalDate date);

    List<MainScheduleViewEntity> getAllSeancesByDate(LocalDate date);

    Map<LocalDate, Map<Integer, String>> getAllMoviesByPeriod(LocalDate periodStart, LocalDate periodEnd);

    Map<String, List<MainScheduleViewEntity>> getAllSeancesByMovieAndDateGroupByMoviePark(int movieId, LocalDate date);

    CommonResponse createNewSeance(CreateSeanceInput inputJson);

    /**
     * Update seances for next days.
     */
    void updateScheduleTable(int days);

    /**
     * Get info about all places in hall.
     */
    List<HallsEntity> getHallPlacesInfo(int hallId);

    /**
     * Get info about all places for current seance.
     */
    List<SeancePlacesEntity> getSeancePlacesInfo(int seanceId);

    /**
     * Block the place in hall for current seance.
     */
    void blockOrUnblockPlaceOnSeance(BlockUnblockPlaceInput inputJson);
}
