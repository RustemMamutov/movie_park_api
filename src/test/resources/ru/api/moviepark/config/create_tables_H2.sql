create schema if not exists movie_park;

CREATE TABLE movie_park.halls (
	hall_id int4 NOT NULL,
	place_id int4 NOT NULL,
	is_vip bool NOT NULL,
	PRIMARY KEY (hall_id, place_id)
);

CREATE TABLE movie_park.movies (
	id int4 NOT NULL,
	"name" varchar(20) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE movie_park.movie_parks (
	id int4 NOT NULL,
	"name" varchar(20) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE movie_park.main_schedule (
	seance_id serial NOT NULL,
	seance_date date NOT NULL,
	start_time time NOT NULL,
	end_time time NOT NULL,
	movie_park_id int4 NOT NULL,
	movie_id int4 NOT NULL,
	hall_id int4 NOT NULL,
	base_price int4 NOT NULL,
	vip_price int4 NOT NULL,
	PRIMARY KEY (seance_id),
	FOREIGN KEY (movie_id) REFERENCES movie_park.movies(id),
	FOREIGN KEY (movie_park_id) REFERENCES movie_park.movie_parks(id)
);

CREATE TABLE movie_park.seances_places (
	seance_id int4 NOT NULL,
	place_id int4 NOT NULL,
	"blocked" bool NOT NULL,
	PRIMARY KEY (seance_id, place_id)
);

CREATE TABLE movie_park.roles (
	id int4 NOT NULL,
	description varchar NOT NULL,
	permissions varchar NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE movie_park.user_credentials (
	email varchar NOT NULL,
	"password" varchar NOT NULL,
	"role" int4 NOT NULL,
	PRIMARY KEY (email),
	FOREIGN KEY ("role") REFERENCES movie_park."roles"(id)
);
