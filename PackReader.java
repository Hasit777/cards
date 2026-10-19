//takes a pack seperates it into lines make sure the lines are valid non negative intergers
//makes sure the pack is not empty or an error doesnt occur whe  opening
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
        packItems.clear();
        
        if (!file.isFile() || !file.canRead()){
            System.out.println("Pack File Cannot Be Read or Found");
            return;
        }
        
        try {
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                try {
                    int value = Integer.parseInt(line);
                    if (value < 0 || !line.matches("[0-9]+")) {//checks whether it is less than 0 or if it isnt a non negative interger
                        System.out.println("Invalid number in pack");
                        packItems.clear();
                        break;
                    }
                    packItems.add(line);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number in pack");
                    packItems.clear();
                    break;
                }
            }
            scanner.close();
            if (packItems.isEmpty()) {
                System.out.println("Pack is Empty or invalid");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Pack file not found");
        }
    }
    
    public ArrayList<String> getPackItems(){
        return new ArrayList<>(packItems);
    }
}
