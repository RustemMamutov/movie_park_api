package ru.api.moviepark.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;

import static ru.api.moviepark.config.Constants.MAIN_SCHEDULE_TABLE_NAME;
import static ru.api.moviepark.config.Constants.SCHEMA_NAME;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
@Table(name = MAIN_SCHEDULE_TABLE_NAME, schema = SCHEMA_NAME)
public class MainScheduleEntity {
    @Id
    @Column(name = "seance_id")
    Integer seanceId;

    @Column(name = "seance_date")
    LocalDate seanceDate;

    @Column(name = "start_time")
    LocalTime startTime;

    @Column(name = "end_time")
    LocalTime endTime;

    @Column(name = "movie_park_id")
    Integer movieParkId;

    @Column(name = "movie_id")
    Integer movieId;

    @Column(name = "hall_id")
    Integer hallId;

    @Column(name = "base_price")
    Integer basePrice;

    @Column(name = "vip_price")
    Integer vipPrice;

    public static MainScheduleEntity createMainScheduleEntity(Integer seanceId, CreateSeanceInput inputJson) {
        return MainScheduleEntity
                .builder()
                .seanceId(seanceId)
                .seanceDate(inputJson.getDate())
                .startTime(inputJson.getStartTime())
                .endTime(inputJson.getEndTime())
                .movieParkId(inputJson.getMovieParkId())
                .movieId(inputJson.getMovieId())
                .hallId(inputJson.getHallId())
                .basePrice(inputJson.getBasePrice())
                .vipPrice(inputJson.getVipPrice())
                .build();
    }
}
