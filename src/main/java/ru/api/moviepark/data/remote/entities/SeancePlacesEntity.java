package ru.api.moviepark.data.remote.entities;

import lombok.*;
import ru.api.moviepark.config.CONSTANTS;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@IdClass(SeancePlacesId.class)
@Entity
@Table(name = CONSTANTS.SEANCE_PLACES_TABLE_NAME, schema = CONSTANTS.SCHEMA_NAME)
public class SeancePlacesEntity {
    @Id
    @Column(name = "seance_id")
    private Integer seanceId;

    @Column(name = "hall_id")
    private Integer hallId;

    @Id
    @Column(name = "hall_row")
    private Integer hallRow;

    @Id
    private Integer place;

    @Column(name = "is_vip")
    private Boolean isVip;

    private Integer price;

    private Boolean blocked;
}