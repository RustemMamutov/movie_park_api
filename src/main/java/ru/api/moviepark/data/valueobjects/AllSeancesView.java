package ru.api.moviepark.data.valueobjects;


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
public class AllSeancesView {

    @Column(name = "seance_id")
    Integer seanceId;

    @Column(name = "seance_date")
    Date seanceDate;

    @Column(name = "start_time")
    Time startTime;

    @Column(name = "end_time")
    Time endTime;

    @Column(name = "movie_name")
    String movieName;

    @Column(name = "hall_id")
    Integer hallId;
}
