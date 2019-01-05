package ru.api.moviepark.controller.valueobjects;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BlockPlaceInput {
    @Column(name = "seanceId")
    private Integer seanceId;

    @Column(name = "line")
    private Integer line;

    @Column(name = "place")
    private Integer place;

    @Column(name = "isBlocked")
    private Boolean isBlocked;
}
