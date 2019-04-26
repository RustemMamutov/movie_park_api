package ru.api.moviepark.data.entities.id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class SeancesPlaces2019Id implements Serializable {
    @Column(name = "seance_id")
    Integer seanceId;

    @Column(name = "hall_row")
    Integer hallRow;

    @Column(name = "place")
    Integer place;
}
