import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

public class Emails{
    public Vector emails() throws FileNotFoundException{
        Scanner sc = new Scanner(new File("emails.txt"));
        Vector emails = new Vector();
        while (sc.hasNextLine()){
            emails.add(sc.nextLine());
        }
        return emails;
    }
}
