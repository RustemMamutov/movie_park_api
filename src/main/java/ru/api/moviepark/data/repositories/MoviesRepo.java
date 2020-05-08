package ru.api.moviepark.data.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.api.moviepark.data.entities.MoviesEntity;

import java.util.Optional;

public interface MoviesRepo extends CrudRepository<MoviesEntity, Integer> {

    @Query(value = "SELECT CASE WHEN count(id)> 0 then true else false end from MoviesEntity where id = :id")
    Optional<Boolean> checkIdExists(@Param("id") int income_id);
}
