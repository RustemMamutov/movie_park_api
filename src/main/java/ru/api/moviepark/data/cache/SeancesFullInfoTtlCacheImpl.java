package ru.api.moviepark.data.cache;

import lombok.Getter;
import ru.api.moviepark.data.entities.SeancePlacesEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static ru.api.moviepark.config.Constants.MAX_CACHE_LIFE_TIME;
import static ru.api.moviepark.config.Constants.MIN_CACHE_LIFE_TIME;

public class SeancesFullInfoTtlCacheImpl implements SeancesFullInfoTtlCache {

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
            if (!other.canEqual(this)) return false;
            return this.seanceId == other.seanceId;
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

    @Getter
    public static class CacheValue {
        private final long cacheTime;
        private final List<SeancePlacesEntity> seanceFullInfo;

        public static CacheValue of(long cacheTime, List<SeancePlacesEntity> seanceFullInfo) {
            return new CacheValue(cacheTime, seanceFullInfo);
        }

        private CacheValue(long cacheTime, List<SeancePlacesEntity> seanceFullInfo) {
            this.cacheTime = cacheTime;
            this.seanceFullInfo = seanceFullInfo;
        }
    }

    private long cacheLifeTime = 5000;
    private final Map<CacheKey, CacheValue> ttlCache = new TreeMap<>();

    public CacheValue getElementFromCache(int seanceId) {
        return ttlCache.get(CacheKey.of(seanceId));
    }

    public void removeElementFromCache(int seanceId) {
        ttlCache.remove(CacheKey.of(seanceId));
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

    public boolean checkElementAndRemoveItIfExpired(int seanceId, long currentTime) {
        CacheKey key = CacheKey.of(seanceId);
        CacheValue value = ttlCache.get(CacheKey.of(seanceId));
        if (value == null) {
            return false;
        }

        long delta = currentTime - value.getCacheTime();
        if (delta > cacheLifeTime) {
            ttlCache.remove(key);
            return false;
        }
        return true;
    }

    public void clearSomeFirstElementsInCache(long currentTime) {
        int countOfElements = ttlCache.size() > 5 ? 5 : ttlCache.size();
        List<CacheKey> keyList = new ArrayList(ttlCache.keySet()).subList(0, countOfElements);

        for (CacheKey key : keyList) {
            long delta = currentTime - key.getCacheTime();
            if (delta > cacheLifeTime) {
                ttlCache.remove(key);
            }
        }
    }

    public void addSeanceInfoToCache(int seanceId, List<SeancePlacesEntity> seanceFullInfo) {
        long currentTime = System.currentTimeMillis();
        ttlCache.put(CacheKey.of(seanceId, currentTime), CacheValue.of(currentTime, seanceFullInfo));
    }
}
