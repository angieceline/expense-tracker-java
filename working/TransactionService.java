package com.celine.expensetracker;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public class TransactionService {
    private final TransactionDao dao = new TransactionDao();

    public void add(Transaction t) throws Exception {
        if (t.getAmount().compareTo(BigDecimal.ZERO) <= 0) throw new Exception("Amount must be > 0");
        dao.save(t);
    }

    public List<Transaction> listAll() throws Exception {
        return dao.findAll();
    }

    public MonthlySummary monthlySummary(YearMonth ym) throws Exception {
        int year = ym.getYear();
        int month = ym.getMonthValue();
        var list = dao.findByMonth(year, month);
        BigDecimal totalExpenses = BigDecimal.ZERO;
        BigDecimal totalIncome = BigDecimal.ZERO;
        for (Transaction t : list) {
            if (t.getType() == TransactionType.EXPENSE) totalExpenses = totalExpenses.add(t.getAmount());
            else totalIncome = totalIncome.add(t.getAmount());
        }
        return new MonthlySummary(totalIncome, totalExpenses);
    }

    public static class MonthlySummary {
        public final BigDecimal income;
        public final BigDecimal expenses;
        public MonthlySummary(BigDecimal income, BigDecimal expenses) {
            this.income = income;
            this.expenses = expenses;
        }
    }

    public void delete(long id) throws Exception {
    new TransactionDao().deleteById(id);
}

}
