package activitytracker;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ActivityDao {

    private DataSource dataSource;

    public ActivityDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public Activity insertActivity(Activity activity) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO activities(start_time, activity_desc, activity_type) VALUES (?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setTimestamp(1, Timestamp.valueOf(activity.getStartime()));
            stmt.setString(2, activity.getDesc());
            stmt.setString(3, activity.getType().toString());
            stmt.executeUpdate();
            Activity result = getIdAfterExecuted(activity, stmt);
            insertActivityTrackPoints(activity.getTrackPoints(), result.getId());
            return result;

        } catch (SQLException sqlException) {
            throw new IllegalStateException("Connection failed", sqlException);
        }
    }

    private void insertActivityTrackPoints(List<TrackPoint> trackPoints, long activityId) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO track_points(act_time, lat, lon, activityId) VALUES (?,?,?,?)")
            ) {
                for (TrackPoint trackPoint : trackPoints) {
                    if (!isValidLatLon(trackPoint.getLat(), trackPoint.getLon())) {
                        throw new IllegalArgumentException("Invalid lat or lon");
                    }
                    stmt.setDate(1, Date.valueOf(trackPoint.getTime()));
                    stmt.setDouble(2, trackPoint.getLat());
                    stmt.setDouble(3, trackPoint.getLon());
                    stmt.setLong(4, activityId);
                    stmt.executeUpdate();
                }
                conn.commit();
            } catch (IllegalArgumentException iae) {
                conn.rollback();
                throw new IllegalArgumentException("Wrong trackpoint", iae);
            }
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Connection failed", sqlException);
        }
    }

    private boolean isValidLatLon(double lat, double lon) {
        if (lat > 90 || lat < -90 || lon > 180 || lon < -180) {
            return false;
        }
        return true;
    }

    private Activity getIdAfterExecuted(Activity activity, PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
                long id = rs.getLong(1);
                return new Activity(id, activity.getStartime(), activity.getDesc(), activity.getType());
            }
        }
        throw new IllegalStateException("Cannot get keys!");
    }

    public List<Activity> selectActivityBeforeDate(LocalDate date) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM activities WHERE start_time < ?");
        ) {
            stmt.setString(1, date.toString());
            /*
            LocalDateTime actualDate = date.atTime(0, 0, 0);
            stmt.setTimestamp(1, Timestamp.valueOf(actualDate));
            */
            return selectActivityByPreparedStatement(stmt);

        } catch (SQLException sqlException) {
            throw new IllegalStateException("Connection failed", sqlException);
        }

    }

    private List<Activity> selectActivityByPreparedStatement(PreparedStatement stmt) {
        List<Activity> result = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Activity activity = new Activity(
                        rs.getLong("id"),
                        rs.getTimestamp("start_time").toLocalDateTime(),
                        rs.getString("activity_desc"),
                        ActivityType.valueOf(rs.getString("activity_type")));
                result.add(activity);
            }
            return result;
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Can not execute", sqlException);
        }
    }
  private List<TrackPoint> selectTrackpointByPreparedStatement(PreparedStatement stmt) {
        List<TrackPoint> result = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                TrackPoint trackPoint = new TrackPoint(
                        rs.getLong("id"),
                        rs.getDate("act_time").toLocalDate(),
                        rs.getDouble("lat"),
                        rs.getDouble("lon")
                        );
                result.add(trackPoint);
            }
            return result;
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Can not execute", sqlException);
        }
    }

    public Activity selectAvtivityById(long id) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt1 = conn.prepareStatement("SELECT * FROM activities WHERE id = ?");
                PreparedStatement stmt2 = conn.prepareStatement("SELECT * FROM track_points WHERE activityId = ?")
        ) {
            stmt1.setLong(1, id);
            List<Activity> result = selectActivityByPreparedStatement(stmt1);
            if (result.size() == 1) {
                stmt2.setLong(1, id);
                List<TrackPoint> resultPoints = selectTrackpointByPreparedStatement(stmt2);
                result.get(0).addTrackPoints(resultPoints);
                return result.get(0);
            }
            throw new IllegalArgumentException("Wrong id");
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Connection failed", sqlException);
        }
    }

    public List<Activity> selectAllActivities() {
        List<Activity> result = new ArrayList<>();
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM activities")
        ) {
            return selectActivityByPreparedStatement(stmt);

        } catch (SQLException sqlException) {
            throw new IllegalStateException("Connection failed");
        }
    }

    public List<Activity> selectActivitiesByType(ActivityType activityType) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM activities WHERE activity_type = ?")
        ) {
            stmt.setString(1, activityType.toString());
            return selectActivityByPreparedStatement(stmt);


        } catch (SQLException sqlException) {
            throw new IllegalStateException("Connection failed", sqlException);
        }
    }
}
