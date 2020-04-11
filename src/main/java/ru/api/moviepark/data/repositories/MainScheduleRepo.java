package ru.api.moviepark.data.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.api.moviepark.data.entities.MainScheduleEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MainScheduleRepo extends CrudRepository<MainScheduleEntity, Integer> {

    @Query(value = "SELECT max(id) from MainScheduleEntity ")
    Optional<Integer> findMaxId();

    @Query(value = "SELECT seanceDate from MainScheduleEntity e where e.seanceId = :id")
    LocalDate findDateBySeanceId(@Param("id") int seanceId);

    List<MainScheduleEntity> findSeancesEntityBySeanceDateAndHallId(LocalDate date, int hallId);
}
