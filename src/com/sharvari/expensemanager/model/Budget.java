package com.sharvari.expensemanager.model;

public class Budget {

    private int userId;
    private Category category;
    private int month;
    private int year;
    private double limit;


    public Budget(int userId, Category category, int month, int year, double limit) {
        this.userId = userId;
        this.category = category;
        this.month = month;
        this.year = year;
        this.limit = limit;
    }


    public String toFileString() {
        return userId + "|" + category + "|" + month + "|" + year + "|" + limit;
    }

    public static Budget fromFileString(String line) {
        String[] p = line.split("\\|");
        return new Budget(
                Integer.parseInt(p[0]),
                Category.valueOf(p[1]),
                Integer.parseInt(p[2]),
                Integer.parseInt(p[3]),
                Double.parseDouble(p[4])
        );
    }

    public int getUserId() { return userId; }
    public Category getCategory() { return category; }
    public int getMonth() { return month; }
    public int getYear() { return year; }
    public double getLimit() { return limit; }


    @Override
    public String toString() {
        return String.format("Budget[%s | %02d/%d | Limit: %.2f]", category, month, year, limit);
    }


}
