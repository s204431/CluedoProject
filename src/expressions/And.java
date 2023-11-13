package expressions;

public class And extends Expression {
    public And(Expression left, Expression right) {
        operands = new Expression[] {left, right};
    }

    public And(Expression[] operands) {
        this.operands = operands;
    }

}
