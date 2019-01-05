package ru.api.moviepark.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.api.moviepark.controller.valueobjects.BlockPlaceInput;
import ru.api.moviepark.controller.valueobjects.CommonResponse;
import ru.api.moviepark.controller.valueobjects.CreateSeanceInput;
import ru.api.moviepark.data.entities.SeancesEntity;
import ru.api.moviepark.data.repositories.SeancesRepo;
import ru.api.moviepark.services.valueobjects.PlaceInHallInfo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class DBPostgreService {

    private JdbcTemplate jdbcTemplate;
    private TransactionTemplate transactionTemplate;
    private SeancesRepo seancesRepo;

    public DBPostgreService(JdbcTemplate jdbcTemplate,
                            TransactionTemplate transactionTemplate,
                            SeancesRepo seancesRepo) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
        this.seancesRepo = seancesRepo;
    }


    /**
     * Checking input data before creating seance.
     * @param inputJson
     * @return
     */
    private CommonResponse checkCreateSeanceInput(CreateSeanceInput inputJson){
        LocalDate inputDate = inputJson.getDate();
        if (inputDate.isBefore(LocalDate.now())){
            return CommonResponse.INVALID_DATE;
        }

        String movieIdSqlQuery = "select count(id) from movie_park.movies where id = " + inputJson.getMovieId();
        Integer count = jdbcTemplate.queryForObject(movieIdSqlQuery, Integer.class);
        if (count == 0){
            return CommonResponse.INVALID_MOVIE;
        }

        String hallIdSqlQuery = "select count(hall_id) from movie_park.halls where hall_id = " + inputJson.getHallId();
        count = jdbcTemplate.queryForObject(hallIdSqlQuery, Integer.class);
        if (count == 0){
            return CommonResponse.INVALID_HALL;
        }

        if (inputJson.getBasePrice() <= 0){
            return CommonResponse.INVALID_PRICE;
        }

        int inputHallId = inputJson.getHallId();
        LocalTime inputStartTime = inputJson.getStartTime();
        LocalTime inputEndTime = inputJson.getEndTime();

        List<SeancesEntity> allSeancesForDate = seancesRepo.findSeancesEntityByDateAndHallId(inputDate, inputHallId);
        for (SeancesEntity entity : allSeancesForDate){
            LocalTime localStartTime = entity.getStartTime();
            LocalTime localEndTime = entity.getEndTime();
            boolean startChecker = localStartTime.isBefore(inputStartTime) && inputStartTime.isBefore(localEndTime);
            boolean endChecker = localStartTime.isBefore(inputEndTime) && inputEndTime.isBefore(localEndTime);
            if (startChecker || endChecker){
                return CommonResponse.INVALID_TIME_PERIOD;
            }
        }

        return CommonResponse.VALID_DATA;
    }

    /**
     * Create new seance and add it to db.
     * @param inputJson
     */
    public CommonResponse addSeance(CreateSeanceInput inputJson){
        CommonResponse response = checkCreateSeanceInput(inputJson);
        if (!response.equals(CommonResponse.VALID_DATA)){
            return response;
        }

        Integer maxId = jdbcTemplate.queryForObject(
                "select max(id) from movie_park.seances", Integer.class);
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
                .seanceTableExists(false)
                .build();
        seancesRepo.save(newSeanceEntity);
        return CommonResponse.SEANCE_ADDED;
    }


    private List<Integer> getIdListFromRows(String sqlQuery) {
        try {
            log.info("Executing sql query " + sqlQuery);
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlQuery);
            List<Integer> idList = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                Integer id = (Integer) row.get("id");
                idList.add(id);
            }
            return idList;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * List of seances id for all days before today where seance table exists.
     */
    private List<Integer> getSeancesIdWhereSeanceTableExistForSeancesBeforeToday() {
        String today = LocalDate.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String sqlForm = "select id from movie_park.seances where seance_table_exists = 'true' and  date < '%s';";
        String sqlQuery = String.format(sqlForm, today);

        return getIdListFromRows(sqlQuery);
    }

    /**
     * List of seances id for today and tomorrow where seance table doesn't exist.
     *
     * @return
     */
    private List<Integer> getSeancesIdWhereSeanceTableDoesntExistTodayAndTomorrow() {
        String today = LocalDate.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String tomorrow = LocalDate.now().plusDays(1).format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String sqlForm = "select id from movie_park.seances where seance_table_exists = 'false' and " +
                "'%s' <= date and date <= '%s';";
        String sqlQuery = String.format(sqlForm, today, tomorrow);

        return getIdListFromRows(sqlQuery);
    }

    private String getDestinationTableName(Tables table) {
        switch (table) {
            case HALLS:
                return "movie_park.halls";
            case MOVIES:
                return "movie_park.movies";
            case PRICES_DELTA:
                return "movie_park.prices_delta";
            case SEANCES_TABLE:
                return "movie_park.seances";
            case SEANCES_VIEW:
                return "movie_park.seances_schedule";
            default:
                return "";
        }
    }

    /**
     * Sql query for creating table by seance id.
     *
     * @param seanceId - seance id
     * @return
     */
    private String getCreateSeanceTableSqlQuery(int seanceId) {
        String tableName = "movie_park.seance" + seanceId;
        String sqlForm = "CREATE TABLE if not exists %s " +
                "(line int4 not NULL, " +
                "place int4 not NULL, " +
                "isvip bool NOT NULL, " +
                "price int4 not NULL, " +
                "isblocked bool not NULL)";
        return String.format(sqlForm, tableName);
    }

    /**
     * Fill existing seance table.
     *
     * @param seanceId
     * @return
     */
    private String getFillSeanceTableSqlQuery(int seanceId) {
        String tableName = "movie_park.seance" + seanceId;
        String sqlForm = "insert into %s (" +
                "select " +
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
                "where seances.id = %s)";
        return String.format(sqlForm, tableName, seanceId);
    }

    /**
     * Creating tables for all seances for today and tomorrow.
     */
    public void createTablesForAllMissingSeancesTodayAndTomorrow() {
        try {
            log.info("Creating tables for today and tomorrow");
            List<Integer> idList = getSeancesIdWhereSeanceTableDoesntExistTodayAndTomorrow();
            idList.forEach(id -> {
                String createTable = getCreateSeanceTableSqlQuery(id);
                String fillTable = getFillSeanceTableSqlQuery(id);
                String setExistenceAsTrue =
                        "update movie_park.seances set seance_table_exists = true where id = " + id;
                transactionTemplate.execute(status -> {
                    jdbcTemplate.execute(createTable);
                    jdbcTemplate.execute(fillTable);
                    jdbcTemplate.execute(setExistenceAsTrue);
                    return null;
                });
            });
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete all outdated tables.
     */
    public void deleteOldSeanceTablesBeforeToday() {
        try {
            log.info("Deleting tables for all days before today");
            List<Integer> idList = getSeancesIdWhereSeanceTableExistForSeancesBeforeToday();
            idList.forEach(id -> {
                String dropTableSql = "drop table movie_park.seance" + id;
                String setExistenceAsFalse =
                        "update movie_park.seances set seance_table_exists = false where id = " + id;
                transactionTemplate.execute(status -> {
                    jdbcTemplate.execute(dropTableSql);
                    jdbcTemplate.execute(setExistenceAsFalse);
                    return null;
                });
            });
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Get info about all places in hall for current seance.
     *
     * @param seanceId
     * @return
     */
    public List<PlaceInHallInfo> getSeanceFullInfo(int seanceId) {
        try {
            log.info("Getting full info for seance id = " + seanceId);
            String sqlForm = "select * from movie_park.seance%s;";
            String sqlQuery = String.format(sqlForm, seanceId);
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlQuery);
            List<PlaceInHallInfo> placeInfoList = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                Integer line = (Integer) row.get("line");
                Integer place = (Integer) row.get("place");
                Boolean isVip = (Boolean) row.get("isvip");
                Integer price = (Integer) row.get("price");
                Boolean isBlocked = (Boolean) row.get("isblocked");
                PlaceInHallInfo info = PlaceInHallInfo.builder()
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
    public void blockPlaceOnSeance(BlockPlaceInput inputJson) {
        try {
            log.info("Blocking place in seance");
            String tableName = "movie_park.seance" + inputJson.getSeanceId();
            String sqlForm = "update %s set isblocked = true where line = %s and place = %s";
            String sql = String.format(sqlForm, tableName,
                    inputJson.getLine(), inputJson.getPlace());
            transactionTemplate.execute(status -> {
                jdbcTemplate.execute(sql);
                return null;
            });
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}