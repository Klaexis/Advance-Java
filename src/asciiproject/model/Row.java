package asciiproject.model;

import java.util.ArrayList;
import java.util.List;

public class Row {
    private List<Pair> cells;

    public Row(List<Pair> cells) {
        this.cells = cells;
    }

    public List<Pair> getCells() {
        return cells;
    }

    public void setCells(ArrayList<Pair> cells) {
        this.cells = cells;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Pair p : cells) {
            sb.append(p.toString()).append(" ");
        }
        return sb.toString().trim();
    }
}