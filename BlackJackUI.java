import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class BlackJackUI {
    private BlackJackModel model;
    
    // UI components
    private int boardWidth = 600;
    private int boardHeight = boardWidth;
    private int cardWidth = 110;
    private int cardHeight = 154;
    
    private JFrame frame;
    private GamePanel gamePanel;
    private JPanel buttonPanel;
    private JPanel betPanel;
    private JButton hitButton;
    private JButton stayButton;
    private JButton newGameButton;
    private JButton placeBetButton;
    private JTextField betAmountField;
    private JLabel moneyLabel;
    private JLabel betLabel;
    
    public BlackJackUI(BlackJackModel model) {
        this.model = model;
        initializeUI();
    }
    
    private void initializeUI() {
        // Create frame
        frame = new JFrame("Black Jack");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create game panel
        gamePanel = new GamePanel();
        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53, 101, 77));
        frame.add(gamePanel);
        
        // Create betting panel at the top
        betPanel = new JPanel();
        betPanel.setBackground(new Color(53, 101, 77));
        
        // Money display
        moneyLabel = new JLabel("Money: $" + model.getPlayerMoney());
        moneyLabel.setForeground(Color.WHITE);
        moneyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        betPanel.add(moneyLabel);
        
        // Current bet display
        betLabel = new JLabel("Current Bet: $0");
        betLabel.setForeground(Color.WHITE);
        betLabel.setFont(new Font("Arial", Font.BOLD, 16));
        betPanel.add(betLabel);
        
        // Bet amount field
        betAmountField = new JTextField(5);
        betPanel.add(new JLabel("Bet Amount: $"));
        betPanel.add(betAmountField);
        
        // Place bet button
        placeBetButton = new JButton("Place Bet");
        placeBetButton.setFocusable(false);
        placeBetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int betAmount = Integer.parseInt(betAmountField.getText());
                    if (model.placeBet(betAmount)) {
                        updateBetDisplay();
                        hitButton.setEnabled(true);
                        stayButton.setEnabled(true);
                        placeBetButton.setEnabled(false);
                        betAmountField.setEnabled(false);
                    } else {
                        JOptionPane.showMessageDialog(frame, 
                            "Invalid bet amount. Must be between 1 and " + model.getPlayerMoney(), 
                            "Invalid Bet", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, 
                        "Please enter a valid number", 
                        "Invalid Input", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        betPanel.add(placeBetButton);
        
        frame.add(betPanel, BorderLayout.NORTH);
        
        // Create button panel
        buttonPanel = new JPanel();
        
        // Hit button
        hitButton = new JButton("Hit");
        hitButton.setFocusable(false);
        hitButton.setEnabled(false); // Disabled until bet is placed
        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.playerHit();
                updateUI();
                
                if (model.isGameOver()) {
                    hitButton.setEnabled(false);
                    stayButton.setEnabled(false);
                    newGameButton.setEnabled(true);
                    updateMoneyDisplay();
                }
            }
        });
        buttonPanel.add(hitButton);
        
        // Stay button
        stayButton = new JButton("Stay");
        stayButton.setFocusable(false);
        stayButton.setEnabled(false); // Disabled until bet is placed
        stayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.playerStay();
                updateUI();
                
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);
                newGameButton.setEnabled(true);
                updateMoneyDisplay();
            }
        });
        buttonPanel.add(stayButton);
        
        // New Game button
        newGameButton = new JButton("New Game");
        newGameButton.setFocusable(false);
        newGameButton.setEnabled(false);
        newGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.startNewGame();
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);
                newGameButton.setEnabled(false);
                placeBetButton.setEnabled(true);
                betAmountField.setEnabled(true);
                betAmountField.setText("");
                updateBetDisplay();
                updateUI();
            }
        });
        buttonPanel.add(newGameButton);
        
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
    
    private void updateMoneyDisplay() {
        moneyLabel.setText("Money: $" + model.getPlayerMoney());
    }
    
    private void updateBetDisplay() {
        betLabel.setText("Current Bet: $" + model.getCurrentBet());
    }
    
    public void updateUI() {
        gamePanel.repaint();
    }
    
    // Inner class for the game panel
    private class GamePanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            try {
                // Draw hidden card
                Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                if (model.isGameOver()) {
                    hiddenCardImg = new ImageIcon(getClass().getResource(model.getHiddenCard().getImagePath())).getImage();
                }
                g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight, null);
                
                // Draw dealer's hand
                for (int i = 0; i < model.getDealerHand().size(); i++) {
                    Card card = model.getDealerHand().get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, cardWidth + 25 + (cardWidth + 5)*i, 20, cardWidth, cardHeight, null);
                }
                
                // Draw player's hand
                for (int i = 0; i < model.getPlayerHand().size(); i++) {
                    Card card = model.getPlayerHand().get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, 20 + (cardWidth + 5)*i, 320, cardWidth, cardHeight, null);
                }
                
                // Draw dealer's hand value
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.setColor(Color.white);
                
                if (model.isGameOver()) {
                    g.drawString("Value: " + model.getDealerHandValue(), 20, 200);
                } else {
                    g.drawString("Value: " + model.getDealerVisibleValue(), 20, 200);
                }
                
                // Draw player's hand value
                g.drawString("Value: " + model.getPlayerHandValue(), 20, 300);
                
                // Draw game result if game is over
                if (model.isGameOver()) {
                    g.setFont(new Font("Arial", Font.BOLD, 30));
                    g.setColor(Color.white);
                    String resultText = model.getGameResult();
                    
                    // Add win/loss amount to result text
                    if (resultText.equals("You Win!")) {
                        resultText += " +$" + model.getCurrentBet();
                    } else if (resultText.equals("You Lose!")) {
                        resultText += " -$" + model.getCurrentBet();
                    }
                    
                    g.drawString(resultText, 220, 250);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
} 