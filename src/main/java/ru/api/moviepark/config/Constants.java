package ru.api.moviepark.config;

import java.time.format.DateTimeFormatter;

public class Constants {

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final String SCHEMA_NAME = "movie_park";

    public static final String MAIN_SCHEDULE_TABLE_NAME = "main_schedule";

    public static final String HALLS_TABLE_NAME = "halls";

    public static final String MOVIES_TABLE_NAME = "movies";

    public static final String PRICES_DELTA_TABLE_NAME = "prices_delta";

    public static final String SEANCE_PLACES_TABLE_NAME = "seances_places";

    public static final String MAIN_SCHEDULE_VIEW_NAME = "main_schedule_view";

    public static final String MAIN_SCHEDULE_VIEW_FULL =
            String.format("%s.%s", SCHEMA_NAME, MAIN_SCHEDULE_VIEW_NAME);

    public static final long MAX_CACHE_LIFE_TIME = 30000;

    public static final long MIN_CACHE_LIFE_TIME = 2000;

    public static final int SEANCE_INFO_CACHE_FLUSH_TIMEOUT = 3;

    public static final int RPS_MAP_FLUSH_TIMEOUT = 3;

    public static final int RPS_LIFE_TIME = 120;
}
