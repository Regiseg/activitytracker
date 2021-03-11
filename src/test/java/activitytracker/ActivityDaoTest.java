package activitytracker;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActivityDaoTest {
    ActivityDao activityDao;

    @BeforeEach
    public void init() {
        MariaDbDataSource dataSource;
        try {
            dataSource = new MariaDbDataSource();
            dataSource.setUrl("jdbc:mariadb://localhost:3306/activitytracker?useUnicode=true");
            dataSource.setUser("activitytracker");
            dataSource.setPassword("activitytracker");

        } catch (SQLException sqlException) {
            throw new IllegalStateException("Connection failed!", sqlException);
        }

        Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.clean();
        flyway.migrate();

        activityDao = new ActivityDao(dataSource);
    }

    @Test
    public void selectAllTest() {
        assertEquals(3, activityDao.selectAllActivities().size());
    }

    @Test
    public void insertActivity() {
        Activity activity = new Activity(LocalDateTime.of(2021, 02, 23, 10, 22), "Hiking in Bakony", ActivityType.HIKING);
        activityDao.insertActivity(activity);
        assertEquals(4, activityDao.selectAllActivities().size());
    }

    @Test
    public void selectBeforeDateTest() {
        System.out.println(activityDao.selectActivityBeforeDate(LocalDate.of(2020, 03, 15)));
    }

    @Test
    public void findByIdTest() {
        Activity activity = new Activity(LocalDateTime.of(2021, 02, 23, 10, 22), "Hiking in Bakony", ActivityType.HIKING);
        Activity result = activityDao.insertActivity(activity);

        assertEquals(activity.getStartime(), activityDao.selectAvtivityById(result.getId()).getStartime());
        assertEquals(activity.getDesc(), activityDao.selectAvtivityById(result.getId()).getDesc());
        assertEquals(activity.getType(), activityDao.selectAvtivityById(result.getId()).getType());
        System.out.println(activityDao.selectAvtivityById(result.getId()).getId());
        System.out.println(activityDao.selectAvtivityById(result.getId()).getStartime());
        System.out.println(activityDao.selectAvtivityById(result.getId()).getDesc());
        System.out.println(activityDao.selectAvtivityById(result.getId()).getType());
    }

    @Test
    public void insertActivityWithTrackPointsTest() {
        Activity activity = new Activity(LocalDateTime.of(2021, 02, 23, 10, 22), "Hiking in", ActivityType.HIKING);
        activity.addTrackPoints(List.of(
                new TrackPoint(LocalDate.of(2021, 2, 23), 67, 23),
                new TrackPoint(LocalDate.of(2021, 2, 23), 68, 22),
                new TrackPoint(LocalDate.of(2021, 2, 23), 68, 21)
        ));

        Activity ac = activityDao.insertActivity(activity);

        System.out.println(activityDao.selectAvtivityById(ac.getId()));


    }

}