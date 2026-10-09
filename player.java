import java.util.List;

public class Player implements Runnable {

    private final int playerID; //the way we identify a thread 
    private final List<Card> player_cards; //the cards the player holds
    private final int biased_value; //the value the ai wants to have
    private final CardDeck drawDeck; 
    private final CardDeck discardDeck;
    private final stateGame Gamestate;

    public Player(
            int playerID,
            List<Card> player_cards,
            CardDeck drawDeck,
            CardDeck discardDeck) {

        this.playerID = playerID;
        this.player_cards = player_cards;
        this.biased_value = playerID; 
        this.drawDeck = drawDeck;
        this.discardDeck = discardDeck;
        this.Gamestate = Gamestate;
    }

    @Override
    public void run() {
        System.out.println("player " + playerId + "  thread has been officially started .");

        // the player's game loop will go here.
        while (!Gamestate.isFinished()) {

        // 1. Draw a card

        // 2. Choose a card to discard

        // 3. Discard the card

        // 4. Check for a win

    }

    System.out.println("player " + playerId + " thread has now finished.");
    }
}
