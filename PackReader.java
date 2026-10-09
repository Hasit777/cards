import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class PackReader
{
    private final File file;
    private ArrayList<String> packItems;

    public PackReader(String fileName)
    {
        file = new File(fileName);
        packItems = new ArrayList<>();
    }
    
    public void ReadPack(){
        try {
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                packItems.add(line);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Pack file not found");
        }
    }
    
    public ArrayList<String> getPackItems(){
        return new ArrayList<>(packItems);
    }
}