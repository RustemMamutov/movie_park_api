package ru.api.moviepark.data.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.api.moviepark.data.entities.HallsEntity;
import ru.api.moviepark.data.entities.HallsEntityId;

import java.util.List;
import java.util.Optional;

public interface HallsRepo extends CrudRepository<HallsEntity, HallsEntityId> {

    @Query(value = "SELECT CASE WHEN count(e.hallId)> 0 then true else false end from HallsEntity e where e.hallId = :id")
    Optional<Boolean> checkHallIdExists(@Param("id") int hallId);

    Optional<List<HallsEntity>> findAllByHallId(int hallId);
}
