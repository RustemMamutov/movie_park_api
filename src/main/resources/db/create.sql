DROP ALL OBJECTS;

create schema if not exists movie_park_develop;

create table movie_park_develop.halls (
    hall_id int4 NOT NULL,
    place_id int4 NOT NULL,
    is_vip bool NOT NULL,
    PRIMARY KEY (hall_id, place_id)
);

create table movie_park_develop.movies (
    id int4 NOT NULL,
    name VARCHAR NOT NULL,
    PRIMARY KEY (id)
);

create table movie_park_develop.movie_parks (
    id int4 NOT NULL,
    name text NOT NULL,
    PRIMARY KEY (id)
);

create table movie_park_develop.main_schedule (
    seance_id int4 NOT NULL,
    seance_date date NOT NULL,
    start_time time NOT NULL,
    end_time time NOT NULL,
    movie_park_id int4 NOT NULL,
    movie_id int4 NOT NULL,
    hall_id int4 NOT NULL,
    base_price int4 NOT NULL,
    vip_price int4 NOT NULL,
    PRIMARY KEY (seance_id),
    FOREIGN KEY (movie_park_id) REFERENCES movie_park_develop.movie_parks(id),
    FOREIGN KEY (movie_id) REFERENCES movie_park_develop.movies(id)
);

create table movie_park_develop.seances_places (
    seance_id int4 NOT NULL,
    place_id int4 NOT NULL,
    blocked bool NOT NULL,
    PRIMARY KEY (seance_id, place_id)
);

create or replace view movie_park_develop.main_schedule_view
as select
    main_schedule.seance_id,
    main_schedule.seance_date,
    main_schedule.start_time,
    main_schedule.end_time,
    movie_parks.id AS movie_park_id,
    movie_parks.name AS movie_park_name,
    movies.id AS movie_id,
    movies.name AS movie_name,
    main_schedule.hall_id,
    main_schedule.base_price,
    main_schedule.vip_price
from movie_park_develop.main_schedule
join movie_park_develop.movie_parks ON main_schedule.movie_park_id = movie_parks.id
join movie_park_develop.movies ON main_schedule.movie_id = movies.id;
