package ru.api.moviepark.data.valueobjects;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BlockUnblockPlaceInput {
    private Integer seanceId;
    private Boolean blocked;
    private List<Integer> placeIdList;
}
