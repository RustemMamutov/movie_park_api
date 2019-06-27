package ru.api.moviepark.data;

import ru.api.moviepark.controller.CommonResponse;
import ru.api.moviepark.data.valueobjects.AllSeancesView;
import ru.api.moviepark.data.valueobjects.BlockPlaceInput;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;
import ru.api.moviepark.data.valueobjects.PlaceInHallInfo;

import java.time.LocalDate;
import java.util.List;

public interface DatabaseClient {
    List<AllSeancesView> getAllSeances();

    List<AllSeancesView> getAllSeancesForDate(LocalDate date);

    CommonResponse createNewSeance(CreateSeanceInput inputJson);

    void fillScheduleTableForDate(LocalDate date);

    List<PlaceInHallInfo> getSeanceFullInfo(int seanceId);

    void blockOrUnblockPlaceOnSeance(BlockPlaceInput inputJson);
}
