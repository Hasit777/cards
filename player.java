import java.util.List;

public class Player implements Runnable {

    private final int playerID; //the way we identify a thread 
    private final List<Card> player_cards; //the cards the player holds
    private final int biased_value; //the value the ai wants to have
    private final CardDeck drawDeck; 
    private final CardDeck discardDeck;

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
    }

    @Override
    public void run() {
        // the player's game loop will go here.
    }
}
