package ru.api.moviepark.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MovieParkEnvironment {
    @Value("${max.cache.life.time}")
    private long maxCacheLifeTime;

    @Value("${min.cache.life.time}")
    private long minCacheLifeTime ;

    @Value("${seance.info.cache.flush.timeout}")
    private int seanceInfoCacheFlushTimeout;

    @Value("${rps.map.flush.timeout}")
    private int rpsMapFlushTimeout;

    @Value("${rps.life.time}")
    private int rpsLifeTime;

    public long getMaxCacheLifeTime() {
        return maxCacheLifeTime;
    }

    public long getMinCacheLifeTime() {
        return minCacheLifeTime;
    }

    public int getSeanceInfoCacheFlushTimeout() {
        return seanceInfoCacheFlushTimeout;
    }

    public int getRpsMapFlushTimeout() {
        return rpsMapFlushTimeout;
    }

    public int getRpsLifeTime() {
        return rpsLifeTime;
    }
}