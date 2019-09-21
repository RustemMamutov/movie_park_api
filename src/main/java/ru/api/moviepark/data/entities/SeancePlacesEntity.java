package ru.api.moviepark.data.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import static ru.api.moviepark.config.Constants.SCHEMA_NAME;
import static ru.api.moviepark.config.Constants.SEANCE_PLACES_TABLE_NAME;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@IdClass(SeancePlacesId.class)
@Entity
@Table(name = SEANCE_PLACES_TABLE_NAME, schema = SCHEMA_NAME)
public class SeancePlacesEntity {
    @Id
    @Column(name = "seance_id")
    private Integer seanceId;

    @Column(name = "place_id")
    private Integer placeId;

    private Boolean blocked;
}
