## Easiest way to run: use docker compose
docker-compose up -d

## Docker only
### 1 step: start container with postgresl DB for testing

docker pull mamutovrm1/mp_api_testdb_img:latest

docker run --name psql_doc --env PGDATA=/opt/psql_data -p 5432:5432 -i mamutovrm1/mp_api_testdb_img:latest &

### 2 step: start container with microservice on the same server (need to authorize)

docker pull mamutovrm1/mp_api_service_doc:latest

docker run --name mp_api_service_doc -e "spring.datasource.local.url=jdbc:postgresql://**YOUR_SERVER_HOST:PORT**/trade_center" -p 9000:9000 mamutovrm1/mp_api_service_img:latest &

## Api for movie-park

Authorization:

 login    | password  | permissions 
--------  | --------  | --------
admin     | password  | read, modify, create, delete 
operator  | password  | read, modify
read_only | password  | read


### Method supports:  
  
**GET /movie-park-api/seances/info/{id}**

Returns seance info by id in json format

**GET /movie-park-api/seances/places/info/{id}**

Returns seance places info by id in json format

**PUT /movie-park-api/seances/places/block** (need modify permissions)

request body:
```json
{
    "seanceId": 1,
    "placeIdList" : [101, 102, 103]
}
```

Block places in seance

**PUT /movie-park-api/seances/places/unblock** (need modify permissions)

request body:
```json
{
    "seanceId": 1,
    "placeIdList" : [101, 102, 103]
}
```

Unblock places in seance

**PUT /movie-park-api/seances/places/update-schedule?days=1** (need modify permissions)

Update schedule by n days from today

**GET /movie-park-api/seances/all-by-period?periodStart=2030-01-01&periodEnd=2030-01-02**

Get all seances by period

**GET /movie-park-api/seances/all-by-movie-and-date?movieId=1&date=2030-01-01**

Get all seances by date and movie

**POST /movie-park-api/seances/create** (need create permissions)

request body:
```json
{
	"date": "2020-12-01",
	"startTime": "07:00:00",
	"endTime": "07:50:00",
	"movieParkId": 1,
	"movieId": 1,
	"hallId": 101,
	"basePrice": 100,
	"vipPrice": 200
}
```

**DELETE /movie-park-api/seances/delete/{id}** (need delete permissions)

Delete seance by id

**GET /movie-park-api/movies/all-by-period?periodStart=2030-01-01&periodEnd=2030-01-02**

Get all movies by period

**GET /movie-park-api/halls/places-info/{id}**

Get all hall places info by hall id