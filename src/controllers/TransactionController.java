package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.Modality;
import javafx.util.Callback;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;



import models.Transaction;
import models.Transaction.Category;
import models.UserSession;
import Services.TransactionService;
import application.Utilities.AlertHelper;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.Month;


public class TransactionController implements Initializable {

    private TransactionService transactionService = new TransactionService();

    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    @FXML
    private TableView<Transaction> tableView;

    @FXML
    private Label lblUser;

    @FXML
    private Label lblTotal;

    @FXML
    private ComboBox<Transaction.Category> categoryOptions;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField amountField;

    @FXML
    private Button addButton;

    @FXML
    private Button searchButton;
    
    @FXML
    private DatePicker datePicker;

    @FXML
    private DatePicker editDatePicker;
    
    @FXML
    private TableColumn<Transaction, String> editColumn;
    

    @FXML
    private TableColumn<Transaction, Void> deleteColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            populateTableItems();
            populateTransactionTypeOptions();
            setTotalLabelText();
            setLoggedInLabelText();
            editColumn.setCellFactory(editButtonTableCell(this));
            deleteColumn.setCellFactory(deleteButtonTableCell());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addTransaction(ActionEvent event) throws IOException {
        Window owner = addButton.getScene().getWindow();

        if (descriptionField.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Please enter a description");
            return;
        }

        if (amountField.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Please enter an amount");
            return;
        }

        if (categoryOptions.getValue() == null) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Select your transaction category");
            return;
        }
        if (datePicker.getValue() == null) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Please select a date");
            return;
        }

        String description = descriptionField.getText();
      
//        Float amount = Float.valueOf(amountField.getText());
////        Transaction.Type type = typeOptions.getValue();
//        Transaction.Category category = categoryOptions.getValue();
//        LocalDate date = datePicker.getValue();
//
//
////        Transaction transaction = transactionService.create(description, amount, category, UserSession.getInstance().getUser().getId(),date);
//////        transaction.setCreatedAt(date.atStartOfDay());
////        transactions.add(transaction);
//        Transaction transaction = transactionService.create(description, amount, category, UserSession.getInstance().getUser().getId(), date);
//        transactions.add(transaction);
//
//        setTotalLabelText();
//        clearInputs();
        try {
            Float amount = Float.valueOf(amountField.getText());
            if (amount < 0) {
                throw new NumberFormatException("Please enter a correct amount");
            }

            Transaction.Category category = categoryOptions.getValue();
            LocalDate date = datePicker.getValue();

            Transaction transaction = transactionService.create(description, amount, category, UserSession.getInstance().getUser().getId(), date);
            transactions.add(transaction);
            // sort transactions based on the transaction date
            Collections.sort(transactions, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction t1, Transaction t2) {
                    return t2.getCreatedAt().toLocalDate().compareTo(t1.getCreatedAt().toLocalDate());
                }
            });
            tableView.refresh();

            setTotalLabelText();
            clearInputs();
        } catch (NumberFormatException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Please enter a valid, non-negative amount");
        }
    }
    
    @FXML
    public void editTransaction(ActionEvent event) {
        // Get the selected transaction from the TableView
        Transaction transaction = tableView.getSelectionModel().getSelectedItem();
        if (transaction == null) {
            // No transaction is selected
            return;
        }

        // Open a new window or dialog for editing the transaction
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edit_transaction.fxml"));
            Parent root = loader.load();
            EditTransactionController editTransactionController = loader.getController();
            editTransactionController.setTransaction(transaction);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Transaction");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Refresh the TableView after editing
            refresh(null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Callback<TableColumn<Transaction, String>, TableCell<Transaction, String>> editButtonTableCell(TransactionController transactionController) {
        return param -> new TableCell<Transaction, String>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    transactionController.editTransaction(new ActionEvent());
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        };
    }
    
    private Callback<TableColumn<Transaction, Void>, TableCell<Transaction, Void>> deleteButtonTableCell() {
        return param -> new TableCell<Transaction, Void>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    deleteTransaction(transaction);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        };
    }

    
//    @FXML
//    public void deleteTransaction(ActionEvent event) throws IOException {
//        Transaction transaction = tableView.getSelectionModel().getSelectedItem();
//
//        transactions.remove(transaction);
//        transactionService.delete(transaction.getId());
//
//        setTotalLabelText();
//        clearInputs();
//    }
    
    private void deleteTransaction(Transaction transaction) {
        transactions.remove(transaction);
        transactionService.delete(transaction.getId());

        setTotalLabelText();
        clearInputs();
    }

    @FXML   
	public void searchTransaction(ActionEvent event) throws IOException {
        String description = descriptionField.getText();
        Category category = categoryOptions.getValue();
        
        transactions = FXCollections.observableArrayList(transactionService.search(description, category));
        tableView.setItems(transactions);
        
        // sort transactions based on the transaction date
        Collections.sort(transactions, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction t1, Transaction t2) {
                return t2.getCreatedAt().toLocalDate().compareTo(t1.getCreatedAt().toLocalDate());
            }
        });

        setTotalLabelText();
    }

/*
    @FXML
    public void searchTransaction(ActionEvent event) throws IOException {
        String description = descriptionField.getText();
        Category category = categoryOptions.getValue();
        LocalDate date = editDatePicker.getValue();
        LocalDate startDate = date.withDayOfMonth(1);
        LocalDate endDate = date.plusMonths(1).withDayOfMonth(1).minusDays(1);

        transactions = FXCollections.observableArrayList(transactionService.search(description, category, startDate, endDate));
        tableView.setItems(transactions);

        setTotalLabelText();
    }
*/
    
    @FXML
    public void refresh(ActionEvent event) throws IOException {
        transactions = FXCollections.observableArrayList();

        populateTableItems();
        // sort transactions based on the transaction date
        Collections.sort(transactions, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction t1, Transaction t2) {
                return t2.getCreatedAt().toLocalDate().compareTo(t1.getCreatedAt().toLocalDate());
            }
        });
        populateTransactionTypeOptions();
        setTotalLabelText();
        setLoggedInLabelText();
        setTotalLabelText();
        clearInputs();
    }

    @FXML
    public void goToPieChart(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/pie_chart.fxml"));
        Parent root = (Parent) loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }
    
    @FXML   
    public void backLogin(ActionEvent event) throws IOException {
    	backLogin((Stage) ((Node) event.getSource()).getScene().getWindow());
    }
    
    public void backLogin(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Scene scene = new Scene(loader.load(), 800, 500);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }
    
//    public Callback<TableColumn<Transaction, Void>, TableCell<Transaction, Void>> editButtonTableCell() {
//        return param -> new EditButtonTableCell<>(transaction -> {
//            // Your edit transaction logic here
//            System.out.println("Editing: " + transaction.getDescription());
//            return null;
//        });
//    }
//    
    private void populateTableItems() throws IOException {
        transactions.addAll(transactionService.getByUserId(UserSession.getInstance().getUser().getId()));

//        tableView.setRowFactory(new Callback<TableView<Transaction>, TableRow<Transaction>>() {
//            @Override
//            public TableRow<Transaction> call(TableView<Transaction> tableView) {
//                final TableRow<Transaction> row = new TableRow<Transaction>() {
//                    @Override
//                    protected void updateItem(Transaction data, boolean empty) {
//                        super.updateItem(data, empty);
//                        if (data != null && data.getType().equals(Transaction.Type.INCOME)) {
//                            setStyle("-fx-text-background-color: green;");
//                        } else {
//                            setStyle("-fx-text-background-color: red;");
//                        }
//                    }
//                };
//
//                return row;
//            }
//        });
        
        tableView.setRowFactory(new Callback<TableView<Transaction>, TableRow<Transaction>>() {
            @Override
            public TableRow<Transaction> call(TableView<Transaction> tableView) {
                final TableRow<Transaction> row = new TableRow<Transaction>() {
                    @Override
                    protected void updateItem(Transaction data, boolean empty) {
                        super.updateItem(data, empty);
                        if (data != null && data.getCategory() != null) {
                            switch (data.getCategory()) {
                                case SALARY:
                                    setStyle("-fx-text-background-color: green;");
                                    break;
                                case INVESTMENT_INCOME:
                                    setStyle("-fx-text-background-color: green;");
                                    break;
                                case OTHER_INCOME:
                                    setStyle("-fx-text-background-color: green;");
                                    break;
                                case HOUSING:
                                    setStyle("-fx-text-background-color: red;");
                                    break;
                                case UTILITIES:
                                    setStyle("-fx-text-background-color: red;");
                                    break;
                                case GROCERIES:
                                    setStyle("-fx-text-background-color: red;");
                                    break;
                                case DINING:
                                    setStyle("-fx-text-background-color: red;");
                                    break;
                                case CLOTHING:
                                    setStyle("-fx-text-background-color: red;");
                                    break;
                                case ENTERTAINMENT:
                                    setStyle("-fx-text-background-color: red;");
                                    break;
                                case TRANSPORTATION:
                                    setStyle("-fx-text-background-color: red;");
                                    break;
                                case INSURANCE:
                                    setStyle("-fx-text-background-color: red;");
                                    break;
                                case HEALTH_WELLNESS:
                                    setStyle("-fx-text-background-color: red;");
                                    break;
                                case DIGITAL_PRODUCT:
                                    setStyle("-fx-text-background-color: red;");
                                    break;
                                case TRAVELING:
                                    setStyle("-fx-text-background-color: red;");
                                    break;
                                case OTHER_OUTCOME:
                                    setStyle("-fx-text-background-color: red;");
                                    break;
                                // Add more cases if you have more categories
                                default:
                                    setStyle("-fx-text-background-color: black;");
                                    break;
                            }
                        } else {
                            setStyle("-fx-text-background-color: black;");
                        }
                    }
                };

                return row;
            }
        });


        tableView.setItems(transactions);
    }

    private void populateTransactionTypeOptions() {
//        typeOptions.getItems().setAll(Transaction.Type.values());
    	categoryOptions.getItems().setAll(Transaction.Category.values());
    }

    private void setLoggedInLabelText() {
        lblUser.setText(String.format("Logged in as: %s", UserSession.getInstance().getUser().getUsername()));
    }

    private void setTotalLabelText() {
        float sum = transactions.stream().collect(Collectors.summingDouble(o -> o.getAmount())).floatValue();
        lblTotal.setText(String.format("Total: %.2f", sum));

        if (sum == 0) {
            lblTotal.setTextFill(Paint.valueOf("black"));
        } else if (sum > 0) {
            lblTotal.setTextFill(Paint.valueOf("green"));
        } else {
            lblTotal.setTextFill(Paint.valueOf("red"));
        }
    }

    private void clearInputs() {
        descriptionField.clear();
        amountField.clear();
//        typeOptions.getSelectionModel().clearSelection();
        categoryOptions.getSelectionModel().clearSelection();
    }
}