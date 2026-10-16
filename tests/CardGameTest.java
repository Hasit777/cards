import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class CardGameTest {

    // create a temp file for testing 
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    // to get rid of old files and reset winner 
    @Before
    public void Newstart() {
        Player.resetWinner();
        deleteOutFIles();
    }

    @After
    public void Endgame() {
        deleteOutFIles();
        Player.resetWinner();
    }

    private void deleteOutFIles() {
        for (int i = 1; i <= 4; i++) {
            new File("player" + i + "_output.txt").delete();
            new File("deck" + i + "_output.txt").delete();
        }
    }

    // exctracts the card values written after the colon in an output line
    private List<Integer> numbersAfterColon(String line) {
        String rest = line.substring(line.indexOf(':') + 1).trim();
        List<Integer> result = new ArrayList<Integer>();

        if (rest.isEmpty()) {//return an empty if nothing there
            return result;
        }

        for (String token : rest.split("\\s+")) {//split everything else 
            result.add(Integer.parseInt(token));
        }

        return result;
    }

    // writes the supplied card values to a temporary pack file.
    private String PackLines(String name, String... values) throws Exception {
        File file = new File(folder.getRoot(), name);
        Files.write(file.toPath(), java.util.Arrays.asList(values));
        return file.getPath();
    }

    // checks that two decks still contain the original cards.
    private void assertCardTotals(CardDeck a, CardDeck b, long originalSum, long originalSquares) {
        long finalSum = 0;
        long finalSquares = 0;
        int cardCount = 0;

        
        for (Card card : a.getContents()) {// count and calculate totals for every card in deck a
            int value = card.getValue();
            finalSum += value;
            finalSquares += (long) value * value;
            cardCount++;
        }

        // ciunt and calculate totals for every card in deck b
        for (Card card : b.getContents()) {
            int value = card.getValue();
            finalSum += value;
            finalSquares += (long) value * value;
            cardCount++;
        }

        assertEquals("Card count changed", 100, cardCount);
        assertEquals("Card value sum changed", originalSum, finalSum);
        assertEquals("Card squared sum changed", originalSquares, finalSquares);
    }

    //makes an array containing the same card value multiple times
    private String[] repeat(int value, int count) {
        String[] values = new String[count];

        for (int i = 0; i < count; i++) {
            values[i] = Integer.toString(value);
        }

        return values;
    }

    // gives input to the main java as if the user gave instructions to it
    private void runTheGame(String... inputs) throws Exception {
        InputStream oldIn = System.in;

        try {
            String input = String.join("\n", inputs) + "\n";
            System.setIn(new ByteArrayInputStream(input.getBytes("UTF-8")));
            CardGame.main(new String[0]);
        } finally {
            System.setIn(oldIn);
        }
    }

    // returns the output file 
    private List<String> playerOutput(int playerId) throws Exception {
        return Files.readAllLines(new File("player" + playerId + "_output.txt").toPath());
    }

    @Test(timeout = 35000)
    public void playerCountCheck() throws Exception {
        String pack = PackLines("valid.txt", repeat(1, 8));

        runTheGame("I....AM....Atomic", "0", "-333333333", "1", pack);
        assertEquals("player 1 initial hand 1 1 1 1", playerOutput(1).get(0));
    }

    @Test(timeout = 35000)
    public void PackCheck() throws Exception {
        String shortPack = PackLines("notEnoughNumbers.txt", repeat(1, 7));
        String invalidNumber = PackLines("Alphabet.txt", "L", "O", "N", "E", "L", "Y", "6", "7");
        String negative = PackLines("negative.txt", "-6", "-7", "-2", "1", "1", "1", "1", "1");
        String missing = new File(folder.getRoot(), "missing.txt").getPath();
        String valid = PackLines("normal.txt", repeat(1, 8));
        runTheGame("1", shortPack, invalidNumber, negative, missing, valid);
        assertEquals("player 1 initial hand 1 1 1 1", playerOutput(1).get(0));
    }

    // makes super that cards with value zero are accepted
    @Test(timeout = 35000)
    public void OtherValueCardsAccept() throws Exception {
        String pack = PackLines("zeros.txt", repeat(0, 8));

        runTheGame("1", pack);
        assertEquals("player 1 initial hand 0 0 0 0", playerOutput(1).get(0));
        assertTrue(playerOutput(1).contains("player 1 wins"));
    }

    // supervises that the total value of all cards remains the same after the game
    @Test(timeout = 35000)
    public void cardsAreConserved() throws Exception {
        int players = 4;
        List<Integer> pack = new ArrayList<Integer>();
        for (int value = 1; value <= players; value++) {
            for (int i = 0; i < 8; i++) {
                pack.add(value);
            }
        }
        java.util.Collections.shuffle(pack, new java.util.Random(42));
        int originalTotal = 0;
        for (int value : pack) {
            originalTotal += value;
        }
        String[] lines = new String[pack.size()];

        for (int i = 0; i < lines.length; i++) {
            lines[i] = Integer.toString(pack.get(i));
        }

        runTheGame("4", PackLines("conserve.txt", lines));
        int finalTotal = 0;

        for (int id = 1; id <= players; id++) {
            List<String> playerFile = playerOutput(id);

            // the final output line contains that player's final hand
            List<Integer> hand = numbersAfterColon(playerFile.get(playerFile.size() - 1));

            for (int value : hand) {
                finalTotal += value;
            }

            // check player's deck output file should exist
            File deckFile = new File("deck" + id + "_output.txt");
            assertTrue("deck" + id + "_output.txt missing", deckFile.exists());
            // read the cards remaining in this deck
            List<Integer> deck = numbersAfterColon(Files.readAllLines(deckFile.toPath()).get(0));

            for (int value : deck) {
                finalTotal += value;
            }
        }
        assertEquals("card values were lost or duplicated", originalTotal, finalTotal);
    }

    // check that the initial hands are dealt correctly and a winner is recorded
    @Test(timeout = 35000)
    public void CardsDealtAndWinner() throws Exception {
        String pack = PackLines("deal.txt", "1", "2", "1", "3", "1", "4", "1", "5", "2", "3", "4", "5", "2", "3", "4", "5");

        runTheGame("2", pack);
        assertEquals("player 1 initial hand 1 1 1 1", playerOutput(1).get(0));
        assertEquals("player 2 initial hand 2 3 4 5", playerOutput(2).get(0));
        assertTrue(playerOutput(1).contains("player 1 wins"));
        assertTrue(playerOutput(1).contains("player 1 exits"));
        assertTrue(playerOutput(2).contains("player 2 exits"));
    }

    // tests that a win produces exactly four expected output lines per player
    @Test(timeout = 35000)
    public void InitiateWinEveryPlayer() throws Exception {
        String pack = PackLines("immediate.txt", "1", "2", "1", "3", "1", "4", "1", "5", "2", "3", "4", "5", "2", "3", "4", "5");

        runTheGame("2", pack);

        assertEquals(java.util.Arrays.asList(
                "player 1 initial hand 1 1 1 1",
                "player 1 wins",
                "player 1 exits",
                "player 1 final hand: 1 1 1 1"),
                playerOutput(1));
        assertEquals(java.util.Arrays.asList(
                "player 2 initial hand 2 3 4 5",
                "player 1 has informed player 2 that player 1 has won",
                "player 2 exits",
                "player 2 hand: 2 3 4 5"),
                playerOutput(2));
    }

    // makes sure that exactly one of the two players is declared the winner
    @Test(timeout = 35000)
    public void OneWinnerOnly() throws Exception {
        String pack = PackLines("both.txt", "1", "2", "1", "2", "1", "2", "1", "2", "1", "2", "1", "2", "1", "2", "1", "2");

        runTheGame("2", pack);
        boolean player1Won = playerOutput(1).contains("player 1 wins");
        boolean player2Won = playerOutput(2).contains("player 2 wins");
        assertNotEquals(player1Won, player2Won);
    }

    // runs the game fully with four players 
    @Test(timeout = 35000)
    public void FullGameRun() throws Exception {
        int players = 4;
        List<Integer> values = new ArrayList<Integer>();
        for (int value = 1; value <= players; value++) {
            for (int i = 0; i < 8; i++) {
                values.add(value);
            }
        }
        java.util.Collections.shuffle(values, new java.util.Random(42));//shuffle

        String[] lines = new String[values.size()];

        for (int i = 0; i < lines.length; i++) {
            lines[i] = Integer.toString(values.get(i));
        }

        String pack = PackLines("full.txt", lines);
        runTheGame("4", pack);

        // a winner must have been recorde and their ID must be valid
        assertTrue(Player.getWinnerID() >= 1);
        assertTrue(Player.getWinnerID() <= players);

        int winnerCount = 0;

        for (int id = 1; id <= players; id++) {
            List<String> file = playerOutput(id);

            // all players must eventually exit
            assertTrue(file.contains("player " + id + " exits"));
            // this is to check every line in this player's output file
            for (String line : file) {
                // all recorded current hand must contain four cards
                if (line.contains("current hand is")) {
                    String hand = line.substring(line.indexOf("current hand is") + "current hand is".length()).trim();
                    assertEquals("Player " + id + " has an incorrect hand size: " + line, 4, hand.split("\\s+").length);
                }

                // player  cannot discard a card matching their own ID
                if (line.contains("discards a ")) {
                    String discarded = line.substring(line.indexOf("discards a ") + "discards a ".length()).trim();
                    int cardValue = Integer.parseInt(discarded.split("\\s+")[0]);
                    assertNotEquals("Player " + id + " discarded their preferred value", id, cardValue);
                }

                //  check each line that declares a player the winner.
                if (line.equals("player " + id + " wins")) {
                    winnerCount++;
                }
            }

            //final recorded hand must contain four cards.
            String last = file.get(file.size() - 1);
            String hand = last.substring(last.indexOf(':') + 1).trim();

            assertEquals(4, hand.split("\\s+").length);
        }

        // only one player can win the game
        assertEquals("There must be exactly one winner", 1, winnerCount);
    }

    // to run several threads that move cards between two decks while locking both decks
    @Test(timeout = 10000)
    public void concurrencyBetweenDecks() throws Exception {
        CardDeck a = new CardDeck(1);
        CardDeck b = new CardDeck(2);

        long originalSum = 0;
        long originalSquares = 0;

        // put 50 cards into each deck which is 100 cards in total
        for (int i = 0; i < 50; i++) {
            a.add(new Card(i));
            b.add(new Card(i + 50));
        }

        // reocrding the original sum and sum of squares for later comparison
        for (int i = 0; i < 100; i++) {
            originalSum += i;
            originalSquares += (long) i * i;
        }

        // stop the threads at a starting gate until all have been started
        CountDownLatch start = new CountDownLatch(1);
        Thread[] players = new Thread[4];

        for (int t = 0; t < players.length; t++) {
            // those evenindexed threads move from a to b while odd indexed do vice versa
            final boolean flag = t % 2 == 0;

            players[t] = new Thread(() -> {
                try {
                    // wait until the main thread releases the starting gate
                    start.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                // each thread attempts 2,000 card transfers
                for (int i = 0; i < 2000; i++) {

                    // all threads lock a first and b second to keep lock order consistent
                    a.lock();

                    try {
                        b.lock();

                        try {
                            //draw from the source deck selected by flag
                            Card card = flag ? a.draw() : b.draw();

                            // If a card was drawn thenadd it to the other deck
                            if (card != null) {
                                if (flag) {
                                    b.add(card);
                                } else {
                                    a.add(card);
                                }
                            }
                        } finally {
                            // let go of b even if an operation throws exception
                            b.unlock();
                        }
                    } finally {
                        // release a too
                        a.unlock();
                    }
                }
            });

            // start this thread and it will wait
            players[t].start();
        }

        // start all threads now
        start.countDown();

        for (Thread player : players) {
            player.join();
        }
        assertCardTotals(a, b, originalSum, originalSquares);
    }

    // checkss that the synchronized deck operations still preserve the card totals
    @Test(timeout = 10000)
    public void concurrencyDrawAndAdd() throws Exception {
        CardDeck a = new CardDeck(1);
        CardDeck b = new CardDeck(2);

        long originalSum = 0;
        long originalSquares = 0;

        // Put 50 cards into each deck, giving 100 cards in total.
        for (int i = 0; i < 50; i++) {
            a.add(new Card(i));
            b.add(new Card(i + 50));
        }
        for (int i = 0; i < 100; i++) {
            originalSum += i;
            originalSquares += (long) i * i;
        }

        CountDownLatch start = new CountDownLatch(1);
        Thread[] players = new Thread[4];

        for (int t = 0; t < players.length; t++) {
            // same as earlier for even and odd threads 
            final boolean flag = t % 2 == 0;

            players[t] = new Thread(() -> {
                try {
                    start.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                // every thread attempts 2,000 draw & add operations
                for (int i = 0; i < 2000; i++) {

                    // draw from the deck selected by flag
                    Card card = flag ? a.draw() : b.draw();

                    // if a card was drawn then add it to the opposite deck
                    if (card != null) {
                        if (flag) {
                            b.add(card);
                        } else {
                            a.add(card);
                        }
                    }
                }
            });

            players[t].start();
        }

        start.countDown();

        for (Thread player : players) {
            player.join();
        }
        assertCardTotals(a, b, originalSum, originalSquares);
    }
}
