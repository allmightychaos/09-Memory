import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GameBoard extends JPanel {
    private ArrayList<Card> cards = new ArrayList<>();
    private int pairCount;
    private int foundPairs;
    private ArrayList<Player> players;
    private int currentPlayerIndex;
    private Card firstSelectedCard = null;
    private Card secondSelectedCard = null;

    public GameBoard(int pairCount, ArrayList<Player> players) {
        this.pairCount = pairCount;
        this.players = players;
        this.currentPlayerIndex = 0;
        this.foundPairs = 0;
        initializeBoard();
    }

    private void initializeBoard() {
        ArrayList<Icon> icons = loadIcons(pairCount);

        setLayout(new GridLayout(0, calculateColumns(pairCount), 10, 10));
        for (int i = 0; i < pairCount * 2; i++) {
            Card card = new Card(icons.get(i % pairCount), createBackIcon());
            card.addActionListener(new CardFlipListener());
            cards.add(card);
            add(card);
        }

        Collections.shuffle(cards);
        for (Card card : cards) {
            add(card);
        }
    }

    private ArrayList<Icon> loadIcons(int pairCount) {
        String[] iconNames = {
                "dice-multiple.png",
                "ghost.png",
                "home-city.png",
                "ice-cream.png",
                "lightbulb-on.png",
                "lighthouse.png",
                "television-classic.png",
                "train.png",
                "tshirt-v.png",
                "weather-lightning-rainy.png"
        };

        ArrayList<Icon> icons = new ArrayList<>();
        for (int i = 0; i < pairCount; i++) {
            URL imageUrl = getClass().getResource("/" + iconNames[i]);
            ImageIcon icon = new ImageIcon(imageUrl);
            icon.setDescription(iconNames[i]);
            icons.add(icon);
        }
        return icons;
    }

    private Icon createBackIcon() {
        int width = 48;
        int height = 48;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return new ImageIcon(image);
    }

    private int calculateColumns(int pairCount) {
        switch (pairCount) {
            case 3: return 2;
            case 4: return 2;
            case 5: return 2;
            case 6: return 3;
            case 7: return 2;
            case 8: return 4;
            case 9: return 3;
            case 10: return 4;
            default: return 4;
        }
    }

    private class CardFlipListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Card clickedCard = (Card) e.getSource();
            if (clickedCard.isFlipped() || (firstSelectedCard != null && secondSelectedCard != null)) {
                return;
            }

            clickedCard.flipCard();
            if (firstSelectedCard == null) {
                firstSelectedCard = clickedCard;
            } else if (secondSelectedCard == null) {
                secondSelectedCard = clickedCard;
                checkForMatch();
            }
        }
    }

    private void checkForMatch() {
        boolean isMatch = firstSelectedCard.isMatching(secondSelectedCard);
        if (isMatch) {
            foundPairs++;
            players.get(currentPlayerIndex).incrementScore();

            Timer correctPairTimer = getCorrectPairTimer();
            correctPairTimer.start();
        } else {
            Timer timer = new Timer(200, ae -> {
                firstSelectedCard.flipCard();
                secondSelectedCard.flipCard();
                resetSelectedCards();
            });
            timer.setRepeats(false);
            timer.start();
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        updateTitle();
    }


    private Timer getCorrectPairTimer() {
        Timer correctPairTimer = new Timer(250, ae -> {
            firstSelectedCard.setVisible(false);
            secondSelectedCard.setVisible(false);
            firstSelectedCard.setEnabled(false);
            secondSelectedCard.setEnabled(false);
            resetSelectedCards();
            revalidate();
            repaint();
            if (foundPairs == pairCount) {
                endGame();
            }
        });
        correctPairTimer.setRepeats(false);
        return correctPairTimer;
    }

    private Player determineWinner() {
        return Collections.max(players, Comparator.comparing(Player::getScore));
    }

    private void endGame() {
        players.sort(Comparator.comparing(Player::getScore).reversed());

        int highestScore = players.get(0).getScore();
        long winnersCount = players.stream().filter(p -> p.getScore() == highestScore).count();

        String message = getMessage(winnersCount, highestScore);

        JOptionPane.showMessageDialog(
                GameBoard.this,
                message,
                "Spielende",
                JOptionPane.INFORMATION_MESSAGE
        );

        SwingUtilities.getWindowAncestor(this).dispose();
    }

    private String getMessage(long winnersCount, int highestScore) {
        StringBuilder message;
        if (winnersCount == players.size()) {
            message = new StringBuilder("Das Spiel endet mit einem Unentschieden!");
        } else if (winnersCount > 1) {
            message = new StringBuilder("Die Spieler ");
            for (Player p : players) {
                if (p.getScore() == highestScore) {
                    message.append(p.getName()).append(", ");
                }
            }
            message = new StringBuilder(message.substring(0, message.length() - 2));
            message.append(" gewinnen mit ").append(highestScore).append(" Punkten!");
        } else {
            message = new StringBuilder("Der Gewinner ist " + players.get(0).getName() + " mit " + highestScore + " Punkten!");
        }
        return message.toString();
    }

    private void resetSelectedCards() {
        firstSelectedCard = null;
        secondSelectedCard = null;
    }

    public void updateTitle() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (players.size() > 1) {
            topFrame.setTitle(players.get(currentPlayerIndex).getName() + " - " + players.get(currentPlayerIndex).getScore());
        } else {
            topFrame.setTitle(players.get(0).getName() + " - " + players.get(0).getScore());
        }
    }
}