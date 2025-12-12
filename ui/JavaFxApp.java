package com.celine.expensetracker;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class JavaFxApp extends Application {

    private final TransactionService service = new TransactionService();
    private final ObservableList<Transaction> data = FXCollections.observableArrayList();
    private TableView<Transaction> table;

    public static void main(String[] args) {
        // Launch JavaFX
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Expense Tracker");

        table = new TableView<>();
        table.setItems(data);

        TableColumn<Transaction, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        idCol.setPrefWidth(60);

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(90);

        TableColumn<Transaction, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getAmount().toPlainString()));
        amountCol.setPrefWidth(100);

        TableColumn<Transaction, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(110);

        TableColumn<Transaction, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(120);

        TableColumn<Transaction, String> noteCol = new TableColumn<>("Note");
        noteCol.setCellValueFactory(new PropertyValueFactory<>("note"));
        noteCol.setPrefWidth(200);

        table.getColumns().addAll(idCol, typeCol, amountCol, dateCol, categoryCol, noteCol);

        // Controls
        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> onAdd());

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> onDelete());

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> loadData());

        Button exportBtn = new Button("Export CSV");
        exportBtn.setOnAction(e -> onExport());

        HBox controls = new HBox(8, addBtn, deleteBtn, refreshBtn, exportBtn);
        controls.setPadding(new Insets(8));

        BorderPane root = new BorderPane();
        root.setCenter(table);
        root.setTop(controls);
        Scene scene = new Scene(root, 800, 500);
        primaryStage.setScene(scene);

        loadData();
        primaryStage.show();
    }

    private void loadData() {
        try {
            List<Transaction> all = service.listAll();
            data.setAll(all);
        } catch (Exception ex) {
            showError("Failed to load data: " + ex.getMessage());
        }
    }

    private void onAdd() {
        // Simple dialog-based add. Not fancy but it works.
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.setTitle("Add Transaction");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField typeFld = new TextField();
        typeFld.setPromptText("EXPENSE or INCOME");

        TextField amountFld = new TextField();
        amountFld.setPromptText("Amount e.g. 123.45");

        DatePicker datePicker = new DatePicker(LocalDate.now());

        TextField categoryFld = new TextField();
        categoryFld.setPromptText("Category");

        TextField noteFld = new TextField();
        noteFld.setPromptText("Note");

        GridPane grid = new GridPane();
        grid.setVgap(6);
        grid.setHgap(6);
        grid.setPadding(new Insets(10));
        grid.addRow(0, new Label("Type:"), typeFld);
        grid.addRow(1, new Label("Amount:"), amountFld);
        grid.addRow(2, new Label("Date:"), datePicker);
        grid.addRow(3, new Label("Category:"), categoryFld);
        grid.addRow(4, new Label("Note:"), noteFld);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    TransactionType type = TransactionType.valueOf(typeFld.getText().trim().toUpperCase());
                    BigDecimal amount = new BigDecimal(amountFld.getText().trim());
                    LocalDate date = datePicker.getValue();
                    String cat = categoryFld.getText().trim();
                    String note = noteFld.getText().trim();
                    return new Transaction(type, amount, date, cat, note);
                } catch (Exception ex) {
                    showError("Invalid input: " + ex.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Transaction> res = dialog.showAndWait();
        res.ifPresent(t -> {
            try {
                service.add(t);
                loadData();
            } catch (Exception ex) {
                showError("Failed to save: " + ex.getMessage());
            }
        });
    }

    private void onDelete() {
        Transaction sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Select a transaction to delete.");
            return;
        }
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Delete transaction ID " + sel.getId() + " ?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> ans = conf.showAndWait();
        if (ans.isPresent() && ans.get() == ButtonType.YES) {
            try {
                service.delete(sel.getId());
                loadData();
            } catch (Exception ex) {
                showError("Delete failed: " + ex.getMessage());
            }
        }
    }

    private void onExport() {
        try {
            java.nio.file.Path out = java.nio.file.Paths.get("transactions.csv");
            CsvExporter.export(service.listAll(), out);
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Exported to " + out.toAbsolutePath(), ButtonType.OK);
            a.showAndWait();
        } catch (Exception ex) {
            showError("Export failed: " + ex.getMessage());
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }
}
