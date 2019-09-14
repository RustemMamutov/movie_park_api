package ru.api.moviepark.data.dbclient;

import ru.api.moviepark.controller.CommonResponse;
import ru.api.moviepark.data.entities.HallsEntity;
import ru.api.moviepark.data.entities.SeancePlacesEntity;
import ru.api.moviepark.data.valueobjects.AllSeancesView;
import ru.api.moviepark.data.valueobjects.BlockUnblockPlaceInput;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;

import java.time.LocalDate;
import java.util.List;

public interface DatabaseClient {

    void changeCacheLifeTime(long cacheLifeTime);

    List<AllSeancesView> getAllSeances();

    List<AllSeancesView> getAllSeancesForDate(LocalDate date);

    CommonResponse createNewSeance(CreateSeanceInput inputJson);

    /**
     * Update seances for next days.
     */
    void updateScheduleTable(int days);

    /**
     * Get info about all places in hall.
     */
    List<HallsEntity> getHallFullInfo(int hallId);

    /**
     * Get info about all places for current seance.
     */
    List<SeancePlacesEntity> getSeanceFullInfo(int seanceId);

    /**
     * Block the place in hall for current seance.
     */
    void blockOrUnblockPlaceOnSeance(BlockUnblockPlaceInput inputJson);

}
