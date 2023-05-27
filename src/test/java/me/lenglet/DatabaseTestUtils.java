package me.lenglet;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Properties;

public class DatabaseTestUtils {

    static { //runs when the main class is loaded.
        System.setProperty("org.jboss.logging.provider", "slf4j");
    }

    public static String DATABASE_NAME = "db-test";
    public static String DATABASE_USER = "jim";
    public static String DATABASE_PASSWORD = "jaguar";

    public static JdbcDatabaseContainer POSTGRES = new PostgreSQLContainer(DockerImageName.parse("postgres:15.2-alpine").asCompatibleSubstituteFor("postgres"))
            .withDatabaseName(DATABASE_NAME)
            .withUsername(DATABASE_USER)
            .withPassword(DATABASE_PASSWORD);

    public static SessionFactory createSessionFactory(JdbcDatabaseContainer<?> container) {
        final var properties = new Properties();
        properties.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        properties.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:" + container.getFirstMappedPort() + "/" + DATABASE_NAME);
        properties.setProperty("hibernate.connection.username", DATABASE_USER);
        properties.setProperty("hibernate.connection.password", DATABASE_PASSWORD);
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("show_sql", "true");


        final var sessionFactory = new Configuration()
                .addProperties(properties)
                .addAnnotatedClass(Book.class)
                .addAnnotatedClass(Author.class)
                .buildSessionFactory();
        return sessionFactory;
    }
}
