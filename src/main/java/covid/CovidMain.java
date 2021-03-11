package covid;
import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


public class CovidMain {

    private DataSource dataSource;

    public CovidMain(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void registrationACitizenFromConsole(DataSource dataSource) {
        Registration registration = new Registration();
        new CovidDao(dataSource).registrationOneCitizen(registration.getValidCitizenFromConsole(dataSource));
    }

    public void registrationCitizensFromFile(DataSource dataSource) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Kérem írja be a fájlnevet:");
        String filename = scanner.nextLine();

        Registration registration = new Registration();
        List<Citizen> citizenList = registration.getValidCitizensFromFile(filename);
        new CovidDao(dataSource).registrationCitizens(citizenList);
    }

    public void writeCitizensForVaccinationToFileByZip(DataSource dataSource) {
        Scanner scanner = new Scanner(System.in);
        String zip = new Registration().readZip(scanner);
        System.out.println("Kérem írja be a fájlnevet:");
        String filename = scanner.nextLine();

        CovidDao covidDao = new CovidDao(dataSource);
        List<Citizen> citizens = covidDao.getCitizensForVaccinationByZip(zip);
        new Registration().writeCitizensToFile(citizens, filename);
    }

    private void vaccination() {
        Vaccine vaccine = new Registration().administrationVaccine(dataSource);
        new CovidDao(dataSource).adminVaccination(vaccine);
    }

    private void riport() {
        HashMap<String, Riport> riport = new CovidDao(dataSource).processRiport();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Melyik irányitószám adataira kíváncsi?");
        String zip = scanner.nextLine();
        System.out.println(riport.get(zip));
    }

    public void menuToConsole() {
        System.out.println("1. Regisztráció\n" +
                "2. Tömeges regisztráció\n" +
                "3. Generálás\n" +
                "4. Oltás / Oltás meghiúsulás\n" +
                "5. Riport\n" + "\n" +
                "6. Kilépés");
    }

    public void runMenu() {
        Scanner scanner = new Scanner(System.in);
        int menuNumber = 0;
        while (menuNumber != 6) {
            menuToConsole();
            System.out.println("Kérem adja meg a menü számát:");
            try {
                menuNumber = Integer.parseInt(scanner.nextLine());
                executeMenu(menuNumber);
            } catch (NumberFormatException nfe) {
                System.out.println("Nyomatékosan kérem egy egész számot adjon meg 1 és 6 között!");
            } catch (IllegalArgumentException | ArithmeticException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void executeMenu(int menuNumber) {
        switch (menuNumber) {
            case 1: {
                registrationACitizenFromConsole(dataSource);
                return;
            }
            case 2: {
                registrationCitizensFromFile(dataSource);
                return;
            }
            case 3: {
                writeCitizensForVaccinationToFileByZip(dataSource);
                return;
            }
            case 4: {
                vaccination();
                return;
            }
            case 5: {
                riport();
            }
            case 6: {
                System.out.println("A viszon'lágytojás!");
                return;
            }
            default: {
                System.out.println("Nem létezik ilyen menüpont!");
            }
        }
    }


    public static void main(String[] args) {

        MariaDbDataSource dataSource ;
        try {
            dataSource = new MariaDbDataSource();
            dataSource.setUrl("jdbc:mariadb://localhost:3306/covid?useUnicode=true");
            dataSource.setUser("covid");
            dataSource.setPassword("covid");
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Connection failed!", sqlException);
        }


        CovidMain covidMain = new CovidMain(dataSource);


        covidMain.runMenu();


        //   System.out.println(new CovidDao(dataSource).infoBeforeVaccination("123458304"));
/*
        new CovidMain().writeCitizensForVaccinationToFileByZip(dataSource, "3400");

*/

/*
        new CovidMain().registrationCitizensFromFile(dataSource, "registrations.csv");
        new CovidMain().registrationACitizenFromConsole(dataSource);
*/

    }


}
