package ru.api.moviepark.service.cache;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.api.moviepark.data.entities.MainScheduleEntity;

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

@Slf4j
public class SeanceInfoTtlCache {

    @Getter
    public static class SeanceInfoCacheValue {
        private final long cacheTime;
        private final Map<Integer, MainScheduleEntity> seanceFullInfoMap;

        private static SeanceInfoCacheValue of(long cacheTime, Map<Integer, MainScheduleEntity> seanceFullInfo) {
            return new SeanceInfoCacheValue(cacheTime, seanceFullInfo);
        }

        private static SeanceInfoCacheValue of(long cacheTime, MainScheduleEntity seanceFullInfo) {
            Map<Integer, MainScheduleEntity> seancesMap = new HashMap<>();
            seancesMap.put(seanceFullInfo.getSeanceId(), seanceFullInfo);
            return new SeanceInfoCacheValue(cacheTime, seancesMap);
        }

        private SeanceInfoCacheValue(long cacheTime, Map<Integer, MainScheduleEntity> seanceFullInfo) {
            this.cacheTime = cacheTime;
            this.seanceFullInfoMap = seanceFullInfo;
        }

        private boolean isExpired(long currentTimeMillis, long cacheLifeTime) {
            return currentTimeMillis - cacheTime > cacheLifeTime * 1000;
        }
    }

    private static long cacheLifeTime = 3600;
    private static final Map<LocalDate, SeanceInfoCacheValue> seanceInfoListTtlCache = new ConcurrentHashMap<>();

    public static void initSeanceInfoCache() {
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
                log.debug("Start flushing seances cache. Size before flush: {}", seanceInfoListTtlCache.size());
                long current = System.currentTimeMillis();
                for (Map.Entry<LocalDate, SeanceInfoCacheValue> entry : seanceInfoListTtlCache.entrySet()) {
                    if (entry.getValue().isExpired(current, cacheLifeTime)) {
                        log.debug("Remove elements by date: {}", entry.getKey());
                        seanceInfoListTtlCache.remove(entry.getKey());
                    }
                }
                log.debug("Finish flushing seance cache. Size after flush: {}", seanceInfoListTtlCache.size());
            }
        }, 1, 1800, SECONDS);
    }

    public static MainScheduleEntity getSeanceByIdFromCache(int seanceId) {
        for (SeanceInfoCacheValue seanceInfoCacheValue : seanceInfoListTtlCache.values()) {
            if (seanceInfoCacheValue.getSeanceFullInfoMap().containsKey(seanceId)) {
                return seanceInfoCacheValue.getSeanceFullInfoMap().get(seanceId);
            }
        }
        return null;
    }

    public static Map<Integer, MainScheduleEntity> getSeancesMapByDateFromCache(LocalDate date) {
        return seanceInfoListTtlCache.get(date).getSeanceFullInfoMap();
    }

    public static List<MainScheduleEntity> getSeancesListByDateFromCache(LocalDate date) {
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

    public static void addSeanceInfoToCache(MainScheduleEntity seanceFullInfo) {
        if (!seanceInfoListTtlCache.containsKey(seanceFullInfo.getSeanceDate())) {
            seanceInfoListTtlCache.put(seanceFullInfo.getSeanceDate(),
                    SeanceInfoCacheValue.of(System.currentTimeMillis(), seanceFullInfo));
        } else {
            seanceInfoListTtlCache.get(seanceFullInfo.getSeanceDate()).getSeanceFullInfoMap().put(
                    seanceFullInfo.getSeanceId(), seanceFullInfo);
        }
    }

    public static void addSeanceInfoToCache(List<MainScheduleEntity> seanceFullInfoList) {
        seanceFullInfoList.forEach(SeanceInfoTtlCache::addSeanceInfoToCache);
    }

    public static void clearAllCache() {
        seanceInfoListTtlCache.clear();
    }
}
