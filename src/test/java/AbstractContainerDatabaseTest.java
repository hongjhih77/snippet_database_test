import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.sql.*;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractContainerDatabaseTest {

    protected JdbcDatabaseContainer<?> container = getContainer();

    protected abstract int getIsolationLevel();

    protected abstract JdbcDatabaseContainer<?> getContainer();

    @BeforeEach
    public void before() {
        container.start();
    }

    @AfterEach
    public void afterAll() {
        container.close();
    }

    @Test
    public void test() throws SQLException {
        try (Connection connectionA = getConnection(container);
             Connection connectionB = getConnection(container)) {

            connectionA.setTransactionIsolation(getIsolationLevel());
            connectionB.setTransactionIsolation(getIsolationLevel());

            connectionA.setAutoCommit(false);
            connectionB.setAutoCommit(false);

            //then A getting the records by a pivot time
            ResultSet A_get_1 = performQuery(connectionA, "SELECT COUNT(1) AS COUNTER FROM bill WHERE is_sync = 0 and issue_time <= 2");
            A_get_1.next();
            int count_A_get_1 = A_get_1.getInt(1);
            assertThat(count_A_get_1).as("A transaction SELECT query succeeds").isEqualTo(2);

            //then B insert one
            performQuery(connectionB, "INSERT INTO bill(issue_time) VALUES (2)");
            connectionB.commit();

            //then A update records
            performQuery(connectionA, "UPDATE bill SET is_sync = 1 WHERE is_sync = 0 and issue_time <= 2");
            connectionA.commit();

            ResultSet A_get = performQuery(connectionA, "SELECT COUNT(1) AS COUNTER FROM bill WHERE is_sync = 1");
            A_get.next();
            int count = A_get.getInt(1);
            assertThat(count).as("A transaction SELECT query succeeds").isEqualTo(2);
        }
    }

    protected ResultSet performQuery(Connection connection, String sql) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(sql);
        return statement.getResultSet();
    }

    protected Connection getConnection(JdbcDatabaseContainer<?> container) {
        try {
            Class.forName(container.getDriverClassName());
            return DriverManager.getConnection(
                    container.getJdbcUrl(), container.getUsername(), container.getPassword());
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
