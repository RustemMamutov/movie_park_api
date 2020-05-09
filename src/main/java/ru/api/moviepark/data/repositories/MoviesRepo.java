package ru.api.moviepark.data.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.api.moviepark.data.entities.MoviesEntity;

public interface MoviesRepo extends CrudRepository<MoviesEntity, Integer> {
}
