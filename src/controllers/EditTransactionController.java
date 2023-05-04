package controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.time.LocalDate;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Transaction;
import Services.TransactionService;
import javafx.scene.control.DatePicker;



public class EditTransactionController {

    private TransactionService transactionService = new TransactionService();
    
    @FXML
    private TextField descriptionField;

    @FXML
    private TextField amountField;
    
    @FXML
    private DatePicker editDatePicker;

    @FXML
    private ComboBox<Transaction.Category> categoryOptions;
    
    private Transaction transaction;
    
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
        descriptionField.setText(transaction.getDescription());
        amountField.setText(String.valueOf(transaction.getAmount()));
        categoryOptions.setItems(FXCollections.observableArrayList(Transaction.Category.values()));
        categoryOptions.setValue(transaction.getCategory());
        editDatePicker.setValue(transaction.getCreatedAt().toLocalDate());
    }

    @FXML
    public void save(ActionEvent event) {
        String description = descriptionField.getText();
        Float amount = Float.valueOf(amountField.getText());
        Transaction.Category category = categoryOptions.getValue();
        LocalDate date = editDatePicker.getValue();
        
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setCategory(category);
        transaction.setCreatedAt(date.atStartOfDay());
        
        // transactionService.update(transaction);
        transactionService.update(transaction.getId(), transaction.getDescription(), transaction.getAmount(), 
        		transaction.getCategory(), transaction.getCreatedAt());

        
        Stage stage = (Stage) descriptionField.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cancel(ActionEvent event) {
        Stage stage = (Stage) descriptionField.getScene().getWindow();
        stage.close();
    }
}

