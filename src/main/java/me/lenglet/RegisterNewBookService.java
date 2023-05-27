package me.lenglet;

import jakarta.transaction.Transactional;

public class RegisterNewBookService {

    private final BookRepository bookRepository;

    public RegisterNewBookService(
            BookRepository bookRepository
    ) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public long execute(String title) {
        final var book = new Book();
        book.setTitle(title);
        this.bookRepository.persist(book);
        return book.getId();
    }
}
