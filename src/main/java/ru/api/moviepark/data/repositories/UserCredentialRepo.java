package ru.api.moviepark.data.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.api.moviepark.data.entities.UserCredentialEntity;

public interface UserCredentialRepo extends CrudRepository<UserCredentialEntity, String> {
}
