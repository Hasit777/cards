import static org.junit.Assert.*;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PackReaderTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    private PackReader makePack(String... values) throws Exception {
        File file = folder.newFile();
        Files.write(file.toPath(), Arrays.asList(values));
        PackReader reader = new PackReader(file.getPath());
        reader.ReadPack();
        return reader;
    }
    
    @Test
    public void validPack() throws Exception {
        PackReader reader = makePack( "0", "1", "2", "3", "4", "5", "6", "2000000");
        assertEquals(8, reader.getPackItems().size());
        assertEquals("0", reader.getPackItems().get(0));
        assertEquals("2000000", reader.getPackItems().get(7));
    }
    
    @Test
    public void missingFile() {
        File file = new File(folder.getRoot(), "missing.txt");
        PackReader reader = new PackReader(file.getPath());
        reader.ReadPack();
        assertTrue(reader.getPackItems().isEmpty());
    }
    
    @Test
    public void emptyFile() throws Exception{
        PackReader reader = makePack();
        assertTrue(reader.getPackItems().isEmpty());
    }
    
    @Test
    public void packSizeCheck() throws Exception {
        PackReader reader = makePack("1", "2", "3", "4", "5", "6", "7");
        assertNotEquals(8, reader.getPackItems().size());
    }
    
    @Test
    public void readingTwice() throws Exception {
        File file = folder.newFile();
        Files.write(file.toPath(), Arrays.asList("1", "2", "3"));
        PackReader reader = new PackReader(file.getPath());
        reader.ReadPack();
        reader.ReadPack();
        assertEquals(3, reader.getPackItems().size());
    }
    
    @Test
    public void invalidNumbers() throws Exception {
        String[] invalid = {"EdgerunnerSeason2", "-5", "3.5", "+2", "2147483648", "1 2"};
        for (String value : invalid) {
            PackReader reader = makePack("1", value, "2");
            assertTrue("Accepted invalid value: " + value,
                    reader.getPackItems().isEmpty());
        }
    }
    
    @Test
    public void blankLineInPack() throws Exception {
        PackReader reader = makePack("1", "", "2");
        assertTrue(reader.getPackItems().isEmpty());
    }
    
    @Test
    public void invalidFileClearsPreviousCards() throws Exception {
        File file = folder.newFile();
        Files.write(file.toPath(), Arrays.asList("1", "2", "3"));
        PackReader reader = new PackReader(file.getPath());
        reader.ReadPack();
        assertEquals(3, reader.getPackItems().size());
        Files.write(file.toPath(), Arrays.asList("1", "abc", "3"));
        reader.ReadPack();
        assertTrue(reader.getPackItems().isEmpty());
    }
    
    
}