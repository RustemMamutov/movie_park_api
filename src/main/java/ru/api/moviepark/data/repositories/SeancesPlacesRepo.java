package ru.api.moviepark.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.api.moviepark.data.entities.SeancePlacesEntity;
import ru.api.moviepark.data.entities.SeancePlacesId;

import java.util.List;

public interface SeancesPlacesRepo extends JpaRepository<SeancePlacesEntity, SeancePlacesId> {

    List<SeancePlacesEntity> findAllBySeanceId(int id);

    @Modifying
    @Query("update SeancePlacesEntity entity set entity.blocked = :blocked where entity.seanceId = :seanceId " +
            "and entity.placeId IN :placeIdList")
    void blockOrUnblockThePlace(@Param("seanceId") int seanceId,
                                @Param("placeIdList") List<Integer> placeIdList,
                                @Param("blocked") boolean blocked);

}
