import os
import random
from collections import namedtuple
from copy import deepcopy
from datetime import datetime as dt
from datetime import time
from datetime import timedelta as td
from random import randrange

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
    2: "CinemaPark2"
}

movie_park_halls = {
    101: hall_info(td(minutes=20), td(hours=8), td(hours=18),
                   {time(18, 0): [200, 300], time(12, 0): [150, 250], time(6, 0): [100, 200], time(0, 0): [200, 300]},
                   {1: 9, 2: 9, 3: 9, 4: 9, 5: 10, 6: 10, 7: 10, 8: 10, 9: 10}),
    102: hall_info(td(minutes=30), td(hours=10), td(hours=15),
                   {time(18, 0): [200, 300], time(12, 0): [150, 250], time(6, 0): [100, 200], time(0, 0): [200, 300]},
                   {1: 7, 2: 7, 3: 9, 4: 6, 5: 10, 6: 10, 7: 10, 8: 10}),
    103: hall_info(td(minutes=40), td(hours=9), td(hours=16),
                   {time(18, 0): [200, 300], time(12, 0): [150, 250], time(6, 0): [100, 200], time(0, 0): [200, 300]},
                   {1: 5, 2: 11, 3: 13, 4: 15}),
    201: hall_info(td(minutes=30), td(hours=8), td(hours=17),
                   {time(18, 0): [200, 300], time(12, 0): [150, 250], time(6, 0): [150, 250], time(0, 0): [200, 300]},
                   {1: 9, 2: 9, 3: 9, 4: 9, 5: 10, 6: 10, 7: 10, 8: 10, 9: 10}),
    202: hall_info(td(minutes=40), td(hours=9), td(hours=16),
                   {time(18, 0): [200, 300], time(12, 0): [150, 250], time(6, 0): [150, 250], time(0, 0): [200, 300]},
                   {1: 7, 2: 7, 3: 9, 4: 6, 5: 10, 6: 10, 7: 10, 8: 10}),
    203: hall_info(td(minutes=40), td(hours=10), td(hours=15),
                   {time(18, 0): [200, 300], time(12, 0): [150, 250], time(6, 0): [150, 250], time(0, 0): [200, 300]},
                   {1: 5, 2: 11, 3: 13, 4: 15})
}


def generate_movies_list(count):
    curr_movies = list()
    for i in range(1, count):
        start_date = dt(2021, 1, 1) + (i-1) * td(days=3)
        end_date = start_date + td(days=30)
        curr_movies.append(
            movie_info(i, "Фильм" + str(i),
                       td(hours=randrange(1, 3), minutes=10*randrange(1, 6)),
                       start_date, end_date)
        )
    return curr_movies


movies = [
    movie_info(1, "Т 34", td(hours=2, minutes=19), dt(2019, 1, 1), dt(2019, 1, 31)),
    movie_info(2, "Мэри Поппинс возвращается", td(hours=2, minutes=10), dt(2019, 1, 3), dt(2019, 2, 3)),
    movie_info(3, "Крид 2", td(hours=2, minutes=10), dt(2019, 1, 3), dt(2019, 2, 3)),
    movie_info(4, "Стекло", td(hours=2, minutes=9), dt(2019, 1, 17), dt(2019, 2, 17)),
    movie_info(5, "Зеленая книга", td(hours=2, minutes=10), dt(2019, 1, 24), dt(2019, 2, 24)),
    movie_info(6, "Завод", td(hours=1, minutes=49), dt(2019, 2, 7), dt(2019, 3, 7)),
    movie_info(7, "Алита: боевой ангел", td(hours=2, minutes=1), dt(2019, 2, 14), dt(2019, 3, 14)),
    movie_info(8, "Власть", td(hours=2, minutes=12), dt(2019, 2, 21), dt(2019, 3, 21)),
    movie_info(9, "Наркокурьер", td(hours=1, minutes=56), dt(2019, 2, 28), dt(2019, 3, 28)),
    movie_info(10, "Капитан Марвел", td(hours=2, minutes=3), dt(2019, 3, 7), dt(2019, 4, 7)),
    movie_info(11, "Рожденный стать королем", td(hours=2, minutes=0), dt(2019, 3, 14), dt(2019, 4, 14)),
    movie_info(12, "Балканский рубеж", td(hours=2, minutes=31), dt(2019, 3, 21), dt(2019, 4, 21)),
    movie_info(13, "Дамбо", td(hours=1, minutes=52), dt(2019, 3, 28), dt(2019, 4, 28)),
    movie_info(14, "Пляжный бездельник", td(hours=1, minutes=35), dt(2019, 3, 28), dt(2019, 4, 28)),
    movie_info(15, "Шазам!", td(hours=2, minutes=12), dt(2019, 4, 4), dt(2019, 5, 4)),
    movie_info(16, "Хеллбой", td(hours=2, minutes=0), dt(2019, 4, 11), dt(2019, 5, 11)),
    movie_info(17, "После", td(hours=1, minutes=45), dt(2019, 4, 18), dt(2019, 5, 18)),
    movie_info(18, "Игры разумов", td(hours=2, minutes=4), dt(2019, 4, 25), dt(2019, 5, 25)),
    movie_info(19, "Большое путешествие", td(hours=1, minutes=20), dt(2019, 4, 27), dt(2019, 5, 27)),
    movie_info(20, "Мстители: Финал", td(hours=3, minutes=1), dt(2019, 4, 29), dt(2019, 5, 29)),
    movie_info(21, "В метре друг от друга", td(hours=1, minutes=56), dt(2019, 5, 1), dt(2019, 5, 31)),
    movie_info(22, "Братство", td(hours=1, minutes=53), dt(2019, 5, 10), dt(2019, 6, 10)),
    movie_info(23, "Джон Уик 3", td(hours=2, minutes=10), dt(2019, 5, 16), dt(2019, 6, 16)),
    movie_info(24, "Покемон. Детектив Пикачу", td(hours=1, minutes=44), dt(2019, 5, 16), dt(2019, 6, 16)),
    movie_info(25, "Алладин", td(hours=2, minutes=8), dt(2019, 5, 23), dt(2019, 6, 23)),
    movie_info(26, "Люди Икс: Тёмный Феникс", td(hours=1, minutes=54), dt(2019, 6, 6), dt(2019, 7, 6)),
    movie_info(27, "Люди в черном: интернэшнл", td(hours=1, minutes=55), dt(2019, 6, 12), dt(2019, 7, 12)),
    movie_info(28, "История игрушек 4", td(hours=1, minutes=40), dt(2019, 6, 20), dt(2019, 7, 20)),
    movie_info(29, "Дылда", td(hours=2, minutes=19), dt(2019, 6, 20), dt(2019, 7, 20)),
    movie_info(30, "Курск", td(hours=1, minutes=58), dt(2019, 6, 27), dt(2019, 7, 27)),
    movie_info(31, "Человек паук: Вдали от дома", td(hours=2, minutes=9), dt(2019, 7, 4), dt(2019, 8, 4)),
    movie_info(32, "Паразиты", td(hours=2, minutes=12), dt(2019, 7, 4), dt(2019, 7, 4)),
    movie_info(33, "Анна", td(hours=1, minutes=40), dt(2019, 7, 11), dt(2019, 8, 11)),
    movie_info(34, "Король лев", td(hours=1, minutes=58), dt(2019, 7, 18), dt(2019, 8, 18)),
    movie_info(35, "Солнцестояние", td(hours=2, minutes=18), dt(2019, 7, 18), dt(2019, 8, 18)),
    movie_info(36, "Форсаж: Хоббс и Шоу", td(hours=2, minutes=16), dt(2019, 8, 1), dt(2019, 9, 30)),
    movie_info(37, "Однажды... в Голливуде", td(hours=2, minutes=40), dt(2019, 8, 8), dt(2019, 9, 8)),
    movie_info(38, "Angry Birds в кино 2", td(hours=1, minutes=37), dt(2019, 8, 15), dt(2019, 9, 15)),
    movie_info(39, "Падение ангела", td(hours=2, minutes=1), dt(2019, 8, 22), dt(2019, 9, 22)),
    movie_info(40, "Я иду искать", td(hours=1, minutes=34), dt(2019, 8, 29), dt(2019, 9, 29)),
    movie_info(41, "Оно 2", td(hours=2, minutes=50), dt(2019, 9, 5), dt(2019, 10, 5)),
    movie_info(42, "Щегол", td(hours=2, minutes=29), dt(2019, 9, 12), dt(2019, 10, 12)),
    movie_info(43, "Рэмбо:Последняя кровь", td(hours=1, minutes=39), dt(2019, 9, 19), dt(2019, 10, 19)),
    movie_info(44, "К звездам", td(hours=2, minutes=4), dt(2019, 9, 26), dt(2019, 10, 26)),
    movie_info(45, "Герой", td(hours=2, minutes=12), dt(2019, 9, 26), dt(2019, 10, 26)),
    movie_info(46, "Джокер", td(hours=2, minutes=2), dt(2019, 10, 3), dt(2019, 11, 3)),
    movie_info(47, "Эверест", td(hours=1, minutes=37), dt(2019, 10, 3), dt(2019, 11, 3)),
    movie_info(48, "Гемини", td(hours=1, minutes=57), dt(2019, 10, 10), dt(2019, 11, 10)),
    movie_info(49, "Малефисента: Владычеца тьмы", td(hours=1, minutes=58), dt(2019, 10, 17), dt(2019, 11, 17)),
    movie_info(50, "Текст", td(hours=2, minutes=12), dt(2019, 10, 24), dt(2019, 11, 24)),
    movie_info(51, "Докстор сон", td(hours=2, minutes=3), dt(2019, 11, 7), dt(2019, 12, 7)),
    movie_info(52, "Во все тяжкое", td(hours=1, minutes=31), dt(2019, 11, 7), dt(2019, 12, 7)),
    movie_info(53, "FORD против FERRARI", td(hours=2, minutes=32), dt(2019, 11, 14), dt(2019, 12, 14)),
    movie_info(54, "Аванпост", td(hours=2, minutes=32), dt(2019, 11, 21), dt(2019, 12, 21)),
    movie_info(55, "Достать ножи", td(hours=2, minutes=10), dt(2019, 11, 28), dt(2019, 12, 28)),
    movie_info(56, "Холодное сердце 2", td(hours=1, minutes=43), dt(2019, 11, 28), dt(2019, 12, 28)),
    movie_info(57, "21 мост", td(hours=1, minutes=39), dt(2019, 12, 5), dt(2020, 1, 5)),
    movie_info(58, "Сиротский бруклин", td(hours=2, minutes=24), dt(2019, 12, 5), dt(2020, 1, 5)),
    movie_info(59, "Джуманджи:Новый уровень", td(hours=2, minutes=3), dt(2019, 12, 12), dt(2020, 1, 12)),
    movie_info(60, "Звездные войны: Скайуокер. Восход", td(hours=2, minutes=22), dt(2019, 12, 19), dt(2020, 1, 19)),
    movie_info(61, "Холоп", td(hours=1, minutes=49), dt(2019, 12, 26), dt(2020, 1, 26)),
    movie_info(62, "Вторжение", td(hours=2, minutes=14), dt(2020, 1, 1), dt(2020, 1, 31)),
    movie_info(63, "Камуфляж и шпионаж", td(hours=1, minutes=42), dt(2020, 1, 9), dt(2020, 2, 9)),
    movie_info(64, "Маяк", td(hours=1, minutes=50), dt(2020, 1, 16), dt(2020, 2, 16)),
    movie_info(65, "Плохие парни навсегда", td(hours=2, minutes=4), dt(2020, 1, 23), dt(2020, 2, 23)),
    movie_info(66, "1917", td(hours=1, minutes=59), dt(2020, 1, 30), dt(2020, 2, 28)),
    movie_info(67, "Хищные птицы: Потрясающая история Харли Квинн", td(hours=1, minutes=49), dt(2020, 2, 6), dt(2020, 3, 6)),
    movie_info(68, "Джентельмены", td(hours=1, minutes=53), dt(2020, 2, 13), dt(2020, 3, 13)),
    movie_info(69, "Скандал", td(hours=1, minutes=49), dt(2020, 2, 13), dt(2020, 3, 13)),
    movie_info(70, "Лёд 2", td(hours=2, minutes=12), dt(2020, 2, 14), dt(2020, 3, 14)),
    movie_info(71, "Соник в кино", td(hours=1, minutes=40), dt(2020, 2, 20), dt(2020, 3, 20)),
    movie_info(72, "Человек-неведимка", td(hours=2, minutes=5), dt(2020, 3, 5), dt(2020, 4, 5)),
    movie_info(73, "Вперед", td(hours=1, minutes=42), dt(2020, 3, 3), dt(2020, 4, 5)),
    movie_info(74, "Бладшот", td(hours=1, minutes=49), dt(2020, 3, 12), dt(2020, 4, 12)),
    movie_info(75, "Тайная жизнь", td(hours=3, minutes=0), dt(2020, 3, 19), dt(2020, 4, 19)),
    movie_info(76, "Тролли.Мировой тур", td(hours=1, minutes=30), dt(2020, 3, 19), dt(2020, 4, 19)),
    movie_info(77, "Горы,солнце и любовь", td(hours=1, minutes=38), dt(2020, 4, 2), dt(2020, 5, 2)),
    movie_info(78, "Отель для самоубийц", td(hours=1, minutes=30), dt(2020, 4, 16), dt(2020, 5, 16)),
    movie_info(79, "Прекрасные лжецы", td(hours=1, minutes=20), dt(2020, 4, 23), dt(2020, 5, 23)),
    movie_info(80, "Запретная кухня", td(hours=1, minutes=25), dt(2020, 4, 30), dt(2020, 5, 30)),
    movie_info(81, "Прощай", td(hours=1, minutes=51), dt(2020, 5, 14), dt(2020, 6, 14)),
    movie_info(82, "Странники терпенья", td(hours=1, minutes=45), dt(2020, 5, 14), dt(2020, 6, 14)),
    movie_info(83, "Звонок", td(hours=1, minutes=52), dt(2020, 5, 21), dt(2020, 6, 21)),
    movie_info(84, "Книга моря", td(hours=1, minutes=25), dt(2020, 5, 21), dt(2020, 6, 21)),
    movie_info(85, "Мы умираем молодыми", td(hours=1, minutes=32), dt(2020, 6, 4), dt(2020, 7, 4)),
    movie_info(86, "Дикая роза", td(hours=1, minutes=42), dt(2020, 6, 11), dt(2020, 7, 11)),
    movie_info(87, "Где-то там", td(hours=1, minutes=34), dt(2020, 6, 16), dt(2020, 7, 16)),
    movie_info(88, "Убийства по открыткам", td(hours=1, minutes=44), dt(2020, 6, 25), dt(2020, 7, 25)),
    movie_info(89, "Мисс Плохое поведение", td(hours=1, minutes=47), dt(2020, 7, 6), dt(2020, 8, 6)),
    movie_info(90, "Основатель", td(hours=1, minutes=55), dt(2020, 7, 23), dt(2020, 8, 23)),
    movie_info(91, "Ловушка разума", td(hours=1, minutes=28), dt(2020, 7, 23), dt(2020, 8, 23)),
    movie_info(92, "Махинаторы", td(hours=1, minutes=31), dt(2020, 7, 30), dt(2020, 8, 30)),
    movie_info(93, "Мой шпион", td(hours=1, minutes=40), dt(2020, 8, 1), dt(2020, 8, 31)),
    movie_info(94, "Побег из Претории", td(hours=1, minutes=46), dt(2020, 8, 1), dt(2020, 8, 31)),
    movie_info(95, "Неистовый", td(hours=1, minutes=30), dt(2020, 8, 6), dt(2020, 9, 6)),
    movie_info(96, "Форпост", td(hours=2, minutes=3), dt(2020, 8, 13), dt(2020, 9, 13)),
    movie_info(97, "Гренландия", td(hours=2, minutes=0), dt(2020, 8, 20), dt(2020, 9, 20)),
    movie_info(98, "Агент Ева", td(hours=1, minutes=37), dt(2020, 8, 20), dt(2020, 9, 20)),
    movie_info(99, "Вратарь галактики", td(hours=1, minutes=58), dt(2020, 8, 27), dt(2020, 9, 27)),
    movie_info(100, "Новые мутанты", td(hours=1, minutes=39), dt(2020, 9, 3), dt(2020, 10, 3)),
    movie_info(101, "Довод", td(hours=2, minutes=30), dt(2020, 9, 3), dt(2020, 10, 3)),
    movie_info(102, "Мулан", td(hours=2, minutes=0), dt(2020, 9, 10), dt(2020, 10, 10)),
    movie_info(103, "После.Глава 2", td(hours=1, minutes=47), dt(2020, 9, 17), dt(2020, 10, 17)),
    movie_info(104, "Стрельцов", td(hours=1, minutes=41), dt(2020, 9, 24), dt(2020, 10, 24)),
    movie_info(105, "Капоне.Лицо со шрамом", td(hours=1, minutes=44), dt(2020, 10, 1), dt(2020, 10, 31)),
    movie_info(106, "Гудбай,Америка", td(hours=1, minutes=45), dt(2020, 10, 8), dt(2020, 11, 8)),
    movie_info(107, "KITOBOY", td(hours=1, minutes=33), dt(2020, 10, 8), dt(2020, 11, 8)),
    movie_info(108, "Доктор Лиза", td(hours=2, minutes=0), dt(2020, 10, 22), dt(2020, 11, 22)),
    movie_info(109, "Ведьмы", td(hours=1, minutes=46), dt(2020, 10, 29), dt(2020, 11, 29)),
    movie_info(110, "Выбивая долги", td(hours=1, minutes=35), dt(2020, 11, 4), dt(2020, 12, 4)),
    movie_info(111, "Побочный эффект", td(hours=1, minutes=33), dt(2020, 11, 5), dt(2020, 12, 5)),
    movie_info(112, "Еще по одной", td(hours=1, minutes=57), dt(2020, 11, 12), dt(2020, 12, 12)),
    movie_info(113, "Афера по-голливудски", td(hours=1, minutes=44), dt(2020, 11, 19), dt(2020, 12, 19)),
    movie_info(114, "Искуственный интелект", td(hours=1, minutes=46), dt(2020, 11, 26), dt(2020, 12, 26)),
    movie_info(115, "Человек из подольска", td(hours=1, minutes=32), dt(2020, 11, 26), dt(2020, 12, 26)),
    movie_info(116, "Трое", td(hours=2, minutes=7), dt(2020, 12, 3), dt(2021, 1, 3)),
    movie_info(117, "Неадекватные люди 2", td(hours=2, minutes=15), dt(2020, 12, 10), dt(2021, 1, 10)),
    movie_info(118, "На твоей волне", td(hours=1, minutes=36), dt(2020, 12, 17), dt(2021, 1, 17)),
    movie_info(119, "Семейка Крудс: Новоселье", td(hours=1, minutes=35), dt(2020, 12, 24), dt(2021, 1, 24)),
    movie_info(120, "Конь Юлий и большие скачки", td(hours=1, minutes=17), dt(2020, 12, 31), dt(2021, 1, 31))
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
    movie_park_id = int(hall_id / 100)
    while seances_start_dt <= seances_end_dt:
        random_movie = random.choice(actual_movies)
        curr_seance_end_dt = seances_start_dt + random_movie.movie_time
        base_price, vip_price = get_prices_by_policy_and_time(prices_policy, seances_start_dt.time())
        local_result.append(
            seance_info(seance_id, seances_start_dt.date(),
                        seances_start_dt.time(), curr_seance_end_dt.time(),
                        movie_park_id, random_movie.id, hall_id, base_price, vip_price)
        )
        delta_minute_to_round_up = 10 - curr_seance_end_dt.minute % 10
        seances_start_dt = curr_seance_end_dt + td(minutes=delta_minute_to_round_up) + seances_timeout
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
                result_list.extend(
                    generate_seances_for_date_and_hall(seance_id, curr_date, each_hall_id, actual_movies))

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


main('2019-01-01', 1000)
