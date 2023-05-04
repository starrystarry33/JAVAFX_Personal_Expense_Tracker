package models;



import application.Utilities.LocalDateTimeFormatterHelper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private String id;

    private String description;

//    private Type type;
    private Category category;

    private Float amount;

    private LocalDateTime createdAt;

    private String userId;

//    public static enum Type {
//        INCOME, OUTCOME
//    }
    public static enum Category {
        // Income categories
        SALARY, INVESTMENT_INCOME, OTHER_INCOME,
        // Expense categories
        HOUSING, UTILITIES, GROCERIES, DINING, CLOTHING, ENTERTAINMENT, TRANSPORTATION, INSURANCE, HEALTH_WELLNESS, DIGITAL_PRODUCT, TRAVELING, OTHER_OUTCOME
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public Type getType() {
//        return type;
//    }
//
//    public void setType(Type type) {
//        this.type = type;
//    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    
    public float getAmount() {
        // Check if the category is an expense category
        if (getCategory() == Category.HOUSING || getCategory() == Category.UTILITIES || getCategory() == Category.GROCERIES ||
        	getCategory() == Category.DINING || getCategory() == Category.CLOTHING || getCategory() == Category.ENTERTAINMENT ||
        	getCategory() == Category.TRANSPORTATION || getCategory() == Category.INSURANCE || getCategory() == Category.HEALTH_WELLNESS ||
            getCategory() == Category.DIGITAL_PRODUCT || getCategory() == Category.TRAVELING || getCategory() == Category.OTHER_OUTCOME)
        {
        	return amount * -1;
        }

        return amount;
    }

//    public float getAmount() {
//        if(getType() == Type.OUTCOME) {
//            return amount * -1;
//        }
//
//        return amount;
//    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

//    public String toWriteable() {
//        return String.format("%s,%s,%s,%.2f,%s,%s", this.id, this.description, this.type.toString(), this.amount, LocalDateTimeFormatterHelper.format(this.createdAt), this.userId);
//    }
    public String toWriteable() {
        return String.format("%s,%s,%s,%.2f,%s,%s", this.id, this.description, this.category.toString(), this.amount, LocalDateTimeFormatterHelper.format(this.createdAt), this.userId);
    }
}