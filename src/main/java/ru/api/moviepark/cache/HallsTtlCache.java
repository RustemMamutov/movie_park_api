package ru.api.moviepark.cache;

import lombok.extern.slf4j.Slf4j;
import ru.api.moviepark.data.entities.HallsEntity;
import ru.api.moviepark.data.repositories.HallsRepo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class HallsTtlCache {
    private static final Map<Integer, List<HallsEntity>> hallsTtlCache = new ConcurrentHashMap<>();

    private static LocalDateTime lastUpdateTime;
    private static final long ttlSeconds = 43200;
    private static HallsRepo hallsRepo;

    public static void setHallsRepo(HallsRepo hallsRepo) {
        HallsTtlCache.hallsRepo = hallsRepo;
    }

    public static void init() {
        updateCache(LocalDateTime.now());
    }

    public static List<HallsEntity> getElementsById(int id) {
        validateCache();
        return hallsTtlCache.get(id);
    }

    public static boolean containsElementById(int id) {
        validateCache();
        return hallsTtlCache.containsKey(id);
    }

    private static void validateCache() {
        LocalDateTime now = LocalDateTime.now();
        if (Duration.between(lastUpdateTime, now).getSeconds() > ttlSeconds) {
            updateCache(now);
        }
    }

    public static void updateCache(LocalDateTime updateTime) {
        hallsTtlCache.clear();
        log.debug("Update halls cache");
        List<HallsEntity> hallsEntityList = (List<HallsEntity>) hallsRepo.findAll();
        hallsEntityList.forEach(entity -> {
            int hallId = entity.getHallId();
            hallsTtlCache.putIfAbsent(hallId, new ArrayList<>());
            hallsTtlCache.get(hallId).add(entity);
        });
        lastUpdateTime = updateTime;
        log.debug("Halls cache updated");
    }
}
