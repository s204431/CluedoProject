package expressions;

public class Or extends Expression {
    public Or(Expression left, Expression right) {
        operands = new Expression[] {left, right};
    }

    public Or(Expression[] operands) {
        this.operands = operands;
    }
}
