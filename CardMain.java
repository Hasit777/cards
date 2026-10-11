import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CardMain {

    public static void main(String[] args) throws InterruptedException {

        Scanner input = new Scanner(System.in);

        // ask how many players
        int num_players;

        while (true) {
            System.out.print("Enter number of players: ");
            try {
                num_players = Integer.parseInt(input.nextLine());

                if (num_players > 0) {
                    break;
                }

                System.out.println("Enter a positive number.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }

        // read and validate the pack
        ArrayList<String> packItems;

        while (true) {
            System.out.print("Enter pack filename: ");
            String fileName = input.nextLine();

            PackReader reader = new PackReader(fileName);
            reader.ReadPack();
            packItems = reader.getPackItems();

            boolean valid_pack = packItems.size() == (8 * num_players);

            for (String num : packItems) {
                try {
                    int value = Integer.parseInt(num.trim());

                    if (value < 0) {
                        valid_pack = false;
                    }
                } catch (NumberFormatException e) {
                    valid_pack = false;
                }
            }

            if (valid_pack) {
                break;
            }

            System.out.println(
                "Invalid pack. It must contain exactly "
                + (8 * num_players)
                + " non-negative integers, one per line."
            );
        }

        // convert the pack strings into Cards
        ArrayList<Card> pack = new ArrayList<>();

        for (String item : packItems) {
            pack.add(new Card(Integer.parseInt(item.trim())));
        }

        // create the decks and player card lists
        ArrayList<CardDeck> decks = new ArrayList<>();
        ArrayList<List<Card>> player_cards = new ArrayList<>();

        for (int i = 0; i < num_players; i++) {
            decks.add(new CardDeck(i + 1));
            player_cards.add(new ArrayList<Card>());
        }

        // deal four cards to each player
        for (int i = 0; i < 4 * num_players; i++) {
            player_cards.get(i % num_players).add(pack.get(i));
        }

        // dustribute the remaining cards to the decks
        for (int i = 4 * num_players; i < pack.size(); i++) {
            int deckIndex = (i - 4 * num_players) % num_players;
            decks.get(deckIndex).add(pack.get(i));
        }

        // ceate the shared game state
        stateGame Gamestate = new stateGame();

        // create and start the player threads
        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < num_players; i++) {

            int nextDeck = (i + 1) % num_players;

            Player player = new Player(
                i + 1,
                player_cards.get(i),
                decks.get(i),
                decks.get(nextDeck),
                Gamestate
            );

            Thread thread = new Thread(player);
            threads.add(thread);
            thread.start();
        }

        // TEMPORARY: stop the threads for testing
        Thread.sleep(300);
        Gamestate.endGame();

        // 9. Wait for every thread to finish
        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("All players have finished.");

        input.close();
    }
}
