CREATE or replace function
    create_new_seances(days int) RETURNS void
AS
$$
INSERT into cinema_park.seances_places
    (
        select main_schedule.seance_id,
               halls.row,
               halls.place,
               halls.is_vip,
               main_schedule.base_price + prices_delta.price_delta as price,
               false                                               as blocked
        from cinema_park.main_schedule
                 inner join cinema_park.halls
                            on main_schedule.hall_id = halls.hall_id
                 inner join cinema_park.prices_delta
                            on prices_delta.period_start_date < main_schedule.seance_date
                                and main_schedule.seance_date <= prices_delta.period_end_date
                                and halls.is_vip = prices_delta.is_vip
                                and prices_delta.start_time <= main_schedule.start_time
                                and main_schedule.start_time <= prices_delta.end_time
        where main_schedule.seance_date <= (select CURRENT_DATE + days * INTERVAL '1 day')
    )
ON conflict (seance_id, hall_row, place)
    DO nothing
$$
    LANGUAGE SQL;
