package ru.api.moviepark.service;

import ru.api.moviepark.data.dto.MainScheduleDTO;
import ru.api.moviepark.data.entities.HallsEntity;
import ru.api.moviepark.data.entities.SeancePlacesEntity;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MovieParkClient {

    MainScheduleDTO getSeanceById(int seanceId);

    /**
     * Get info about all places in hall.
     */
    List<HallsEntity> getHallPlacesInfo(int hallId);

    /**
     * Get info about all places for current seance.
     */
    List<SeancePlacesEntity> getSeancePlacesInfo(int seanceId);

    List<MainScheduleDTO> getAllSeancesByDate(LocalDate date);

    List<MainScheduleDTO> getAllSeancesByPeriod(LocalDate periodStart, LocalDate periodEnd);

    Map<Integer, String> getAllMoviesByDate(LocalDate date);

    Map<LocalDate, Map<Integer, String>> getAllMoviesByPeriod(LocalDate periodStart, LocalDate periodEnd);

    Map<String, List<MainScheduleDTO>> getAllSeancesByMovieAndDateGroupByMoviePark(int movieId, LocalDate date);

    void createNewSeance(CreateSeanceInput inputJson);

    void deleteSeance(int seanceId);

    /**
     * Update seances for next days.
     */
    void updateScheduleTable(int days);

    /**
     * Block or unblock the places in hall for current seance.
     */
    void blockOrUnblockPlaceOnSeance(int seanceId, List<Integer> placeList, boolean blocked);
}
