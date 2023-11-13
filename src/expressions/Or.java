package expressions;

public class Or extends Expression {
    public Or(Expression left, Expression right) {
        operands = new Expression[] {left, right};
    }

    public Or(Expression[] operands) {
        this.operands = operands;
    }

    public String toString() {
        String s = "(";
        for (int i = 0; i < operands.length; i++) {
            s += operands[i].toString();
            if (i != operands.length-1) {
                s += " | ";
            }
        }
        return s + ")";
    }
}
