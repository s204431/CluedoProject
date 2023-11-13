import expressions.*;

public class Main {
    public static void main(String[] args) {
        PossibleWorldModel model = new PossibleWorldModel(4, 9, 6, 6);
        int[] trueWorld = model.trueWorld.predicatesAsInts();
        Expression expression1 = new And(new Expression[] {new Predicate(0, trueWorld[0]), new Predicate(1, trueWorld[1]), new Predicate(2, trueWorld[2])});
        Expression expression2 = new Knows(0, new Predicate(0, trueWorld[0]));
        //model.publicAnnouncement(expression1);
        model.publicAnnouncement(new Predicate(0, trueWorld[0]));
        System.out.println(model.allNodes.size());
        System.out.println(model.evaluateExpression(expression2));
    }
}