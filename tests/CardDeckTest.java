import static org.junit.Assert.*;
import java.io.File;
import org.junit.Before;
import java.util.concurrent.CountDownLatch;
import org.junit.Rule;
import org.junit.Test;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.junit.rules.TemporaryFolder;
 
/** JUnit 4.13.2 tests for CardDeck: FIFO, validation, output file, thread safety. */
public class CardDeckTest {
 
    private CardDeck deck;
 
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();   // JUnit 4 way to get a temp directory
 
    @Before //set up a deck before the code
    public void setUp() {
        deck = new CardDeck(3);
    }
 
    @Test
    public void testFIFObehaviour() {
        deck.add(new Card(33));
        deck.add(new Card(21));
        deck.add(new Card(900)); //adds all the cards into a exisitng deck 
        assertEquals(33, deck.draw().getValue());
        assertEquals(21, deck.draw().getValue()); //to see if you draw in the order and it matches 
        assertEquals(900, deck.draw().getValue());
    }

    @Test
    public void testLILObehaviour() {
        deck.add(new Card(33));
        deck.add(new Card(21));
        deck.draw();                      // removes the 1
        deck.add(new Card(900));            // must sit behind the 2
 
        assertEquals(21, deck.draw().getValue());
        assertEquals(900, deck.draw().getValue());
    }


    @Test
    public void emptyDeckCheck() {
        assertNull(deck.draw());
    }

        @Test(expected = IllegalArgumentException.class)
    public void CantAddToNull() {
        deck.add(null);
    }
 
    @Test(expected = IllegalArgumentException.class)
    public void DeckId0Rejected() {
        new CardDeck(0);
    }
 
    @Test(expected = IllegalArgumentException.class)
    public void NoNegativeCardVal() {
        new Card(-1);
    }

    @Test
    public void contentsLinesCheck() {
        assertEquals("deck3 contents:", deck.contentsLine());
        deck.add(new Card(3));
        deck.add(new Card(2));
        assertEquals(deck.contentsLine());
    }
        @Test
    public void writeOutputFileCreatesFileWithFinalContents() throws Exception {
        deck.add(new Card(8));
        deck.add(new Card(6));
 
        deck.writeOutputFile(folder.getRoot().getPath());
 
        File file = new File(folder.getRoot(), "deck3_output.txt");
        assertTrue(file.exists());
        List<String> lines = Files.readAllLines(file.toPath());
        assertEquals(1, lines.size());
        assertEquals("deck3 contents: 8 6", lines.get(0));
    }
    
    @Test
    public void OutputFIleWithContent() throws Exception {
        deck.add(new Card(8));
        deck.add(new Card(6));

        deck.writeOutputFile(folder.getRoot().getPath());

        File file = new File(folder.getRoot(), "deck3_output.txt");
        List<String> lines = Files.readAllLines(file.toPath());

        assertEquals("deck3 contents: 8 6", lines.get(0));
    }


        
    @Test(timeout = 10000)
    public void concurrencyCheckOnTheDeck() throws Exception {
        final int threads = 4;
        final int cards_thread = 300;
        List<Thread> players = new ArrayList<Thread>();
        CountDownLatch startSignal = new CountDownLatch(1);

        for (int t = 0; t < threads; t++) {
            final int counter = t * cards_thread;

            Thread player = new Thread() {
                @Override
                public void run() {
                    try {
                        startSignal.await();

                        for (int i = 0; i < cards_thread; i++) {
                            deck.add(new Card(counter + i));
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            };

            players.add(player);
        }

        for (Thread p : players) {
            p.start();
        }

        startSignal.countDown();

        for (Thread p : players) {
            p.join();
        }

        assertEquals(threads * cards_thread, deck.size());
        int[] lastSeen = {-1, -1, -1, -1};

        for (Card c : deck.getContents()) {
            int thread = c.getValue() / cards_thread; //gives the range
            int index = c.getValue() % cards_thread; //gives the id

            assertTrue(index > lastSeen[thread]);
            lastSeen[thread] = index;
        }
    }
    @Test(timeout = 10000)
    public void lockedDeckBlocksOtherThreadsUntilUnlocked() throws Exception {
        final boolean[] finished = {false};
        Thread other = new Thread() {
            @Override
            public void run() {
                deck.size();                  // needs the lock
                finished[0] = true;
            }
        };
 
        deck.lock();
        other.start();
        Thread.sleep(200);
        assertFalse("other thread got in while deck was locked", finished[0]);
 
        deck.unlock();
        other.join();
        assertTrue(finished[0]);
    }
}


