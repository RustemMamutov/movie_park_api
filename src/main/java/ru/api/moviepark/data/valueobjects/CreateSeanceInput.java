package ru.api.moviepark.data.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreateSeanceInput {
    @NotNull
    private LocalDate date;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;
    @NotNull
    private Integer movieParkId;
    @NotNull
    private Integer movieId;
    @NotNull
    private Integer hallId;
    @NotNull
    private Integer basePrice;
    @NotNull
    private Integer vipPrice;
}
