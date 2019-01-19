package ru.api.moviepark.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
@Table(name = "seances", schema = "movie_park")
public class SeancesEntity {
    @Id
    Integer id;
    LocalDate date;
    @Column(name = "start_time")
    LocalTime startTime;

    @Column(name = "end_time")
    LocalTime endTime;

    @Column(name = "movie_id")
    Integer movieId;

    @Column(name = "hall_id")
    Integer hallId;

    @Column(name = "base_price")
    Integer basePrice;
}
