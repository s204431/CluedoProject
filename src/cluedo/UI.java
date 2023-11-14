package cluedo;

import javax.swing.*;
import java.awt.*;

public class UI extends JPanel {
    public static final int CARD_WIDTH = 100;
    public static final int CARD_HEIGHT = 200;
    public static final int SMALL_CARD_WIDTH = 75;
    public static final int SMALL_CARD_HEIGHT = 125;

    // Names for rooms, weapons and people respectively.
    public static final String[][] cardNames = {
            { "Hall", "Lounge", "Kitchen", "Entrance", "Ballroom", "Dinette", "Bedroom", "Library", "Study" },
            { "Stick", "Dagger", "Pipe", "Revolver", "Rope", "Spanner", "Uzi", "Sword", "Crowbar" },
            { "Rose", "Lauren", "Jones", "Bond", "Scarlet", "Bobby", "Black", "White", "Ben" }
    };

    // Colors for cards based on card type
    public static final Color[] cardColors = { new Color(0, 0, 255), new Color(200, 0, 0), new Color(0, 100, 0) };

    // Strings for card types (shown on cards)
    public static final String[] cardTypeStrings = { "Room", "Weapon", "Person" };

    private Game game;

    public UI(Game game) {
        this.game = game;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = 1000;
        double height = 700;

        setPreferredSize(new Dimension((int)width, (int)height));

        JFrame frame = new JFrame("Simplified Cluedo");
        frame.getContentPane().add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        Card[][] playerCards = game.getPlayerCards();

        // Paint background
        g2.setColor(Color.GRAY);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Draw lines separating players
        g2.setStroke(new BasicStroke(6));
        g2.setColor(Color.BLACK);
        int x1 = getWidth() * 4 / 9;
        g2.drawLine(x1, 0, x1, getHeight());
        g2.drawLine(x1, getHeight() / 3, getWidth(), getHeight() / 3);
        g2.drawLine(x1, getHeight() * 2 / 3, getWidth(), getHeight() * 2 / 3);

        // Paint current player text
        int currentPlayer = game.getCurrentPlayerToGuess();
        g2.setFont(new Font("Arial Black", Font.PLAIN, 50));
        String playerText = "Player " + (currentPlayer + 1);
        int playerTextWidth = g2.getFontMetrics().stringWidth(playerText);
        g2.setColor(Color.BLACK);
        g2.drawString(playerText, 50 + (20 + CARD_WIDTH * 3) / 2 - playerTextWidth / 2, 75);

        // Paint current players cards
        for (int i = 0; i < playerCards[currentPlayer].length; i++) {

            // Draw card outline
            int x = 50 + (20 + CARD_WIDTH) * (i % 3);
            int y = 100 + (20 + CARD_HEIGHT) * (i / 3);
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(x, y, CARD_WIDTH, CARD_HEIGHT);
            g2.setStroke(new BasicStroke(4));
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, CARD_WIDTH, CARD_HEIGHT);

            // Draw card text
            g.setFont(new Font("Arial Black", Font.PLAIN, 20));
            Card card = playerCards[currentPlayer][i];
            String text = cardNames[card.getCardType()][card.getCardNumber()];
            int textWidth = g2.getFontMetrics().stringWidth(text);
            g2.setColor(cardColors[card.getCardType()]);
            g2.drawString(text, x + CARD_WIDTH / 2 - textWidth / 2, y + CARD_HEIGHT / 2);

            // Draw card type text
            g.setFont(new Font("Arial", Font.PLAIN, 15));
            text = cardTypeStrings[card.getCardType()];
            textWidth = g.getFontMetrics().stringWidth(text);
            g2.setColor(Color.BLACK);
            g2.drawString(text, x + CARD_WIDTH / 2 - textWidth / 2, y + CARD_HEIGHT / 10);
        }

        // Paint rest of players cards
        int counter = 0;
        for (int i = 0; i < playerCards.length; i++) {

            // Skip current player (already drawn)
            if (i == currentPlayer) {
                continue;
            }

            // Paint player text
            g2.setFont(new Font("Arial Black", Font.PLAIN, 25));
            playerText = "Player " + (i + 1);
            playerTextWidth = g2.getFontMetrics().stringWidth(playerText);
            g2.setColor(Color.BLACK);
            g2.drawString(playerText, 150 + (20 + CARD_WIDTH) * 3 + (15 + SMALL_CARD_WIDTH * 5) / 2 - playerTextWidth / 2, 50 + 225 * counter);

            for (int j = 0; j < playerCards[i].length; j++) {

                // Draw small card outline
                g.setFont(new Font("Arial Black", Font.PLAIN, 14));
                int x = 150 + (20 + CARD_WIDTH) * 3 + (15 + SMALL_CARD_WIDTH) * j;
                int y = 75 + 225 * counter;
                g2.setColor(Color.LIGHT_GRAY);
                g2.fillRect(x, y, SMALL_CARD_WIDTH, SMALL_CARD_HEIGHT);
                g2.setColor(Color.BLACK);
                g2.drawRect(x, y, SMALL_CARD_WIDTH, SMALL_CARD_HEIGHT);

                // Draw card text
                Card card = playerCards[i][j];
                String text = cardNames[card.getCardType()][card.getCardNumber()];
                int textWidth = g.getFontMetrics().stringWidth(text);
                g2.setColor(cardColors[card.getCardType()]);
                g2.drawString(text, x + SMALL_CARD_WIDTH / 2 - textWidth / 2, y + SMALL_CARD_HEIGHT / 2);

                // Draw card type text
                g.setFont(new Font("Arial", Font.PLAIN, 14));
                text = cardTypeStrings[card.getCardType()];
                textWidth = g.getFontMetrics().stringWidth(text);
                g2.setColor(Color.BLACK);
                g2.drawString(text, x + SMALL_CARD_WIDTH / 2 - textWidth / 2, y + SMALL_CARD_HEIGHT / 8);
            }

            counter++;
        }
    }
}
