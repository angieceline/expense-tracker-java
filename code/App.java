package com.celine.expensetracker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Scanner;

public class App {
    private final TransactionService service = new TransactionService();
    private final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        new App().run();
    }

    private void run() {
        System.out.println("== Personal Expense Tracker ==");
        while (true) {
            printMenu();
            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> addTransaction();
                    case "2" -> listTransactions();
                    case "3" -> monthlySummary();
                    case "4" -> deleteTransaction();
                    case "5" -> exportCsv();
                    case "6" -> { System.out.println("Bye."); return; }
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n1) Add transaction");
        System.out.println("2) List transactions");
        System.out.println("3) Monthly summary");
        System.out.println("4) Delete transaction by ID");
        System.out.println("5) Export transactions to CSV");
        System.out.println("6) Exit");
        System.out.print("Choose: ");
    }

    private void addTransaction() throws Exception {
        System.out.print("Type (expense/income): ");
        TransactionType type = TransactionType.valueOf(sc.nextLine().trim().toUpperCase());
        System.out.print("Amount (e.g., 123.45): ");
        BigDecimal amount = new BigDecimal(sc.nextLine().trim());
        System.out.print("Date (yyyy-MM-dd), blank for today: ");
        String d = sc.nextLine().trim();
        LocalDate date = d.isEmpty() ? LocalDate.now() : LocalDate.parse(d);
        System.out.print("Category: ");
        String cat = sc.nextLine().trim();
        System.out.print("Note: ");
        String note = sc.nextLine().trim();
        Transaction t = new Transaction(type, amount, date, cat, note);
        service.add(t);
        System.out.println("Saved.");
    }

    private void listTransactions() throws Exception {
        List<Transaction> list = service.listAll();
        if (list.isEmpty()) {
            System.out.println("No transactions.");
            return;
        }
        System.out.println("ID | TYPE | AMOUNT | DATE | CATEGORY");
        for (Transaction t : list) {
            System.out.println(t);
        }
    }

    private void monthlySummary() throws Exception {
        System.out.print("Enter year-month (YYYY-MM), blank for current: ");
        String s = sc.nextLine().trim();
        YearMonth ym = s.isEmpty() ? YearMonth.now() : YearMonth.parse(s);
        var sum = service.monthlySummary(ym);
        System.out.println("Month: " + ym);
        System.out.println("Total income : " + sum.income.toPlainString());
        System.out.println("Total expenses: " + sum.expenses.toPlainString());
        System.out.println("Net balance   : " + sum.income.subtract(sum.expenses).toPlainString());
    }

    private void deleteTransaction() {
    try {
        System.out.print("Enter ID to delete: ");
        String line = sc.nextLine().trim();
        if (line.isEmpty()) {
            System.out.println("No ID entered.");
            return;
        }
        long id = Long.parseLong(line);
        System.out.print("Are you sure? (y/N): ");
        String confirm = sc.nextLine().trim().toLowerCase();
        if (!confirm.equals("y") && !confirm.equals("yes")) {
            System.out.println("Cancelled.");
            return;
        }
        service.delete(id);
        System.out.println("Deleted (if existed).");
    } catch (NumberFormatException e) {
        System.out.println("Invalid ID. Please enter a numeric id.");
    } catch (Exception e) {
        System.out.println("Delete failed: " + e.getMessage());
    }
}


    private void exportCsv() {
    try {
        List<Transaction> list = service.listAll();
        if (list.isEmpty()) {
            System.out.println("No transactions to export.");
            return;
        }
        java.nio.file.Path out = java.nio.file.Paths.get("transactions.csv");
        CsvExporter.export(list, out);
        System.out.println("Exported " + list.size() + " transaction(s) to " + out.toAbsolutePath());
    } catch (Exception e) {
        System.out.println("Export failed: " + e.getMessage());
    }
}

}
