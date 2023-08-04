import org.junit.ClassRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.sql.Connection;

public class MySQLTest extends AbstractContainerDatabaseTest {

    private static final Logger logger = LoggerFactory.getLogger(MySQLTest.class);

    @ClassRule
    public static MySQLContainer<?> container = new MySQLContainer<>(TestImages.MYSQL_57_IMAGE)
            .withLogConsumer(new Slf4jLogConsumer(logger))
            .withInitScript("table_create.sql");

    @Override
    protected int getIsolationLevel() {
        return Connection.TRANSACTION_REPEATABLE_READ;
    }

    @Override
    protected JdbcDatabaseContainer<?> getContainer() {
        return container;
    }
}
