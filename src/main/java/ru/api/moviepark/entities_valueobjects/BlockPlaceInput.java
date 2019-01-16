package ru.api.moviepark.entities_valueobjects;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BlockPlaceInput {
    private Integer seanceId;
    private Integer line;
    private Integer place;
    private Boolean isBlocked;
}
