import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 09.04.13
 * Time: 0:07
 * To change this template use File | Settings | File Templates.
 */
public class Java2ObjCConverter {
    String inputString;
    SyntaxAnalizator syntaxAnalizator;
    String lexemDataPath;
    ArrayList<Lexem> lexems;

    public Java2ObjCConverter(String inputString) {
        this.inputString = inputString;
        syntaxAnalizator = new SyntaxAnalizator(this);
    }

    public void generateOutputFile(ArrayList<Lexem> lexems) {
         for(int i = 0; i < lexems.size(); i++) {
             Lexem lexem = lexems.get(i);
             System.out.println("Lexem # = " + i);
             System.out.println("\t id: " + lexem.id + " type: " + lexem.type + " name: " + lexem.name);
         }
        this.lexems = lexems;
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        BufferedReader in = new BufferedReader(new FileReader("input.txt"));
        String inputString = "";
        while (in.ready()) {
            String s = in.readLine();
            inputString += s;
        }
        in.close();
        Java2ObjCConverter converter = new Java2ObjCConverter(inputString);
        try {
            ArrayList<Lexem> lexems = converter.syntaxAnalizator.getOutput();
            converter.generateOutputFile(lexems);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
