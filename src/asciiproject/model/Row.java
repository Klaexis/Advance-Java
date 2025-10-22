package asciiproject.model;

import java.util.ArrayList;

public class Row {
    private ArrayList<Pair> cells;

    public Row() {
        this.cells = new ArrayList<>();
    }

    public Row(ArrayList<Pair> cells) {
        this.cells = cells;
    }

    public ArrayList<Pair> getCells() {
        return cells;
    }

    public void setCells(ArrayList<Pair> cells) {
        this.cells = cells;
    }

    public void addCell(Pair pair) {
        cells.add(pair);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Pair p : cells) {
            sb.append(p.toString()).append(" ");
        }
        return sb.toString().trim();
    }
}