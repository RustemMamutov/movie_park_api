package ru.api.moviepark.data.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.api.moviepark.config.CONSTANTS;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = CONSTANTS.MOVIES_TABLE_NAME, schema = CONSTANTS.SCHEMA_NAME)
public class MoviesEntity {
    @Id
    @Column(name = "movie_id")
    private Integer movieId;

    @Column(name = "movie_name")
    private String movieName;
}
