package ai;

import cluedo.Card;
import expressions.*;

import java.util.ArrayList;
import java.util.List;
import models.PossibleWorldModel;
import models.EventModel;

public class EpistemicAI extends AI {

    private PossibleWorldModel baseModel;
    public PossibleWorldModel model;
    public List<Integer> otherPlayers = new ArrayList<>();
    public List<Card>[] sharedInformation; //Shared information with each other players.

    public EpistemicAI(Card[] cards, int playerIndex, int nPlayers, int nRooms, int nWeapons, int nPeople, int[] trueWorld) {
        super(cards, playerIndex, nPlayers, nRooms, nWeapons, nPeople, trueWorld);
        this.model = new PossibleWorldModel(nPlayers, nRooms, nWeapons, nPeople);
        this.baseModel = new PossibleWorldModel(nPlayers, nRooms, nWeapons, nPeople);
        for (int i = 0; i < nPlayers; i++) {
            if (i != playerIndex) {
                otherPlayers.add(i);
            }
        }
        sharedInformation = new List[nPlayers-1];
        for (int i = 0; i < sharedInformation.length; i++) {
            sharedInformation[i] = new ArrayList<>();
        }
        initializeModel();
    }

    //Initialize model with cards given to the ai.AI.
    public void initializeModel() {
        Not[] literals = new Not[cards.length];
        for (int i = 0; i < cards.length; i++) {
            literals[i] = new Not(new Predicate(cards[i].getCardType(), cards[i].getCardNumber()));
        }
        Expression expression = new And(literals);
        EventModel event = new EventModel(nPlayers);
        EventModel.Node root = event.createNode(expression);
        event.createEdge(root, root, false, new int[] {playerIndex});
        EventModel.Node trueNode = event.createNode(new Value(true));
        List<Integer> allOtherPlayers = new ArrayList<>();
        for (int i = 0; i < nPlayers; i++) {
            if (i != playerIndex) {
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
        baseModel.productUpdate(event, trueWorld);
    }

    //Returns {isFinal, room, weapon, person}, where isFinal tells whether the move is final (1) or not (0).
    public int[] makeMove() {
        return null;
    }

    //Called when another AI shows this AI a card OR the other way around.
    public void wasShownCard(Card card, int otherPlayer) {
        if (sharedInformation[otherPlayers.indexOf(otherPlayer)].contains(card)) {
            return;
        }
        sharedInformation[otherPlayers.indexOf(otherPlayer)].add(card);
        model = baseModel.copy();
        for (int i : otherPlayers) {
            if (sharedInformation[otherPlayers.indexOf(i)].isEmpty()) {
                continue;
            }
            Not[] expressions = new Not[sharedInformation[otherPlayers.indexOf(i)].size()];
            for (int j = 0; j < expressions.length; j++) {
                Card card1 = sharedInformation[otherPlayers.indexOf(i)].get(j);
                expressions[j] = new Not(new Predicate(card1.getCardType(), card1.getCardNumber()));
            }
            Expression expression = new And(expressions);
            EventModel event = new EventModel(nPlayers);
            EventModel.Node root = event.createNode(expression);
            event.createEdge(root, root, false, new int[] {playerIndex, i});
            EventModel.Node trueNode = event.createNode(new Value(true));
            List<Integer> allOtherPlayers = new ArrayList<>();
            for (int j = 0; j < nPlayers; j++) {
                if (j != playerIndex && j != i) {
                    allOtherPlayers.add(j);
                }
            }
            int[] otherPlayers = new int[allOtherPlayers.size()];
            for (int j = 0; j < allOtherPlayers.size(); j++) {
                otherPlayers[j] = allOtherPlayers.get(j);
            }
            event.createEdge(root, trueNode, false, otherPlayers);
            event.createEdge(trueNode, trueNode, false);
            model.productUpdate(event, trueWorld);
        }
        /*Expression expression = new Not(new Predicate(card.getCardType(), card.getCardNumber()));
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
        model.productUpdate(event, trueWorld);*/
    }

    //Probably doesn't work at the moment.
    //Called when two other players show a card to each other.
    public void otherPlayersShownCard(Card[] possibleCardsShown, int otherPlayer1, int otherPlayer2) {
        int[] t = new int[] {possibleCardsShown[0].getCardType(), possibleCardsShown[1].getCardType(), possibleCardsShown[2].getCardType()};
        int[] n = new int[] {possibleCardsShown[0].getCardNumber(), possibleCardsShown[1].getCardNumber(), possibleCardsShown[2].getCardNumber()};
        Expression publicAnnouncement = new Or(new Not[] {new Not(new Predicate(t[0], n[0])), new Not(new Predicate(t[1], n[1])), new Not(new Predicate(t[2], n[2]))});
        model.publicAnnouncement(publicAnnouncement);
        baseModel.publicAnnouncement(publicAnnouncement);
        /*Expression expression = new And(new Predicate[] {new Predicate(0, trueWorld[0]), new Predicate(1, trueWorld[1]), new Predicate(2, trueWorld[2])});
        EventModel event = new EventModel(nPlayers);
        EventModel.Node root = event.createNode(expression);
        EventModel.Node case1 = event.createNode(new Not(new Predicate(possibleCardsShown[0].getCardType(), possibleCardsShown[0].getCardNumber())));
        EventModel.Node case2 = event.createNode(new Not(new Predicate(possibleCardsShown[1].getCardType(), possibleCardsShown[1].getCardNumber())));
        EventModel.Node case3 = event.createNode(new Not(new Predicate(possibleCardsShown[2].getCardType(), possibleCardsShown[2].getCardNumber())));
        event.createEdge(root, root, false);
        event.createEdge(root, case1, false);
        event.createEdge(root, case2, false);
        event.createEdge(root, case3, false);
        event.createEdge(case1, case1, false, new int[] {otherPlayer1, otherPlayer2});
        event.createEdge(case2, case2, false, new int[] {otherPlayer1, otherPlayer2});
        event.createEdge(case3, case3, false, new int[] {otherPlayer1, otherPlayer2});
        model.productUpdate(event, trueWorld);*/
    }


}
