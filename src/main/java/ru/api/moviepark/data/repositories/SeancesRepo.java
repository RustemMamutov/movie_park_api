package ru.api.moviepark.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.api.moviepark.data.entities.SeancesEntity;

public interface SeancesRepo extends JpaRepository<SeancesEntity, Integer> {
}
