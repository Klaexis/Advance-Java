package asciiproject.model;

public class Pair {
    // Fields for key and value
    private String key;
    private String value;

    // Constructor
    public Pair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    // Getters and Setters
    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "(" + key + " , " + value + ")"; // Format as (key , value)
    }
}