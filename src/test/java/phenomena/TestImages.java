package phenomena;

import org.testcontainers.utility.DockerImageName;

public class TestImages {

    public static final DockerImageName MYSQL_57_IMAGE = DockerImageName.parse("mysql:5.7.34");

    public static final DockerImageName MYSQL_80_IMAGE = DockerImageName.parse("mysql:8.0.24");

    public static final DockerImageName PostgresQL_9_IMAGE = DockerImageName.parse("postgres:9.6.24");
}
