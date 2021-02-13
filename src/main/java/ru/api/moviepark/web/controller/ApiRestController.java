package ru.api.moviepark.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.api.moviepark.data.dto.MainScheduleDTO;
import ru.api.moviepark.data.entities.HallsEntity;
import ru.api.moviepark.data.entities.MoviesEntity;
import ru.api.moviepark.data.entities.SeancePlacesEntity;
import ru.api.moviepark.data.valueobjects.BlockUnblockPlaceInput;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;
import ru.api.moviepark.data.valueobjects.MoviesInfoInput;
import ru.api.moviepark.security.AllowApiCreate;
import ru.api.moviepark.security.AllowApiDelete;
import ru.api.moviepark.security.AllowApiModify;
import ru.api.moviepark.service.MovieParkClient;
import ru.api.moviepark.swagger.AddToSwagger;
import ru.api.moviepark.web.CustomResponse;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static ru.api.moviepark.env.Constants.dateTimeFormatter;
import static ru.api.moviepark.web.CustomResponse.*;

@RestController
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@AddToSwagger
@RequestMapping("/movie-park/api")
public class ApiRestController {

    private final MovieParkClient movieParkClient;

    public ApiRestController(MovieParkClient movieParkClient) {
        this.movieParkClient = movieParkClient;
    }

    @GetMapping("/seances/info/{seanceId}")
    public MainScheduleDTO getSeanceById(@PathVariable int seanceId) {
        return movieParkClient.getSeanceById(seanceId);
    }

    @GetMapping("/seances/places/info/{seanceId}")
    public List<SeancePlacesEntity> getSeanceFullInfo(@PathVariable int seanceId) {
        return movieParkClient.getSeancePlacesInfo(seanceId);
    }

    @PutMapping("/seances/places/block")
    @AllowApiModify
    public ResponseEntity<CustomResponse> blockPlaces(@Valid @RequestBody BlockUnblockPlaceInput inputJson) {
        movieParkClient.blockOrUnblockPlaceOnSeance(inputJson.getSeanceId(), inputJson.getPlaceIdList(), true);
        return PLACES_BLOCKED.entity(HttpStatus.ACCEPTED);
    }

    @PutMapping("/seances/places/unblock")
    @AllowApiModify
    public ResponseEntity<CustomResponse> unblockPlaces(@Valid @RequestBody BlockUnblockPlaceInput inputJson) {
        movieParkClient.blockOrUnblockPlaceOnSeance(inputJson.getSeanceId(), inputJson.getPlaceIdList(), false);
        return PLACES_UNBLOCKED.entity(HttpStatus.ACCEPTED);
    }

    @PutMapping("/seances/places/update-schedule")
    @AllowApiModify
    public ResponseEntity<CustomResponse> updateScheduleTable(@RequestParam int days) {
        movieParkClient.updateScheduleTable(days);
        return TABLE_FILLED.entity(HttpStatus.ACCEPTED);
    }

    @GetMapping("/seances/all-by-period")
    public List<MainScheduleDTO> getAllSeancesForPeriod(@RequestParam String periodStart,
                                                        @RequestParam String periodEnd) {
        LocalDate periodStartDate = LocalDate.parse(periodStart, dateTimeFormatter);
        LocalDate periodEndDate = LocalDate.parse(periodEnd, dateTimeFormatter);
        return movieParkClient.getAllSeancesByPeriod(periodStartDate, periodEndDate);
    }

    @GetMapping("/seances/all-by-movie-and-date")
    public Map<String, List<MainScheduleDTO>> getAllSeancesByMovieAndDateGroupByMoviePark(
            @RequestParam int movieId, @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
        return movieParkClient.getAllSeancesByMovieAndDateGroupByMoviePark(movieId, localDate);
    }

    @PostMapping("/seances/create")
    @ResponseStatus(HttpStatus.CREATED)
    @AllowApiCreate
    public void createSeance(@Valid @RequestBody CreateSeanceInput inputJson) {
        movieParkClient.createNewSeance(inputJson);
    }

    @DeleteMapping("/seances/delete/{seanceId}")
    @AllowApiDelete
    public void deleteSeance(@PathVariable int seanceId) {
        movieParkClient.deleteSeance(seanceId);
    }

    @PostMapping("/movies/all-by-id-set")
    public Map<Integer, MoviesEntity> getAllMoviesByIdSet(@RequestBody MoviesInfoInput input) {
        return movieParkClient.getAllMoviesByIdSet(input.getMovieIdSet());
    }

    @GetMapping("/movies/all-by-period")
    public Map<LocalDate, Map<Integer, String>> getAllMoviesByPeriod(@RequestParam String periodStart,
                                                                     @RequestParam String periodEnd) {
        LocalDate periodStartDate = LocalDate.parse(periodStart, dateTimeFormatter);
        LocalDate periodEndDate = LocalDate.parse(periodEnd, dateTimeFormatter);
        return movieParkClient.getAllMoviesByPeriod(periodStartDate, periodEndDate);
    }

    @GetMapping("/halls/places-info/{hallId}")
    public List<HallsEntity> getHallFullInfo(@PathVariable int hallId) {
        return movieParkClient.getHallPlacesInfo(hallId);
    }
}
