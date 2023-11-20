package ai;

import cluedo.Card;
import expressions.Expression;
import expressions.Not;
import expressions.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleAI extends AI {

    //Simple AI that simply keeps track of the cards that have been ruled out.

    public List<Integer>[] stillPossibleCards;
    private int[] lastGuess = null;
    boolean lastGuessFinal = false;

    public SimpleAI(Card[] cards, int playerIndex, int nPlayers, int nRooms, int nWeapons, int nPeople, int[] trueWorld) {
        super(cards, playerIndex, nPlayers, nRooms, nWeapons, nPeople, trueWorld);
        //Initialize stillPossibleCards.
        stillPossibleCards = new List[] { new ArrayList<>(), new ArrayList<>(), new ArrayList<>() };

        for (int i = 0; i < nRooms; i++) {
            stillPossibleCards[0].add(i);
        }
        for (int i = 0; i < nWeapons; i++) {
            stillPossibleCards[1].add(i);
        }
        for (int i = 0; i < nPeople; i++) {
            stillPossibleCards[2].add(i);
        }
        //Rule out own cards.
        for (Card card : cards) {
            stillPossibleCards[card.getCardType()].remove((Integer)card.getCardNumber());
        }
    }

    public void wasShownCard(Card card, int otherPlayer) {
        //Rule out given card.
        stillPossibleCards[card.getCardType()].remove((Integer) card.getCardNumber());
    }

    public void everyoneWasShownCard(Card card) {
        stillPossibleCards[card.getCardType()].remove((Integer)card.getCardNumber());
    }

    //Returns {isFinal, room, weapon, person}, where isFinal tells whether the move is final (1) or not (0).
    public int[] makeMove() {
        //Check if we know the correct answer.
        if (stillPossibleCards[0].size() == 1 && stillPossibleCards[1].size() == 1 && stillPossibleCards[2].size() == 1) {
            return new int[] {1, stillPossibleCards[0].get(0), stillPossibleCards[1].get(0), stillPossibleCards[2].get(0)};
        }
        if (lastGuessFinal) {
            return new int[] {1, lastGuess[0], lastGuess[1], lastGuess[2]};
        }
        //Choose random cards from still possible cards since we do not know the correct answer.
        Random r = new Random();
        int card1 = stillPossibleCards[0].get(r.nextInt(stillPossibleCards[0].size()));
        int card2 = stillPossibleCards[1].get(r.nextInt(stillPossibleCards[1].size()));
        int card3 = stillPossibleCards[2].get(r.nextInt(stillPossibleCards[2].size()));
        lastGuess = new int[] {card1, card2, card3};
        return new int[] {0, card1, card2, card3};
    }

    public Card showCard(int playerToShowCardTo, ArrayList<Card> matchingCards) {
        //Show a random card.
        return matchingCards.get(new Random().nextInt(matchingCards.size()));
    }

    public Card showEveryoneCard(List<Card> cards) {
        return cards.get(new Random().nextInt(cards.size()));
    }

    //Indicates that last time this player made a move no one showed a card.
    public void noOneShowedCard() {
        lastGuessFinal = true;
    }
}
