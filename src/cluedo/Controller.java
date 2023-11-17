package cluedo;

import ai.AI;
import ai.SimpleAI;

public class Controller {
    private Game game;
    private UI ui;
    private AI[] AIPlayers;

    public Controller(Game game, UI ui) {
        this.game = game;
        this.ui = ui;
        Card[][] playerCards = game.getPlayerCards();
        Card[] murdererCards = game.getMurdererCards();
        int[] trueWorld = { murdererCards[0].getCardNumber(), murdererCards[1].getCardNumber(), murdererCards[2].getCardNumber() };
        AIPlayers = new AI[Game.N_PLAYERS];

        // Initialize AI players with 4 simple AIs.
        for (int i = 0; i < Game.N_PLAYERS; i++) {
            AIPlayers[i] = new SimpleAI(playerCards[i], i, Game.N_PLAYERS, Game.N_ROOMS, Game.N_WEAPONS, Game.N_PEOPLE, trueWorld);
        }
        game.setAIPlayers(AIPlayers);
    }

    public void playAIGame() {
        while (!game.isGameOver()) {
            // Get current player
            int currentPlayer = game.getCurrentPlayerToGuess();

            // Make player construct a guess
            int[] guess = AIPlayers[currentPlayer].makeMove();

            // Check if guess final
            boolean finalGuess = guess[0] == 1;

            // Extract guessed cards from AI guess
            Card[] guessedCards = { new Card(0, guess[1]), new Card(1, guess[2]), new Card(2, guess[3]) };

            // Make the guess and get response
            ValueHolder response = game.makeGuess(guessedCards, finalGuess);

            // Print response
            /*
            if (response == null) {
                System.out.println("Response is null");
            } else {
                System.out.println(
                        "Player to see: " + (response.playerToSeeCard + 1) + "\n" +
                                "Player to show: " + (response.playerToShowCard + 1) + "\n" +
                                "Card to show: " + game.cardToString(response.cardToShow) + "\n" +
                                "Guessed cards: " + game.cardToString(response.guessedCards[0]) + " " + game.cardToString(response.guessedCards[1]) + " " + game.cardToString(response.guessedCards[2]) + "\n"
                );
            }*/

            // Use response to update AI knowledge
            if (response != null && response.cardToShow != null) {
                for (int i = 0; i < Game.N_PLAYERS; i++) {

                    // The knowledge of players who was/has shown the card will be updated differently than the others.
                    if (i == response.playerToShowCard) {
                        AIPlayers[i].wasShownCard(response.cardToShow, response.playerToSeeCard);
                    } else if (i == response.playerToSeeCard) {
                        AIPlayers[i].wasShownCard(response.cardToShow, response.playerToShowCard);
                    } else {
                        AIPlayers[i].otherPlayersShownCard(response.guessedCards, response.playerToShowCard, response.playerToSeeCard);
                    }
                }
            }

            // Update UI
            ui.repaint();

            // Wait
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
