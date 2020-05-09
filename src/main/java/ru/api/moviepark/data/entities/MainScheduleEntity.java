package ru.api.moviepark.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.api.moviepark.data.dto.MainScheduleDTO;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.api.moviepark.env.Constants.MAIN_SCHEDULE_TABLE_NAME;
import static ru.api.moviepark.env.Constants.SCHEMA_NAME;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
@Table(name = MAIN_SCHEDULE_TABLE_NAME, schema = SCHEMA_NAME)
public class MainScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seance_id", nullable = false)
    private Integer seanceId;

    @Column(name = "seance_date")
    private LocalDate seanceDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "movie_park_id", referencedColumnName = "id")
    private MovieParksEntity movieParkEntity;

    @ManyToOne
    @JoinColumn(name = "movie_id", referencedColumnName = "id")
    private MoviesEntity movieEntity;

    @Column(name = "hall_id")
    private Integer hallId;

    @Column(name = "base_price")
    private Integer basePrice;

    @Column(name = "vip_price")
    private Integer vipPrice;

    public static MainScheduleEntity createMainScheduleEntity(CreateSeanceInput inputJson) {
        return MainScheduleEntity
                .builder()
                .seanceDate(inputJson.getDate())
                .startTime(inputJson.getStartTime())
                .endTime(inputJson.getEndTime())
                .movieParkEntity(new MovieParksEntity(inputJson.getMovieParkId()))
                .movieEntity(new MoviesEntity(inputJson.getMovieId()))
                .hallId(inputJson.getHallId())
                .basePrice(inputJson.getBasePrice())
                .vipPrice(inputJson.getVipPrice())
                .build();
    }

    public static List<MainScheduleDTO> convertToDtoList(List<MainScheduleEntity> entityList) {
        return entityList.stream()
                .map(MainScheduleEntity::convertToDto)
                .collect(Collectors.toList());
    }

    public MainScheduleDTO convertToDto() {
        return MainScheduleDTO.builder()
                .seanceId(this.getSeanceId())
                .seanceDate(this.getSeanceDate())
                .startTime(this.getStartTime())
                .endTime(this.getEndTime())
                .movieParkId(this.getMovieParkEntity().getMovieParkId())
                .movieParkName(this.getMovieParkEntity().getMovieParkName())
                .movieId(this.getMovieEntity().getMovieId())
                .movieName(this.getMovieEntity().getMovieName())
                .hallId(this.getHallId())
                .basePrice(this.getBasePrice())
                .vipPrice(this.getVipPrice())
                .build();
    }
}
