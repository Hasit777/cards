import java.util.ArrayList;
import java.util.List;

public class CardGame {

    public static void main(String[] args) throws InterruptedException {

      
        CardDeck deck1 = new CardDeck(1);//make the decks
        CardDeck deck2 = new CardDeck(2);
        CardDeck deck3 = new CardDeck(3);

        
        deck1.add(new Card(1));//add test cards to the deck
        deck1.add(new Card(2));

        deck2.add(new Card(2));
        deck2.add(new Card(3));

        deck3.add(new Card(3));
        deck3.add(new Card(1));

        
        List<Card> player_cards1 = new ArrayList<>();//each players gets their starting cards
        player_cards1.add(new Card(1));
        player_cards1.add(new Card(2));
        player_cards1.add(new Card(3));
        player_cards1.add(new Card(4));

        List<Card> player_cards2 = new ArrayList<>();
        player_cards2.add(new Card(2));
        player_cards2.add(new Card(3));
        player_cards2.add(new Card(4));
        player_cards2.add(new Card(5));

        List<Card> player_cards3 = new ArrayList<>();
        player_cards3.add(new Card(3));
        player_cards3.add(new Card(4));
        player_cards3.add(new Card(5));
        player_cards3.add(new Card(6));

    
        stateGame Gamestate = new stateGame();//shared gamestate

      
        Player player1 = new Player(1, player_cards1, deck1, deck2);//make the player 
        Player player2 = new Player(2, player_cards2, deck2, deck3);
        Player player3 = new Player(3, player_cards3, deck3, deck1);

        
        Thread thread1 = new Thread(player1);//make the threads
        Thread thread2 = new Thread(player2);
        Thread thread3 = new Thread(player3);

        
        thread1.start();//start the threads 
        thread2.start();
        thread3.start();

        
        Thread.sleep(367);//to check the game loop briefly

        
        Gamestate.endGame();

        thread1.join();
        thread2.join();
        thread3.join();

        System.out.println("all players have finished.");
    }
}
