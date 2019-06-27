package ru.api.moviepark.data.remote_db.entities;

import lombok.Builder;

import java.io.Serializable;

@Builder
public class SeancePlacesId implements Serializable {
    private Integer seanceId;

    private Integer hallRow;

    private Integer place;
}
