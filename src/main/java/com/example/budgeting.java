package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.io.*;
import javafx.application.Application;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class budgeting extends Application {
    // array of strings that contains spending topics
    String[] topics = { "Food", "Entertainment", "Transportation", "Housing", "Utilities", "Miscellaneous" };
    // randomly populated array of 6 doubles that contains spending amounts

    @Override
    public void stop() throws Exception {
        Budget.saveBudget();
        super.stop();
    }

    @Override
    public void start(Stage stage) {
        // Create a tab pane to hold the tabs
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        Tab homeTab = createHomeTab();
        Tab incomeTab = createIncomeTab();
        Tab chartTab = chartTab();
        Tab expensesTab = ExpensesTab();
        Tab testTab = testTab();
        Tab historyTab = historyTab();
        // Add the tabs to the tab pane
        tabPane.getTabs().addAll(homeTab, incomeTab, expensesTab, historyTab, chartTab, testTab);

        // Create a scene with the tab pane as the root
        Scene scene = new Scene(tabPane);
        scene.getRoot().setStyle("-fx-font-family: 'serif'");
        stage.setScene(scene);

        stage.show();
    }

    // move history tab here
    private Tab historyTab() {
        // Create a tab for the history
        Tab historyTab = new Tab("History");
        GridPane historyGrid = new GridPane();
        // format history grid
        formatGrid(historyGrid);
        // Create a box to hold the history items
        VBox historyBox = new VBox();
        historyBox.setPadding(new Insets(10, 10, 10, 10));
        historyBox.setSpacing(8);
        historyBox.setAlignment(Pos.TOP_LEFT);
        // Create a label for the budget history
        Label historyLabel = new Label("Budget History:");
        historyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        // add refresh button and event to update labels
        Button refreshButton = new Button("Refresh");
        refreshButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            historyBox.getChildren().clear();
            historyBox.getChildren().addAll(historyLabel, refreshButton);
            Budget.getSpendingByTopic().forEach((topic, total) -> {
                // Create a label for each transaction
                Label transactionLabel = new Label(topic + " " + total);
                // Add the label to the box
                historyBox.getChildren().add(transactionLabel);
            });
        });
        // position refresh button top right
        refreshButton.setAlignment(Pos.TOP_RIGHT);
        // loop through dates from transactions

        // Add the label to the box
        historyBox.getChildren().addAll(historyLabel, refreshButton);
        // Set the content of the history tab to the box
        historyTab.setContent(historyBox);
        return historyTab;

    }

    // function to format each gridpane
    private GridPane formatGrid(GridPane grid) {
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.setMinSize(250, 100);
        grid.setMaxSize(500, 200);
        grid.setPrefSize(350, 150);
        return grid;
    }

    private Tab chartTab() {
        // Create a tab for the chart
        Tab chartTab = new Tab("Charts");
        // Create a grid pane for the chart
        GridPane chartGrid = new GridPane();
        formatGrid(chartGrid);
        // Create a pie chart
        PieChart pieChart = new PieChart();

        ArrayList<PieChart.Data> chartData = new ArrayList<>();

        Budget.getSpendingByTopic().forEach((topic, total) -> {
            chartData.add(new PieChart.Data(topic + " " + total, total));
        });

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(chartData);
        // add event listener for change in data
        pieChartData.addListener((ListChangeListener.Change<? extends PieChart.Data> c) -> {
            while (c.next()) {
                if (c.wasUpdated()) {
                    // update the chart
                    pieChart.setData(pieChartData);
                }
            }
        });

        pieChart.setData(pieChartData);

        // Add the pie chart to the center of the grid
        chartGrid.add(pieChart, 0, 2);

        // center pie chart in screen
        chartGrid.setAlignment(Pos.CENTER);
        // Set the content of the chart tab to the grid
        chartTab.setContent(chartGrid);

        // refresh content of chart tab

        return chartTab;
    }

    // function to create the income tab
    private Tab createIncomeTab() {
        // Create a tab for the income
        Tab incomeTab = new Tab("Income");
        GridPane incomeGrid = new GridPane();
        formatGrid(incomeGrid);
        Label incomeLabel = new Label("Income:");
        TextField incomeField = new TextField();
        incomeGrid.add(incomeLabel, 0, 0);
        incomeGrid.add(incomeField, 1, 0);
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select a date");
        incomeGrid.add(datePicker, 1, 1);

        Button saveIncomeButton = new Button("Save Income");
        saveIncomeButton.setOnAction(event -> {
            // make sure incomeField is not empty and is a number
            if (incomeField.getText().isEmpty() || !incomeField.getText().matches("[0-9]+")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Input");
                alert.setContentText("Please enter a valid income amount.");
                alert.showAndWait();
                return;
            }
            // populate transaction list
            Budget.addTransaction(
                    new Transaction("Income", Double.parseDouble(incomeField.getText()), datePicker.getValue()));
        });
        incomeGrid.add(saveIncomeButton, 1, 2);
        incomeTab.setContent(incomeGrid);
        return incomeTab;
    }

    // how to save variables to a file
    private Tab testTab() {

        Label spendingAmountsLabel = new Label();
        Tab testTAB = new Tab("Tests");
        GridPane testGrid = new GridPane();
        formatGrid(testGrid);
        // Create a button to test the save to file function
        Button saveToFileButton = new Button("Save to File");
        saveToFileButton.setOnAction(event -> {
            Budget.budget.saveBudget();
        });
        // Create a label to display the current date
        Label currentDateLabel = new Label();
        currentDateLabel.setText("Current Date: " + LocalDate.now());

        // add it to grid
        testGrid.add(spendingAmountsLabel, 0, 2);
        testGrid.add(saveToFileButton, 0, 0);
        testGrid.add(currentDateLabel, 0, 1);

        testTAB.setContent(testGrid);

        return testTAB;
    }

    // function that parses dates from a text file
    public LocalDate parseDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);
        return localDate;
    }

    // function to create the expenses tab with parameter for expenses array
    private Tab ExpensesTab() {
        // Create a tab for the expenses
        Tab expensesTab = new Tab("Expenses");
        // Create a grid pane for the expenses
        GridPane expensesGrid = new GridPane();
        formatGrid(expensesGrid);
        // Create a combo box for the spending topics
        ComboBox<String> spendingTopicsComboBox = new ComboBox<>();
        spendingTopicsComboBox.getItems().addAll(topics);
        // Create the expenses label and field
        Label expensesLabel = new Label("Expenses:");
        TextField expensesField = new TextField();
        expensesGrid.add(expensesLabel, 0, 0);
        expensesGrid.add(expensesField, 1, 0);
        // Create a date picker for the expenses
        DatePicker datePicker2 = new DatePicker();
        datePicker2.setPromptText("Select a date");
        expensesGrid.add(datePicker2, 1, 1);
        // Create a button to save the expenses
        Button saveExpensesButton = new Button("Save Expenses");
        saveExpensesButton.setOnAction(event -> {
            // check that a topic has been selected
            if (spendingTopicsComboBox.getValue() == null) {
                System.out.println("Please select a topic");
            } else {// clear all textt fields

                // switch statement for each type of topic
                switch (spendingTopicsComboBox.getValue()) {
                    case "Food":
                        // populate addTransaction method
                        Budget.addTransaction(new Transaction("Food", Double.parseDouble(expensesField.getText()) * -1,
                                datePicker2.getValue()));
                        break;
                    case "Rent":
                        Budget.addTransaction(new Transaction("Rent", Double.parseDouble(expensesField.getText()) * -1,
                                datePicker2.getValue()));
                        break;
                    case "Utilities":
                        // populate addTransaction method
                        Budget.addTransaction(
                                new Transaction("Utilities", Double.parseDouble(expensesField.getText()) * -1,
                                        datePicker2.getValue()));
                        break;
                    case "Transportation":
                        // populate addTransaction method and multiple the double by -1
                        Budget.addTransaction(new Transaction("Transportation",
                                Double.parseDouble(expensesField.getText()) * -1, datePicker2.getValue()));
                        break;
                    case "Entertainment":
                        // populate addTransaction method
                        Budget.addTransaction(new Transaction("Entertainment",
                                Double.parseDouble(expensesField.getText()) * -1, datePicker2.getValue()));
                        break;
                    case "Other":
                        // populate addTransaction method
                        Budget.addTransaction(new Transaction("Other", Double.parseDouble(expensesField.getText()) * -1,
                                datePicker2.getValue()));
                        break;
                }
                LocalDate date = datePicker2.getValue();
                expensesField.clear();
                // make expensesfield say "expenses"
                expensesField.setPromptText("Expenses");
            }

        });
        // set content of the expenses tab to the grid pane
        expensesGrid.add(spendingTopicsComboBox, 1, 2);
        expensesGrid.add(saveExpensesButton, 1, 3);
        expensesTab.setContent(expensesGrid);
        return expensesTab;
    }

    // function that calculates amount of days between 2 dates
    public long daysBetween(LocalDate date1, LocalDate date2) {
        long daysBetween = ChronoUnit.DAYS.between(date1, date2);
        return daysBetween;
    }

    private Tab createHomeTab() {
        // Create the home tab
        Tab homeTab = new Tab();
        homeTab.setText("Home");
        // Create the layout for the home tab
        GridPane layout = new GridPane();
        formatGrid(layout);
        // add button with question mark
        Button helpButton = new Button("?");
        GridPane.setConstraints(helpButton, 1, 1);
        Label infoLabel = new Label(
                "this is a home tab currently it is being fed hard coded data for testing purposes.  To add an income to your balance immediately you can go to income and select any date before the current.");
        // make label text wrap
        infoLabel.setWrapText(true);
        // limit label length
        infoLabel.setMaxWidth(150);
        infoLabel.setVisible(false);
        // if user hovers over help button make info label visible
        helpButton.setOnMouseEntered(event -> {
            infoLabel.setVisible(true);
        });
        // if user stops hovering over help button make info label invisible
        helpButton.setOnMouseExited(event -> {
            infoLabel.setVisible(false);
        });
        TextField startingBudgetField = new TextField();
        startingBudgetField.setPrefWidth(100);
        // label to display balance
        Label balance = new Label("Balance: " + Budget.budget.getBalance());

        GridPane.setConstraints(balance, 2, 1);
        layout.getChildren().addAll(balance, helpButton,
                infoLabel);
        homeTab.setContent(layout);
        return homeTab;
    }

    // local date
    public static void main(String[] args) {
        Budget.loadBudget();
        launch(args);
    }
}

// budget class
class Budget implements Serializable {

    public static Budget budget;

    public static void loadBudget() {
        File file = new File("budget.txt");
        try (FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);) {
            budget = (Budget) objectInputStream.readObject();
        } catch (Exception e) {
            budget = new Budget();
            addTransaction(new Transaction("Deposit", 1000, LocalDate.now().minusDays(100)));
            addTransaction(new Transaction("Transportation", -100, LocalDate.now().minusDays(3)));
            addTransaction(new Transaction("Food", -120, LocalDate.now().minusDays(2)));
            addTransaction(new Transaction("Rent", -150, LocalDate.now().minusDays(1)));
            addTransaction(new Transaction("Other", -190, LocalDate.now().minusDays(4)));

        }
    }

    public static void saveBudget() {
        File file = new File("budget.shiii");// if for some reason this doesnt work the extension can be changed and the
                                             // file can be deleted for testing purposes havent slept in 3 days hope
                                             // this
                                             // works ;{
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);) {
            objectOutputStream.writeObject(budget);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<Transaction> transactions = new ArrayList<Transaction>();

    public Budget() {

    }

    public static void addTransaction(Transaction expense) {
        budget.transactions.add(expense);
    }

    public static Map<String, Double> getSpendingByTopic() {
        LocalDate now = LocalDate.now();
        Map<String, Double> result = new HashMap<String, Double>();
        for (Transaction expense : budget.transactions) {
            if (expense.getDate().isAfter(now))
                continue;
            if (expense.getAmount() > 0)
                continue;
            if (result.containsKey(expense.getTopic())) {
                result.put(expense.getTopic(), result.get(expense.getTopic()) + expense.getAmount());
            } else {
                result.put(expense.getTopic(), expense.getAmount());
            }
        }
        return result;
    }

    // get balance
    public static double getBalance() {
        double balance = 0;
        LocalDate now = LocalDate.now();
        for (Transaction expense : budget.transactions)
            if (!expense.getDate().isAfter(now)) {
                balance += expense.getAmount();
            }
        return balance;
    }

}

class Transaction implements Serializable {
    private String topic;
    private double amount;
    private LocalDate date;

    public Transaction(String topic, double amount, LocalDate date) {
        this.topic = topic;
        this.amount = amount;
        this.date = date;
    }

    public String getTopic() {
        return topic;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

}

/**
 * 
 * deposit yesterday
 * expense yesterday
 * 
 * <--- balance
 * 
 * expense tomorrow
 * 
 * 
 * <---- blanace
 * 
 */