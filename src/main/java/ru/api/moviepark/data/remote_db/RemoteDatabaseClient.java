package ru.api.moviepark.data.remote_db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.api.moviepark.controller.CommonResponse;
import ru.api.moviepark.data.DatabaseClient;
import ru.api.moviepark.data.remote_db.entities.MainScheduleEntity;
import ru.api.moviepark.data.remote_db.repositories.MainScheduleRepo;
import ru.api.moviepark.data.remote_db.repositories.SeancesPlacesRepo;
import ru.api.moviepark.data.valueobjects.AllSeancesView;
import ru.api.moviepark.data.valueobjects.BlockPlaceInput;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;
import ru.api.moviepark.data.valueobjects.PlaceInHallInfo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.api.moviepark.controller.CommonResponse.SEANCE_ADDED;
import static ru.api.moviepark.controller.CommonResponse.VALID_DATA;
import static ru.api.moviepark.util.CheckInputUtil.checkCreateSeanceInput;

@Service
@Slf4j
public class RemoteDatabaseClient implements DatabaseClient {

    private final JdbcTemplate jdbcTemplate;
    private final MainScheduleRepo mainScheduleRepo;
    private final SeancesPlacesRepo seancesPlacesRepo;

    public RemoteDatabaseClient(JdbcTemplate jdbcTemplate, MainScheduleRepo mainScheduleRepo, SeancesPlacesRepo seancesPlacesRepo) {
        this.jdbcTemplate = jdbcTemplate;
        this.mainScheduleRepo = mainScheduleRepo;
        this.seancesPlacesRepo = seancesPlacesRepo;
    }

    @Override
    public List<AllSeancesView> getAllSeances() {
//        String tableName = SEANCES_VIEW.getTableName();
//        String sqlQuery = String.format("select * from %s;", tableName);
//        return jdbcTemplate.query(sqlQuery, new AllSeancesViewRowMapper());
        return null;
    }

    @Override
    public List<AllSeancesView> getAllSeancesForDate(LocalDate date) {
//        String tableName = SEANCES_VIEW.getTableName();
//        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//        String sqlQuery = String.format("select * from %s where date = '%s'", tableName, dateStr);
//        return jdbcTemplate.query(sqlQuery, new AllSeancesViewRowMapper());
        return null;
    }


    @Override
    public CommonResponse createNewSeance(CreateSeanceInput inputJson) {
        CommonResponse response = checkCreateSeanceInput(inputJson);
        if (!response.equals(VALID_DATA)) {
            return response;
        }

        int newSeanceId = mainScheduleRepo.findMaxId().orElse(0) + 1;
        MainScheduleEntity newSeanceEntity = MainScheduleEntity.createMainScheduleEntity(newSeanceId, inputJson);
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
//            return seancesPlacesRepo.findBySeanceId(seanceId);
            return null;
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