package com.celine.expensetracker;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    private long id;
    private TransactionType type;
    private BigDecimal amount;
    private LocalDate date;
    private String category;
    private String note;

    public Transaction(long id, TransactionType type, BigDecimal amount, LocalDate date, String category, String note) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.note = note;
    }

    public Transaction(TransactionType type, BigDecimal amount, LocalDate date, String category, String note) {
        this(0, type, amount, date, category, note);
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public String getCategory() { return category; }
    public String getNote() { return note; }

    @Override
    public String toString() {
        return String.format("%d | %s | %s | %s | %s", id, type, amount.toPlainString(), date, category == null ? "" : category);
    }
}
