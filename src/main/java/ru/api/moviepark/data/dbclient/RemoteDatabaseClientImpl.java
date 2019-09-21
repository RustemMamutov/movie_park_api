package ru.api.moviepark.data.dbclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.api.moviepark.controller.CommonResponse;
import ru.api.moviepark.data.cache.SeancesFullInfoTtlCache;
import ru.api.moviepark.data.entities.HallsEntity;
import ru.api.moviepark.data.entities.MainScheduleEntity;
import ru.api.moviepark.data.entities.SeancePlacesEntity;
import ru.api.moviepark.data.mappers.AllSeancesViewRowMapper;
import ru.api.moviepark.data.repositories.HallsRepo;
import ru.api.moviepark.data.repositories.MainScheduleRepo;
import ru.api.moviepark.data.repositories.SeancesPlacesRepo;
import ru.api.moviepark.data.valueobjects.AllSeancesView;
import ru.api.moviepark.data.valueobjects.BlockUnblockPlaceInput;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.api.moviepark.config.Constants.MAIN_SCHEDULE_VIEW_FULL;
import static ru.api.moviepark.config.Constants.SCHEMA_NAME;
import static ru.api.moviepark.config.Constants.dateTimeFormatter;
import static ru.api.moviepark.controller.CommonResponse.SEANCE_ADDED;
import static ru.api.moviepark.controller.CommonResponse.VALID_DATA;
import static ru.api.moviepark.data.cache.SeancesFullInfoTtlCacheImpl.CacheValue;
import static ru.api.moviepark.data.entities.MainScheduleEntity.createMainScheduleEntity;
import static ru.api.moviepark.util.CheckInputUtil.checkCreateSeanceInput;

@Service
@Slf4j
public class RemoteDatabaseClientImpl implements DatabaseClient {

    private final JdbcTemplate jdbcTemplate;
    private final HallsRepo hallsRepo;
    private final MainScheduleRepo mainScheduleRepo;
    private final SeancesPlacesRepo seancesPlacesRepo;

    private SeancesFullInfoTtlCache fullInfoTtlCache;

    public RemoteDatabaseClientImpl(JdbcTemplate jdbcTemplate,
                                    HallsRepo hallsRepo,
                                    MainScheduleRepo mainScheduleRepo,
                                    SeancesPlacesRepo seancesPlacesRepo,
                                    SeancesFullInfoTtlCache fullInfoTtlCache) {
        this.jdbcTemplate = jdbcTemplate;
        this.hallsRepo = hallsRepo;
        this.mainScheduleRepo = mainScheduleRepo;
        this.seancesPlacesRepo = seancesPlacesRepo;
        this.fullInfoTtlCache = fullInfoTtlCache;
    }

    public void changeCacheLifeTime(long cacheLifeTime) {
        fullInfoTtlCache.setCacheLifeTime(cacheLifeTime);
    }

    public AllSeancesView getSeanceById(int seanceId) {
        String sqlQuery = String.format("select * from %s where seance_id = '%s'", MAIN_SCHEDULE_VIEW_FULL, seanceId);
        return (AllSeancesView) jdbcTemplate.query(sqlQuery, new AllSeancesViewRowMapper()).get(0);
    }

    public List<AllSeancesView> getAllSeancesForDate(LocalDate date) {
        String sqlQuery = String.format("select * from %s where seance_date = '%s'",
                MAIN_SCHEDULE_VIEW_FULL, date.format(dateTimeFormatter));
        return jdbcTemplate.query(sqlQuery, new AllSeancesViewRowMapper());
    }

    public List<AllSeancesView> getAllSeances() {
        String sqlQuery = String.format("select * from %s;", MAIN_SCHEDULE_VIEW_FULL);
        return jdbcTemplate.query(sqlQuery, new AllSeancesViewRowMapper());
    }

    public Map<Integer, String> getAllMoviesByDate(LocalDate date) {
        String sqlQuery = String.format("select distinct(movie_id), movie_name from %s where seance_date = '%s';",
                MAIN_SCHEDULE_VIEW_FULL, date);

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery);
        Map<Integer, String> result = new HashMap<>();
        while (sqlRowSet.next()) {
            int movieId = sqlRowSet.getInt("movie_id");
            String movieName = sqlRowSet.getString("movie_name");
            result.put(movieId, movieName);
        }
        return result;
    }

    public Map<String, List<AllSeancesView>> getAllSeancesByMovieAndDateGroupByMoviePark(int movieId, LocalDate date) {
        String sqlQuery = String.format("select * from %s where movie_id = %s and seance_date = '%s';",
                MAIN_SCHEDULE_VIEW_FULL, movieId, date);
        List<AllSeancesView> resultFromDb = jdbcTemplate.query(sqlQuery, new AllSeancesViewRowMapper());
        Map<String, List<AllSeancesView>> result = new HashMap<>();
        resultFromDb.forEach(currSeance -> {
            String movieParkName = currSeance.getMovieParkName();
            if (!result.containsKey(movieParkName)) {
                result.put(movieParkName, new ArrayList<>());
            }
            result.get(movieParkName).add(currSeance);
        });
        return result;
    }

    public CommonResponse createNewSeance(CreateSeanceInput inputJson) {
        CommonResponse response = checkCreateSeanceInput(inputJson);
        if (!response.equals(VALID_DATA)) {
            return response;
        }

        int newSeanceId = mainScheduleRepo.findMaxId().orElse(0) + 1;
        MainScheduleEntity newSeanceEntity = createMainScheduleEntity(newSeanceId, inputJson);
        mainScheduleRepo.save(newSeanceEntity);
        return SEANCE_ADDED;
    }

    /**
     * Update seances for next days.
     */
    @Transactional
    public void updateScheduleTable(int days) {
        try {
            jdbcTemplate.execute(String.format("select %s.create_new_seances(%s);", SCHEMA_NAME, days));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    /**
     * Get info about all places in hall.
     */
    public List<HallsEntity> getHallPlacesInfo(int hallId) {
        return hallsRepo.findAllByHallId(hallId).orElse(null);
    }

    /**
     * Get info about all places for current seance.
     */
    public List<SeancePlacesEntity> getSeancePlacesInfo(int seanceId) {
        log.info("Getting full info for seance id = " + seanceId);
        try {
            if (fullInfoTtlCache.checkCacheContainsElement(seanceId)) {
                return ((CacheValue) fullInfoTtlCache.getElementFromCache(seanceId)).getSeanceFullInfo();
            } else {
                List<SeancePlacesEntity> seanceFullInfo = seancesPlacesRepo.findAllBySeanceId(seanceId);
                fullInfoTtlCache.addSeanceInfoToCache(seanceId, seanceFullInfo);
                return seanceFullInfo;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    /**
     * Block/unblock the place in hall for current seance.
     */
    @Transactional
    public void blockOrUnblockPlaceOnSeance(BlockUnblockPlaceInput inputJson) {
        log.info("Updating places {} in seance {}. Set value: {}",
                inputJson.getPlaceIdList(), inputJson.getSeanceId(), inputJson.getBlocked());
        try {
            int seanceId = inputJson.getSeanceId();
            seancesPlacesRepo.blockOrUnblockThePlace(seanceId, inputJson.getPlaceIdList(), inputJson.getBlocked());
            fullInfoTtlCache.removeElementFromCache(seanceId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}
