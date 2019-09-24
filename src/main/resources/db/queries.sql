CREATE OR REPLACE FUNCTION movie_park_develop.create_new_seances(days integer)
 RETURNS void
 LANGUAGE sql
AS $function$
INSERT into movie_park_develop.seances_places
    (
        select main_schedule.seance_id,
               halls.place_id,
               false as "blocked"
        from movie_park_develop.main_schedule
	         inner join movie_park_develop.halls
                on main_schedule.hall_id = halls.hall_id
        where main_schedule.seance_date <= (select CURRENT_DATE + days * INTERVAL '1 day')
        order by seance_id, place_id
    )
ON conflict (seance_id, place_id)
    DO nothing
$function$
;