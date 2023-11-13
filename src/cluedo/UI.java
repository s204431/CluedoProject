package cluedo;

import javax.swing.*;
import java.awt.*;

public class UI extends JPanel {
    public static final int CARD_WIDTH = 100;
    public static final int CARD_HEIGHT = 200;
    public static final int SMALL_CARD_WIDTH = 75;
    public static final int SMALL_CARD_HEIGHT = 125;

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

        // Paint current players cards
        g.setFont(new Font("Arial", Font.BOLD, 20));
        int currentPlayer = game.getCurrentPlayerToGuess();
        for (int i = 0; i < playerCards[currentPlayer].length; i++) {

            // Draw card outline
            int x = 50 + (20 + CARD_WIDTH) * (i % 3);
            int y = 100 + (20 + CARD_HEIGHT) * (i / 3);
            g2.setColor(Color.BLACK);
            g2.fillRect(x, y, CARD_WIDTH, CARD_HEIGHT);

            // Draw card text
            Card card = playerCards[currentPlayer][i];
            String text = game.cardToString(card);
            int textWidth = g.getFontMetrics().stringWidth(text);
            g2.setColor(Color.WHITE);
            g2.drawString(text, x + CARD_WIDTH / 2 - textWidth / 2, y + CARD_HEIGHT / 2);
        }

        // Paint rest of players cards
        g.setFont(new Font("Arial", Font.BOLD, 15));
        int counter = 0;
        for (int i = 0; i < playerCards.length; i++) {

            // Skip current player (already drawn)
            if (i == currentPlayer) {
                continue;
            }

            for (int j = 0; j < playerCards[i].length; j++) {

                // Draw small card outline
                int x = 150 + (20 + CARD_WIDTH) * 3 + (15 + SMALL_CARD_WIDTH) * j;
                int y = 100 + 200 * counter;
                g2.setColor(Color.BLACK);
                g2.fillRect(x, y, SMALL_CARD_WIDTH, SMALL_CARD_HEIGHT);

                // Draw card text
                Card card = playerCards[i][j];
                String text = game.cardToString(card);
                int textWidth = g.getFontMetrics().stringWidth(text);
                g2.setColor(Color.WHITE);
                g2.drawString(text, x + SMALL_CARD_WIDTH / 2 - textWidth / 2, y + SMALL_CARD_HEIGHT / 2);
            }
            counter++;
        }
    }
}
