package ru.api.moviepark.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.api.moviepark.controller.valueobjects.CreateSeanceInputJson;
import ru.api.moviepark.controller.valueobjects.InputJson;
import ru.api.moviepark.services.valueobjects.PlaceInfo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class DBPostgreService {

    private JdbcTemplate jdbcTemplate;
    private TransactionTemplate transactionTemplate;

    public DBPostgreService(JdbcTemplate jdbcTemplate,
                            TransactionTemplate transactionTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    private List<Integer> getIdListFromRows(String sqlQuery) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlQuery);
        List<Integer> idList = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Integer id = (Integer) row.get("id");
            idList.add(id);
        }
        return idList;
    }

    private List<Integer> getSeancesIdWhereSeanceTableExistForSeancesBeforeToday(){
        String today = LocalDate.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String sqlForm = "select id from movie_park.seances where seance_table_exists = 'true' and  date < '%s';";
        String sqlQuery = String.format(sqlForm, today);

        return getIdListFromRows(sqlQuery);
    }

    private List<Integer> getSeancesIdWhereSeanceTableDoesntExistTodayAndTomorrow(){
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

    private String  getCreateSeanceTableSqlQuery(int id){
        String tableName = "movie_park.seance" + id;
        String sqlForm = "CREATE TABLE if not exists %s " +
                "(line int4 not NULL, " +
                "place int4 not NULL, " +
                "isvip bool NOT NULL, " +
                "price int4 not NULL, " +
                "isblocked bool not NULL)";
        return String.format(sqlForm, tableName);
    }

    private String getFillSeanceTableSqlQuery(int id){
        String tableName = "movie_park.seance" + id;
        String sqlForm =
                "insert into %s (" +
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
        return String.format(sqlForm, tableName, id);
    }

    public void createTablesForAllMissingSeancesTodayAndTomorrow() {
        try {
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
            throw new RuntimeException(e);
        }
    }

    public void deleteOldSeanceTablesBeforeToday(){
        try {
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
            throw new RuntimeException(e);
        }
    }

    public List<PlaceInfo> getSeanceFullInfo(int seanceId){
        String sqlForm = "select * from movie_park.seance%s;";
        String sqlQuery = String.format(sqlForm, seanceId);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlQuery);
        List<PlaceInfo> placeInfoList = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Integer line = (Integer) row.get("line");
            Integer place = (Integer) row.get("place");
            Boolean isVip = (Boolean) row.get("isvip");
            Integer price = (Integer) row.get("price");
            Boolean isBlocked = (Boolean) row.get("isblocked");
            PlaceInfo info = PlaceInfo.builder()
                    .line(line)
                    .place(place)
                    .isVip(isVip)
                    .price(price)
                    .isBlocked(isBlocked)
                    .build();
            placeInfoList.add(info);
        }
        return placeInfoList;
    }

    public void blockPlaceOnSeance(InputJson inputJson){
        String tableName = "movie_park.seance" + inputJson.getSeanceId();
        String sqlForm = "update %s set isblocked = true where line = %s and place = %s";
        String sql = String.format(sqlForm, tableName,
                inputJson.getLine(), inputJson.getPlace());
        transactionTemplate.execute(status -> {
            jdbcTemplate.execute(sql);
            return null;
        });
    }
}