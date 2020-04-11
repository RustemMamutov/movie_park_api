package ru.api.moviepark.service.cache;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import static java.util.concurrent.TimeUnit.HOURS;

@Slf4j
public class MoviesInfoTtlCache {

    private static final Map<LocalDate, Map<Integer, String>> moviesSortByDateTtlCache = new ConcurrentHashMap<>();

    public static void initMoviesInfoCache() {
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
                moviesSortByDateTtlCache.clear();
            }
        }, 1, 24, HOURS);
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
