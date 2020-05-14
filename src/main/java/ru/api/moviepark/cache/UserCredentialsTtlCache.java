package ru.api.moviepark.cache;

import lombok.extern.slf4j.Slf4j;
import ru.api.moviepark.data.entities.UserCredentialEntity;
import ru.api.moviepark.data.repositories.UserCredentialRepo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class UserCredentialsTtlCache {
    private static final Map<String, UserCredentialEntity> userCredentialTtlCache = new ConcurrentHashMap<>();

    private static LocalDateTime lastUpdateTime;
    private static final long ttlSeconds = 60;
    private static UserCredentialRepo userCredentialRepo;

    public static void setUserCredentialRepo(UserCredentialRepo repo) {
        userCredentialRepo = repo;
    }

    public static void init() {
        updateCache(LocalDateTime.now());
    }

    public static UserCredentialEntity getElementByEmail(String email) {
        validateCache();
        return userCredentialTtlCache.get(email);
    }

    public static boolean containsElementByEmail(String email) {
        validateCache();
        return userCredentialTtlCache.containsKey(email);
    }

    private static void validateCache() {
        LocalDateTime now = LocalDateTime.now();
        if (Duration.between(lastUpdateTime, now).getSeconds() > ttlSeconds) {
            updateCache(now);
        }
    }

    public static void updateCache(LocalDateTime updateTime) {
        userCredentialTtlCache.clear();
        log.debug("Update user credentials cache");
        List<UserCredentialEntity> userCredentialEntityList =
                (List<UserCredentialEntity>) userCredentialRepo.findAll();
        userCredentialEntityList.forEach(entity -> userCredentialTtlCache.put(entity.getEmail(), entity));
        lastUpdateTime = updateTime;
        log.debug("User credentials cache updated");
    }
}
