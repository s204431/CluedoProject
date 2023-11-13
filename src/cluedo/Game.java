package cluedo;

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

    private boolean gameOver;

    // Constructor for a game state.
    public Game() {
        resetGame();
        //printMurdererCards();
        //printPlayerCards();
    }

    // Makes a guess for a player.
    public void makeGuess(Card[] guessedCards, boolean finalGuess) {
        // Player wins or loses if guess is final.
        if (finalGuess) {
            int guessedRoom = guessedCards[0].getCardNumber();
            int guessedWeapon = guessedCards[1].getCardNumber();
            int guessedPerson = guessedCards[2].getCardNumber();

            int murderRoom = murdererCards[0].getCardNumber();
            int murderWeapon = murdererCards[1].getCardNumber();
            int murderPerson = murdererCards[2].getCardNumber();

            // Right final guess
            if (guessedRoom == murderRoom && guessedWeapon == murderWeapon && guessedPerson == murderPerson) {
                gameOver = true;
            } else {    // Wrong final guess
                activePlayers[currentPlayerToGuess] = false;
            }

        } else {    // Not a final guess

            // The next player with a card matching the guess has to show one of those cards.
            int nextPlayer = currentPlayerToGuess;
            ArrayList<Card> matchingCards = new ArrayList<>();
            while (matchingCards.size() == 0) {
                nextPlayer = getNextPlayer(nextPlayer);

                // Check if next player has one or more cards matching the guess.
                for (int i = 0; i < N_PLAYER_CARDS; i++) {
                    Card card = playerCards[nextPlayer][i];
                    int type = card.getCardType();
                    int number = card.getCardNumber();
                    if (guessedCards[type].getCardNumber() == number) {
                        matchingCards.add(card);
                    }
                }
            }

            // Make player choose between matching cards.
            Card cardToShow = showCard(nextPlayer, matchingCards);

            // TODO: Show card to other players
            System.out.println("Shown card: " + cardToString(cardToShow));

            // Change next player to move.
            currentPlayerToGuess = getNextPlayer(currentPlayerToGuess);
        }
    }

    // Returns the card that the player wants to show.
    public Card showCard(int nextPlayer, ArrayList<Card> matchingCards) {
        System.out.println("Player " + nextPlayer + " has to show a card. Choose between the following cards:");
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
        for (int i = 0; i < N_PLAYERS; i++) {
            for (int j = 0; j < N_PLAYER_CARDS; j++) {
                playerCards[i][j] = allPlayerCards.get(i * N_PLAYERS + j);
            }
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

    public Card[][] getPlayerCards() {
        return playerCards;
    }

    public int getCurrentPlayerToGuess() {
        return currentPlayerToGuess;
    }
}
