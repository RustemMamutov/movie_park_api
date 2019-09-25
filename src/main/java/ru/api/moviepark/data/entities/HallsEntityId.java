package ru.api.moviepark.data.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HallsEntityId implements Serializable {
    private Integer hallId;

    private Integer placeId;
}
