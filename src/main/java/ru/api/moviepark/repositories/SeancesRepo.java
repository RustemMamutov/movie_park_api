package ru.api.moviepark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.api.moviepark.entities_valueobjects.SeancesEntity;

import java.time.LocalDate;
import java.util.List;

public interface SeancesRepo extends JpaRepository<SeancesEntity, Integer> {
    public List<SeancesEntity> findSeancesEntityByDateAndHallId(LocalDate date, int hallId);
}
