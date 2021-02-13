package ru.api.moviepark.data.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import static ru.api.moviepark.env.Constants.MOVIES_TABLE_NAME;
import static ru.api.moviepark.env.Constants.SCHEMA_NAME;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = MOVIES_TABLE_NAME, schema = SCHEMA_NAME)
public class MoviesEntity {
    @Id
    @Column(name = "id")
    private Integer movieId;

    @Column(name = "name")
    private String movieName;

    @Column(name = "base64_images")
    private String base64Images;

    public MoviesEntity(int movieId) {
        this.movieId = movieId;
        this.movieName = "";
        this.base64Images = "";
    }
}
