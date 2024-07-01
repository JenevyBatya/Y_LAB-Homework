import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.example.enumManagment.ResponseEnum;
import org.example.managment.ConnectionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.example.managment.ConnectionManager.connection;

public class TestConnectionManager {
    private static final String URL = "jdbc:postgresql://localhost:5433/coworking_test";
    private static final String USER = "gosha";
    private static final String PASSWORD = "12345";
    public static Connection connection = null;

    public static void registeringConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("test_db.changelog/changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update();
            System.out.println("миграция успешна");
            ConnectionManager.setConnection(connection);
        } catch (SQLException e) {
            System.out.println(ResponseEnum.SQL_ERROR);
        } catch (LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }
}
