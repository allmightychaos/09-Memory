import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GameWindow extends JFrame {
    private GameBoard gameBoard;
    private ArrayList<Player> players = new ArrayList<>();
    private int currentPlayerIndex = 0;

    public GameWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setTitle("Memory Spiel");

        setupPlayersAndBoard();
    }

    private void setupPlayersAndBoard() {
        // Dialog zur Spieler- und Paarauswahl
        JDialog setupDialog = new JDialog(this, "Spiel Setup", true);
        setupDialog.setLayout(new FlowLayout());
        setupDialog.setSize(300, 200);

        SpinnerNumberModel numberModel = new SpinnerNumberModel(1, 1, 4, 1);
        JSpinner playerCountSpinner = new JSpinner(numberModel);
        setupDialog.add(new JLabel("Anzahl der Spieler:"));
        setupDialog.add(playerCountSpinner);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int playerCount = (Integer) playerCountSpinner.getValue();
                setupPlayers(playerCount);
                setupDialog.dispose();
                setupGameBoard();
            }
        });
        setupDialog.add(okButton);

        setupDialog.setVisible(true);
    }

    private void setupPlayers(int playerCount) {
        for (int i = 0; i < playerCount; i++) {
            String playerName = null;
            while (playerName == null || playerName.trim().isEmpty()) {
                playerName = JOptionPane.showInputDialog(this, "Name des Spielers " + (i + 1) + ":");
                if (playerName == null || playerName.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Der Name darf nicht leer sein. Gib einen gültigen Namen ein!", "Ungültiger Name", JOptionPane.ERROR_MESSAGE);
                }
            }
            players.add(new Player(playerName.trim()));
        }
    }

    private void setupGameBoard() {
        String[] options = {"3 Icons (6 Felder)", "4 Icons (8 Felder)", "5 Icons (10 Felder)",
                "6 Icons (12 Felder)", "7 Icons (14 Felder)", "8 Icons (16 Felder)",
                "9 Icons (18 Felder)", "10 Icons (20 Felder)"};
        String selectedOption = (String) JOptionPane.showInputDialog(
                this,
                "Wählen Sie die Anzahl der Icons:",
                "Icon Auswahl",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[5] // 16 fields as default selected option
        );

        int numberOfPairs;
        switch (selectedOption) {
            case "3 Icons (6 Felder)": numberOfPairs = 3; break;
            case "4 Icons (8 Felder)": numberOfPairs = 4; break;
            case "5 Icons (10 Felder)": numberOfPairs = 5; break;
            case "6 Icons (12 Felder)": numberOfPairs = 6; break;
            case "7 Icons (14 Felder)": numberOfPairs = 7; break;
            case "8 Icons (16 Felder)": numberOfPairs = 8; break;
            case "9 Icons (18 Felder)": numberOfPairs = 9; break;
            case "10 Icons (20 Felder)": numberOfPairs = 10; break;
            default: return;
        }

        gameBoard = new GameBoard(numberOfPairs, players);
        getContentPane().add(gameBoard, BorderLayout.CENTER);
        gameBoard.updateTitle();
        validate();
    }
}
