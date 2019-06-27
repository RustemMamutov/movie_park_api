package ru.api.moviepark.data.remote_db.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.api.moviepark.config.CONSTANTS;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@IdClass(SeancePlacesId.class)
@Entity
@Table(name = CONSTANTS.SEANCE_PLACES_TABLE_NAME, schema = CONSTANTS.SCHEMA_NAME)
public class SeancePlacesEntity {
    @Id
    @Column(name = "seance_id")
    private Integer seanceId;

    @Id
    @Column(name = "hall_row")
    private Integer hallRow;

    @Id
    @Column(name = "place")
    private Integer place;

    @Column(name = "is_blocked")
    private Boolean blocked;


}
