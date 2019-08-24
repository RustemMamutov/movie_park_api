package ru.api.moviepark.data.dbclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.api.moviepark.controller.CommonResponse;
import ru.api.moviepark.data.cache.SeancesFullInfoTtlCache;
import ru.api.moviepark.data.cache.SeancesFullInfoTtlCacheImpl;
import ru.api.moviepark.data.entities.MainScheduleEntity;
import ru.api.moviepark.data.entities.SeancePlacesEntity;
import ru.api.moviepark.data.mappers.AllSeancesViewRowMapper;
import ru.api.moviepark.data.repositories.MainScheduleRepo;
import ru.api.moviepark.data.repositories.SeancesPlacesRepo;
import ru.api.moviepark.data.valueobjects.AllSeancesView;
import ru.api.moviepark.data.valueobjects.BlockPlaceInput;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;

import java.time.LocalDate;
import java.util.List;

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
    private final MainScheduleRepo mainScheduleRepo;
    private final SeancesPlacesRepo seancesPlacesRepo;

    private SeancesFullInfoTtlCache fullInfoTtlCache = new SeancesFullInfoTtlCacheImpl();

    public RemoteDatabaseClientImpl(JdbcTemplate jdbcTemplate,
                                    MainScheduleRepo mainScheduleRepo,
                                    SeancesPlacesRepo seancesPlacesRepo) {
        this.jdbcTemplate = jdbcTemplate;
        this.mainScheduleRepo = mainScheduleRepo;
        this.seancesPlacesRepo = seancesPlacesRepo;
    }

    public void changeCacheLifeTime(long cacheLifeTime) {
        fullInfoTtlCache.setCacheLifeTime(cacheLifeTime);
    }

    public List<AllSeancesView> getAllSeances() {
        String sqlQuery = String.format("select * from %s;", MAIN_SCHEDULE_VIEW_FULL);
        return jdbcTemplate.query(sqlQuery, new AllSeancesViewRowMapper());
    }

    public List<AllSeancesView> getAllSeancesForDate(LocalDate date) {
        String sqlQuery = String.format("select * from %s where seance_date = '%s'",
                MAIN_SCHEDULE_VIEW_FULL, date.format(dateTimeFormatter));
        return jdbcTemplate.query(sqlQuery, new AllSeancesViewRowMapper());
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
     * Get info about all places in hall for current seance.
     */
    public List<SeancePlacesEntity> getSeanceFullInfo(int seanceId) {
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
    public void blockOrUnblockPlaceOnSeance(BlockPlaceInput inputJson) {
        log.info("Blocking place in seance");
        try {
            int seanceId = inputJson.getSeanceId();
            seancesPlacesRepo.blockOrUnblockThePlace(seanceId, inputJson.getRow(),
                    inputJson.getPlace(), inputJson.getBlocked());
            fullInfoTtlCache.removeElementFromCache(seanceId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}
