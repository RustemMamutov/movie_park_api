package ru.api.moviepark.service.cache;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.api.moviepark.config.MovieParkEnvironment;
import ru.api.moviepark.data.valueobjects.MainScheduleViewEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import static java.util.concurrent.TimeUnit.SECONDS;

@Service
public class SeanceInfoTtlCache {

    private static Logger logger = LoggerFactory.getLogger(SeanceInfoTtlCache.class);

    @Getter
    public static class SeanceInfoCacheValue {
        private final long cacheTime;
        private final Map<Integer, MainScheduleViewEntity> seanceFullInfoMap;

        private static SeanceInfoCacheValue of(long cacheTime, Map<Integer, MainScheduleViewEntity> seanceFullInfo) {
            return new SeanceInfoCacheValue(cacheTime, seanceFullInfo);
        }

        private static SeanceInfoCacheValue of(long cacheTime, MainScheduleViewEntity seanceFullInfo) {
            Map<Integer, MainScheduleViewEntity> seancesMap = new HashMap<>();
            seancesMap.put(seanceFullInfo.getSeanceId(), seanceFullInfo);
            return new SeanceInfoCacheValue(cacheTime, seancesMap);
        }

        private SeanceInfoCacheValue(long cacheTime, Map<Integer, MainScheduleViewEntity> seanceFullInfo) {
            this.cacheTime = cacheTime;
            this.seanceFullInfoMap = seanceFullInfo;
        }

        private boolean isExpired(long currentTimeMillis, long cacheLifeTime) {
            return currentTimeMillis - cacheTime > cacheLifeTime * 1000;
        }
    }

    private static long cacheLifeTime = 3600;
    private static final Map<LocalDate, SeanceInfoCacheValue> seanceInfoListTtlCache = new ConcurrentHashMap<>();

    private static MovieParkEnvironment env;

    public static void initSeanceInfoCache(MovieParkEnvironment env) {
        SeanceInfoTtlCache.env = env;
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread th = new Thread(r);
                th.setDaemon(true);
                return th;
            }
        });

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                logger.debug("Start flushing seances cache. Size before flush: {}", seanceInfoListTtlCache.size());
                long current = System.currentTimeMillis();
                for (Map.Entry<LocalDate, SeanceInfoCacheValue> entry : seanceInfoListTtlCache.entrySet()) {
                    if (entry.getValue().isExpired(current, cacheLifeTime)) {
                        logger.debug("Remove elements by date: {}", entry.getKey());
                        seanceInfoListTtlCache.remove(entry.getKey());
                    }
                }
                logger.debug("Finish flushing seance cache. Size after flush: {}", seanceInfoListTtlCache.size());
            }
        }, 1, 1800, SECONDS);
    }

    public static MainScheduleViewEntity getSeanceByIdFromCache(int seanceId) {
        for (SeanceInfoCacheValue seanceInfoCacheValue : seanceInfoListTtlCache.values()) {
            if (seanceInfoCacheValue.getSeanceFullInfoMap().containsKey(seanceId)) {
                return seanceInfoCacheValue.getSeanceFullInfoMap().get(seanceId);
            }
        }
        return null;
    }

    public static Map<Integer, MainScheduleViewEntity> getSeancesMapByDateFromCache(LocalDate date) {
        return seanceInfoListTtlCache.get(date).getSeanceFullInfoMap();
    }

    public static List<MainScheduleViewEntity> getSeancesListByDateFromCache(LocalDate date) {
        return new ArrayList<>(getSeancesMapByDateFromCache(date).values());
    }

    public static boolean checkCacheContainsElementById(int seanceId) {
        for (SeanceInfoCacheValue seanceInfoCacheValue : seanceInfoListTtlCache.values()) {
            if (seanceInfoCacheValue.getSeanceFullInfoMap().containsKey(seanceId)) {
                return true;
            }
        }

        return false;
    }

    public static boolean checkCacheContainsElementByDate(LocalDate date) {
        return seanceInfoListTtlCache.get(date) != null;
    }

    public static void addSeanceInfoToCache(MainScheduleViewEntity seanceFullInfo) {
        if (!seanceInfoListTtlCache.containsKey(seanceFullInfo.getSeanceDate())) {
            seanceInfoListTtlCache.put(seanceFullInfo.getSeanceDate(),
                    SeanceInfoCacheValue.of(System.currentTimeMillis(), seanceFullInfo));
        } else {
            seanceInfoListTtlCache.get(seanceFullInfo.getSeanceDate()).getSeanceFullInfoMap().put(
                    seanceFullInfo.getSeanceId(), seanceFullInfo);
        }
    }

    public static void addSeanceInfoToCache(List<MainScheduleViewEntity> seanceFullInfoList) {
        seanceFullInfoList.forEach(SeanceInfoTtlCache::addSeanceInfoToCache);
    }

    public static void clearAllCache() {
        seanceInfoListTtlCache.clear();
    }
}
