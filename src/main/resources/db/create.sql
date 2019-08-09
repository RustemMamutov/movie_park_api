CREATE SCHEMA IF NOT EXISTS cinema_park;

CREATE TABLE IF NOT EXISTS movie_park.halls
(
 hall_id int4 NOT NULL,
 row int4 NOT NULL,
 place int4 NOT NULL,
 is_vip boolean NOT NULL,
 PRIMARY KEY (hall_id, row, place)
);

CREATE TABLE IF NOT EXISTS movie_park.main_schedule
(
 seance_id int4 NOT NULL,
 seance_date date NOT NULL,
 start_time time NOT NULL,
 end_time time NOT NULL,
 movie_id int4 NOT NULL,
 hall_id int4 NOT NULL,
 base_price int4 NOT null,
 PRIMARY KEY (seance_id)
);

CREATE TABLE IF NOT EXISTS movie_park.movies
(
 movie_id int4 NOT NULL,
 movie_name text NOT NULL,
 CONSTRAINT movies_pkey PRIMARY KEY (movie_id)
);

CREATE TABLE IF NOT EXISTS movie_park.prices_delta
(
 start_time time NOT NULL,
 end_time time NOT NULL,
 period_start_date date NOT NULL,
 period_end_date date NOT NULL,
 is_vip bool NOT NULL,
 price_delta int4 NOT NULL
);

CREATE TABLE IF NOT EXISTS movie_park.seances_places
(
 seance_id int4 NOT NULL,
 hall_id int4 NOT NULL,
 hall_row int4 NOT NULL,
 place int4 NOT NULL,
 is_vip bool NOT NULL,
 price int4 NOT NULL,
 "blocked" bool NOT NULL,
 PRIMARY KEY (seance_id, hall_row, place)
);

create or replace view
 movie_park.main_schedule_view as
select main_schedule.seance_id,
 main_schedule.seance_date,
 main_schedule.start_time,
 main_schedule.end_time,
 movies.movie_name,
 main_schedule.hall_id
from movie_park.main_schedule
 inner join movie_park.movies
 on main_schedule.movie_id = movies.movie_id;
