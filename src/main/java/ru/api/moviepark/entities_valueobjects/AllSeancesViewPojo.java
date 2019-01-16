package ru.api.moviepark.entities_valueobjects;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.sql.Date;
import java.sql.Time;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class AllSeancesViewPojo {

    Integer id;
    Date date;

    @Column(name = "start_time")
    Time startTime;

    @Column(name = "end_time")
    Time endTime;

    String name;

    @Column(name = "hall_id")
    Integer hallId;
}
