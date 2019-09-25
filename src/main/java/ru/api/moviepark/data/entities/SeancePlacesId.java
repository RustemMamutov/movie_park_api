package ru.api.moviepark.data.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SeancePlacesId implements Serializable {
    private Integer seanceId;

    private Integer placeId;
}
