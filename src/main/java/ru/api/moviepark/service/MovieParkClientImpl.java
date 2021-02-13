package ru.api.moviepark.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.api.moviepark.data.dto.MainScheduleDTO;
import ru.api.moviepark.data.entities.HallsEntity;
import ru.api.moviepark.data.entities.MainScheduleEntity;
import ru.api.moviepark.data.entities.MoviesEntity;
import ru.api.moviepark.data.entities.SeancePlacesEntity;
import ru.api.moviepark.data.repositories.HallsRepo;
import ru.api.moviepark.data.repositories.MainScheduleRepo;
import ru.api.moviepark.data.repositories.MoviesRepo;
import ru.api.moviepark.data.repositories.SeancesPlacesRepo;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;
import ru.api.moviepark.util.InputPreconditionsUtil;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static ru.api.moviepark.config.CacheConfig.*;
import static ru.api.moviepark.data.entities.MainScheduleEntity.createMainScheduleEntity;
import static ru.api.moviepark.env.Constants.SCHEMA_NAME;
import static ru.api.moviepark.util.InputPreconditionsUtil.checkCreateSeanceInput;

@Service
@Slf4j
public class MovieParkClientImpl implements MovieParkClient {

    private final JdbcTemplate jdbcTemplate;
    private final HallsRepo hallsRepo;
    private final MainScheduleRepo mainScheduleRepo;
    private final SeancesPlacesRepo seancesPlacesRepo;
    private final MoviesRepo moviesRepo;

    @Resource
    private MovieParkClientImpl self;

    public MovieParkClientImpl(JdbcTemplate jdbcTemplate,
                               HallsRepo hallsRepo,
                               MainScheduleRepo mainScheduleRepo,
                               SeancesPlacesRepo seancesPlacesRepo,
                               MoviesRepo moviesRepo) {
        this.jdbcTemplate = jdbcTemplate;
        this.hallsRepo = hallsRepo;
        this.mainScheduleRepo = mainScheduleRepo;
        this.seancesPlacesRepo = seancesPlacesRepo;
        this.moviesRepo = moviesRepo;
    }

    @Override
    @Cacheable(cacheNames = SEANCE_INFO_CACHE_BY_ID)
    public MainScheduleDTO getSeanceById(int seanceId) {
        log.info("Getting seance by seance id = " + seanceId);
        InputPreconditionsUtil.checkSeanceIdExists(seanceId);
        return mainScheduleRepo.findById(seanceId).get().convertToDto();
    }

    @Cacheable(cacheNames = MOVIES_INFO_CACHE)
    public MoviesEntity getMovieById(int id) {
        log.info("get movie info by id = " + id);
        return moviesRepo.findById(id).get();
    }

    @Override
    public Map<Integer, MoviesEntity> getAllMoviesByIdSet(Set<Integer> idList) {
        Map<Integer, MoviesEntity> result = new HashMap<>();
        idList.forEach(id -> result.put(id, self.getMovieById(id)));
        return result;
    }

    @Override
    @Cacheable(value = HALLS_INFO_CACHE)
    public List<HallsEntity> getHallPlacesInfo(int hallId) {
        log.info("Getting all places for hall id = " + hallId);
        InputPreconditionsUtil.checkHallIdExists(hallId);
        return hallsRepo.findAllByHallId(hallId).get();
    }

    @Override
    @Cacheable(value = SEANCE_PLACES_CACHE)
    public List<SeancePlacesEntity> getSeancePlacesInfo(int seanceId) {
        log.info("Getting full info for seance id = " + seanceId);
        InputPreconditionsUtil.checkSeanceIdExists(seanceId);
        return seancesPlacesRepo.findAllBySeanceId(seanceId);
    }

    @Override
    @Cacheable(value = SEANCE_INFO_CACHE_BY_DATE)
    public List<MainScheduleDTO> getAllSeancesByDate(LocalDate date) {
        List<MainScheduleEntity> entities = mainScheduleRepo.findAllBySeanceDate(date);
        return MainScheduleEntity.convertToDtoList(entities);
    }

    @Override
    public List<MainScheduleDTO> getAllSeancesByPeriod(LocalDate periodStart,
                                                       LocalDate periodEnd) {
        List<MainScheduleDTO> result = new ArrayList<>();
        while (periodStart.isBefore(periodEnd.plusDays(1))) {
            result.addAll(getAllSeancesByDate(periodStart));
            periodStart = periodStart.plusDays(1);
        }

        return result;
    }

    @Override
    @Cacheable(value = MOVIES_INFO_BY_DATE_CACHE)
    public Map<Integer, String> getAllMoviesByDate(LocalDate date) {
        List<MainScheduleDTO> entities = getAllSeancesByDate(date);
        Map<Integer, String> result = new HashMap<>();
        entities.forEach(dto -> result.putIfAbsent(dto.getMovieId(), dto.getMovieName()));

        return result;
    }

    @Override
    public Map<LocalDate, Map<Integer, String>> getAllMoviesByPeriod(LocalDate periodStart,
                                                                     LocalDate periodEnd) {
        Map<LocalDate, Map<Integer, String>> result = new HashMap<>();
        while (periodStart.isBefore(periodEnd.plusDays(1))) {
            result.put(periodStart, self.getAllMoviesByDate(periodStart));
            periodStart = periodStart.plusDays(1);
        }
        return result;
    }

    @Override
    public Map<String, List<MainScheduleDTO>> getAllSeancesByMovieAndDateGroupByMoviePark(
            int movieId, LocalDate date) {
        Map<String, List<MainScheduleDTO>> result = new HashMap<>();

        List<MainScheduleDTO> seancesByPeriod = getAllSeancesByPeriod(date, date.plusDays(1));
        seancesByPeriod.stream()
                .filter(dto -> dto.getSeanceDate().equals(date)
                        && dto.getStartTime().isAfter(LocalTime.of(6, 0)) ||
                        dto.getSeanceDate().equals(date.plusDays(1))
                                && dto.getStartTime().isBefore(LocalTime.of(6, 0)))
                .filter(dto -> dto.getMovieId() == movieId)
                .forEach(dto -> {
                    String movieParkName = dto.getMovieParkName();
                    result.putIfAbsent(movieParkName, new ArrayList<>());
                    result.get(movieParkName).add(dto);
                });

        return result;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public synchronized void createNewSeance(CreateSeanceInput inputJson) {
        checkCreateSeanceInput(inputJson);
        MainScheduleEntity newSeanceEntity = createMainScheduleEntity(inputJson);
        mainScheduleRepo.save(newSeanceEntity);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteSeance(int seanceId) {
        InputPreconditionsUtil.checkSeanceIdExists(seanceId);
        mainScheduleRepo.deleteById(seanceId);
        seancesPlacesRepo.deleteAllBySeanceId(seanceId);
    }

    @Override
    @Transactional
    public void updateScheduleTable(int days) {
        try {
            jdbcTemplate.execute(String.format("select %s.create_new_seances(%s);", SCHEMA_NAME, days));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = SEANCE_PLACES_CACHE, key = "#seanceId")
    public synchronized void blockOrUnblockPlaceOnSeance(int seanceId, List<Integer> placeList,
                                                         boolean blocked) {
        InputPreconditionsUtil.checkSeanceIdExists(seanceId);
        log.info("Updating places {} in seance {}. Set blocked value: {}", placeList, seanceId, blocked);
        seancesPlacesRepo.blockOrUnblockThePlace(seanceId, placeList, blocked);
    }
}
