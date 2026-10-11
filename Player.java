import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Player implements Runnable {
    private static final AtomicInteger winnerID = new AtomicInteger(0);
 
    private final int playerID;              // the way we identify a thread
    private final List<Card> player_cards;   // the cards the player holds (arrival order)
    private final CardDeck drawDeck;         // deck to the left: draw from the front
    private final CardDeck discardDeck;      // deck to the right: discard to the back
    private final stateGame Gamestate;       // shared by every player
 
    public Player(
            int playerID,
            List<Card> player_cards,
            CardDeck drawDeck,
            CardDeck discardDeck,
            stateGame Gamestate) {
 
        this.playerID = playerID;
        this.player_cards = player_cards;
        this.drawDeck = drawDeck;
        this.discardDeck = discardDeck;
        this.Gamestate = Gamestate;
    }

    static void resetWinner() {
        winnerID.set(0);
    }
 
    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(new FileWriter("player" + playerID + "_output.txt"))) {
            out.println("player " + playerID + " initial hand " + handToString());
            if (hasWinningHand()) {
                declareWin();
            }
            while (!isGameOver()) {
                boolean tookTurn = takeTurn(out);
                if (hasWinningHand()) {             
                    declareWin();
                } else if (!tookTurn) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            writeEnding(out);
        } catch (IOException e) {
            System.err.println("player " + playerID
                    + " could not write its output file: " + e.getMessage());
        }
    }
    
    private boolean isGameOver() {
        return winnerID.get() != 0 || Gamestate.isFinished();
    }

    private boolean takeTurn(PrintWriter out) {
        CardDeck first = drawDeck.getId() < discardDeck.getId() ? drawDeck : discardDeck;
        CardDeck second = (first == drawDeck) ? discardDeck : drawDeck;
 
        first.lock();
        try {
            second.lock();
            try {
                if (isGameOver()) {
                    return false;
                }
                Card drawn = drawDeck.draw();
                if (drawn == null) {
                    return false;                 // empty deck retry later
                }
                player_cards.add(drawn);          // hand is temporarily 5 cards
                out.println("player " + playerID + " draws a " + drawn.getValue() + " from deck " + drawDeck.getId());
                int index = chooseDiscardIndex();
                Card discarded = player_cards.remove(index);   // back to 4 cards
                discardDeck.add(discarded);
                out.println("player " + playerID + " discards a " + discarded.getValue() + " to deck " + discardDeck.getId());
                out.println("player " + playerID + " current hand is " + handToString());
                return true;
 
            } finally {
                second.unlock();
            }
        } finally {
            first.unlock();
        }
    }
    
    private int chooseDiscardIndex() {
        for (int i = 0; i < player_cards.size(); i++) {
            if (player_cards.get(i).getValue() != playerID) {
                return i;
            }
        }
        //fail safe incase mis deal in cards SHOULD NEVER HAPPEN
        return player_cards.size() - 1;
    }
    
    private boolean hasWinningHand() {
        if (player_cards.size() != 4) {
            return false;
        }
        int first = player_cards.get(0).getValue();
        for (Card c : player_cards) {
            if (c.getValue() != first) {
                return false;
            }
        }
        return true;
    }
    
    private void declareWin() {
        if (winnerID.compareAndSet(0, playerID)) {
            Gamestate.endGame();
        }
    }
    
    private void writeEnding(PrintWriter out) {
        int winner = winnerID.get();
 
        if (winner == playerID) {
            System.out.println("player " + playerID + " wins");
            out.println("player " + playerID + " wins");
            out.println("player " + playerID + " exits");
            out.println("player " + playerID + " final hand: " + handToString());
        } else if (winner != 0) {
            out.println("player " + winner + " has informed player " + playerID + " that player " + winner + " has won");
            out.println("player " + playerID + " exits");
            out.println("player " + playerID + " hand: " + handToString());
        } else {
            out.println("player " + playerID + " exits");
            out.println("player " + playerID + " hand: " + handToString());
        }
    }
 
    private String handToString() {
        String result = "";
        for (int i = 0; i < player_cards.size(); i++) {
            if (i > 0) {
                result = result + " ";
            }
            result = result + player_cards.get(i).getValue();
        }
        return result;
    }
 
    public int getPlayerID() {
        return playerID;
    }

    public List<Card> getHand() {
        return new ArrayList<>(player_cards);
    }
}
 
