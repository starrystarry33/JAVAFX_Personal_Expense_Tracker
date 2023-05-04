package controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import models.UserSession;
import Services.TransactionService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

//public class PieChartController implements Initializable {
//
//    private TransactionService transactionService = new TransactionService();
//
//    @FXML
//    private PieChart piechart;
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        try {
//            ObservableList<Data> pieChartData = FXCollections.observableArrayList(transactionService.getPieChartData(UserSession.getInstance().getUser().getId()));
//            pieChartData.forEach(data ->
//                    data.nameProperty().bind(
//                            Bindings.concat(
//                                    data.getName(), " ", data.pieValueProperty()
//                            )
//                    )
//            );
//
//
//            piechart.setTitle("INCOME vs OUTCOME");
//            piechart.setData(pieChartData);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}

public class PieChartController implements Initializable {

    private TransactionService transactionService = new TransactionService();

    @FXML
    private PieChart incomePieChart;

    @FXML
    private PieChart outcomePieChart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ObservableList<Data> incomePieChartData = FXCollections.observableArrayList(transactionService.getIncomePieChartData(UserSession.getInstance().getUser().getId()));
            incomePieChartData.forEach(data ->
                    data.nameProperty().bind(
                            Bindings.concat(
                                    data.getName(), " ", data.pieValueProperty()
                            )
                    )
            );

            incomePieChart.setTitle("INCOME");
            incomePieChart.setData(incomePieChartData);

            ObservableList<Data> outcomePieChartData = FXCollections.observableArrayList(transactionService.getOutcomePieChartData(UserSession.getInstance().getUser().getId()));
            outcomePieChartData.forEach(data ->
                    data.nameProperty().bind(
                            Bindings.concat(
                                    data.getName(), " ", data.pieValueProperty()
                            )
                    )
            );

            outcomePieChart.setTitle("OUTCOME");
            outcomePieChart.setData(outcomePieChartData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
