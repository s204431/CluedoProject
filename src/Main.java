import ai.EpistemicAI;
import cluedo.Card;
import cluedo.Game;
import cluedo.UI;
import expressions.*;
import models.EventModel;
import models.PossibleWorldModel;

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

        EpistemicAI ai = new EpistemicAI(new Card[] {new Card(0, 1)}, 0, 4, 3, 2, 2, new int[] {0, 0, 0});
        System.out.println(ai.model.allNodes.size());
        ai.wasShownCard(new Card(0, 1), 1);
        System.out.println(ai.model.allNodes.size());
        ai.wasShownCard(new Card(0, 2), 1);
        System.out.println(ai.model.allNodes.size());
        System.out.println(ai.model.evaluateExpression(new Knows(ai.playerIndex, new Not(new Predicate(0, 1))), ai.trueWorld));
        System.out.println(ai.model.evaluateExpression(new Knows(1, new Not(new Predicate(0, 1))), ai.trueWorld));
        System.out.println(ai.model.evaluateExpression(new Knows(ai.playerIndex, new Not(new Predicate(0, 2))), ai.trueWorld));
        System.out.println(ai.model.evaluateExpression(new Knows(1, new Not(new Predicate(0, 2))), ai.trueWorld));
        System.out.println(ai.model.evaluateExpression(new Knows(ai.playerIndex, new Not(new Predicate(0, 0))), ai.trueWorld));

        System.out.println(ai.model.evaluateExpression(new Knows(ai.playerIndex, new Predicate(0, 0)), ai.trueWorld));
        System.out.println(ai.model.evaluateExpression(new Knows(ai.playerIndex, new And(new Predicate[] {new Predicate(0, 0), new Predicate(1, 0), new Predicate(2, 0)})), ai.trueWorld));

        ai.wasShownCard(new Card(1, 1), 1);
        ai.wasShownCard(new Card(2, 1), 1);
        System.out.println(ai.model.allNodes.size());

        System.out.println(ai.model.evaluateExpression(new Knows(ai.playerIndex, new And(new Predicate[] {new Predicate(0, 0), new Predicate(1, 0), new Predicate(2, 0)})), ai.trueWorld));
        System.out.println(ai.model.evaluateExpression(new Knows(1, new And(new Predicate[] {new Predicate(0, 0), new Predicate(1, 0), new Predicate(2, 0)})), ai.trueWorld));

        testCase1();

        new UI(new Game());
    }

    private static void testCase1() {
        boolean success = true;
        Game game = new Game();
        Card[][] cards = game.getPlayerCards();
        Card[] murdererCards = game.getMurdererCards();
        int[] trueWorld = new int[] {murdererCards[0].getCardNumber(), murdererCards[1].getCardNumber(), murdererCards[2].getCardNumber()};
        EpistemicAI ai = new EpistemicAI(cards[0], 0, 4, 9, 7, 7, trueWorld);
        for (int i = 1; i < cards.length; i++) {
            for (int j = 0; j < cards[i].length; j++) {
                ai.wasShownCard(cards[i][j], i);
                System.out.println(ai.model.allNodes.size());
                boolean knowsTrueWorld = ai.model.evaluateExpression(new Knows(ai.playerIndex, new And(new Predicate[] {new Predicate(0, trueWorld[0]), new Predicate(1, trueWorld[1]), new Predicate(2, trueWorld[2])})), ai.trueWorld);
                System.out.println("Agent knows the true world: " + knowsTrueWorld);
                if (knowsTrueWorld && !(i == cards.length-1 && j == cards[i].length-1)) {
                    success = false;
                }
            }
        }
        boolean knowsTrueWorld = ai.model.evaluateExpression(new Knows(ai.playerIndex, new And(new Predicate[] {new Predicate(0, trueWorld[0]), new Predicate(1, trueWorld[1]), new Predicate(2, trueWorld[2])})), ai.trueWorld);
        if (success && knowsTrueWorld) {
            System.out.println("Passed test case 1");
        }
        else {
            System.out.println("Failed test case 1");
        }
    }
}