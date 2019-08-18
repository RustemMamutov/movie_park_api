package ru.api.moviepark.data;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.api.moviepark.config.CONSTANTS;
import ru.api.moviepark.controller.CommonResponse;
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
import java.util.Map;
import java.util.TreeMap;

import static ru.api.moviepark.controller.CommonResponse.SEANCE_ADDED;
import static ru.api.moviepark.controller.CommonResponse.VALID_DATA;
import static ru.api.moviepark.util.CheckInputUtil.checkCreateSeanceInput;

import static ru.api.moviepark.config.CONSTANTS.MAX_CACHE_LIFE_TIME;
import static ru.api.moviepark.config.CONSTANTS.dateTimeFormatter;

@Service
@Slf4j
public class RemoteDatabaseClient {

    private final JdbcTemplate jdbcTemplate;
    private final MainScheduleRepo mainScheduleRepo;
    private final SeancesPlacesRepo seancesPlacesRepo;

    private final Map<CacheKey, List<SeancePlacesEntity>> ttlCache = new TreeMap<>();
    private long cacheLifeTime = 5000;

    @Getter
    static class CacheKey implements Comparable {

        private final int seanceId;
        private final long cacheTime;

        public static CacheKey of(int seanceId, long cacheTime) {
            return new CacheKey(seanceId, cacheTime);
        }

        public static CacheKey of(int seanceId) {
            return new CacheKey(seanceId, 0);
        }

        private CacheKey(int seanceId, long cacheTime) {
            this.seanceId = seanceId;
            this.cacheTime = cacheTime;
        }

        public boolean equals(final Object o) {
            if (o == this) return true;
            if (!(o instanceof CacheKey)) return false;
            final CacheKey other = (CacheKey) o;
            if (!other.canEqual((Object) this)) return false;
            if (this.seanceId != other.seanceId) return false;
            return true;
        }

        protected boolean canEqual(final Object other) {
            return other instanceof CacheKey;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            result = result * PRIME + this.seanceId;
            return result;
        }

        @Override
        public int compareTo(Object o) {
            int seanceId = ((CacheKey) o).getSeanceId();
            if (this.seanceId != seanceId) {
                return Long.compare(((CacheKey) o).getCacheTime(), cacheTime);
            }
            return 0;
        }
    }

    public RemoteDatabaseClient(JdbcTemplate jdbcTemplate,
                                MainScheduleRepo mainScheduleRepo,
                                SeancesPlacesRepo seancesPlacesRepo) {
        this.jdbcTemplate = jdbcTemplate;
        this.mainScheduleRepo = mainScheduleRepo;
        this.seancesPlacesRepo = seancesPlacesRepo;
    }

    private void clearCache() {
        long currentTime = System.currentTimeMillis();
        for (CacheKey key : ttlCache.keySet()) {
            long delta = currentTime - key.getCacheTime();
            if (delta > cacheLifeTime) {
                ttlCache.remove(key);
            }
        }
    }

    private void addSeanceInfoToCache(int seanceId, List<SeancePlacesEntity> seanceFullInfo) {
        long currentTime = System.currentTimeMillis();
        ttlCache.put(CacheKey.of(seanceId, currentTime), seanceFullInfo);
    }

    private void addSeanceInfoToCache(AllSeancesView seanceInfo) {
        int seanceId = seanceInfo.getSeanceId();
        List<SeancePlacesEntity> seanceFullInfo = seancesPlacesRepo.findAllBySeanceId(seanceId);
        addSeanceInfoToCache(seanceId, seanceFullInfo);
    }

    public void changeCacheLifeTime(int cacheLifeTime) {
        if (cacheLifeTime > MAX_CACHE_LIFE_TIME) {
            this.cacheLifeTime = MAX_CACHE_LIFE_TIME;
        } else {
            this.cacheLifeTime = cacheLifeTime;
        }
    }

    public List<AllSeancesView> getAllSeances() {
        String sqlQuery = String.format("select * from %s;", CONSTANTS.MAIN_SCHEDULE_VIEW_FULL);
        List<AllSeancesView> allSeancesList = jdbcTemplate.query(sqlQuery, new AllSeancesViewRowMapper());
        allSeancesList.forEach(seance -> addSeanceInfoToCache(seance));
        return allSeancesList;
    }

    public List<AllSeancesView> getAllSeancesForDate(LocalDate date) {
        String sqlQuery = String.format("select * from %s where seance_date = '%s'",
                CONSTANTS.MAIN_SCHEDULE_VIEW_FULL, date.format(dateTimeFormatter));
        List<AllSeancesView> dateSeancesList = jdbcTemplate.query(sqlQuery, new AllSeancesViewRowMapper());
        dateSeancesList.forEach(seance -> addSeanceInfoToCache(seance));
        return dateSeancesList;
    }

    public CommonResponse createNewSeance(CreateSeanceInput inputJson) {
        CommonResponse response = checkCreateSeanceInput(inputJson);
        if (!response.equals(VALID_DATA)) {
            return response;
        }

        int newSeanceId = mainScheduleRepo.findMaxId().orElse(0) + 1;
        MainScheduleEntity newSeanceEntity = MainScheduleEntity.createMainScheduleEntity(newSeanceId, inputJson);
        mainScheduleRepo.save(newSeanceEntity);
        return SEANCE_ADDED;
    }

    /**
     * Update seances for next days.
     */
    @Transactional
    public void updateScheduleTable(int days) {
        try {
            jdbcTemplate.execute(String.format("select %s.create_new_seances(%s);", CONSTANTS.SCHEMA_NAME, days));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Get info about all places in hall for current seance.
     */
    public List<SeancePlacesEntity> getSeanceFullInfo(int seanceId) {
        log.info("Getting full info for seance id = " + seanceId);
        try {
            clearCache();
            if (ttlCache.containsKey(CacheKey.of(seanceId))) {
                return ttlCache.get(CacheKey.of(seanceId));
            } else {
                List<SeancePlacesEntity> seanceFullInfo = seancesPlacesRepo.findAllBySeanceId(seanceId);
                addSeanceInfoToCache(seanceId, seanceFullInfo);
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
            ttlCache.remove(CacheKey.of(seanceId));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}