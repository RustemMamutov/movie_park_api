package ru.api.moviepark.data.remote_db.entities;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class SeancePlacesId implements Serializable {
    private Integer seanceId;

    private Integer hallRow;

    private Integer place;
}
