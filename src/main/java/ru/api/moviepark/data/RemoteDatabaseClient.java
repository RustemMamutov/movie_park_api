package ru.api.moviepark.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.api.moviepark.config.CONSTANTS;
import ru.api.moviepark.controller.CommonResponse;
import ru.api.moviepark.data.entities.MainScheduleEntity;
import ru.api.moviepark.data.entities.SeancePlacesEntity;
import ru.api.moviepark.data.mappers.AllSeancesViewRowMapper;
import ru.api.moviepark.data.repositories.MainScheduleRepo;
import ru.api.moviepark.data.repositories.SeancesPlacesRepo;
import ru.api.moviepark.data.valueobjects.AllSeancesView;
import ru.api.moviepark.data.valueobjects.BlockPlaceInput;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.api.moviepark.controller.CommonResponse.SEANCE_ADDED;
import static ru.api.moviepark.controller.CommonResponse.VALID_DATA;
import static ru.api.moviepark.util.CheckInputUtil.checkCreateSeanceInput;

@Service
@Slf4j
public class RemoteDatabaseClient {

    private final JdbcTemplate jdbcTemplate;
    private final MainScheduleRepo mainScheduleRepo;
    private final SeancesPlacesRepo seancesPlacesRepo;

    public RemoteDatabaseClient(JdbcTemplate jdbcTemplate,
                                MainScheduleRepo mainScheduleRepo,
                                SeancesPlacesRepo seancesPlacesRepo) {
        this.jdbcTemplate = jdbcTemplate;
        this.mainScheduleRepo = mainScheduleRepo;
        this.seancesPlacesRepo = seancesPlacesRepo;
    }

    public List<AllSeancesView> getAllSeances() {
        String sqlQuery = String.format("select * from %s;", CONSTANTS.MAIN_SCHEDULE_VIEW_FULL);
        return jdbcTemplate.query(sqlQuery, new AllSeancesViewRowMapper());
    }

    public List<AllSeancesView> getAllSeancesForDate(LocalDate date) {
        String sqlQuery = String.format("select * from %s where seance_date = '%s'",
                CONSTANTS.MAIN_SCHEDULE_VIEW_FULL,
                date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return jdbcTemplate.query(sqlQuery, new AllSeancesViewRowMapper());
    }

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

    /**
     * Update seances for next days.
     */
    @Transactional
    public void updateScheduleTable(int days) {
        try {
            jdbcTemplate.execute(String.format("select %s.create_new_seances(%s);",
                    CONSTANTS.SCHEMA_NAME, days));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Get info about all places in hall for current seance.
     */
    public List<SeancePlacesEntity> getSeanceFullInfo(int seanceId) {
        log.info("Getting full info for seance id = " + seanceId);
        try {
            return seancesPlacesRepo.findAllBySeanceId(seanceId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Block/unblock the place in hall for current seance.
     */
    @Transactional
    public void blockOrUnblockPlaceOnSeance(BlockPlaceInput inputJson) {
        log.info("Blocking place in seance");
        try {
            seancesPlacesRepo.blockOrUnblockThePlace(inputJson.getSeanceId(), inputJson.getRow(),
                    inputJson.getPlace(), inputJson.getBlocked());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}