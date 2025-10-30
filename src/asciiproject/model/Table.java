package asciiproject.model;

import java.util.ArrayList;
import java.util.List;

public class Table {
    // List of Row objects in the table
    private List<Row> rows;

    // Constructor: initializes an empty table
    public Table() {
        this.rows = new ArrayList<>();
    }

    // Constructor with existing rows
    public Table(List<Row> rows) {
        this.rows = rows;
    }

    // Get all rows
    public List<Row> getRows() {
        return rows;
    }

    // Get a specific row
    public Row getRow(int index) {
        return rows.get(index);
    }

    // Add a row
    public void addRow(Row row) {
        rows.add(row);
    }

    // Insert a row at a specific index
    public void addRowAt(int index, Row row) {
        rows.add(index, row);
    }

    // Get number of rows
    public int size() {
        return rows.size();
    }

    // Clear all rows
    public void clear() {
        rows.clear();
    }

    // Check if table is empty
    public boolean isEmpty() {
        return rows.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Table Contents:\n");
        for (Row row : rows) {
            sb.append(row.toString()).append("\n");
        }
        return sb.toString();
    }
}
