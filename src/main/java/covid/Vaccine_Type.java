package covid;

public enum Vaccine_Type {
    SINOPHARM("elölt vírus"),
    PFIZER_BIONTECH("mRNS"),
    ASTRAZENECA("vektorvakvcina"),
    SPUTNIK_V("vektorvakcina"),
    MODERNA("mRNS");

    private String description;

    Vaccine_Type(String description) {
        this.description = description;
    }
}
