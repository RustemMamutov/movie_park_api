import os
from collections import namedtuple
from copy import deepcopy
from datetime import datetime as dt
from datetime import timedelta as td
from datetime import time
import random


hall_info = namedtuple("hall_info", "seance_timeout start_time end_time_delta prices_policy hall_places")
movie_info = namedtuple("movie_info", "id movie_name movie_time start_date end_date")

seance_info = namedtuple("seance_info",
                         "seance_id "
                         "seance_date "
                         "start_time "
                         "end_time "
                         "movie_park_id "
                         "movie_id "
                         "hall_id "
                         "base_price "
                         "vip_price")

movie_parks = {
    1: "CinemaPark1",
    2: "CinemaPark2",
    3: "CinemaPark3",
}

movie_park_halls = {
    101: hall_info(td(minutes=20), td(hours=8), td(hours=18),
                   {time(18, 0): [200, 300], time(12, 0): [150, 250], time(6, 0): [100, 200], time(0, 0): [200, 300]},
                   {1: 10, 2: 10, 3: 10}),
    102: hall_info(td(minutes=30), td(hours=10), td(hours=15),
                   {time(18, 0): [200, 300], time(12, 0): [150, 250], time(6, 0): [100, 200], time(0, 0): [200, 300]},
                   {1: 10, 2: 10, 3: 10}),
    103: hall_info(td(minutes=40), td(hours=9), td(hours=16),
                   {time(18, 0): [200, 300], time(12, 0): [150, 250], time(6, 0): [100, 200], time(0, 0): [200, 300]},
                   {1: 10, 2: 10, 3: 10}),
    201: hall_info(td(minutes=30), td(hours=8), td(hours=17),
                   {time(18, 0): [200, 300], time(12, 0): [150, 250], time(6, 0): [150, 250], time(0, 0): [200, 300]},
                   {1: 10, 2: 10, 3: 10}),
    202: hall_info(td(minutes=40), td(hours=9), td(hours=16),
                   {time(18, 0): [200, 300], time(12, 0): [150, 250], time(6, 0): [150, 250], time(0, 0): [200, 300]},
                   {1: 10, 2: 10, 3: 10}),
    203: hall_info(td(minutes=40), td(hours=10), td(hours=15),
                   {time(18, 0): [200, 300], time(12, 0): [150, 250], time(6, 0): [150, 250], time(0, 0): [200, 300]},
                   {1: 10, 2: 10, 3: 10})
}

movies = [
    movie_info(1, "Фильм1", td(hours=1, minutes=50), dt(2019, 10, 1), dt(2019, 10, 31)),
    movie_info(2, "Фильм2", td(hours=1, minutes=50), dt(2019, 10, 20), dt(2019, 11, 20)),
    movie_info(3, "Фильм3", td(hours=1, minutes=50), dt(2019, 11, 1), dt(2019, 11, 30)),
    movie_info(4, "Фильм4", td(hours=1, minutes=50), dt(2019, 10, 15), dt(2019, 11, 15)),
    movie_info(5, "Фильм5", td(hours=1, minutes=50), dt(2019, 10, 30), dt(2019, 11, 30)),
    movie_info(6, "Фильм6", td(hours=1, minutes=50), dt(2019, 12, 15), dt(2020, 1, 15)),
    movie_info(7, "Фильм7", td(hours=1, minutes=10), dt(2020, 1, 1), dt(2020, 2, 1)),
    movie_info(8, "Фильм8", td(hours=1, minutes=20), dt(2020, 1, 15), dt(2020, 2, 15)),
    movie_info(9, "Фильм9", td(hours=1, minutes=30), dt(2020, 2, 1), dt(2020, 3, 1)),
    movie_info(10, "Фильм10", td(hours=1, minutes=40), dt(2020, 2, 15), dt(2020, 3, 15)),
    movie_info(11, "Фильм11", td(hours=1, minutes=50), dt(2020, 3, 1), dt(2020, 4, 1)),
    movie_info(12, "Фильм12", td(hours=2, minutes=0), dt(2020, 3, 15), dt(2020, 4, 15)),
    movie_info(13, "Фильм13", td(hours=2, minutes=10), dt(2020, 4, 1), dt(2020, 5, 1)),
    movie_info(14, "Фильм14", td(hours=1, minutes=20), dt(2020, 4, 15), dt(2020, 5, 15)),
    movie_info(15, "Фильм15", td(hours=1, minutes=30), dt(2020, 5, 1), dt(2020, 6, 1)),
    movie_info(16, "Фильм16", td(hours=1, minutes=40), dt(2020, 5, 15), dt(2020, 6, 15)),
    movie_info(17, "Фильм17", td(hours=1, minutes=50), dt(2020, 6, 1), dt(2020, 7, 1)),
    movie_info(18, "Фильм18", td(hours=1, minutes=40), dt(2020, 6, 15), dt(2020, 7, 15)),
    movie_info(19, "Фильм19", td(hours=1, minutes=30), dt(2020, 7, 1), dt(2020, 8, 1)),
    movie_info(20, "Фильм20", td(hours=1, minutes=45), dt(2020, 7, 15), dt(2020, 8, 15))
]


def seance_info_to_string(info):
    return f'{info.seance_id},{info.seance_date},{info.start_time},' \
           f'{info.end_time},{info.movie_park_id},{info.movie_id},' \
           f'{info.hall_id},{info.base_price},{info.vip_price}'


def get_prices_by_policy_and_time(price_policy, curr_time):
    for each_time in price_policy.keys():
        if curr_time >= each_time:
            return price_policy[each_time][0], price_policy[each_time][1]


def get_actual_movies(actual_date):
    local_result = []
    for each_movie in movies:
        if each_movie.start_date <= actual_date <= each_movie.end_date:
            local_result.append(each_movie)

    return local_result


def generate_seances_for_date_and_hall(seance_id, curr_date, hall_id, actual_movies):
    local_result = []
    curr_hall_info = movie_park_halls[hall_id]
    seances_timeout = curr_hall_info.seance_timeout
    seances_start_dt = curr_date + curr_hall_info.start_time
    seances_end_dt = seances_start_dt + curr_hall_info.end_time_delta
    prices_policy = curr_hall_info.prices_policy
    movie_park_id = int(hall_id/100)
    while seances_start_dt <= seances_end_dt:
        random_movie = random.choice(actual_movies)
        curr_seance_end_dt = seances_start_dt + random_movie.movie_time
        base_price, vip_price = get_prices_by_policy_and_time(prices_policy, seances_start_dt.time())
        local_result.append(
            seance_info(seance_id, seances_start_dt.date(),
                        seances_start_dt.time(), curr_seance_end_dt.time(),
                        movie_park_id, random_movie.id, hall_id, base_price, vip_price)
        )
        seances_start_dt = curr_seance_end_dt + seances_timeout
        seance_id += 1

    return local_result


def generate_schedule(period_start_date, days):
    result_list = list()
    curr_date = deepcopy(period_start_date)
    end_date = curr_date + td(days=days)
    seance_id = 1
    while curr_date <= end_date:
        actual_movies = get_actual_movies(curr_date)
        if actual_movies:
            for each_hall_id in movie_park_halls.keys():
                if result_list:
                    seance_id = result_list[-1].seance_id + 1
                result_list.extend(generate_seances_for_date_and_hall(seance_id, curr_date, each_hall_id, actual_movies))

        curr_date = curr_date + td(days=1)

    return result_list


def generate_main_schedule_file(start_date, period_to_calculate_in_days):
    filename = "main_schedule.csv"
    try:
        os.remove(filename)
    except OSError:
        pass

    result = generate_schedule(start_date, period_to_calculate_in_days)

    with open(filename, 'a', encoding="utf-8") as file:
        file.write("seance_id,\"seance_date\",\"start_time\",\"end_time\","
                   "\"movie_park_id\",\"movie_id\",\"hall_id\",\"base_price\",\"vip_price\"\n")
        for each_seance_info in result:
            file.write(seance_info_to_string(each_seance_info))
            file.write('\n')


def generate_movies_file():
    filename = "movies.csv"
    try:
        os.remove(filename)
    except OSError:
        pass

    with open(filename, 'a', encoding="utf-8") as file:
        file.write("id,\"name\"\n")
        for each_movie_name in movies:
            file.write(f'{each_movie_name.id},{each_movie_name.movie_name}')
            file.write('\n')


def generate_movie_parks_file():
    filename = "movie_parks.csv"
    try:
        os.remove(filename)
    except OSError:
        pass

    with open(filename, 'a', encoding="utf-8") as file:
        file.write("id,\"name\"\n")
        for id in movie_parks:
            file.write(f'{id},{movie_parks[id]}')
            file.write('\n')


def generate_halls_file():
    filename = "halls.csv"
    try:
        os.remove(filename)
    except OSError:
        pass

    with open(filename, 'a', encoding="utf-8") as file:
        file.write("hall_id,\"place_id\",\"is_vip\"\n")
        for hall_id in movie_park_halls:
            hall_places = movie_park_halls[hall_id].hall_places
            for each_row in hall_places:
                for place in range(1, hall_places[each_row] + 1):
                    file.write(f'{hall_id},{100 * each_row + place},false')
                    file.write('\n')


def main(start_date_as_str, period_to_calculate_in_days):
    generate_main_schedule_file(dt.strptime(start_date_as_str, "%Y-%m-%d"), period_to_calculate_in_days)
    generate_movies_file()
    generate_movie_parks_file()
    generate_halls_file()


main('2019-11-03', 365)
