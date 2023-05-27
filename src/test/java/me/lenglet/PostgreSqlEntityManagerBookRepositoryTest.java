package me.lenglet;

import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static me.lenglet.DatabaseTestUtils.POSTGRES;
import static me.lenglet.DatabaseTestUtils.createSessionFactory;
import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.ext.ScriptUtils.runInitScript;

@Tag("contract")
@Testcontainers
class PostgreSqlEntityManagerBookRepositoryTest {

    @Container
    private static JdbcDatabaseContainer<?> postgreSQL = POSTGRES;

    private SessionFactory sessionFactory;

    @BeforeEach
    void setUp() {
        runInitScript(new JdbcDatabaseDelegate(this.postgreSQL, ""), "init.sql");
        this.sessionFactory = createSessionFactory(this.postgreSQL);
    }

    @AfterEach
    void tearDown() {
        if (this.sessionFactory != null) {
            this.sessionFactory.close();
        }
    }

    @Test
    void testFindByIdWhenEntityDoesNotExistReturnNull() {
        executeInTransaction(repository -> {
            final var book = repository.findById(22L);
            assertNull(book);
        });
    }

    @Disabled
    @Test
    void testFindAllWhenNoEntitiesReturnEmptySet() {
        executeInTransaction(repository -> {
            final var books = repository.findAll();
            assertNotNull(books);
            assertTrue(books.isEmpty());
        });
    }

    @Test
    void testPersist() {
        AtomicLong bookId = new AtomicLong();

        executeInTransaction(repository -> {
            final var book = new Book();
            book.setTitle("Jim Morrison");

            final var author = new Author();
            author.setName("Jack");
            author.setBooks(Set.of(book));

            book.setAuthor(author);

            repository.persist(book);

            assertNotNull(book.getId());
            bookId.set(book.getId());
        });

        executeInTransaction(repository -> {

            final var book = repository.findById(bookId.get());
            assertNotNull(book);

            final var books = repository.findAll();
            assertNotNull(books);
            assertEquals(3, books.size());
        });

    }

    @Test
    void test() {
        executeInTransaction((repository -> {
            final var projection = repository.findAllAuthorNameAndBookTitles();
            assertEquals(1, projection.size());
            assertEquals(2, projection.iterator().next().bookTitles().size());
        }));
    }

    private void executeInTransaction(Consumer<PostgreSqlEntityManagerBookRepository> repositoryConsumer) {
        executeInTransaction(this.sessionFactory, entityManager -> repositoryConsumer.accept(new PostgreSqlEntityManagerBookRepository(entityManager)));
    }

    private static void executeInTransaction(SessionFactory sessionFactory, Consumer<EntityManager> consumer) {
        final var session = sessionFactory.openSession();
        final var transaction = session.beginTransaction();

        consumer.accept(session.unwrap(EntityManager.class));

        session.flush();

        transaction.commit();
    }
}