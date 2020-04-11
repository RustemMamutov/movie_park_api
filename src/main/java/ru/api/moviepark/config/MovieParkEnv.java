package ru.api.moviepark.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MovieParkEnv {
    @Value("${cache.tables.lifetime.max}")
    private long maxCacheLifeTime;

    @Value("${cache.tables.lifetime.min}")
    private long minCacheLifeTime;

    @Value("${cache.tables.timeouts.seance_info_flush}")
    private int seanceInfoCacheFlushTimeout;

    @Value("${cache.rps.timeouts.cron_timeout}")
    private int rpsMapFlushTimeout;

    @Value("${cache.rps.lifetime}")
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