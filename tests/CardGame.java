import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class CardGameTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

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
        }
    }

    private String PackLines(String name, String... values) //writes the values to a file with one on each line
            throws Exception {
        File file = new File(folder.getRoot(), name);
        Files.write(file.toPath(), java.util.Arrays.asList(values));
        return file.getPath();
    }

    private String[] repeat(int value, int count) { //a helper to get card string integers
        String[] values = new String[count];

        for (int i = 0; i < count; i++) {
            values[i] = Integer.toString(value);
        }

        return values;
    }

    private void runTheGame(String... inputs) throws Exception { //a helper so i dont need to manually run the code everytime
        InputStream oldIn = System.in;

        try {
            String input = String.join("\n", inputs) + "\n"; 
            System.setIn(new ByteArrayInputStream(
                    input.getBytes("UTF-8"))); // to read data from a custom text string instead of the keyboard

            CardGame.main(new String[0]);//calls card 
        } finally {
            System.setIn(oldIn);//restores the original input stream for no fake inputs
        }
    }

    private List<String> playerOutput(int playerId) throws Exception {
        return Files.readAllLines(
                new File("player" + playerId + "_output.txt").toPath());
    }

    @Test(timeout = 35000)
    public void playerCountCheck() throws Exception {
        String pack = PackLines("valid.txt", repeat(1, 8));

        runTheGame("I....AM....Atomic", "0", "-333333333", "1", pack);

        assertEquals("player 1 initial hand 1 1 1 1",
                playerOutput(1).get(0));
    }

    @Test(timeout = 35000)
    public void PackCheck() throws Exception {
        String shortPack = PackLines("notEnoughNumbers.txt", repeat(1, 7));
        String invalidNumber = PackLines("Alphabet.txt",
                "L", "O", "N", "E", "L", "Y", "6", "7");
        String negative = PackLines("negative.txt",
                "-6", "-7", "-2", "1", "1", "1", "1", "1");
        String missing = new File(
                folder.getRoot(), "missing.txt").getPath();
        String valid = PackLines("normal.txt", repeat(1, 8));

        runTheGame("1", notEnoughNumbers,Alphabet ,
                negative, missing, normal);

        assertEquals("player 1 initial hand 1 1 1 1",
                playerOutput(1).get(0));
    }

    @Test(timeout = 35000)
    public void OtherValueCardsAccept() throws Exception {
        String pack = PackLines("zeros.txt", repeat(0, 8));

        runTheGame("1", pack);

        assertEquals("player 1 initial hand 0 0 0 0",
                playerOutput(1).get(0));
        assertTrue(playerOutput(1).contains("player 1 wins"));
    }

    @Test(timeout = 35000)
    public void CardsDealtAndWinner()
            throws Exception {
        String pack = PackLines("deal.txt",
                "1", "2", "1", "3", "1", "4", "1", "5",
                "2", "3", "4", "5", "2", "3", "4", "5");

        runTheGame("2", pack);

        assertEquals("player 1 initial hand 1 1 1 1",
                playerOutput(1).get(0));
        assertEquals("player 2 initial hand 2 3 4 5",
                playerOutput(2).get(0));

        assertTrue(playerOutput(1).contains("player 1 wins"));
        assertTrue(playerOutput(1).contains("player 1 exits"));
        assertTrue(playerOutput(2).contains("player 2 exits"));
    }

    @Test(timeout = 35000)
    public void OneWinnerOnly() throws Exception {
        String pack = PackLines("both.txt",
                "1", "2", "1", "2", "1", "2", "1", "2",
                "1", "2", "1", "2", "1", "2", "1", "2");

        runTheGame("2", pack);

        int winners = 0;
        boolean player1Won = playerOutput(1).contains("player 1 wins");
        boolean player2Won = playerOutput(2).contains("player 2 wins");
        assertNotEquals(player1Won, player2Won);


    }
