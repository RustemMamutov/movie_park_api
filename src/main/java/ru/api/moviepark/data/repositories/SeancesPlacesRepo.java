package ru.api.moviepark.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.api.moviepark.data.entities.MainScheduleEntity;
import ru.api.moviepark.data.entities.SeancesPlaces2019Entity;
import ru.api.moviepark.data.entities.id.SeancesPlaces2019Id;

import java.time.LocalDate;
import java.util.List;

public interface SeancesPlacesRepo extends JpaRepository<SeancesPlaces2019Entity, SeancesPlaces2019Id> {
}
