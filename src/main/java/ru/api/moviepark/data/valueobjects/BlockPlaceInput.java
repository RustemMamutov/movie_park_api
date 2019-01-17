package ru.api.moviepark.data.valueobjects;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BlockPlaceInput {
    private Integer seanceId;
    private Integer line;
    private Integer place;
    private Boolean isBlocked;
}
