package com.celine.expensetracker;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao {

    public void save(Transaction t) throws SQLException {
        String sql = "INSERT INTO transactions(type, amount, date, category, note) VALUES(?,?,?,?,?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getType().name());
            ps.setString(2, t.getAmount().toPlainString());
            ps.setString(3, t.getDate().toString());
            ps.setString(4, t.getCategory());
            ps.setString(5, t.getNote());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) t.setId(rs.getLong(1));
            }
        }
    }

    public List<Transaction> findAll() throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT id, type, amount, date, category, note FROM transactions ORDER BY date DESC, id DESC";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<Transaction> findByMonth(int year, int month) throws SQLException {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        String sql = "SELECT id, type, amount, date, category, note FROM transactions WHERE date BETWEEN ? AND ? ORDER BY date DESC";
        List<Transaction> list = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, start.toString());
            ps.setString(2, end.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    private Transaction map(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        TransactionType type = TransactionType.valueOf(rs.getString("type"));
        BigDecimal amount = new BigDecimal(rs.getString("amount"));
        LocalDate date = LocalDate.parse(rs.getString("date"));
        String category = rs.getString("category");
        String note = rs.getString("note");
        return new Transaction(id, type, amount, date, category, note);
    }
    
    public void deleteById(long id) throws SQLException {
    try (Connection c = Database.getConnection();
        PreparedStatement ps = c.prepareStatement("DELETE FROM transactions WHERE id = ?")) {
        ps.setLong(1, id);
        ps.executeUpdate();
    }
}
}
