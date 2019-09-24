INSERT INTO movie_park_develop.halls (hall_id,place_id,is_vip) VALUES
(101,101,false),
(101,102,false),
(101,103,true),
(101,104,false),
(101,105,false),
(102,101,false),
(102,102,false),
(102,103,true),
(102,104,false),
(102,105,false),
(201,101,false),
(201,102,false),
(201,103,true),
(201,104,false),
(201,105,false),
(202,101,false),
(202,102,false),
(202,103,true),
(202,104,false),
(202,105,false);

INSERT INTO movie_park_develop.movie_parks (id,name) VALUES
(1,'CinemaPark1'),
(2,'CinemaPark2');

INSERT INTO movie_park_develop.movies (id,name) VALUES
(1,'Film1'),
(2,'Film2');

INSERT INTO movie_park_develop.main_schedule (
seance_id,seance_date,start_time,end_time,movie_park_id,movie_id,hall_id,base_price,vip_price) VALUES
(1,'2030-01-01','09:00:00','10:40:00',1,1,101,100,200),
(2,'2030-01-01','11:00:00','12:40:00',1,1,101,120,220),
(3,'2030-01-01','09:00:00','10:40:00',1,2,102,100,200),
(4,'2030-01-01','09:00:00','10:40:00',1,2,102,120,220),
(5,'2030-01-01','09:00:00','10:40:00',2,1,201,150,250),
(6,'2030-01-01','11:00:00','12:40:00',2,1,201,170,270),
(7,'2030-01-01','09:00:00','10:40:00',2,2,202,150,250),
(8,'2030-01-01','09:00:00','10:40:00',2,2,202,170,270);


INSERT INTO MOVIE_PARK_DEVELOP.SEANCES_PLACES (SEANCE_ID, PLACE_ID, BLOCKED)
SELECT
	main_schedule.seance_id,
	halls.place_id,
	false as BLOCKED
from movie_park_develop.main_schedule
inner join movie_park_develop.halls
	on main_schedule.hall_id = halls.hall_id
	where main_schedule.seance_date <= '2030-01-01'
	order by seance_id, place_id;