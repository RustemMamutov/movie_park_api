package ru.api.moviepark.cache;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.api.moviepark.data.dto.MainScheduleDTO;
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
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class SeanceInfoTtlCache {

    private static final Map<LocalDate, SeanceInfoCacheValue> seanceInfoListTtlCache = new ConcurrentHashMap<>();
    private static long cacheLifeTime = 3600;

    public static void init() {
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

    public static MainScheduleDTO getSeanceById(int seanceId) {
        for (SeanceInfoCacheValue seanceInfoCacheValue : seanceInfoListTtlCache.values()) {
            if (seanceInfoCacheValue.getSeanceFullInfoMap().containsKey(seanceId)) {
                return seanceInfoCacheValue.getSeanceFullInfoMap().get(seanceId);
            }
        }
        return null;
    }

    public static Map<Integer, MainScheduleDTO> getSeancesMapByDate(LocalDate date) {
        return seanceInfoListTtlCache.get(date).getSeanceFullInfoMap();
    }

    public static List<MainScheduleDTO> getSeancesListByDate(LocalDate date) {
        return new ArrayList<>(getSeancesMapByDate(date).values());
    }

    public static boolean containsElementById(int seanceId) {
        for (SeanceInfoCacheValue seanceInfoCacheValue : seanceInfoListTtlCache.values()) {
            if (seanceInfoCacheValue.getSeanceFullInfoMap().containsKey(seanceId)) {
                return true;
            }
        }

        return false;
    }

    public static boolean containsElementByDate(LocalDate date) {
        return seanceInfoListTtlCache.get(date) != null;
    }

    public static void addSeanceInfo(MainScheduleDTO seanceFullInfo) {
        seanceInfoListTtlCache.putIfAbsent(seanceFullInfo.getSeanceDate(),
                SeanceInfoCacheValue.init(System.currentTimeMillis()));

        seanceInfoListTtlCache.get(seanceFullInfo.getSeanceDate())
                .getSeanceFullInfoMap()
                .put(seanceFullInfo.getSeanceId(), seanceFullInfo);
    }

    public static void addSeanceInfo(List<MainScheduleDTO> dtoList) {
        dtoList.forEach(SeanceInfoTtlCache::addSeanceInfo);
    }

    public static void convertToDtoAndAdd(MainScheduleEntity entity) {
        addSeanceInfo(entity.convertToDto());
    }

    public static void convertToDtoAndAdd(List<MainScheduleEntity> entityList) {
        entityList.forEach(SeanceInfoTtlCache::convertToDtoAndAdd);
    }

    public static void clearAllCache() {
        seanceInfoListTtlCache.clear();
    }

    public static void clearCacheByDate(LocalDate date) {
        seanceInfoListTtlCache.remove(date);
    }

    private static List<LocalDate> findDateListBySeanceId(int seanceId) {
        return seanceInfoListTtlCache.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getSeanceFullInfoMap().containsKey(seanceId))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static synchronized void clearCacheBySeanceId(int seanceId) {
        List<LocalDate> result = findDateListBySeanceId(seanceId);
        if (!result.isEmpty()) {
            result.forEach(seanceInfoListTtlCache::remove);
        }
    }

    @Getter
    public static class SeanceInfoCacheValue {
        private final long cacheTime;
        private final Map<Integer, MainScheduleDTO> seanceFullInfoMap;

        private SeanceInfoCacheValue(long cacheTime, Map<Integer, MainScheduleDTO> seanceFullInfo) {
            this.cacheTime = cacheTime;
            this.seanceFullInfoMap = seanceFullInfo;
        }

        private static SeanceInfoCacheValue init(long cacheTime) {
            return new SeanceInfoCacheValue(cacheTime, new HashMap<>());
        }

        private boolean isExpired(long currentTimeMillis, long cacheLifeTime) {
            return currentTimeMillis - cacheTime > cacheLifeTime * 1000;
        }
    }
}
