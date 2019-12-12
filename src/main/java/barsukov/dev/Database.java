package barsukov.dev;

import java.sql.*;

public class Database {
    /**
     * JDBC Driver and database url
     */
    private static       String DATABASE_URL = "jdbc:postgresql://127.0.0.1:5432/";

    /**
     * User and Password
     */
    private static final String USER     = "postgres";
    private static final String PASSWORD = "example";

    /**
     * Database props
     */
    private static final String DB_NAME     = "dins";
    private static final String SCHEMA_NAME = "traffic_limits";
    private static final String TABLE_NAME  = "limits_per_hour";

    public static void init() throws SQLException {
        init_db();
        createTable();
        setValues();
    }

    private static Connection createConnection(String db) throws SQLException {

        Connection connection;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return null;
        }

        System.out.println("Creating database connection...");
        connection = DriverManager.getConnection(DATABASE_URL + db, USER, PASSWORD);
        return connection;
    }

    private static int init_db() throws SQLException {
        Connection connection = createConnection("");
        Statement statement;
        statement = connection.createStatement();
        String sql;
        try {
            sql = "DROP DATABASE dins";
            statement.executeUpdate(sql);
        } catch (Exception e) {
            System.out.println("Database is not created");
        } finally {
            sql = "CREATE DATABASE dins";
            statement.executeUpdate(sql);
            System.out.println("Database created successfully...");
        }
        connection.close();
        return 0;
    }

    private static int createTable() throws SQLException {

        Connection connection = createConnection(DB_NAME);
        Statement statement;

        statement = connection.createStatement();

        String sql;
        try {
            sql = "CREATE SCHEMA IF NOT EXISTS " + SCHEMA_NAME;
            statement.executeUpdate(sql);
            sql = "CREATE TABLE traffic_limits.limits_per_hour (limit_name varchar(255), limit_value bigint, effective_date TIMESTAMPTZ DEFAULT Now())";
            statement.executeUpdate(sql);
        } catch (Exception e) {
            System.out.println("Table is not created");
            e.printStackTrace();
        }
        System.out.println("Table created");
        connection.close();
        return 0;
    }

    public static void setValues() throws SQLException {
        Connection connection = createConnection(DB_NAME);
        Statement statement = connection.createStatement();

        String sql = "INSERT INTO traffic_limits.limits_per_hour(limit_name, limit_value) VALUES ('min', 1024)";
        statement.executeUpdate(sql);
        sql = "INSERT INTO traffic_limits.limits_per_hour(limit_name, limit_value) VALUES ('max',1073741824)";
        statement.executeUpdate(sql);
        System.out.println("Data inserted");
        connection.close();
    }

    public static Long getMin() throws SQLException {
        Connection connection = createConnection(DB_NAME);
        Statement statement = connection.createStatement();

        String sql = "SELECT limit_value FROM traffic_limits.limits_per_hour where limit_name = 'min' order by limits_per_hour.effective_date DESC LIMIT 1";

        ResultSet rs = statement.executeQuery(sql);
        connection.close();
        rs.next();
        return rs.getLong(1);
    }

    public static Long getMax() throws SQLException {
        Connection connection = createConnection(DB_NAME);
        Statement statement = connection.createStatement();

        String sql = "SELECT limit_value FROM traffic_limits.limits_per_hour where limit_name = 'max' order by limits_per_hour.effective_date DESC LIMIT 1";

        ResultSet rs = statement.executeQuery(sql);
        connection.close();
        rs.next();
        return rs.getLong(1);
    }
}