package covid;


import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

public class Registration {

    public List<Citizen> getValidCitizensFromFile(String fileName) {
        Path file = Path.of(fileName);
        List<Citizen> result = new ArrayList<>();
        List<Citizen> wrong = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] lineArr = line.split(";");
                if (isvalidCitizen(lineArr)) {
                    result.add(new Citizen(lineArr[0], lineArr[1], Integer.parseInt(lineArr[2]), lineArr[3], lineArr[4]));
                } else {
                    wrong.add(new Citizen(lineArr[0], lineArr[1], Integer.parseInt(lineArr[2]), lineArr[3], lineArr[4]));
                }
            }
        } catch (IOException ioException) {
            throw new IllegalStateException("Cannot load", ioException);
        }
        System.out.println("Az alábbi személyek regisztrációja rossz adat miatt meghiúsult:");
        System.out.println(wrong);
        return result;
    }

    public boolean isvalidCitizen(String[] lineArr) {
        return (isValidName(lineArr[0]) && isValidZip(lineArr[1]) && isValidAge(lineArr[2]) && isValidEmail(lineArr[3]) && isvalidTAJ(lineArr[4]));
    }

    public Citizen getValidCitizenFromConsole(DataSource dataSource) {
        Map<String, List<String>> cities = new CovidDao(dataSource).loadCities();
        Scanner scanner = new Scanner(System.in);

        String name = readName(scanner);

        String zip = readZip(scanner);
        System.out.println(cities.get(zip));

        int age = readAge(scanner);

        String email = readEmail(scanner);

        String taj = readTAJ(scanner);

        return new Citizen(name, zip, age, email, taj);
    }


    private String readName(Scanner scanner) {
        System.out.println("Kérem a nevét:");
        String name = scanner.nextLine();
        while (!isValidName(name)) {
            System.out.println("A név nem lehet üres!");
            System.out.println("Kérem a nevét:");
            name = scanner.nextLine();
        }
        return name;
    }

    private boolean isValidName(String name) {
        return isNotEmpty(name);
    }

    private boolean isNotEmpty(String str) {
        return (str != null && !str.isBlank());
    }


    protected String readZip(Scanner scanner) {
        System.out.println("Kérem az irányítószámát:");
        String zip = scanner.nextLine();
        while (!isValidZip(zip)) {
            System.out.println("Nem irányítószám!");
            System.out.println("Kérem az irányítószámát:");
            zip = scanner.nextLine();
        }
        return zip;
    }

    private boolean isValidZip(String zip) {
        try {
            Integer.parseInt(zip);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }


    private int readAge(Scanner scanner) {
        System.out.println("Kérem az életkorát:");
        String age = scanner.nextLine();
        while (!isValidAge(age)) {
            System.out.println("Az életkor 10 és 150 év között kell legyen!");
            System.out.println("Kérem az életkorát:");
            age = scanner.nextLine();
        }
        return Integer.parseInt(age);
    }

    private boolean isValidAge(String age) {
        try {
            int temp = Integer.parseInt(age);
            if (temp > 10 && temp < 150) {
                return true;
            }
            return false;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }


    private String readEmail(Scanner scanner) {
        System.out.println("Kérem az email címét:");
        String email1 = scanner.nextLine();
        while (!isValidEmail(email1)) {
            System.out.println("Nem email cím!");
            System.out.println("Kérem az email címét:");
            email1 = scanner.nextLine();
        }

        System.out.println("Kérem az email címét ismét:");
        String email2 = scanner.nextLine();
        while (!email2.equals(email1)) {
            System.out.println("A két email cím nem egyezik!");
            System.out.println("Kérem az email címét ismét:");
            email2 = scanner.nextLine();
        }
        return email1;
    }

    private boolean isValidEmail(String email) {
        return (email.length() >= 3 && email.contains("@"));
    }


    public String readTAJ(Scanner scanner) {
        System.out.println("Kérem a TAJ számát:");
        String taj = scanner.nextLine();
        while (!isvalidTAJ(taj)) {
            System.out.println("Nem TAJ szám!");
            System.out.println("Kérem a TAJ számát:");
            taj = scanner.nextLine();
        }
        return taj;
    }

    private boolean isvalidTAJ(String taj) {
        if (taj.length() != 9) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            try {
                String str = taj.substring(i, i + 1);
                int digit = Integer.parseInt(str);
                if (i % 2 == 0) {
                    sum += digit * 3;
                } else {
                    sum += digit * 7;
                }
            } catch (NumberFormatException nfe) {
                return false;
            }
        }
        if (Integer.parseInt(taj.substring(8)) == sum % 10) {
            return true;
        }
        return false;
    }

    public void writeCitizensToFile(List<Citizen> citizens, String filename) {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filename))) {
            writer.write("Időpont;Név;Irányítószám;Életkor;E-mail cím; TAJ szám\n");
            for (int i = 0; i < citizens.size(); i++) {
                Citizen citizen = citizens.get(i);
                String time = i % 2 == 0 ? 8 + i / 2 + ":00" : 8 + i / 2 + ":30";
                writer.write(time + ";" + citizen.getName() + ";" + citizen.getZip() + ";" + citizen.getAge() + ";" + citizen.getEmail() + ";" + citizen.getTaj() + "\n");
            }
        } catch (IOException ioe) {
            throw new IllegalStateException("Can not write file", ioe);
        }
    }

    public Vaccine administrationVaccine(DataSource dataSource) {
        List<String> registratedTajNumbers = validRegistration();
        Scanner scanner = new Scanner(System.in);
        String taj = readRegistTAJ(registratedTajNumbers, scanner);

        int number_of_vaccination = new CovidDao(dataSource).infoBeforeVaccination(taj);
        checkBeforeVaccination(dataSource, number_of_vaccination, taj);

        LocalDateTime dateTime = LocalDateTime.now();
        VaccinationStatus status = readStatus(scanner);
        String note = checkStatus(scanner, status);
        Vaccine_Type type = (number_of_vaccination == 0 ? readType(scanner) : new CovidDao(dataSource).ifHasVaccination(taj));

        return new Vaccine(taj, dateTime, status, note, type);
    }

    private void checkBeforeVaccination(DataSource dataSource, int number, String taj) {
        if (number > 0) {
            System.out.println("Az első oltásnál kapott vakcina típusa:");
            System.out.println(new CovidDao(dataSource).ifHasVaccination(taj));
        }
        return;
    }

    private Vaccine_Type readType(Scanner scanner) {
        System.out.println("Milyen típusú vakcinával történt az oltás?");
        System.out.println("1. Sinopharm\n" +
                "2. Pfizer-Biontech\n" +
                "3. AstraZeneca\n" +
                "4. Sputnyik V\n" +
                "5. Moderna\n");
        int vac = 0;
        Vaccine_Type type = null;
        while (!(vac > 0 && vac < 6)) {
            try {
                vac = Integer.parseInt(scanner.nextLine());
                if (vac > 0 && vac < 6) {
                    type = selecType(vac);
                } else {
                    System.out.println("Kérem 1 és 5 közötti számot adjon meg!");
                    readType(scanner);
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Nyomatékosan kérem egy egész számot adjon meg 1 és 5 között!");
            } catch (IllegalArgumentException | ArithmeticException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return type;
    }

    private Vaccine_Type selecType(int number) {
        switch (number) {
            case 1: {
                return Vaccine_Type.SINOPHARM;
            }
            case 2: {
                return Vaccine_Type.PFIZER_BIONTECH;
            }
            case 3: {
                return Vaccine_Type.ASTRAZENECA;
            }
            case 4: {
                return Vaccine_Type.SPUTNIK_V;
            }
            case 5: {
                return Vaccine_Type.MODERNA;
            }
            default: {
                return null;
            }
        }
    }


    private String checkStatus(Scanner scanner, VaccinationStatus status) {
        String note = "";
        if (status == VaccinationStatus.FAILED) {
            System.out.println("Kérem írja le röviden, miért hiúsult meg az oltás:");
            note = scanner.nextLine();
        }
        return note;
    }

    private VaccinationStatus readStatus(Scanner scanner) {
        System.out.println("Sikeres volt az oltás? (I/N)");
        String temp = scanner.nextLine();
        VaccinationStatus status = temp.toLowerCase().equals("i") ? VaccinationStatus.SUCCESSFUL : VaccinationStatus.FAILED;
        return status;
    }

    private String readRegistTAJ(List<String> registratedTajNumbers, Scanner scanner) {
        String taj = readTAJ(scanner);
        while (!registratedTajNumbers.contains(taj)) {
            System.out.println("Nem regisztrált TAJ szám!");
            taj = readTAJ(scanner);
        }
        return taj;
    }

    public List<String> validRegistration() {
        List<String> result = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Path.of("citizens.csv"))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String taj = line.split(";")[5];
                result.add(taj);
            }
        } catch (IOException ioException) {
            throw new IllegalStateException("Cannot read", ioException);
        }
        return result;
    }

    /* Először fájlból töltöttem fel a Map-et, de alkalmazás közben adatbázisból dolgozunk nem fájlból.
        public Map<String, List<String>> loadMap() {
            Map<String, List<String>> cities = new HashMap<>();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(Registration.class.getResourceAsStream("zip2021.csv")))) {
                String line = br.readLine();
                while ((line = br.readLine()) != null) {
                    String zip = line.split(";")[0];
                    String city = line.split(";")[1].trim();
                    if (!cities.containsKey(zip)) {
                        cities.put(zip, new ArrayList<>());
                    }
                    cities.get(zip).add(city);
                }
            } catch (IOException ioException) {
                throw new IllegalStateException("Cannot load", ioException);
            }
            return cities;
        }


    // Be kellett töltenem a cities táblát Java-n keresztül, mert nem importálja ez a gép valamiért.
    public List<City> loadCityList() {
        List<City> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(RegistrationFromConsole.class.getResourceAsStream("zip2021.csv")))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] lineArr = line.split(";");
                String zip = lineArr[0];
                String city = lineArr[1].trim();
                if (lineArr.length == 3) {
                    city = city.concat("-").concat(lineArr[2]);
                }
                result.add(new City(zip, city));
            }
        } catch (IOException ioException) {
            throw new IllegalStateException("Cannot load", ioException);
        }
        return result;
    }

    public static void main(String[] args) {

        RegistrationFromConsole registrationFromConsole = new RegistrationFromConsole();
        List<City> cities = registrationFromConsole.loadCityList();

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/covid?useUnicode=true");
        dataSource.setUser("covid");
        dataSource.setPassword("covid");

        CovidDao covidDao = new CovidDao(dataSource);
        covidDao.createCities(cities);
    }
 */
}


