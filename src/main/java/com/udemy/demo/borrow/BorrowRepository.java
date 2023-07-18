package com.udemy.demo.borrow;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jdbc.repository.query.Query;

import java.util.List;

@Repository
public interface BorrowRepository extends CrudRepository<Borrow, Integer> {
    // all borrows by borrower (userconnected)
    @Query("SELECT * FROM Borrow WHERE borrower_id=:borrowerId")
    List<Borrow> findByBorrowerId(Integer borrowerId);

     // all borrows by bookId 
    @Query("SELECT * FROM Borrow WHERE book_id=:bookId")
    List<Borrow> findByBookId(Integer bookId);

}