package ru.api.moviepark.data.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import static ru.api.moviepark.config.Constants.MOVIES_TABLE_NAME;
import static ru.api.moviepark.config.Constants.SCHEMA_NAME;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = MOVIES_TABLE_NAME, schema = SCHEMA_NAME)
public class MoviesEntity {
    @Id
    @Column(name = "movie_id")
    private Integer movieId;

    @Column(name = "movie_name")
    private String movieName;
}
