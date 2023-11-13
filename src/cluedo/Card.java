package cluedo;

public class Card {
    private int cardType;
    private int cardNumber;

    public Card(int cardType, int cardNumber) {
        this.cardType = cardType;
        this.cardNumber = cardNumber;
    }

    public int getCardType() {
        return cardType;
    }

    public int getCardNumber() {
        return cardNumber;
    }
}
