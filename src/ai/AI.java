package ai;

import cluedo.Card;

import java.util.ArrayList;

public abstract class AI {

    public Card[] cards;
    public int playerIndex;
    protected int nPlayers;
    protected int nRooms;
    protected int nWeapons;
    protected int nPeople;
    public int[] trueWorld;

    public AI(Card[] cards, int playerIndex, int nPlayers, int nRooms, int nWeapons, int nPeople, int[] trueWorld) {
        this.cards = cards;
        this.playerIndex = playerIndex;
        this.nPlayers = nPlayers;
        this.nRooms = nRooms;
        this.nWeapons = nWeapons;
        this.nPeople = nPeople;
        this.trueWorld = trueWorld;
    }

    //Returns {isFinal, room, weapon, person}, where isFinal tells whether the move is final (1) or not (0).
    public int[] makeMove() {
        return null;
    }

    public void wasShownCard(Card card, int otherPlayer) {

    }

    public void otherPlayersShownCard(Card[] possibleCardsShown, int otherPlayer1, int otherPlayer2) {

    }

    public Card showCard(int playerToShowCardTo, ArrayList<Card> matchingCards) {
        return null;
    }

}
