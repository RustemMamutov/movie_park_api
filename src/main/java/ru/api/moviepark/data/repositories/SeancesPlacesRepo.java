package ru.api.moviepark.data.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.api.moviepark.data.entities.SeancePlacesEntity;
import ru.api.moviepark.data.entities.SeancePlacesId;

import java.util.List;

public interface SeancesPlacesRepo extends CrudRepository<SeancePlacesEntity, SeancePlacesId> {

    List<SeancePlacesEntity> findAllBySeanceId(int id);

    @Modifying
    @Query("update SeancePlacesEntity entity set entity.blocked = :blocked where entity.seanceId = :id " +
            "and entity.placeId IN :placeIdList")
    void blockOrUnblockThePlace(int id, List<Integer> placeIdList, boolean blocked);

    void deleteAllBySeanceId(int id);
}
