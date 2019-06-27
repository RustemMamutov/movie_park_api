CREATE SCHEMA IF NOT EXISTS cinema_park;

CREATE TABLE cinema_park.main_schedule (
	seance_id int4 NOT NULL,
	seance_date date NOT NULL,
	start_time time NOT NULL,
	end_time time NOT NULL,
	movie_id int4 NOT NULL,
	hall_id int4 NOT NULL,
	base_price int4 NOT null,
	PRIMARY KEY (seance_id)
);

CREATE TABLE cinema_park.seances_places (
    seance_id int4 NOT NULL,
    hall_row int4 NOT NULL,
    place int4 NOT NULL,
    is_blocked boolean NOT NULL,
    PRIMARY KEY (seance_id, hall_row, place)
);

CREATE TABLE cinema_park.seances_places_archive (
    seance_id int4 NOT NULL,
    hall_row int4 NOT NULL,
    place int4 NOT NULL,
    is_blocked boolean NOT NULL,
    PRIMARY KEY (seance_id, hall_row, place)
);

CREATE TABLE cinema_park.movies (
    movie_id int4 NOT NULL,
    movie_name text NOT NULL,
    CONSTRAINT movies_pkey PRIMARY KEY (movie_id)
);

CREATE TABLE cinema_park.halls (
   hall_id int4 NOT NULL,
   row int4 NOT NULL,
   place int4 NOT NULL,
   is_vip boolean NOT NULL,
   PRIMARY KEY (hall_id, row, place)
);