package ru.api.moviepark.service.cache;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.api.moviepark.data.entities.SeancePlacesEntity;
import ru.api.moviepark.env.MovieParkEnv;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class SeancePlacesTtlCache {

    private static final Map<Integer, SeancePlacesCacheValue> seancePlacesInfoTtlCache = new ConcurrentHashMap<>();
    private static long cacheLifeTime = 3;
    private static MovieParkEnv env;

    public static void setEnv(MovieParkEnv env) {
        SeancePlacesTtlCache.env = env;
    }

    public static void initSeancePlacesTtlCache() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread th = new Thread(r);
                th.setDaemon(true);
                return th;
            }
        });

        scheduler.scheduleAtFixedRate(() -> flushCache(System.currentTimeMillis()), 1,
                env.getSeanceInfoCacheFlushTimeout(), SECONDS);
    }

    private static void flushCache(long currentTime) {
        if (seancePlacesInfoTtlCache.isEmpty()) {
            log.debug("The cache is empty and it doesn't need to be flush");
            return;
        }
        log.debug("Start flushing seances places cache. Elements in cache by seanceId: {}",
                Arrays.toString(seancePlacesInfoTtlCache.keySet().toArray()));
        Set<Integer> keysToRemove = seancePlacesInfoTtlCache.entrySet().stream()
                .filter(entry -> entry.getValue().isExpired(currentTime, cacheLifeTime))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        log.debug("Remove elements by seanceId: {}", Arrays.toString(keysToRemove.toArray()));
        seancePlacesInfoTtlCache.keySet().removeAll(keysToRemove);
        log.debug("Finish flushing cache. Elements in cache by seanceId: {}",
                Arrays.toString(seancePlacesInfoTtlCache.keySet().toArray()));
    }

    public static List<SeancePlacesEntity> getSeancePlacesInfoByIdFromCache(int seanceId) {
        return seancePlacesInfoTtlCache.get(seanceId).getSeancePlacesFullInfo();
    }

    public static void removeElementFromCache(int seanceId) {
        seancePlacesInfoTtlCache.remove(seanceId);
    }

    public static void setCacheLifeTime(long cacheLifeTime) {
        if (cacheLifeTime > env.getSeanceInfoCacheFlushTimeoutMax()) {
            SeancePlacesTtlCache.cacheLifeTime = env.getSeanceInfoCacheFlushTimeoutMax();
        } else {
            SeancePlacesTtlCache.cacheLifeTime = Math.max(cacheLifeTime, env.getSeanceInfoCacheFlushTimeoutMin());
        }
    }

    public static boolean checkCacheContainsElement(int seanceId) {
        return seancePlacesInfoTtlCache.containsKey(seanceId);
    }

    public static void addSeancePlacesInfoToCache(int seanceId, List<SeancePlacesEntity> seancePlacesInfo) {
        long currentTime = System.currentTimeMillis();
        seancePlacesInfoTtlCache.put(seanceId, SeancePlacesCacheValue.of(currentTime, seancePlacesInfo));
    }

    @Getter
    public static class SeancePlacesCacheValue {
        private final long cacheTime;
        private final List<SeancePlacesEntity> seancePlacesFullInfo;

        private SeancePlacesCacheValue(long cacheTime, List<SeancePlacesEntity> seancePlacesFullInfo) {
            this.cacheTime = cacheTime;
            this.seancePlacesFullInfo = seancePlacesFullInfo;
        }

        private static SeancePlacesCacheValue of(long cacheTime, List<SeancePlacesEntity> seanceFullInfo) {
            return new SeancePlacesCacheValue(cacheTime, seanceFullInfo);
        }

        private boolean isExpired(long currentTimeMillis, long cacheLifeTime) {
            return currentTimeMillis - cacheTime > cacheLifeTime * 1000;
        }
    }
}
