package cluedo;

public class Card {
    // Names for rooms, weapons and people respectively.
    public static final String[][] cardNames = {
            { "Hall", "Lounge", "Kitchen", "Entrance", "Ballroom", "Dinette", "Bedroom", "Library", "Study" },
            { "Stick", "Dagger", "Pipe", "Revolver", "Rope", "Spanner", "Uzi", "Sword", "Crowbar" },
            { "Rose", "Lauren", "Jones", "Bond", "Scarlet", "Bobby", "Black", "White", "Ben" }
    };

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

    public boolean equals(Object o) {
        if (!(o instanceof Card)) {
            return false;
        }
        return cardType == ((Card) o).cardType && cardNumber == ((Card) o).cardNumber;
    }

    public String toString() {
        return cardNames[cardType][cardNumber];
    }
}
