package com.udemy.demo.book;


import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
@Repository
public interface BookRepository extends CrudRepository<Book, Integer> {
    @Query("SELECT * FROM BOOK WHERE status=:status and author_id <> :userId")
    List<Book> findByStatusAndAuthorIdNot(BookStatus status, Integer userId);

    @Query("SELECT * FROM BOOK WHERE author_id = :userId")
    List<Book> findByAuthorId(Integer userId);

}
