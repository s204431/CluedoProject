package expressions;

public class Value extends Expression {
    public boolean value;

    public Value(boolean value) {
        this.value = value;
    }

    public String toString() {
        return "("+value+")";
    }
}
