package expressions;

public class Knows extends Expression {
    public int agentIndex;
    public Knows(int agentIndex, Expression expression) {
        this.agentIndex = agentIndex;
        operands = new Expression[] {expression};
    }
}
