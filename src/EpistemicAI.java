import cluedo.Card;
import expressions.*;

import java.util.ArrayList;
import java.util.List;

public class EpistemicAI extends AI {

    public PossibleWorldModel model;

    public EpistemicAI(Card[] cards, int playerIndex, int nPlayers, int nRooms, int nWeapons, int nPeople, int[] trueWorld) {
        super(cards, playerIndex, nPlayers, nRooms, nWeapons, nPeople, trueWorld);
        this.model = new PossibleWorldModel(nPlayers, nRooms, nWeapons, nPeople);
    }

    //Returns {isFinal, room, weapon, person}, where isFinal tells whether the move is final (1) or not (0).
    public int[] makeMove() {
        return null;
    }

    public void wasShownCard(Card card, int otherPlayer) {
        Expression expression = new Not(new Predicate(card.getCardType(), card.getCardNumber()));
        if (model.evaluateExpression(new Knows(playerIndex, expression), trueWorld) && model.evaluateExpression(new Knows(otherPlayer, expression), trueWorld)) {
            return;
        }
        EventModel event = new EventModel(nPlayers);
        EventModel.Node root = event.createNode(expression);
        event.createEdge(root, root, false, new int[] {playerIndex, otherPlayer});
        EventModel.Node trueNode = event.createNode(new Value(true));
        List<Integer> allOtherPlayers = new ArrayList<>();
        for (int i = 0; i < nPlayers; i++) {
            if (i != playerIndex && i != otherPlayer) {
                allOtherPlayers.add(i);
            }
        }
        int[] otherPlayers = new int[allOtherPlayers.size()];
        for (int i = 0; i < allOtherPlayers.size(); i++) {
            otherPlayers[i] = allOtherPlayers.get(i);
        }
        event.createEdge(root, trueNode, false, otherPlayers);
        event.createEdge(trueNode, trueNode, false);
        model.productUpdate(event, trueWorld);
    }

    public void otherPlayersShownCard(Card[] possibleCardsShown, int otherPlayer1, int otherPlayer2) {

    }


}
