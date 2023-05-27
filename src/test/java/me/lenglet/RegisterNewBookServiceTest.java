package me.lenglet;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class RegisterNewBookServiceTest {

    @Test
    void testRegisterNewBook() {

        final var service = new RegisterNewBookService(new InMemoryBookRepository());
        final var bookId = service.execute("learning TDD");
        assertNotNull(bookId);

    }

    public static class InMemoryBookRepository implements BookRepository {

        private final AtomicLong idGenerator = new AtomicLong(1L);
        private final Map<Long, Book> books = new HashMap<>();

        @Override
        public void persist(Book book) {
            book.setId(this.idGenerator.getAndIncrement());
            this.books.put(book.getId(), book);
        }

        @Override
        public Book findById(long id) {
            return null;
        }

        @Override
        public Set<Book> findAll() {
            return null;
        }

        @Override
        public Set<AuthorProjectionDto> findAllAuthorNameAndBookTitles() {
            return null;
        }
    }
}