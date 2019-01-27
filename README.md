## Api для работы с кинотеатрами  
  
сервис работает с БД trade_center, schema - movie_park.  
  
### Поддерживаются сдедующие команды:  
  
**Get /movie-park/get-seances-for-date/{dateStr}** 
dateStr - дата в формате YYYY-MM-DD (2019-01-31)  
 
Метод возвращает список всех сеансов для заданной даты (dateStr).
  
**Get /movie-park/get-all-seances**  
Метод возвращает список всех существующих сеансов.  
  
**POST /movie-park/add-seance**  
принимает в качестве параметра JSON вида:  
{  
	"date": "2019-01-31",  
	"startTime": "07:00:00",  
	"endTime": "08:50:00",  
	"movieId": 1,  
	"hallId": 1,  
	"basePrice": 100  
}  
  
Метод добавляет новый сеанс в соответствующую таблицу  

**POST /movie-park/block-unblock-place**  

принимает в качестве параметра JSON вида:  
{  
	"seanceId": 1,  
	"line": 1,  
	"place": 1,  
	"isBlocked": true  
}
  
Метод блокирует/освобождает соответствующее место в зале для заданного сеанса.  
  
  
**POST /movie-park/create-schedule-for-date/{dateStr}**  
dateStr - дата в формате YYYY-MM-DD (2019-01-31)  
  
Метод создает таблицу с расписанием всех сеансов для заданной даты (dateStr).  
  
**Get /movie-park/get-seance-info/{seanceId}**
seanceId - id сеанса.

Метод возвращает информацию обо всех местах в зале для заданного сеанса (seanceId).
