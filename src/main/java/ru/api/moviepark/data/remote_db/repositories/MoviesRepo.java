package ru.api.moviepark.data.remote_db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.api.moviepark.data.remote_db.entities.MoviesEntity;

import java.util.Optional;

public interface MoviesRepo extends JpaRepository<MoviesEntity, Integer> {

    @Query(value = "SELECT CASE WHEN count(id)> 0 then true else false end from MoviesEntity where id = :id")
    Optional<Boolean> checkIdExists(@Param("id") int income_id);
}
