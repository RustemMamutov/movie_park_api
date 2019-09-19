package ru.api.moviepark.data.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import static ru.api.moviepark.config.Constants.HALLS_TABLE_NAME;
import static ru.api.moviepark.config.Constants.SCHEMA_NAME;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@IdClass(HallsEntityId.class)
@Entity
@Table(name = HALLS_TABLE_NAME, schema = SCHEMA_NAME)
public class HallsEntity {

    @Id
    @Column(name = "hall_id")
    private Integer hallId;

    @Id
    @Column(name = "place_id")
    private Integer placeId;
}
