package ru.api.moviepark.data.valueobjects;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
public class MainScheduleViewEntity {

    @Column(name = "seance_id")
    private Integer seanceId;

    @Column(name = "seance_date")
    private LocalDate seanceDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "movie_park_id")
    private Integer movieParkId;

    @Column(name = "movie_park_name")
    private String movieParkName;

    @Column(name = "movie_id")
    private Integer movieId;

    @Column(name = "movie_name")
    private String movieName;

    @Column(name = "hall_id")
    private Integer hallId;

    @Column(name = "base_price")
    private Integer basePrice;

    @Column(name = "vip_price")
    private Integer vipPrice;
}
