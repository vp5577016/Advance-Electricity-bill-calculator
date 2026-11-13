import java.sql.*;

public class schema {
    static final String URL  = "jdbc:mysql://localhost:3306/";
    static final String USER = "root";
    static final String PASS = "vishal@123";

    public static void create() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             Statement s = c.createStatement()) {
            s.executeUpdate("CREATE DATABASE IF NOT EXISTS electricitydb");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS electricitydb.bills ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "customer_id VARCHAR(50), customer_name VARCHAR(100), "
                    + "units DOUBLE, connection_type VARCHAR(20), total_amount DOUBLE)");
        }
    }
}