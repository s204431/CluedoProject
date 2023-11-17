package cluedo;

public class ValueHolder {
    public int playerToSeeCard;
    public int playerToShowCard;
    public Card cardToShow;
    public Card[] guessedCards;

    public ValueHolder(int playerToSeeCard, int playerToShowCard, Card cardToShow, Card[] guessedCards) {
        this.playerToSeeCard = playerToSeeCard;
        this.playerToShowCard = playerToShowCard;
        this.cardToShow = cardToShow;
        this.guessedCards = guessedCards;
    }

}
