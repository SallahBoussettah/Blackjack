import java.util.ArrayList;
import java.util.Random;

public class BlackJackModel {
    private ArrayList<Card> deck;
    private Random random = new Random();

    // dealer
    private Card hiddenCard;
    private ArrayList<Card> dealerHand;
    private int dealerSum;
    private int dealerAceCount;

    // player
    private ArrayList<Card> playerHand;
    private int playerSum;
    private int playerAceCount;
    
    // game state
    private boolean gameOver;
    private String gameResult;
    
    // betting system
    private int playerMoney;
    private int currentBet;

    public BlackJackModel() {
        playerMoney = 1000; // Starting money
        currentBet = 0;
        startNewGame();
    }

    public void startNewGame() {
        // deck
        buildDeck();
        shuffleDeck();

        // dealer
        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size() - 1);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size() - 1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        // player
        playerHand = new ArrayList<Card>();
        playerSum = 0;
        playerAceCount = 0;

        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }
        
        // reset game state
        gameOver = false;
        gameResult = "";
        currentBet = 0;
    }

    private void buildDeck() {
        deck = new ArrayList<Card>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < values.length; j++) {
                Card card = new Card(values[j], types[i]);
                deck.add(card);
            }
        }
    }

    private void shuffleDeck() {
        for (int i = 0; i < deck.size(); i++) {
            int j = random.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i, randomCard);
            deck.set(j, currCard);
        }
    }
    
    public boolean placeBet(int betAmount) {
        if (betAmount <= 0 || betAmount > playerMoney) {
            return false;
        }
        
        currentBet = betAmount;
        return true;
    }

    public void playerHit() {
        if (gameOver || currentBet <= 0) return;
        
        Card card = deck.remove(deck.size() - 1);
        playerSum += card.getValue();
        playerAceCount += card.isAce() ? 1 : 0;
        playerHand.add(card);
        
        if (reducePlayerAce() > 21) {
            gameOver = true;
            gameResult = "You Lose!";
            playerMoney -= currentBet;
        }
    }

    public void playerStay() {
        if (gameOver || currentBet <= 0) return;
        
        gameOver = true;
        
        // Dealer draws until 17 or higher
        while (dealerSum < 17) {
            Card card = deck.remove(deck.size() - 1);
            dealerSum += card.getValue();
            dealerAceCount += card.isAce() ? 1 : 0;
            dealerHand.add(card);
        }
        
        // Calculate final results
        int finalDealerSum = reduceDealerAce();
        int finalPlayerSum = reducePlayerAce();
        
        if (finalPlayerSum > 21) {
            gameResult = "You Lose!";
            playerMoney -= currentBet;
        } else if (finalDealerSum > 21) {
            gameResult = "You Win!";
            playerMoney += currentBet;
        } else if (finalPlayerSum == finalDealerSum) {
            gameResult = "Tie!";
            // No money change on tie
        } else if (finalPlayerSum > finalDealerSum) {
            gameResult = "You Win!";
            playerMoney += currentBet;
        } else {
            gameResult = "You Lose!";
            playerMoney -= currentBet;
        }
    }

    private int reducePlayerAce() {
        int reducedSum = playerSum;
        int aceCount = playerAceCount;
        
        while (reducedSum > 21 && aceCount > 0) {
            reducedSum -= 10;
            aceCount -= 1;
        }
        return reducedSum;
    }

    private int reduceDealerAce() {
        int reducedSum = dealerSum;
        int aceCount = dealerAceCount;
        
        while (reducedSum > 21 && aceCount > 0) {
            reducedSum -= 10;
            aceCount -= 1;
        }
        return reducedSum;
    }
    
    public void setInitialMoney(int money) {
        if (money > 0) {
            this.playerMoney = money;
        }
    }
    
    // Getters
    public Card getHiddenCard() {
        return hiddenCard;
    }
    
    public ArrayList<Card> getDealerHand() {
        return dealerHand;
    }
    
    public ArrayList<Card> getPlayerHand() {
        return playerHand;
    }
    
    public boolean isGameOver() {
        return gameOver;
    }
    
    public String getGameResult() {
        return gameResult;
    }
    
    public int getPlayerHandValue() {
        return reducePlayerAce();
    }
    
    public int getDealerHandValue() {
        return reduceDealerAce();
    }
    
    public int getDealerVisibleValue() {
        // Only count visible cards (not the hidden card)
        int visibleSum = 0;
        int visibleAceCount = 0;
        
        for (Card card : dealerHand) {
            visibleSum += card.getValue();
            if (card.isAce()) {
                visibleAceCount++;
            }
        }
        
        // Reduce aces if needed
        while (visibleSum > 21 && visibleAceCount > 0) {
            visibleSum -= 10;
            visibleAceCount--;
        }
        
        return visibleSum;
    }
    
    public int getPlayerMoney() {
        return playerMoney;
    }
    
    public int getCurrentBet() {
        return currentBet;
    }
    
    public boolean hasBetPlaced() {
        return currentBet > 0;
    }
} 