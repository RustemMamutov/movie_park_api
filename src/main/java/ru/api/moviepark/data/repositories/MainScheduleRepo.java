package ru.api.moviepark.data.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.api.moviepark.data.entities.MainScheduleEntity;

import java.time.LocalDate;
import java.util.List;

public interface MainScheduleRepo extends CrudRepository<MainScheduleEntity, Integer> {

    @Query(value = "SELECT CASE WHEN count(e.seanceId)> 0 then true else false end " +
            "from MainScheduleEntity e where e.seanceId = :id")
    boolean checkSeanceIdExists(int id);

    List<MainScheduleEntity> findSeancesEntityBySeanceDateAndHallId(LocalDate date, int id);

    List<MainScheduleEntity> findAllBySeanceDate(LocalDate date);

    @Query(value = "select e from MainScheduleEntity e where e.seanceDate in " +
            "(select e.seanceDate from MainScheduleEntity e where e.seanceId = :id)")
    List<MainScheduleEntity> findAllSeancesInTheSameDate(int id);
}
