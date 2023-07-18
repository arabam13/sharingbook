package com.udemy.demo.book;

// import com.fasterxml.jackson.annotation.JsonCreator;
// import com.fasterxml.jackson.annotation.JsonIgnore;
// import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
// import com.udemy.demo.borrow.Borrow;
// import com.udemy.demo.json.AuthorDeserializer;
// import com.udemy.demo.json.CategoryDeserializer;
// import org.springframework.data.annotation.PersistenceCreator;
// import org.springframework.data.jdbc.core.mapping.AggregateReference;

import com.udemy.demo.user.UserInfo;
import jakarta.validation.constraints.NotBlank;

// import java.util.HashSet;
// import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

// @Entity
public class Book {

    @Id @JsonIgnore
    private int id;

    @NotBlank
    private String title;

    private BookStatus status;

    @Transient
    private UserInfo author;

    private int authorId;

    @Transient
    private Category category;
    // private AggregateReference<Category,Integer>  category;

    private int categoryId;

    // private final Set<Borrow> borrows = new HashSet<>();


    public Book(String title, int categoryId) {
        this.title = title;
        this.categoryId= categoryId;
    }

    // @PersistenceCreator
    // @JsonCreator
    // public Book(String title, BookStatus status, boolean deleted, AggregateReference<UserInfo,Integer> author, AggregateReference<Category,Integer>  category, Set<Borrow> borrows ) {
    //     this.title = title;
    //     this.status = status;
    //     this.deleted = deleted;
    //     this.author = author;
    //     this.category = category;
    //     //borrows.forEach(this::addBorrow);
    // }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public UserInfo getAuthor() {
        return author;
    }

    public void setAuthor(UserInfo user) {
        this.author = user;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }


    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }

    // public void addBorrow(Borrow borrow) {
    //     borrows.add(borrow);
    //     borrow.book = this;
    // }

    // public Set<Borrow> getBorrows() {
    //     return borrows;
    // }


}