package ru.api.moviepark.data.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.api.moviepark.data.entities.MainScheduleEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MainScheduleRepo extends CrudRepository<MainScheduleEntity, Integer> {

    @Query(value = "SELECT CASE WHEN count(e.seanceId)> 0 then true else false end from MainScheduleEntity e where e.seanceId = :id")
    Optional<Boolean> checkSeanceIdExists(@Param("id") int seanceId);

    List<MainScheduleEntity> findSeancesEntityBySeanceDateAndHallId(LocalDate date, int hallId);

    List<MainScheduleEntity> findAllBySeanceDate(LocalDate date);

    @Query(value = "select e from MainScheduleEntity e " +
            "where e.seanceDate in (select e.seanceDate from MainScheduleEntity e where e.seanceId = :id)")
    List<MainScheduleEntity> findAllSeancesInTheSameDate(@Param("id") int seanceId);
}
