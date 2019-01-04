package ru.api.moviepark.services.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PlaceInfo {
    Integer line;
    Integer place;
    Boolean isVip;
    Integer price;
    Boolean isBlocked;
}
