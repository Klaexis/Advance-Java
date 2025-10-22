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
            // Append the string of the Pair followed by a space
            sb.append(p.toString()).append(" "); // format as (key1 , value1) (key2 , value2) and so on
        }
        // Convert the StringBuilder to a String 
        return sb.toString();
    }
}