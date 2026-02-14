package com.sharvari.expensemanager.model;

import java.time.LocalDate;

public class Expense {

    private int id;
    private String title;
    private double amount;
    private Category category;
    private LocalDate date;
    private String description;


    public Expense(int id, String title, double amount, Category category, LocalDate date, String description){
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
    }


    public int getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public double getAmount(){
        return amount;
    }

    public Category getCategory(){
        return category;
    }

    public LocalDate getDate(){
        return date;
    }

    public String getDescription(){
        return description;
    }


    @Override
    public String toString(){
        return id + "," + title + "," + amount + "," + category + "," + date + "," + description;
    }
}
