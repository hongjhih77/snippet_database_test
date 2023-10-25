package phenomena.lostupdate;

import org.junit.ClassRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import phenomena.TestImages;

import java.sql.Connection;

public class MySQLTest extends AbstractContainerDatabaseTest{

    private static final Logger logger = LoggerFactory.getLogger(phenomena.writeskew.MySQLTest.class);

    @ClassRule
    public static MySQLContainer<?> container = new MySQLContainer<>(TestImages.MYSQL_80_IMAGE)
            .withLogConsumer(new Slf4jLogConsumer(logger))
            .withInitScript("table_create_inventory.sql");
    @Override
    protected int[] getIsolationLevels() {
        return new int[]{Connection.TRANSACTION_REPEATABLE_READ, Connection.TRANSACTION_SERIALIZABLE};
    }

    @Override
    protected JdbcDatabaseContainer<?> getContainer() {
        return container;
    }
}
