package ru.api.moviepark.entities_valueobjects;


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
    LocalDate date;
    LocalTime startTime;
    LocalTime endTime;
    Integer movieId;
    Integer hallId;
    Integer basePrice;
}
