package ru.api.moviepark.config;

import com.google.common.cache.CacheBuilder;
import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@AllArgsConstructor
public class CacheConfig {

    public final static String USER_CREDENTIAL_CACHE = "userCredentialCache";
    public final static String MOVIES_INFO_CACHE = "moviesInfoCache";
    public final static String HALLS_INFO_CACHE = "hallsInfoCache";
    public final static String SEANCE_INFO_CACHE_BY_DATE = "seanceInfoCacheByDate";
    public final static String SEANCE_INFO_CACHE_BY_ID = "seanceInfoCacheById";
    public final static String SEANCE_PLACES_CACHE = "seancePlacesCache";

    @Bean
    public Cache userCredentialByEmailCache() {
        return new GuavaCache(USER_CREDENTIAL_CACHE, CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .build());
    }

    @Bean
    public Cache moviesInfoByDateCache() {
        return new GuavaCache(MOVIES_INFO_CACHE, CacheBuilder.newBuilder()
                .expireAfterWrite(3600, TimeUnit.SECONDS)
                .build());
    }

    @Bean
    public Cache hallsInfoByHallIdCache() {
        return new GuavaCache(HALLS_INFO_CACHE, CacheBuilder.newBuilder()
                .expireAfterWrite(3600, TimeUnit.SECONDS)
                .build());
    }

    @Bean
    public Cache seanceInfoByDateCache() {
        return new GuavaCache(SEANCE_INFO_CACHE_BY_DATE, CacheBuilder.newBuilder()
                .expireAfterWrite(3600, TimeUnit.SECONDS)
                .build());
    }

    @Bean
    public Cache seanceInfoBySeanceIdCache() {
        return new GuavaCache(SEANCE_INFO_CACHE_BY_ID, CacheBuilder.newBuilder()
                .expireAfterWrite(3600, TimeUnit.SECONDS)
                .build());
    }

    @Bean
    public Cache seancePlacesBySeanceIdCache() {
        return new GuavaCache(SEANCE_PLACES_CACHE, CacheBuilder.newBuilder()
                .expireAfterWrite(600, TimeUnit.SECONDS)
                .build());
    }
}