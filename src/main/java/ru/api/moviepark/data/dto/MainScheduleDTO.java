package ru.api.moviepark.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
public class MainScheduleDTO {
    private Integer seanceId;
    private LocalDate seanceDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer movieParkId;
    private String movieParkName;
    private Integer movieId;
    private String movieName;
    private Integer hallId;
    private Integer basePrice;
    private Integer vipPrice;
}
