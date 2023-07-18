package com.udemy.demo.borrow;

import com.udemy.demo.book.Book;
import com.udemy.demo.user.UserInfo;
import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;

public class Borrow {

    @Id @JsonIgnore
    private int id;

    private LocalDate askDate;
    
    private LocalDate closeDate;
    private int borrowerId;
    @Transient
    private UserInfo borrower;

    private int lenderId;
    @Transient
    private UserInfo lender;
    public Integer bookId;

    @Transient
    public Book book;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getAskDate() {
        return askDate;
    }

    public void setAskDate(LocalDate askDate) {
        this.askDate = askDate;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(LocalDate closeDate) {
        this.closeDate = closeDate;
    }


    public int getBorrowerId() {
        return borrowerId;
    }
    public void setBorrowerId(int borrowerId) {
        this.borrowerId = borrowerId;
    }

    public UserInfo getBorrower() {
        return borrower;
    }
    public void setBorrower(UserInfo borrower) {
        this.borrower = borrower;
    }

    public int getLenderId() {
        return lenderId;
    }
    public void setLenderId(int lenderId) {
        this.lenderId = lenderId;
    }

    public UserInfo getLender() {
        return lender;
    }
    public void setLender(UserInfo lender) {
        this.lender = lender;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }


}