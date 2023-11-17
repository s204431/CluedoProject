package cluedo;

import ai.AI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Game {
    /*
    This class represents the state of the game and handles the game logic like assigning cards to the players and so on.
     */

    // Constants to define number of players, rooms, weapons and people.
    public static final int N_PLAYERS = 4;
    public static final int N_ROOMS = 9;
    public static final int N_WEAPONS = 7;
    public static final int N_PEOPLE = 7;
    public static final int N_PLAYER_CARDS = (N_ROOMS + N_WEAPONS + N_PEOPLE - 3) / N_PLAYERS;

    // Murderer is represented by 3 cards.
    private Card[] murdererCards;

    // Array of players, where a player is represented by a number of cards.
    private Card[][] playerCards;

    // Current players turn
    private int currentPlayerToGuess;
    private boolean[] activePlayers;

    // Array of AI players
    private AI[] AIPlayers = { null, null, null, null };

    private boolean gameOver;
    private int round;

    // Constructor for a game state.
    public Game() {
        resetGame();
    }

    // Makes a guess for a player.
    public ValueHolder makeGuess(Card[] guessedCards, boolean finalGuess) {
        int guessedRoom = guessedCards[0].getCardNumber();
        int guessedWeapon = guessedCards[1].getCardNumber();
        int guessedPerson = guessedCards[2].getCardNumber();

        int murderRoom = murdererCards[0].getCardNumber();
        int murderWeapon = murdererCards[1].getCardNumber();
        int murderPerson = murdererCards[2].getCardNumber();

        // Print round and guess
        System.out.println(
                "\n" +
                "Round: " + round++ + "\n" +
                "Player " + (currentPlayerToGuess+1) + " has guessed " +
                Card.cardNames[0][guessedRoom] + ", " +
                Card.cardNames[1][guessedWeapon] + ", " +
                Card.cardNames[2][guessedPerson] + "."
        );

        // Player wins or loses if guess is final.
        if (finalGuess) {
            System.out.println("Guess is final");

            // Check if guess is right or wrong
            if (guessedRoom == murderRoom && guessedWeapon == murderWeapon && guessedPerson == murderPerson) {
                gameOver = true;
                System.out.println("PLAYER " + (currentPlayerToGuess+1) + " WINS!");
            } else {    // Wrong
                activePlayers[currentPlayerToGuess] = false;
                System.out.println("Player " + (currentPlayerToGuess+1) + " has lost due to a wrong final guess.");
            }

            // TODO: Do people show cards when guess is final?
            return null;

        } else {    // Not a final guess

            // The next player with a card matching the guess has to show one of those cards.
            int playerToShowCard = currentPlayerToGuess;
            ArrayList<Card> matchingCards = new ArrayList<>();
            while (matchingCards.size() == 0) {
                playerToShowCard = getNextPlayer(playerToShowCard);

                // Jump out if no other player had matching cards
                if (playerToShowCard == currentPlayerToGuess) {
                    break;
                }

                // Check if next player has one or more cards matching the guess.
                for (int i = 0; i < N_PLAYER_CARDS; i++) {
                    Card card = playerCards[playerToShowCard][i];
                    int type = card.getCardType();
                    int number = card.getCardNumber();
                    if (guessedCards[type].getCardNumber() == number) {
                        matchingCards.add(card);
                    }
                }
            }

            // If only current player has matching cards, skip turn
            if (playerToShowCard == currentPlayerToGuess) {
                System.out.println("No one showed a card.");
                return new ValueHolder(currentPlayerToGuess, playerToShowCard, null, guessedCards);
            }

            Card cardToShow;
            if (matchingCards.size() == 1) {
                cardToShow = matchingCards.get(0);
            } else {    // More than one matching card

                // Check if player is human or AI
                if (AIPlayers[playerToShowCard] == null) {

                    // Make player choose between matching cards.
                    cardToShow = showCard(playerToShowCard, matchingCards);

                } else {    // Player is AI

                    // Make AI choose between matching cards
                    cardToShow = AIPlayers[playerToShowCard].showCard(currentPlayerToGuess, matchingCards);
                }
            }

            // Show card
            System.out.println("Player " + (playerToShowCard+1) + " has shown the card " + Card.cardNames[cardToShow.getCardType()][cardToShow.getCardNumber()] + " to player " + (currentPlayerToGuess+1));

            // Change next player to move.
            int temp = currentPlayerToGuess;
            currentPlayerToGuess = getNextPlayer(currentPlayerToGuess);

            return new ValueHolder(temp, playerToShowCard, cardToShow, guessedCards);
        }
    }

    // Returns the card that the player wants to show.
    public Card showCard(int nextPlayer, ArrayList<Card> matchingCards) {
        System.out.println("Player " + (nextPlayer+1) + " has to show a card. Choose between the following cards:");
        for (int i = 0; i < matchingCards.size(); i++) {
            System.out.print("Card " + (i+1) + ": " + cardToString(matchingCards.get(i)) + "   ");
        }
        System.out.println();
        System.out.println("Choose card to show: ");

        Scanner scan = new Scanner(System.in);
        int idx = 0;
        while (idx <= 0 || idx > matchingCards.size()) {
            while (!scan.hasNextInt()) {
                System.out.println("Input is not a number.");
                scan.nextLine();
            }
            idx = scan.nextInt();
        }

        return matchingCards.get(idx - 1);
    }

    // Returns the next player to move.
    public int getNextPlayer(int currentPlayer) {
        // Increment player to move
        int nextPlayer = currentPlayer + 1;
        if (nextPlayer == N_PLAYERS) {
            nextPlayer = 0;
        }

        // Increment player to move until and active player is found.
        // (In the case that one or more players have made a wrong final guess and lost already)
        while (!activePlayers[nextPlayer]) {
            nextPlayer++;
            if (nextPlayer == N_PLAYERS) {
                nextPlayer = 0;
            }
        }

        return nextPlayer;
    }

    // Resets the game state by choosing a murderer and assigning cards to the players.
    public void resetGame() {
        // Initialize player to move, active players, murderer's cards and players' cards
        currentPlayerToGuess = 0;
        activePlayers = new boolean[N_PLAYERS];
        for (int i = 0; i < N_PLAYERS; i++) {
            activePlayers[i] = true;
        }
        murdererCards = new Card[3];
        playerCards = new Card[N_PLAYERS][N_PLAYER_CARDS];
        gameOver = false;
        round = 1;

        // Choose random murderer.
        Random random = new Random();
        int room = random.nextInt(N_ROOMS);
        int weapon = random.nextInt(N_WEAPONS);
        int person = random.nextInt(N_PEOPLE);
        murdererCards[0] = new Card(0, room);
        murdererCards[1] = new Card(1, weapon);
        murdererCards[2] = new Card(2, person);

        // Shuffle the rest of the cards.
        ArrayList<Card> allPlayerCards = new ArrayList<>();
        for (int i = 0; i < N_ROOMS; i++) {
            if (i == room) {
                continue;
            }
            allPlayerCards.add(new Card(0, i));
        }

        for (int i = 0; i < N_WEAPONS; i++) {
            if (i == weapon) {
                continue;
            }
            allPlayerCards.add(new Card(1, i));
        }

        for (int i = 0; i < N_PEOPLE; i++) {
            if (i == person) {
                continue;
            }
            allPlayerCards.add(new Card(2, i));
        }
        Collections.shuffle(allPlayerCards);

        // Assign the shuffled cards to the players.
        int cardCounter = 0;
        int playerCounter = 0;
        while (cardCounter < allPlayerCards.size()) {
            while (playerCounter < N_PLAYERS) {
                playerCards[playerCounter][cardCounter % N_PLAYER_CARDS] = allPlayerCards.get(cardCounter);
                playerCounter++;
                cardCounter++;
            }
            playerCounter = 0;
        }
    }

    // Prints the cards of the murderer.
    public void printMurdererCards() {
        System.out.println("Murderer's cards:");
        for (int i = 0; i < 3; i++) {
            Card card = murdererCards[i];
            int type = card.getCardType();
            if (type == 0) {
                System.out.print("Room ");
            } else if (type == 1) {
                System.out.print("Weapon ");
            } else if (type == 2) {
                System.out.print("Person ");
            }
            System.out.print(card.getCardNumber() + "   ");
        }
        System.out.println();
    }

    // Prints the cards of the players.
    public void printPlayerCards() {
        System.out.println("Players' cards:");
        for (int i = 0; i < N_PLAYERS; i++) {
            System.out.print("Player " + i + ": ");
            for (int j = 0; j < N_PLAYER_CARDS; j++) {
                Card card = playerCards[i][j];
                int type = card.getCardType();
                int number = card.getCardNumber();
                if (type == 0) {
                    System.out.print("Room ");
                } else if (type == 1) {
                    System.out.print("Weapon ");
                } else if (type == 2) {
                    System.out.print("Person ");
                }
                System.out.print(number + "   ");
            }
            System.out.println();
        }
    }

    // Returns the string representation of a card.
    public String cardToString(Card card) {
        if (card == null) {
            return "null";
        }

        String s = "";
        int cardType = card.getCardType();
        int cardNumber = card.getCardNumber();
        if (cardType == 0) {
            s += "Room ";
        } else if (cardType == 1) {
            s += "Weapon ";
        } else {
            s += "Person ";
        }

        s += "" + cardNumber;
        return s;
    }

    // Getters and setters
    public Card[][] getPlayerCards() {
        return playerCards;
    }

    public int getCurrentPlayerToGuess() {
        return currentPlayerToGuess;
    }

    //For testing.
    public Card[] getMurdererCards() {
        return murdererCards;
    }

    public void setAIPlayers(AI[] AIPlayers) {
        this.AIPlayers = AIPlayers;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
