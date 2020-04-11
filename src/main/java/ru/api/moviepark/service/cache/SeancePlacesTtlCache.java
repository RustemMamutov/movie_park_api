package ru.api.moviepark.service.cache;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.api.moviepark.config.MovieParkEnv;
import ru.api.moviepark.data.entities.SeancePlacesEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class SeancePlacesTtlCache {

    @Getter
    public static class SeancePlacesCacheValue {
        private final long cacheTime;
        private final List<SeancePlacesEntity> seancePlacesFullInfo;

        private static SeancePlacesCacheValue of(long cacheTime, List<SeancePlacesEntity> seanceFullInfo) {
            return new SeancePlacesCacheValue(cacheTime, seanceFullInfo);
        }

        private SeancePlacesCacheValue(long cacheTime, List<SeancePlacesEntity> seancePlacesFullInfo) {
            this.cacheTime = cacheTime;
            this.seancePlacesFullInfo = seancePlacesFullInfo;
        }

        private boolean isExpired(long currentTimeMillis, long cacheLifeTime) {
            return currentTimeMillis - cacheTime > cacheLifeTime * 1000;
        }
    }

    private static long cacheLifeTime = 3;
    private static final Map<Integer, SeancePlacesCacheValue> seancePlacesInfoTtlCache = new ConcurrentHashMap<>();

    private static MovieParkEnv env;

    public static void initSeancePlacesTtlCache(MovieParkEnv env) {
        SeancePlacesTtlCache.env = env;
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
                log.debug("Start flushing seances places cache. Cache size before flush: {}", seancePlacesInfoTtlCache.size());
                long current = System.currentTimeMillis();
                for (Map.Entry<Integer, SeancePlacesCacheValue> entry : seancePlacesInfoTtlCache.entrySet()) {
                    if (entry.getValue().isExpired(current, cacheLifeTime)) {
                        log.debug("Remove element by seanceId: {}", entry.getKey());
                        seancePlacesInfoTtlCache.remove(entry.getKey());
                    }
                }
                log.debug("Finish flushing cache. Cache size after flush: {}", seancePlacesInfoTtlCache.size());
            }
        }, 1, env.getSeanceInfoCacheFlushTimeout(), SECONDS);
    }

    public static List<SeancePlacesEntity> getSeancePlacesInfoByIdFromCache(int seanceId) {
        return seancePlacesInfoTtlCache.get(seanceId).getSeancePlacesFullInfo();
    }

    public static void removeElementFromCache(int seanceId) {
        seancePlacesInfoTtlCache.remove(seanceId);
    }

    public static void setCacheLifeTime(long cacheLifeTime) {
        if (cacheLifeTime > env.getMaxCacheLifeTime()) {
            SeancePlacesTtlCache.cacheLifeTime = env.getMaxCacheLifeTime();
        } else SeancePlacesTtlCache.cacheLifeTime = Math.max(cacheLifeTime, env.getMinCacheLifeTime());
    }

    public static boolean checkCacheContainsElement(int seanceId) {
        return seancePlacesInfoTtlCache.containsKey(seanceId);
    }

    public static void addSeancePlacesInfoToCache(int seanceId, List<SeancePlacesEntity> seancePlacesInfo) {
        long currentTime = System.currentTimeMillis();
        seancePlacesInfoTtlCache.put(seanceId, SeancePlacesCacheValue.of(currentTime, seancePlacesInfo));
    }
}
