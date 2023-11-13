package expressions;

public class Not extends Expression {
    public Not(Expression operand) {
        operands = new Expression[] {operand};
    }

    public String toString() {
        return "(!"+operands[0].toString()+")";
    }
}
