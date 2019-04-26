package ru.api.moviepark.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.api.moviepark.data.entities.MainScheduleEntity;

import java.time.LocalDate;
import java.util.List;

public interface MainScheduleRepo extends JpaRepository<MainScheduleEntity, Integer> {
    List<MainScheduleEntity> findSeancesEntityBySeanceDateAndHallId(LocalDate date, int hallId);
}
