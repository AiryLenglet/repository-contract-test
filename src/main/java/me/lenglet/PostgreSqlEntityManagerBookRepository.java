package me.lenglet;

import jakarta.persistence.EntityManager;
import org.hibernate.query.Query;
import org.hibernate.query.ResultListTransformer;
import org.hibernate.query.TupleTransformer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PostgreSqlEntityManagerBookRepository implements BookRepository {

    private final EntityManager entityManager;

    public PostgreSqlEntityManagerBookRepository(
            EntityManager entityManager
    ) {
        this.entityManager = entityManager;
    }

    @Override
    public void persist(Book book) {
        this.entityManager.persist(book);
    }

    @Override
    public Book findById(long id) {
        return this.entityManager.find(Book.class, id);
    }

    @Override
    public Set<Book> findAll() {
        return this.entityManager.createQuery("""
                        select b from Book b
                        """, Book.class)
                .getResultStream()
                .collect(Collectors.toSet());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<AuthorProjectionDto> findAllAuthorNameAndBookTitles() {
        final var transformer = new AuthorProjectionDtod();
        return (Set<AuthorProjectionDto>) this.entityManager.createQuery("""
                        select
                            a.id as author_id,
                            a.name as author_name,
                            b.title as book_title
                        from Author a
                        left join a.books b
                        """)
                .unwrap(Query.class)
                .setTupleTransformer(transformer)
                .setResultListTransformer(transformer)
                .getResultList()
                .stream()
                .collect(Collectors.toSet());
    }

    private static class AuthorProjectionDtod implements TupleTransformer<AuthorProjectionDto>, ResultListTransformer<AuthorProjectionDto> {

        @Override
        public List<AuthorProjectionDto> transformList(List<AuthorProjectionDto> resultList) {
            return resultList.stream()
                    .collect(Collectors.groupingBy(t -> t.id()))
                    .values()
                    .stream()
                    .flatMap(l -> l.stream()
                            .reduce( (a1, a2) -> new AuthorProjectionDto(
                                    a1.id(),
                                    a1.name(),
                                    Stream.concat(a1.bookTitles().stream(), a2.bookTitles().stream()).collect(Collectors.toSet())
                            )).stream())
                    .toList();
        }

        @Override
        public AuthorProjectionDto transformTuple(Object[] tuple, String[] aliases) {
            return new AuthorProjectionDto(
                    (Long) tuple[0],
                    (String) tuple[1],
                    Set.of((String) tuple[2])
            );
        }
    }


}
