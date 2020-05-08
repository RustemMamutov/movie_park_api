package ru.api.moviepark.data.valueobjects;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BlockUnblockPlaceInput {
    @NotNull
    private Integer seanceId;
    @NotNull
    @NotEmpty
    private List<Integer> placeIdList;
}
