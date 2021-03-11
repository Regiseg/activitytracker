
package covid;

import java.time.LocalDateTime;

public class Citizen {

    private String name;
    private String zip;
    private int age;
    private String email;
    private String taj;
    private int numberOfVaccination = 0;
    private LocalDateTime last_vaccination;

    public Citizen() {
    }

    public Citizen(String name, String zip, int age, String email, String taj) {
        this.name = name;
        this.zip = zip;
        this.age = age;
        this.email = email;
        this.taj = taj;
        numberOfVaccination = 0;
        last_vaccination = null;
    }

    public String getName() {
        return name;
    }

    public String getZip() {
        return zip;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getTaj() {
        return taj;
    }

    public int getNumberOfVaccination() {
        return numberOfVaccination;
    }

    public LocalDateTime getLast_vaccination() {
        return last_vaccination;
    }

    public void giveVaccination() {
        numberOfVaccination++;
    }


    @Override
    public String toString() {
        return "Citizen{" +
                "name='" + name + '\'' +
                ", zip='" + zip + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", taj='" + taj + '\'' +
                ", numberOfVaccination=" + numberOfVaccination +
                ", last_vaccination=" + last_vaccination +
                '}';
    }

}
