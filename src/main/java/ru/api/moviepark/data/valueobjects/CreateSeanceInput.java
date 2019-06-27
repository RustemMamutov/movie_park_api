package ru.api.moviepark.data.valueobjects;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CreateSeanceInput {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer movieId;
    private Integer hallId;
    private Integer basePrice;
}
