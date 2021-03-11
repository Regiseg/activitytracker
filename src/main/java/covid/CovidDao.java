package covid;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CovidDao {
    private DataSource dataSource;

    public CovidDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, List<String>> loadCities() {
        Map<String, List<String>> cities = new HashMap<>();
        try (
                Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT zip, city FROM cities;")
        ) {
            while (rs.next()) {
                String zip = rs.getString("zip");
                String city = rs.getString("city");
                if (!cities.containsKey(zip)) {
                    cities.put(zip, new ArrayList<>());
                }
                cities.get(zip).add(city);
            }
            return cities;
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Connection failed", sqlException);
        }

    }

    public void registrationOneCitizen(Citizen citizen) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO citizens(citizen_name, zip, age, email, taj, number_of_vaccination) VALUES (?,?,?,?,?,?)")) {
                ps.setString(1, citizen.getName());
                ps.setString(2, citizen.getZip());
                ps.setInt(3, citizen.getAge());
                ps.setString(4, citizen.getEmail());
                ps.setString(5, citizen.getTaj());
                ps.setInt(6, citizen.getNumberOfVaccination());
                ps.executeUpdate();

                conn.commit();
            } catch (IllegalArgumentException iae) {
                conn.rollback();
            }
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Cannot insert", sqlException);
        }
    }

    public void registrationCitizens(List<Citizen> citizens) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO citizens(citizen_name, zip, age, email, taj, number_of_vaccination) VALUES (?,?,?,?,?,?)")) {
                for (Citizen citizen : citizens) {
                    ps.setString(1, citizen.getName());
                    ps.setString(2, citizen.getZip());
                    ps.setInt(3, citizen.getAge());
                    ps.setString(4, citizen.getEmail());
                    ps.setString(5, citizen.getTaj());
                    ps.setInt(6, citizen.getNumberOfVaccination());
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (IllegalArgumentException iae) {
                conn.rollback();
            }
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Cannot insert", sqlException);
        }
    }

    public List<Citizen> getCitizensForVaccinationByZip(String zip) {
        List<Citizen> result = new ArrayList<>();

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT citizen_name, age, email, taj FROM citizens WHERE zip = ? AND number_of_vaccination = 0 OR last_vaccination < ? ORDER BY age DESC, citizen_name;")
        ) {
            LocalDateTime lastCheck = LocalDateTime.now().minusDays(15);
            ps.setString(1, zip);
            ps.setTimestamp(2, Timestamp.valueOf(lastCheck));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next() && result.size() < 16) {
                    String name = rs.getString("citizen_name");
                    int age = rs.getInt("age");
                    String email = rs.getString("email");
                    String taj = rs.getString("taj");
                    result.add(new Citizen(name, zip, age, email, taj));
                }
            } catch (SQLException sqlException) {
                throw new IllegalStateException("Cannot query", sqlException);
            }
            return result;
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Cannot connection", sqlException);
        }
    }

    public int infoBeforeVaccination(String taj) {
        int result = 0;
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT number_of_vaccination FROM citizens WHERE taj = ?;")
        ) {
            ps.setString(1, taj);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result = rs.getInt("number_of_vaccination");
                }
            } catch (SQLException sqlException) {
                throw new IllegalStateException("Cannot query", sqlException);
            }
            return result;

        } catch (SQLException sqlException) {
            throw new IllegalStateException("Connection failed", sqlException);
        }
    }

    public Vaccine_Type ifHasVaccination(String taj) {
        Vaccine_Type type = null;
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT vaccination_type FROM vaccinations WHERE taj = ?;")
        ) {
            ps.setString(1, taj);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    type = Vaccine_Type.valueOf(rs.getString("vaccination_type"));
                }
            } catch (SQLException sqlException) {
                throw new IllegalStateException("Cannot query", sqlException);
            }
            return type;

        } catch (SQLException sqlException) {
            throw new IllegalStateException("Connection failed", sqlException);
        }
    }

    public void adminVaccination(Vaccine vaccine) {
        try (Connection conn = dataSource.getConnection()) {
            processAdminVaccination(vaccine, conn);

        } catch (SQLException sqlException) {
            throw new IllegalStateException("Connection failed", sqlException);
        }
    }

    private void processAdminVaccination(Vaccine vaccine, Connection conn) throws SQLException {
        conn.setAutoCommit(false);
        try (
                PreparedStatement ps1 = conn.prepareStatement("INSERT INTO  vaccinations(taj, vaccination_date, vaccination_status, note, vaccination_type) VALUES (?,?,?,?,?);");
                PreparedStatement ps2 = conn.prepareStatement("UPDATE citizens SET last_vaccination = ?, number_of_vaccination = ? WHERE taj = ?;")
        ) {
            ps1.setString(1, vaccine.getTaj());
            ps1.setTimestamp(2, Timestamp.valueOf(vaccine.getDate()));
            ps1.setString(3, vaccine.getStatus().name());
            ps1.setString(4, vaccine.getNote());
            ps1.setString(5, vaccine.getType().name());
            ps1.executeUpdate();

            int numberOfVaccination = selectNumberOfVaccination(vaccine);
            ps2.setTimestamp(1, Timestamp.valueOf(vaccine.getDate()));
            ps2.setInt(2, numberOfVaccination);
            ps2.setString(3, vaccine.getTaj());
            ps2.executeUpdate();
            conn.commit();
        } catch (IllegalArgumentException iae) {
            conn.rollback();
        }
    }

    public int selectNumberOfVaccination(Vaccine vaccine) {
        int result = 0;
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT number_of_vaccination FROM citizens WHERE taj = ?;")
        ) {
            ps.setString(1, vaccine.getTaj());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result = rs.getInt("number_of_vaccination");
                }
            } catch (SQLException sqlException) {
                throw new IllegalStateException("Cannot query", sqlException);
            }
            return (vaccine.getStatus() == VaccinationStatus.SUCCESSFUL ? ++result : result);
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Connection failed", sqlException);
        }
    }

    public HashMap<String, Riport> processRiport() {
        HashMap<String, Riport> result = new HashMap<>();
        try (
                Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT zip, number_of_vaccination FROM citizens;")
        ) {
            while (rs.next()) {
                String zip = rs.getString("zip");
                int numberVac = rs.getInt("number_of_vaccination");
                if (!result.containsKey(zip)) {
                    result.put(zip, new Riport());
                }
                switch (numberVac){
                    case 0 -> result.get(zip).NullVaccinationIncrement();
                    case 1 -> result.get(zip).OneVaccinationIncrement();
                    case 2 -> result.get(zip).TwoVaccinationIncrement();
                }
                /*
                if (numberVac == 0) {
                    result.get(zip).NullVaccinationIncrement();
                }
                if (numberVac == 1) {
                    result.get(zip).OneVaccinationIncrement();
                }
                if (numberVac == 2) {
                    result.get(zip).TwoVaccinationIncrement();
                }*/
            }
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Connection failed", sqlException);
        }
        return result;
    }



    /* A cities tábla jdbc-n keresztül történt feltöltéséhez kellett, utána az sql parancssor ki lett exportálva flyway migration-ba
    public void createCities(List<City> cities) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO cities(zip, city) VALUES (?,?)")) {
                for (City city : cities) {
                    ps.setString(1, city.getZip());
                    ps.setString(2, city.getCity_name());
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (IllegalArgumentException iae) {
                conn.rollback();
            }
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Cannot insert", sqlException);
        }
    }
*/
}
