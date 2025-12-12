package com.celine.expensetracker;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvExporter {

    /**
     * Export transactions to a CSV file with a header row.
     * Uses simple escaping: wraps fields with quotes if they contain comma/newline/quote.
     */
    public static void export(List<Transaction> transactions, Path out) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(out)) {
            // header
            w.write("id,type,amount,date,category,note");
            w.newLine();

            for (Transaction t : transactions) {
                String line = String.join(",",
                        escape(String.valueOf(t.getId())),
                        escape(t.getType().name()),
                        escape(t.getAmount().toPlainString()),
                        escape(t.getDate().toString()),
                        escape(t.getCategory()),
                        escape(t.getNote())
                );
                w.write(line);
                w.newLine();
            }
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        boolean needQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String out = s.replace("\"", "\"\""); // double quotes inside
        return needQuotes ? "\"" + out + "\"" : out;
    }
}
