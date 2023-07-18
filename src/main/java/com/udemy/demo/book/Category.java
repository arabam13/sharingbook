package com.udemy.demo.book;


import org.springframework.data.annotation.Id;

public class Category {
    @Id
    private int id;

    private String label;

    public Category() {
    }

    public Category(String label) {
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}