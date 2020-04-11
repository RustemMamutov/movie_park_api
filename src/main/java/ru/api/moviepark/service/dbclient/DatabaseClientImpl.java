package ru.api.moviepark.service.dbclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.api.moviepark.controller.CommonResponse;
import ru.api.moviepark.data.entities.HallsEntity;
import ru.api.moviepark.data.entities.MainScheduleEntity;
import ru.api.moviepark.data.entities.SeancePlacesEntity;
import ru.api.moviepark.data.mappers.MainScheduleRowMapper;
import ru.api.moviepark.data.repositories.HallsRepo;
import ru.api.moviepark.data.repositories.MainScheduleRepo;
import ru.api.moviepark.data.repositories.SeancesPlacesRepo;
import ru.api.moviepark.data.valueobjects.BlockUnblockPlaceInput;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;
import ru.api.moviepark.service.cache.MoviesInfoTtlCache;
import ru.api.moviepark.service.cache.SeanceInfoTtlCache;
import ru.api.moviepark.service.cache.SeancePlacesTtlCache;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.api.moviepark.config.Constants.MAIN_SCHEDULE_VIEW_FULL;
import static ru.api.moviepark.config.Constants.SCHEMA_NAME;
import static ru.api.moviepark.config.Constants.dateTimeFormatter;
import static ru.api.moviepark.controller.CommonResponse.SEANCE_ADDED;
import static ru.api.moviepark.controller.CommonResponse.VALID_DATA;
import static ru.api.moviepark.data.entities.MainScheduleEntity.createMainScheduleEntity;
import static ru.api.moviepark.util.CheckInputUtil.checkCreateSeanceInput;

@Service
@Slf4j
public class DatabaseClientImpl implements DatabaseClient {

    private final JdbcTemplate jdbcTemplate;
    private final HallsRepo hallsRepo;
    private final MainScheduleRepo mainScheduleRepo;
    private final SeancesPlacesRepo seancesPlacesRepo;
    private final RowMapper mainScheduleRowMapper = new MainScheduleRowMapper();

    public DatabaseClientImpl(JdbcTemplate jdbcTemplate,
                              HallsRepo hallsRepo,
                              MainScheduleRepo mainScheduleRepo,
                              SeancesPlacesRepo seancesPlacesRepo) {
        this.jdbcTemplate = jdbcTemplate;
        this.hallsRepo = hallsRepo;
        this.mainScheduleRepo = mainScheduleRepo;
        this.seancesPlacesRepo = seancesPlacesRepo;
    }

    @Override
    public MainScheduleEntity getSeanceById(int seanceId) {
        MainScheduleEntity result = SeanceInfoTtlCache.getSeanceByIdFromCache(seanceId);
        if (result == null) {
            getAllSeancesByDate(mainScheduleRepo.findDateBySeanceId(seanceId));
            result = SeanceInfoTtlCache.getSeanceByIdFromCache(seanceId);
        }
        return result;
    }

    @Override
    public List<MainScheduleEntity> getAllSeancesByDate(LocalDate date) {
        if (SeanceInfoTtlCache.checkCacheContainsElementByDate(date)) {
            return new ArrayList<>(SeanceInfoTtlCache.getSeancesMapByDateFromCache(date).values());
        }

        String sqlQuery = String.format("select * from %s where seance_date = '%s'",
                MAIN_SCHEDULE_VIEW_FULL, date.format(dateTimeFormatter));
        List<MainScheduleEntity> result = jdbcTemplate.query(sqlQuery, mainScheduleRowMapper);
        SeanceInfoTtlCache.addSeanceInfoToCache(result);
        return result;
    }

    @Override
    public List<MainScheduleEntity> getAllSeancesByPeriod(LocalDate periodStart, LocalDate periodEnd) {
        List<MainScheduleEntity> result = new ArrayList<>();
        while (periodStart.isBefore(periodEnd.plusDays(1))) {
            result.addAll(getAllSeancesByDate(periodStart));
            periodStart = periodStart.plusDays(1);
        }

        return result;
    }

    @Override
    public Map<Integer, String> getAllMoviesByDate(LocalDate date) {
        if (MoviesInfoTtlCache.containsElement(date)) {
            return MoviesInfoTtlCache.getElementByDateFromCache(date);
        }

        String sqlQuery = String.format("select distinct(movie_id), movie_name from %s where seance_date = '%s';",
                MAIN_SCHEDULE_VIEW_FULL, date);

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery);
        Map<Integer, String> result = new HashMap<>();
        while (sqlRowSet.next()) {
            int movieId = sqlRowSet.getInt("movie_id");
            String movieName = sqlRowSet.getString("movie_name");
            result.put(movieId, movieName);
        }

        MoviesInfoTtlCache.addElementToCache(date, result);
        return result;
    }

    @Override
    public Map<LocalDate, Map<Integer, String>> getAllMoviesByPeriod(LocalDate periodStart, LocalDate periodEnd) {
        Map<LocalDate, Map<Integer, String>> result = new HashMap<>();
        while (periodStart.isBefore(periodEnd.plusDays(1))) {
            result.put(periodStart, getAllMoviesByDate(periodStart));
            periodStart = periodStart.plusDays(1);
        }
        return result;
    }

    @Override
    public Map<String, List<MainScheduleEntity>> getAllSeancesByMovieAndDateGroupByMoviePark(int movieId, LocalDate date) {
        Map<String, List<MainScheduleEntity>> result = new HashMap<>();

        getAllSeancesByPeriod(date, date.plusDays(1)).forEach(currSeance -> {
            if (
            (currSeance.getSeanceDate().equals(date) && currSeance.getStartTime().isAfter(LocalTime.of(6,0))) ||
                    (currSeance.getSeanceDate().equals(date.plusDays(1)) && currSeance.getStartTime().isBefore(LocalTime.of(6,0)))
            ) {
                String movieParkName = currSeance.getMovieParkName();
                if (currSeance.getMovieId() == movieId) {
                    if (result.containsKey(movieParkName)) {
                        result.get(movieParkName).add(currSeance);
                    } else {
                        List<MainScheduleEntity> currentMovieParkSeanceList = new ArrayList<>();
                        currentMovieParkSeanceList.add(currSeance);
                        result.put(movieParkName, currentMovieParkSeanceList);
                    }
                }
            }
        });

        return result;
    }

    @Override
    public CommonResponse createNewSeance(CreateSeanceInput inputJson) {
        CommonResponse response = checkCreateSeanceInput(inputJson);
        if (!response.equals(VALID_DATA)) {
            return response;
        }

        MainScheduleEntity newSeanceEntity = createMainScheduleEntity(inputJson);
        mainScheduleRepo.save(newSeanceEntity);
        return SEANCE_ADDED;
    }

    @Transactional
    public void updateScheduleTable(int days) {
        try {
            jdbcTemplate.execute(String.format("select %s.create_new_seances(%s);", SCHEMA_NAME, days));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public List<HallsEntity> getHallPlacesInfo(int hallId) {
        return hallsRepo.findAllByHallId(hallId).orElse(null);
    }

    public List<SeancePlacesEntity> getSeancePlacesInfo(int seanceId) {
        log.info("Getting full info for seance id = " + seanceId);
        if (SeancePlacesTtlCache.checkCacheContainsElement(seanceId)) {
            return SeancePlacesTtlCache.getSeancePlacesInfoByIdFromCache(seanceId);
        } else {
            List<SeancePlacesEntity> seanceFullInfo = seancesPlacesRepo.findAllBySeanceId(seanceId);
            SeancePlacesTtlCache.addSeancePlacesInfoToCache(seanceId, seanceFullInfo);
            return seanceFullInfo;
        }
    }

    @Transactional
    public void blockOrUnblockPlaceOnSeance(BlockUnblockPlaceInput inputJson) {
        log.info("Updating places {} in seance {}. Set value: {}",
                inputJson.getPlaceIdList(), inputJson.getSeanceId(), inputJson.getBlocked());
        int seanceId = inputJson.getSeanceId();
        seancesPlacesRepo.blockOrUnblockThePlace(seanceId, inputJson.getPlaceIdList(), inputJson.getBlocked());
        SeancePlacesTtlCache.removeElementFromCache(seanceId);
    }
}
