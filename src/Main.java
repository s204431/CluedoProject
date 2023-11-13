import cluedo.Card;
import expressions.*;
import jdk.jfr.Event;

public class Main {
    public static void main(String[] args) {
        PossibleWorldModel model = new PossibleWorldModel(4, 9, 6, 6);
        //int[] trueWorld = model.trueWorld.predicatesAsInts();
        //Expression expression1 = new And(new Expression[] {new Predicate(0, trueWorld[0]), new Predicate(1, trueWorld[1]), new Predicate(2, trueWorld[2])});
        //Expression expression2 = new Knows(0, new Predicate(0, trueWorld[0]));
        //model.publicAnnouncement(expression1);
        //model.publicAnnouncement(new Predicate(0, trueWorld[0]));
        System.out.println(model.allNodes.size());
        //System.out.println(model.evaluateExpression(expression2));

        EventModel event = new EventModel(4);
        EventModel.Node root = event.createNode(new Not(new Predicate(0, 1)));
        event.createEdge(root, root, false, new int[] {0, 1});
        EventModel.Node trueNode = event.createNode(new Value(true));
        event.createEdge(root, trueNode, false, new int[] {2, 3});
        event.createEdge(trueNode, trueNode, false);
        System.out.println(event.allNodes.size());
        model.productUpdate(event, new int[] {0, 0, 0});
        System.out.println(model.allNodes.size());
        System.out.println(model.allNodes.get(0).eventNode.pre);

        EpistemicAI ai = new EpistemicAI(null, 0, 4, 9, 7, 7, new int[] {0, 0, 0});
        System.out.println(ai.model.allNodes.size());
        ai.wasShownCard(new Card(0, 1), 1);
        System.out.println(ai.model.allNodes.size());
        ai.wasShownCard(new Card(0, 2), 1);
        System.out.println(ai.model.allNodes.size());
    }
}