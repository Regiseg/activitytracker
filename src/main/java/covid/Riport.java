package covid;

public class Riport {

    private int nullVaccination;
    private int oneVaccination;
    private int twoVaccination;

    public int getNullVaccination() {
        return nullVaccination;
    }

    public int getOneVaccination() {
        return oneVaccination;
    }

    public int getTwoVaccination() {
        return twoVaccination;
    }

    public void NullVaccinationIncrement() {
        nullVaccination++;
    }

    public void OneVaccinationIncrement() {
        oneVaccination++;
    }

    public void TwoVaccinationIncrement() {
        twoVaccination++;
    }

    @Override
    public String toString() {
        return "Riport \n" +
                "Nincs oltása: " + nullVaccination + " főnek.\n" +
                "Egy oltást kapott: " + oneVaccination + " fő.\n" +
                "Két oltást kapott: " + twoVaccination + " fő.\n";
    }
}
