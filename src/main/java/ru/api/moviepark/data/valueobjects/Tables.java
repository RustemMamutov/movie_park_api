package ru.api.moviepark.data.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Tables {
    HALLS("movie_park.halls"),
    MOVIES("movie_park.movies"),
    PRICES_DELTA("movie_park.prices_delta"),
    SEANCES_TABLE("movie_park.seances"),
    SEANCES_VIEW("movie_park.all_seances_schedule");

    String tableName;
}
