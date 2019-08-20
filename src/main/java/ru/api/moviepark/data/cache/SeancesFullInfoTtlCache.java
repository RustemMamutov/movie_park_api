package ru.api.moviepark.data.cache;

import ru.api.moviepark.data.entities.SeancePlacesEntity;

import java.util.List;

public interface SeancesFullInfoTtlCache {

    Object getElementFromCache(int seanceId);

    void removeElementFromCache(int seanceId);

    void setCacheLifeTime(long cacheLifeTime);

    boolean checkElementAndRemoveItIfExpired(int seanceId, long currentTime);

    void clearSomeFirstElementsInCache(long currentTime);

    void addSeanceInfoToCache(int seanceId, List<SeancePlacesEntity> seanceFullInfo);
}
