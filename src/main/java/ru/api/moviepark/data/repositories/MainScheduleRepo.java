package ru.api.moviepark.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.api.moviepark.data.entities.MainScheduleEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MainScheduleRepo extends JpaRepository<MainScheduleEntity, Integer> {

    @Query(value = "SELECT max(id) from MainScheduleEntity ")
    Optional<Integer> findMaxId();


    List<MainScheduleEntity> findSeancesEntityBySeanceDateAndHallId(LocalDate date, int hallId);
}
