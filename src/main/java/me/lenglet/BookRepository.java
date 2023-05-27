package me.lenglet;

import java.util.Set;

public interface BookRepository {

    void persist(Book book);

    Book findById(long id);

    Set<Book> findAll();

    Set<AuthorProjectionDto> findAllAuthorNameAndBookTitles();

    record AuthorProjectionDto(
            Long id,
            String name,
            Set<String> bookTitles
    ) {
    }
}
