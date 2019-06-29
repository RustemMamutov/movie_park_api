package ru.api.moviepark.data.remote.entities;

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
