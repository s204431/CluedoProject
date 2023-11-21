package ai;

import cluedo.Card;
import expressions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.PossibleWorldModel;
import models.EventModel;

public class EpistemicAI extends AI {

    private PossibleWorldModel baseModel;
    public PossibleWorldModel model;
    public List<Integer> otherPlayers = new ArrayList<>();
    public List<Card>[] sharedInformation; //Shared information with each other players.
    private int[] lastMove = null;
    private boolean lastMoveFinal = false;

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

    public void everyoneWasShownCard(Card card) {
        Expression publicAnnouncement = new Not(new Predicate(card.getCardType(), card.getCardNumber()));
        model.publicAnnouncement(publicAnnouncement);
        baseModel.publicAnnouncement(publicAnnouncement);
    }

    //Returns {isFinal, room, weapon, person}, where isFinal tells whether the move is final (1) or not (0).
    public int[] makeMove() {
        if (lastMoveFinal) {
            return new int[] {1, lastMove[0], lastMove[1], lastMove[2]};
        }
        int[] knownSolution = getKnownSolution();
        if (knownSolution[0] >= 0 && knownSolution[1] >= 0 && knownSolution[2] >= 0) {
            return new int[] {1, knownSolution[0], knownSolution[1], knownSolution[2]};
        }
        //Find out which cards are considered possible.
        List<Integer>[] possibleCards = new List[3];
        for (int i = 0; i < 3; i++) {
            possibleCards[i] = new ArrayList<>();
            if (knownSolution[i] >= 0) {
                possibleCards[i].add(knownSolution[i]);
                continue;
            }
            int amount =  i == 0 ? nRooms : (i == 1 ? nWeapons : nPeople);
            for (int j = 0; j < amount; j++) {
                if (!model.evaluateExpression(new Knows(playerIndex, new Not(new Predicate(i, j))), trueWorld)) {
                    possibleCards[i].add(j);
                }
            }
        }
        Random r = new Random();
        int room = possibleCards[0].get(r.nextInt(possibleCards[0].size()));
        int weapon = possibleCards[1].get(r.nextInt(possibleCards[1].size()));
        int person = possibleCards[2].get(r.nextInt(possibleCards[2].size()));
        lastMove = new int[] {room, weapon, person};
        return new int[] {0, room, weapon, person};
    }

    //Returns {room, weapon, person} if known. Those not known are -1.
    private int[] getKnownSolution() {
        int knownRoom = -1;
        int knownWeapon = -1;
        int knownPerson = -1;
        for (int i = 0; i < nRooms; i++) {
            if (model.evaluateExpression(new Knows(playerIndex, new Predicate(0, i)), trueWorld)) {
                knownRoom = i;
                break;
            }
        }
        for (int i = 0; i < nWeapons; i++) {
            if (model.evaluateExpression(new Knows(playerIndex, new Predicate(1, i)), trueWorld)) {
                knownWeapon = i;
                break;
            }
        }
        for (int i = 0; i < nPeople; i++) {
            if (model.evaluateExpression(new Knows(playerIndex, new Predicate(2, i)), trueWorld)) {
                knownPerson = i;
                break;
            }
        }
        return new int[] {knownRoom, knownWeapon, knownPerson};
    }

    //Shows a card that the player already knows if possible. Otherwise, chooses random card.
    public Card showCard(int playerToShowCardTo, ArrayList<Card> matchingCards) {
        List<Card> knownByOtherPlayer = new ArrayList<>();
        for (Card card : matchingCards) {
            if (model.evaluateExpression(new Knows(playerIndex, new Knows(playerToShowCardTo, new Not(new Predicate(card.getCardType(), card.getCardNumber())))), trueWorld)) {
                knownByOtherPlayer.add(card);
            }
        }
        if (!knownByOtherPlayer.isEmpty()) {
            List<Card> possibilities = getMaxKnownCards(knownByOtherPlayer);
            return possibilities.get(new Random().nextInt(possibilities.size()));
        }
        List<Card> possibilities = getMaxKnownCards(matchingCards);
        return possibilities.get(new Random().nextInt(possibilities.size()));
    }

    //Finds the cards that are already known by the most other players.
    private List<Card> getMaxKnownCards(List<Card> cards) {
        int[] nKnows = new int[cards.size()];
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            for (int j = 0; j < nPlayers; j++) {
                if (j != playerIndex && model.evaluateExpression(new Knows(playerIndex, new Knows(j, new Not(new Predicate(card.getCardType(), card.getCardNumber())))), trueWorld)) {
                    nKnows[i]++;
                }
            }
        }
        int max = -1;
        for (int i = 0; i < nKnows.length; i++) {
            if (nKnows[i] > max) {
                max = nKnows[i];
            }
        }
        List<Card> possibilities = new ArrayList<>();
        for (int i = 0; i < nKnows.length; i++) {
            if (nKnows[i] == max) {
                possibilities.add(cards.get(i));
            }
        }
        return possibilities;
    }

    public Card showEveryoneCard(List<Card> cards) {
        int[] nKnown = new int[cards.size()];
        for (int i = 0; i < cards.size(); i++) {
            for (int j = 0; j < nPlayers; j++) {
                if (j != playerIndex && model.evaluateExpression(new Knows(playerIndex, new Knows(j, new Not(new Predicate(cards.get(i).getCardType(), cards.get(i).getCardNumber())))), trueWorld)) {
                    nKnown[i]++;
                }
            }
        }
        int max = -1;
        for (int i = 0; i < nKnown.length; i++) {
            if (nKnown[i] > max) {
                max = nKnown[i];
            }
        }
        List<Card> cardsToChooseFrom = new ArrayList<>();
        for (int i = 0; i < cards.size(); i++) {
            if (nKnown[i] == max) {
                cardsToChooseFrom.add(cards.get(i));
            }
        }
        return cardsToChooseFrom.get(new Random().nextInt(cardsToChooseFrom.size()));
    }

    //Indicates that last time this player made a move no one showed a card.
    public void noOneShowedCard() {
        for (Card card : cards) {
            if (card.getCardNumber() == lastMove[card.getCardType()]) {
                return;
            }
        }
        lastMoveFinal = true;
    }


}
