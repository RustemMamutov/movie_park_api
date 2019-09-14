package ru.api.moviepark.data.entities;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class HallsEntityId implements Serializable {
    private Integer hallId;

    private Integer placeId;
}
