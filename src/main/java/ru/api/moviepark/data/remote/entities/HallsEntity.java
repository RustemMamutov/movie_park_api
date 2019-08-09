package ru.api.moviepark.data.remote.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.api.moviepark.config.CONSTANTS;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@IdClass(HallsEntityId.class)
@Entity
@Table(name = CONSTANTS.HALLS_TABLE_NAME, schema = CONSTANTS.SCHEMA_NAME)
public class HallsEntity {

    @Id
    @Column(name = "hall_id")
    private Integer hallId;

    @Id
    @Column(name = "row")
    private Integer row;

    @Id
    @Column(name = "place")
    private Integer place;

    @Column(name = "is_vip")
    private Boolean isVip;


}


