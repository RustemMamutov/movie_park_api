package ru.api.moviepark.data.valueobjects;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MoviesInfoInput {

    @NotNull
    @NotEmpty
    private Set<Integer> movieIdSet;
}
