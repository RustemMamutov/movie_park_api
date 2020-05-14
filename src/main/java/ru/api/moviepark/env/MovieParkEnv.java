package ru.api.moviepark.env;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
@Getter
public class MovieParkEnv {
    @Value("${cache.seance_info.flush_timeout_max}")
    private long seanceInfoCacheFlushTimeoutMax;

    @Value("${cache.seance_info.flush_timeout_min}")
    private long seanceInfoCacheFlushTimeoutMin;

    @Value("${cache.seance_info.flush_timeout}")
    private int seanceInfoCacheFlushTimeout;

    @Value("${cache.rps.flush_timeout}")
    private int rpsMapFlushTimeout;

    @Value("${cache.rps.lifetime}")
    private int rpsLifeTime;
}