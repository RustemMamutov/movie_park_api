package ru.api.moviepark.data.valueobjects;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreateSeanceInput {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer movieParkId;
    private Integer movieId;
    private Integer hallId;
    private Integer basePrice;
    private Integer vipPrice;
}
