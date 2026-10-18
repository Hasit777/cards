import static org.junit.Assert.*;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

public class PlayerTest
{
    @Before
    public void setup() {
        Player.resetWinner();
        new File("player1_output.txt").delete();
        new File("player2_output.txt").delete();
    }
    
    @After
    public void cleanUp(){
        new File("player1_output.txt").delete();
        new File("player2_output.txt").delete();
    }
    
    private ArrayList<Card> makeHand(int... values){
        ArrayList<Card> hand = new ArrayList<>();
        for (int value: values){
            hand.add(new Card(value));
        }
        return hand;
    }
    
    private List<String> output(int id) throws Exception{
        return Files.readAllLines(new File("player" + id + "_output.txt").toPath());
    }
    
    private void startPlayers(Player... players) throws Exception {
        ArrayList<Thread> threads = new ArrayList<>();
        for (Player player : players){
            Thread thread = new Thread(player);
            thread.setDaemon(true);
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join(2000);
            assertFalse("Player thread did not finish", thread.isAlive());
        }
    }
    
    private List<Integer> allCards(Player[] players, CardDeck... decks){
        ArrayList<Integer> values = new ArrayList<>();
        for (Player player : players) {
            for (Card card : player.getHand()) {
                values.add(card.getValue());
            }
        }
        for (CardDeck deck : decks ){
            for (Card card : deck.getContents()) {
                values.add(card.getValue());
            }
        }
        Collections.sort(values);
        return values;
    }
    
    @Test(timeout = 5000)
    public void discardHandSize() throws Exception {
        stateGame Gamestate = new stateGame();
        CardDeck deck1 = new CardDeck(1);
        CardDeck deck2 = new CardDeck(2);
        deck1.add(new Card(1));
        deck1.add(new Card(1));
        Player player = new Player(1, makeHand(2, 3, 1, 1), deck1, deck2, Gamestate);
        List<Integer> before = allCards(new Player[]{player}, deck1, deck2);
        startPlayers(player);
        List<String> lines = output(1);
        assertTrue(lines.contains("player 1 discards a 2 to deck 2"));
        assertTrue(lines.contains("player 1 discards a 3 to deck 2"));
        assertTrue(lines.contains("player 1 current hand is 3 1 1 1"));
        assertTrue(lines.contains("player 1 current hand is 1 1 1 1"));
        assertEquals("deck2 contents: 2 3", deck2.contentsLine());
        assertEquals(4, player.getHand().size());
        assertEquals(before, allCards(new Player[]{player}, deck1, deck2));
        
    }
    
    @Test(timeout = 5000)
    public void gameStop() throws Exception{
        stateGame Gamestate = new stateGame();
        Gamestate.endGame();
        CardDeck deck = new CardDeck(1);
        deck.add(new Card(1));
        Player player = new Player(1, makeHand(1, 2, 3, 4), deck, deck, Gamestate);
        startPlayers(player);
        assertEquals(4, player.getHand().size());
        assertEquals(1, deck.size());
        assertTrue(output(1).contains("player 1 exits"));
        assertFalse(output(1).toString().contains("draws a"));
        
    }
    
    @Test(timeout = 5000)
    public void keepCards() throws Exception {
        stateGame Gamestate = new stateGame();
        CardDeck deck1 = new CardDeck(1);
        CardDeck deck2 = new CardDeck(2);
        deck1.add(new Card(1));
        deck2.add(new Card(2));
        Player player1 = new Player(1, makeHand(1, 1, 1, 3), deck1, deck2, Gamestate);
        Player player2 = new Player(2, makeHand(2, 2, 2, 4), deck2, deck1, Gamestate);
        Player[] players = {player1, player2};
        List<Integer> before = allCards(players, deck1, deck2);
        startPlayers(player1, player2);
        assertTrue(Gamestate.isFinished());
        assertEquals(before, allCards(players, deck1, deck2));
        assertEquals(4, player1.getHand().size());
        assertEquals(4, player2.getHand().size());
        boolean firstWon = output(1).contains("player 1 wins");
        boolean secondWon = output(2).contains("player 2 wins");
        assertTrue(firstWon != secondWon);
        assertTrue(output(1).contains("player 1 exits"));
        assertTrue(output(2).contains("player 2 exits"));
    }
}