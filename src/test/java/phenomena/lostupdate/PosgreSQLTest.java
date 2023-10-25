package phenomena.lostupdate;

import org.junit.ClassRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import phenomena.TestImages;
import phenomena.writeskew.MySQLTest;

import java.sql.Connection;

public class PosgreSQLTest extends AbstractContainerDatabaseTest{

    private static final Logger logger = LoggerFactory.getLogger(MySQLTest.class);

    @ClassRule
    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>(TestImages.PostgresQL_9_IMAGE)
            .withLogConsumer(new Slf4jLogConsumer(logger))
            .withInitScript("table_create_inventory.sql");

    @Override
    protected int[] getIsolationLevels() {
        return new int[]{Connection.TRANSACTION_REPEATABLE_READ};
    }

    @Override
    protected JdbcDatabaseContainer<?> getContainer() {
        return container;
    }
}
