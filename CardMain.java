import java.util.ArrayList;
import java.util.List;

public class CardGame {

    public static void main(String[] args) throws InterruptedException {

      
        List<Card> player_cards1 = new ArrayList<>();
        List<Card> player_cards2 = new ArrayList<>();
        List<Card> player_cards3 = new ArrayList<>();

        CardDeck deck1 = new CardDeck(1);
        CardDeck deck2 = new CardDeck(2);
        CardDeck deck3 = new CardDeck(3);

        PackReader reader = new PackReader("pack.txt");
        reader.ReadPack();
        ArrayList<String> pack = reader.getPackItems();
        int cardIndex = 0;

        for (int i = 0; i < 4; i++) {
            player_cards1.add(new Card(Integer.parseInt(pack.get(cardIndex++))));
            player_cards2.add(new Card(Integer.parseInt(pack.get(cardIndex++))));
            player_cards3.add(new Card(Integer.parseInt(pack.get(cardIndex++))));
        }

        int deckIndex = 0;

        for (int i = cardIndex; i < pack.size(); i++) {
        
            Card card = new Card(Integer.parseInt(pack.get(i)));
        
            if (deckIndex == 0) {
                deck1.add(card);
            } else if (deckIndex == 1) {
                deck2.add(card);
            } else {
                deck3.add(card);
            }
        
            deckIndex++;
        
            if (deckIndex == 3) {
                deckIndex = 0;
            }
        }
        stateGame Gamestate = new stateGame();//shared gamestate
        

      
        Player player1 = new Player(1, player_cards1, deck1);//make the player 
        Player player2 = new Player(2, player_cards2, deck2);
        Player player3 = new Player(3, player_cards3, deck3);

        
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
