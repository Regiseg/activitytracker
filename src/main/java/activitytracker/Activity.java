package activitytracker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Activity {
    private long id;
    private LocalDateTime startime;
    private String desc;
    private ActivityType type;
    private List<TrackPoint> trackPoints = new ArrayList<>();

    public Activity(long id, LocalDateTime startime, String desc, ActivityType type) {
        this.id = id;
        this.startime = startime;
        this.desc = desc;
        this.type = type;
    }

    public Activity(LocalDateTime startime, String desc, ActivityType type) {
        this.startime = startime;
        this.desc = desc;
        this.type = type;
    }

    public void addTrackPoint(TrackPoint trackPoint) {
        trackPoints.add(trackPoint);
    }

    public void addTrackPoints(List<TrackPoint> trackPoints){
        this.trackPoints.addAll(trackPoints);
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getStartime() {
        return startime;
    }

    public String getDesc() {
        return desc;
    }

    public ActivityType getType() {
        return type;
    }

    public List<TrackPoint> getTrackPoints() {
        return new ArrayList<>(trackPoints);
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", startime=" + startime +
                ", desc='" + desc + '\'' +
                ", type=" + type +
                ", trackPoints=" + trackPoints +
                '}';
    }
}
