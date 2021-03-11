package covid;

import java.time.LocalDateTime;

public class Vaccine {

    private String taj;
    private LocalDateTime date;
    private VaccinationStatus status;
    private Vaccine_Type type;
    private String note;

    public Vaccine(String taj, LocalDateTime date, VaccinationStatus status, String note, Vaccine_Type type) {
        this.taj = taj;
        this.date = date;
        this.status = status;
        this.type = type;
        this.note = note;
    }

    public Vaccine(String taj, LocalDateTime date, VaccinationStatus status, Vaccine_Type type) {
        this.taj = taj;
        this.date = date;
        this.status = status;
        this.type = type;
    }

    public String getTaj() {
        return taj;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public VaccinationStatus getStatus() {
        return status;
    }

    public Vaccine_Type getType() {
        return type;
    }

    public String getNote() {
        return note;
    }

    @Override
    public String toString() {
        return "Vaccine{" +
                "taj='" + taj + '\'' +
                ", date=" + date +
                ", status=" + status +
                ", type=" + type +
                ", note='" + note + '\'' +
                '}';
    }
}
