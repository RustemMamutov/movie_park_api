package ru.api.moviepark.service.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.api.moviepark.config.MovieParkEnvironment;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

@Service
public class MoviesInfoTtlCache {

    private static Logger logger = LoggerFactory.getLogger(MoviesInfoTtlCache.class);

    private static long cacheLifeTime = 3600;
    private static final Map<LocalDate, Map<Integer, String>> moviesSortByDateTtlCache = new ConcurrentHashMap<>();

    private static MovieParkEnvironment env;

    public static void initSeanceInfoCache(MovieParkEnvironment env) {
        MoviesInfoTtlCache.env = env;
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread th = new Thread(r);
                th.setDaemon(true);
                return th;
            }
        });
    }

    public static Map<Integer, String> getElementByDateFromCache(LocalDate date) {
        return moviesSortByDateTtlCache.get(date);
    }

    public static boolean containsElement(LocalDate date) {
        return moviesSortByDateTtlCache.containsKey(date);
    }

    public static void addElementToCache(LocalDate date, Map<Integer, String> movies) {
        moviesSortByDateTtlCache.put(date, movies);
    }
}
