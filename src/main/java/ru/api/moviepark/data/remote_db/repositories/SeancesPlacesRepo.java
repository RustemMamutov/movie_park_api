package ru.api.moviepark.data.remote_db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.api.moviepark.data.remote_db.entities.SeancePlacesEntity;
import ru.api.moviepark.data.remote_db.entities.SeancePlacesId;

import java.util.List;
import java.util.Optional;

public interface SeancesPlacesRepo extends JpaRepository<SeancePlacesEntity, SeancePlacesId> {


    SeancePlacesEntity findBySeanceId();
}
