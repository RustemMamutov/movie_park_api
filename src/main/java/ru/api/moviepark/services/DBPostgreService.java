package ru.api.moviepark.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.api.moviepark.entities_valueobjects.*;
import ru.api.moviepark.repositories.SeancesRepo;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class DBPostgreService {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SeancesRepo seancesRepo;

    public DBPostgreService(JdbcTemplate jdbcTemplate,
                            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                            SeancesRepo seancesRepo) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.seancesRepo = seancesRepo;
    }

    private List<AllSeancesViewPojo> getSeancesPojoListFromRows(String sqlQuery) {
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlQuery);
            List<AllSeancesViewPojo> pojoList = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                Integer id = (Integer) row.get("id");

                Date seanceDate = (Date) row.get("date");
                Time startTime = (Time) row.get("start_time");
                Time endTime = (Time) row.get("end_time");
                String name = (String) row.get("name");
                Integer hallId = (Integer) row.get("hall_id");
                AllSeancesViewPojo pojo = AllSeancesViewPojo.builder()
                        .id(id)
                        .date(seanceDate)
                        .startTime(startTime)
                        .endTime(endTime)
                        .name(name)
                        .hallId(hallId)
                        .build();
                pojoList.add(pojo);
            }
            return pojoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<AllSeancesViewPojo> getAllSeances() {
        String tableName = getDestinationTableName(Tables.SEANCES_VIEW);
        String sqlQuery = String.format("select * from %s;", tableName);
        log.info("Executing sql query " + sqlQuery);
        return getSeancesPojoListFromRows(sqlQuery);
    }

    public List<AllSeancesViewPojo> getAllSeancesForDate(LocalDate date) {
        String tableName = getDestinationTableName(Tables.SEANCES_VIEW);
        String sqlQueryForm = "select * from %s where date = '%s'";
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String sqlQuery = String.format(sqlQueryForm, tableName, dateStr);
        log.info("Executing sql query " + sqlQuery);
        return getSeancesPojoListFromRows(sqlQuery);
    }

    /**
     * Checking input data before creating seance.
     *
     * @param inputJson
     * @return
     */
    private CommonResponse checkCreateSeanceInput(CreateSeanceInput inputJson) {
        LocalDate inputDate = inputJson.getDate();
        if (inputDate.isBefore(LocalDate.now())) {
            return CommonResponse.INVALID_DATE;
        }

        String tableName = getDestinationTableName(Tables.MOVIES);
        String movieIdSqlQuery = String.format("select count(id) from %s where id = %s",
                tableName, inputJson.getMovieId());
        Integer count = jdbcTemplate.queryForObject(movieIdSqlQuery, Integer.class);
        if (count == 0) {
            return CommonResponse.INVALID_MOVIE;
        }

        tableName = getDestinationTableName(Tables.HALLS);
        String hallIdSqlQuery = String.format("select count(id) from %s where id = %s",
                tableName, inputJson.getHallId());
        count = jdbcTemplate.queryForObject(hallIdSqlQuery, Integer.class);
        if (count == 0) {
            return CommonResponse.INVALID_HALL;
        }

        if (inputJson.getBasePrice() <= 0) {
            return CommonResponse.INVALID_PRICE;
        }

        int inputHallId = inputJson.getHallId();
        LocalTime inputStartTime = inputJson.getStartTime();
        LocalTime inputEndTime = inputJson.getEndTime();

        List<SeancesEntity> allSeancesForDate = seancesRepo.findSeancesEntityByDateAndHallId(inputDate, inputHallId);
        for (SeancesEntity entity : allSeancesForDate) {
            LocalTime localStartTime = entity.getStartTime();
            LocalTime localEndTime = entity.getEndTime();
            boolean startChecker = localStartTime.isBefore(inputStartTime) && inputStartTime.isBefore(localEndTime);
            boolean endChecker = localStartTime.isBefore(inputEndTime) && inputEndTime.isBefore(localEndTime);
            if (startChecker || endChecker) {
                return CommonResponse.INVALID_TIME_PERIOD;
            }
        }

        return CommonResponse.VALID_DATA;
    }

    /**
     * Create new seance and add it to db.
     *
     * @param inputJson
     */
    public CommonResponse addSeance(CreateSeanceInput inputJson) {
        CommonResponse response = checkCreateSeanceInput(inputJson);
        if (!response.equals(CommonResponse.VALID_DATA)) {
            return response;
        }

        String tableName = getDestinationTableName(Tables.SEANCES_TABLE);
        String maxIdSqlQuery = String.format("select max(id) from %s;", tableName);
        Integer maxId = jdbcTemplate.queryForObject(maxIdSqlQuery, Integer.class);
        int newSeanceId = maxId + 1;
        SeancesEntity newSeanceEntity = SeancesEntity
                .builder()
                .id(newSeanceId)
                .date(inputJson.getDate())
                .startTime(inputJson.getStartTime())
                .endTime(inputJson.getEndTime())
                .movieId(inputJson.getMovieId())
                .hallId(inputJson.getHallId())
                .basePrice(inputJson.getBasePrice())
                .build();
        seancesRepo.save(newSeanceEntity);
        return CommonResponse.SEANCE_ADDED;
    }

    private String getDestinationTableName(Tables table) {
        switch (table) {
            case HALLS:
                return "movie_park1.halls";
            case MOVIES:
                return "movie_park1.movies";
            case PRICES_DELTA:
                return "movie_park1.prices_delta";
            case SEANCES_TABLE:
                return "movie_park1.seances";
            case SEANCES_VIEW:
                return "movie_park1.all_seances_schedule";
            default:
                return "";
        }
    }

    /**
     * Fill existing seance table.
     */
    private String getFillScheduleTableForDaySqlQuery(LocalDate date, String tableName) {
        String todayStrISO = date.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String sqlForm = "insert into %s " +
                "(select " +
                "seances.id, " +
                "halls.line, " +
                "halls.place, " +
                "halls.isvip, " +
                "seances.base_price + prices_delta.price_delta as price, " +
                "false as isblocked " +
                "from movie_park.seances " +
                "inner join movie_park.halls " +
                "on seances.hall_id = halls.hall_id " +
                "inner join movie_park.prices_delta " +
                "on " +
                "prices_delta.period_start_date < seances.date " +
                "and seances.date <= prices_delta.period_end_date " +
                "and halls.isvip = prices_delta.isvip " +
                "and prices_delta.start_time <= seances.start_time " +
                "and seances.start_time <= prices_delta.end_time " +
                "where date = '%s')";
        return String.format(sqlForm, tableName, todayStrISO);
    }


    private String getDestinationTableName(LocalDate date) {
        return "movie_park1.schedule_" + date.format(DateTimeFormatter.ofPattern("yyyyMM"));
    }

    private String getTempTableName() {
        return "temp_schedule_" + (int) (Math.random() * 10000000);
    }

    private String getInsertOnConflictSql(String destinationTableName,
                                          String tempTableName) {
        return String.format(
                "INSERT INTO %s select*from %s ON CONFLICT (id, line, place) DO NOTHING;",
                destinationTableName, tempTableName);
    }

    /**
     * Creating tables for today.
     */
    @Transactional
    public void fillScheduleTableForDate(LocalDate date) {
        log.info("Creating tables for today");


        MapSqlParameterSource argMap = new MapSqlParameterSource()
                .addValue("tempTable", getTempTableName())
                .addValue("destinationTable", getDestinationTableName(date));

        String tempTableName = getTempTableName();
        String destinationTableName = getDestinationTableName(date);

        String createTempTable = String.format("select * into temp %s from %s limit 0;",
                tempTableName, destinationTableName);
        String fillTempTable = getFillScheduleTableForDaySqlQuery(date, tempTableName);
        String insertOnConflictSql = getInsertOnConflictSql(destinationTableName, tempTableName);
        String dropTempTableSql = String.format("drop table %s;", tempTableName);

        try {
            jdbcTemplate.execute(createTempTable);
            jdbcTemplate.execute(fillTempTable);
            jdbcTemplate.execute(insertOnConflictSql);
            jdbcTemplate.execute(dropTempTableSql);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private LocalDate getDateForSeanceId(int seanceId) {
        String tableName = "movie_park1.seances";
        String sqlForm = "select date from %s where id = %s";
        String sqlQuery = String.format(sqlForm, tableName, seanceId);
        return jdbcTemplate.queryForObject(sqlQuery, LocalDate.class);
    }

    /**
     * Get info about all places in hall for current seance.
     *
     * @param seanceId
     * @return
     */
    public List<PlaceInHallInfo> getSeanceFullInfo(int seanceId) {
        LocalDate date = getDateForSeanceId(seanceId);
        log.info("Getting full info for seance id = " + seanceId);
        String todayStr = date.format(
                DateTimeFormatter.ofPattern("yyyyMMdd"));
        String tableName = "movie_park1.schedule_" + todayStr;
        String sqlForm = "select * from %s where id = %s";
        String sqlQuery = String.format(sqlForm, tableName, seanceId);

        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlQuery);
            List<PlaceInHallInfo> placeInfoList = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                Integer line = (Integer) row.get("line");
                Integer place = (Integer) row.get("place");
                Boolean isVip = (Boolean) row.get("isvip");
                Integer price = (Integer) row.get("price");
                Boolean isBlocked = (Boolean) row.get("isblocked");
                PlaceInHallInfo info = PlaceInHallInfo.builder()
                        .id(seanceId)
                        .line(line)
                        .place(place)
                        .isVip(isVip)
                        .price(price)
                        .isBlocked(isBlocked)
                        .build();
                placeInfoList.add(info);
            }
            return placeInfoList;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Block the place in hall for current seance.
     *
     * @param inputJson
     */
    @Transactional
    public void blockOrUnblockPlaceOnSeance(BlockPlaceInput inputJson) {
        log.info("Blocking place in seance");
        LocalDate date = getDateForSeanceId(inputJson.getSeanceId());
        String todayStr = date.format(
                DateTimeFormatter.ofPattern("yyyyMMdd"));
        String tableName = "movie_park1.schedule_" + todayStr;
        String sqlForm = "update %s set isblocked = %s where id = %s and line = %s and place = %s";
        String sqlQuery = String.format(sqlForm, tableName, inputJson.getIsBlocked(), inputJson.getSeanceId(),
                inputJson.getLine(), inputJson.getPlace());
        try {
            jdbcTemplate.execute(sqlQuery);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}