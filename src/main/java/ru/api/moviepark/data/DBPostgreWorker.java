package ru.api.moviepark.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.api.moviepark.controller.CommonResponse;
import static ru.api.moviepark.controller.CommonResponse.*;
import ru.api.moviepark.data.rowmappers.AllSeancesViewRowMapper;
import ru.api.moviepark.data.rowmappers.PlaceInHallInfoRowMapper;
import ru.api.moviepark.data.entities.MainScheduleEntity;
import ru.api.moviepark.data.valueobjects.*;
import static ru.api.moviepark.data.valueobjects.Tables.*;
import ru.api.moviepark.data.repositories.MainScheduleRepo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;



@Service
@Slf4j
public class DBPostgreWorker implements DatabaseWorker{

    private final JdbcTemplate jdbcTemplate;
    private final MainScheduleRepo mainScheduleRepo;

    public DBPostgreWorker(JdbcTemplate jdbcTemplate,
                           MainScheduleRepo mainScheduleRepo) {
        this.jdbcTemplate = jdbcTemplate;
        this.mainScheduleRepo = mainScheduleRepo;
    }

    @Override
    public List<AllSeancesView> getAllSeances() {
        String tableName = SEANCES_VIEW.getTableName();
        String sqlQuery = String.format("select * from %s;", tableName);
        return jdbcTemplate.query(sqlQuery, new AllSeancesViewRowMapper());
    }

    @Override
    public List<AllSeancesView> getAllSeancesForDate(LocalDate date) {
        String tableName = SEANCES_VIEW.getTableName();
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String sqlQuery = String.format("select * from %s where date = '%s'", tableName, dateStr);
        return jdbcTemplate.query(sqlQuery, new AllSeancesViewRowMapper());
    }

    private CommonResponse checkCreateSeanceInput(CreateSeanceInput inputJson) {
        LocalDate inputDate = inputJson.getDate();
        if (inputDate.isBefore(LocalDate.now())) {
            return INVALID_DATE;
        }

        String tableName = MOVIES.getTableName();
        String movieIdSqlQuery = String.format("select count(id) from %s where id = %s",
                tableName, inputJson.getMovieId());
        Integer count = jdbcTemplate.queryForObject(movieIdSqlQuery, Integer.class);
        if (count == 0) {
            return INVALID_MOVIE;
        }

        tableName = HALLS.getTableName();
        String hallIdSqlQuery = String.format("select count(id) from %s where id = %s",
                tableName, inputJson.getHallId());
        count = jdbcTemplate.queryForObject(hallIdSqlQuery, Integer.class);
        if (count == 0) {
            return INVALID_HALL;
        }

        if (inputJson.getBasePrice() <= 0) {
            return INVALID_PRICE;
        }

        int inputHallId = inputJson.getHallId();
        LocalTime inputStartTime = inputJson.getStartTime();
        LocalTime inputEndTime = inputJson.getEndTime();

        List<MainScheduleEntity> allSeancesForDate = mainScheduleRepo.findSeancesEntityBySeanceDateAndHallId(inputDate, inputHallId);
        for (MainScheduleEntity entity : allSeancesForDate) {
            LocalTime localStartTime = entity.getStartTime();
            LocalTime localEndTime = entity.getEndTime();
            boolean startChecker = localStartTime.isBefore(inputStartTime) && inputStartTime.isBefore(localEndTime);
            boolean endChecker = localStartTime.isBefore(inputEndTime) && inputEndTime.isBefore(localEndTime);
            if (startChecker || endChecker) {
                return INVALID_TIME_PERIOD;
            }
        }

        return VALID_DATA;
    }

    @Override
    public CommonResponse createNewSeance(CreateSeanceInput inputJson) {
        CommonResponse response = checkCreateSeanceInput(inputJson);
        if (!response.equals(VALID_DATA)) {
            return response;
        }

        String tableName = SEANCES_TABLE.getTableName();
        String maxIdSqlQuery = String.format("select max(id) from %s;", tableName);
        Integer maxId = jdbcTemplate.queryForObject(maxIdSqlQuery, Integer.class);
        if (maxId == null) {
            maxId = 1;
        }
        int newSeanceId = maxId + 1;
        MainScheduleEntity newSeanceEntity = MainScheduleEntity
                .builder()
                .seanceId(newSeanceId)
                .seanceDate(inputJson.getDate())
                .startTime(inputJson.getStartTime())
                .endTime(inputJson.getEndTime())
                .movieId(inputJson.getMovieId())
                .hallId(inputJson.getHallId())
                .basePrice(inputJson.getBasePrice())
                .build();
        mainScheduleRepo.save(newSeanceEntity);
        return SEANCE_ADDED;
    }

    private String getDestinationTableName(LocalDate date) {
        return "movie_park1.schedule_" + date.format(DateTimeFormatter.ofPattern("yyyyMM"));
    }

    private String getTempTableName() {
        return "temp_schedule_" + (int) (Math.random() * 10000000);
    }

    /**
     * Fill existing seance table.
     */
    private String getFillScheduleTableForDaySql(LocalDate date, String tableName) {
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

    private String getInsertOnConflictSql(String destinationTableName,
                                          String tempTableName) {
        return String.format(
                "INSERT INTO %s select*from %s ON CONFLICT (id, line, place) DO NOTHING;",
                destinationTableName, tempTableName);
    }

    /**
     * Filling table data for today.
     */
    @Override
    @Transactional
    public void fillScheduleTableForDate(LocalDate date) {
        log.info("Creating tables for today");

        String tempTableName = getTempTableName();
        String destinationTableName = getDestinationTableName(date);

        String createTempTableSql = String.format("select * into temp %s from %s limit 0;",
                tempTableName, destinationTableName);
        String fillTempTable = getFillScheduleTableForDaySql(date, tempTableName);
        String insertOnConflictSql = getInsertOnConflictSql(destinationTableName, tempTableName);
        String dropTempTableSql = String.format("drop table %s;", tempTableName);

        try {
            jdbcTemplate.execute(createTempTableSql);
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
        String sqlQuery = String.format("select date from %s where id = ?", tableName);
        return jdbcTemplate.queryForObject(sqlQuery, new Object[]{seanceId}, LocalDate.class);
    }

    /**
     * Get info about all places in hall for current seance.
     *
     * @param seanceId
     * @return
     */
    @Override
    public List<PlaceInHallInfo> getSeanceFullInfo(int seanceId) {
        LocalDate date = getDateForSeanceId(seanceId);
        log.info("Getting full info for seance id = " + seanceId);
        String tableName = getDestinationTableName(date);
        String sqlQuery = String.format("select * from %s where id = ?", tableName);

        try {
            return jdbcTemplate.query(sqlQuery, new Object[]{seanceId},
                    new PlaceInHallInfoRowMapper());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Block/unblock the place in hall for current seance.
     *
     * @param inputJson
     */
    @Override
    @Transactional
    public void blockOrUnblockPlaceOnSeance(BlockPlaceInput inputJson) {
        log.info("Blocking place in seance");
        LocalDate date = getDateForSeanceId(inputJson.getSeanceId());
        String tableName = getDestinationTableName(date);
        String sqlQuery = String.format(
                "update %s set isblocked = ? where id = ? and line = ? and place = ?",
                tableName);
        Object[] parameters = new Object[]{
                inputJson.getIsBlocked(), inputJson.getSeanceId(),
                inputJson.getLine(), inputJson.getPlace()};
        try {
            jdbcTemplate.update(sqlQuery, parameters);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}