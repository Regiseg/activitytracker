package activitytracker;

import org.flywaydb.core.Flyway;
import org.mariadb.jdbc.MariaDbDataSource;
import java.sql.*;
import java.time.LocalDateTime;


public class ActivityTrackerMain {


    public static void main(String[] args) {


        MariaDbDataSource dataSource;
        try {
            dataSource = new MariaDbDataSource();
            dataSource.setUrl("jdbc:mariadb://localhost:3306/activitytracker?useUnicode=true");
            dataSource.setUser("activitytracker");
            dataSource.setPassword("activitytracker");

        } catch (SQLException sqlException) {
            throw new IllegalStateException("Connection failed!", sqlException);
        }

       /*
        Activity activity = new Activity(LocalDateTime.of(2021, 02, 23, 10, 22), "Biking in Bakony", ActivityType.BIKING);
        Activity activity2 = new Activity(LocalDateTime.of(2021, 02, 23, 10, 22), "Hiking in Bakony", ActivityType.HIKING);
        Activity activity3 = new Activity(LocalDateTime.of(2021, 02, 23, 10, 22), "Running in Bakony", ActivityType.RUNNING);
*/


        Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.clean();
        flyway.migrate();

        ActivityDao activityDao = new ActivityDao(dataSource);

        System.out.println(activityDao.selectAvtivityById(2));
        System.out.println(activityDao.selectActivitiesByType(ActivityType.HIKING));
    }
}
