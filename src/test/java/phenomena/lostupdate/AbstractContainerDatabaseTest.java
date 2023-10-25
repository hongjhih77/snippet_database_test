package phenomena.lostupdate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.sql.*;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractContainerDatabaseTest {
    protected JdbcDatabaseContainer<?> container = getContainer();

    protected abstract int[] getIsolationLevels();

    protected abstract JdbcDatabaseContainer<?> getContainer();

    @BeforeEach
    public void before() {
        container.start();
    }

    @AfterEach
    public void afterAll() {
        container.close();
    }

    @ParameterizedTest
    @MethodSource("getIsolationLevels")
    public void test(int isolationLevel) throws SQLException {
        try (Connection connectionA = getConnection(container);
             Connection connectionB = getConnection(container)) {

            connectionA.setTransactionIsolation(isolationLevel);
            connectionB.setTransactionIsolation(isolationLevel);

            connectionA.setAutoCommit(false);
            connectionB.setAutoCommit(false);

            //then A getting quantity by id
            ResultSet A_get_1 = performQuery(connectionA, "SELECT quantity FROM inventory WHERE item_id = 1");
            A_get_1.next();
            int count_A_get_1 = A_get_1.getInt(1);
            assertThat(count_A_get_1).as("A transaction SELECT query succeeds").isEqualTo(10);

            //then B getting quantity by id
            ResultSet B_get_1 = performQuery(connectionB, "SELECT quantity FROM inventory WHERE item_id = 1");
            B_get_1.next();
            int count_B_get_1 = B_get_1.getInt(1);
            assertThat(count_B_get_1).as("A transaction SELECT query succeeds").isEqualTo(10);

            //then A update records
            performQuery(connectionA, "UPDATE inventory SET quantity = 6 WHERE item_id = 1");
            connectionA.commit();

            //then B update records
            performQuery(connectionB, "UPDATE inventory SET quantity = 9 WHERE item_id = 1");
            connectionB.commit();

            ResultSet A_get = performQuery(connectionA, "SELECT quantity FROM inventory WHERE item_id = 1");
            A_get.next();
            int count = A_get.getInt(1);
            assertThat(count).as("A transaction SELECT query on isolation level: " + isolationLevel).isEqualTo(6);
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
