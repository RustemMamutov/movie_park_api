package ru.api.moviepark.data.cache;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.api.moviepark.data.entities.SeancePlacesEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import static java.util.concurrent.TimeUnit.SECONDS;
import static ru.api.moviepark.config.Constants.MAX_CACHE_LIFE_TIME;
import static ru.api.moviepark.config.Constants.MIN_CACHE_LIFE_TIME;
import static ru.api.moviepark.config.Constants.SEANCE_INFO_CACHE_FLUSH_TIMEOUT;

public class SeancesFullInfoTtlCacheImpl implements SeancesFullInfoTtlCache {

    private Logger logger = LoggerFactory.getLogger(SeancesFullInfoTtlCacheImpl.class);

    @Getter
    public static class CacheValue {
        private final long cacheTime;
        private final List<SeancePlacesEntity> seanceFullInfo;

        private static CacheValue of(long cacheTime, List<SeancePlacesEntity> seanceFullInfo) {
            return new CacheValue(cacheTime, seanceFullInfo);
        }

        private CacheValue(long cacheTime, List<SeancePlacesEntity> seanceFullInfo) {
            this.cacheTime = cacheTime;
            this.seanceFullInfo = seanceFullInfo;
        }

        private boolean isExpired(long currentTimeMillis, long cacheLifeTime) {
            return currentTimeMillis - cacheTime > cacheLifeTime;
        }
    }

    private long cacheLifeTime = 5000;
    private final Map<Integer, CacheValue> ttlCache = new ConcurrentHashMap<>();

    public SeancesFullInfoTtlCacheImpl() {
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
                logger.debug("Start flushing cache. Cache size before flush: {}", ttlCache.size());
                long current = System.currentTimeMillis();
                for (Map.Entry<Integer, CacheValue> entry : ttlCache.entrySet()) {
                    if (entry.getValue().isExpired(current, cacheLifeTime)) {
                        logger.debug("Remove element by seanceId: {}", entry.getKey());
                        ttlCache.remove(entry.getKey());
                    }
                }
                logger.debug("Finish flushing cache. Cache size after flush: {}", ttlCache.size());
            }
        }, 1, SEANCE_INFO_CACHE_FLUSH_TIMEOUT, SECONDS);
    }

    public CacheValue getElementFromCache(int seanceId) {
        return ttlCache.get(seanceId);
    }

    public void removeElementFromCache(int seanceId) {
        ttlCache.remove(seanceId);
    }

    public void setCacheLifeTime(long cacheLifeTime) {
        if (cacheLifeTime > MAX_CACHE_LIFE_TIME) {
            this.cacheLifeTime = MAX_CACHE_LIFE_TIME;
        } else if (cacheLifeTime < MIN_CACHE_LIFE_TIME) {
            this.cacheLifeTime = MIN_CACHE_LIFE_TIME;
        } else {
            this.cacheLifeTime = cacheLifeTime;
        }
    }

    public boolean checkCacheContainsElement(int seanceId) {
        return ttlCache.containsKey(seanceId);
    }

    public void addSeanceInfoToCache(int seanceId, List<SeancePlacesEntity> seanceFullInfo) {
        long currentTime = System.currentTimeMillis();
        ttlCache.put(seanceId, CacheValue.of(currentTime, seanceFullInfo));
    }
}
