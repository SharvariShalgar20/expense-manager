package com.sharvari.expensemanager.model;

public enum Category {
    FOOD,
    TRAVEL,
    SHOPPING,
    BILLS,
    ENTERTAINMENT,
    HEALTH,
    EDUCATION,
    OTHER;

    public static Category fromString(String input){
        try{
            return Category.valueOf(input.trim().toUpperCase());
        }catch(IllegalArgumentException e){
            System.out.println("Invalid category, defaulting to Other");
            return OTHER;
        }
    }

}
