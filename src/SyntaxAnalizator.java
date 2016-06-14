import java.util.ArrayList;
import java.util.HashMap;
import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 09.04.13
 * Time: 0:08
 * To change this template use File | Settings | File Templates.
 */
public class SyntaxAnalizator {
    String inputString;
    char[] str;
    ArrayList<Lexem> tokenList;
    HashMap<Integer, String> types;
    ArrayList<Lexem> output;
    Connection con;

    public SyntaxAnalizator(Java2ObjCConverter converter) {
        tokenList = new ArrayList<Lexem>();
        types = new HashMap<Integer, String>();
        this.inputString = converter.inputString;
        output = new ArrayList<Lexem>();
    }

    public Lexem getLexemByString(String input) {
        for(int i = 0; i < tokenList.size(); i++) {
            if(tokenList.get(i).name == input)
                return tokenList.get(i);
        }
        return new Lexem(0, 0, input);
    }

    public void fillFromDatabase(String path) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:"+path);
        con.setAutoCommit(false);
        //fillTypes(path);
        fillLexems(path);
        con.close();
    }

    public void fillTypes(String path) throws ClassNotFoundException, SQLException {
        //connect to db
        Statement stat = con.createStatement();
        ResultSet rs = stat.executeQuery("SELECT * FROM types;");
        while(rs.next()) {
            int typeId = rs.getInt("id");
            String typeValue = rs.getString("value");
            types.put(typeId, typeValue);
        }
    }

    public void fillLexems(String path) throws ClassNotFoundException, SQLException {
        //connect to db
        Statement stat = con.createStatement();
        ResultSet rs = stat.executeQuery("SELECT * FROM lexems;");
        while(rs.next()) {
            int lexId = rs.getInt("id");
            int lexType = rs.getInt("typeId");
            String lexName = rs.getString("name");

            System.out.println("new lexem: " + lexId + " " + lexType + " " + lexName);

            Lexem lexem = new Lexem(lexId, lexType, lexName);
            tokenList.add(lexem);
        }
    }

    public void parseInputString() {
        output = new ArrayList<Lexem>();

        inputString = inputString.replace('\n', ' ');
        String[] words = inputString.split(" ");
        Lexem spaceLexem = getLexemByString(" ");
        for(int i = 0; i < words.length; i++) {
            Lexem lexem = getLexemByString(words[i]);
            output.add(lexem);
            output.add(spaceLexem);
        }
    }

    private int extractBlockComment(int index) {
        int i = index;
        while (true) {
            if(str[i] == '*' && str[i + 1] == '/') break;
            i++;
        }
        i += 2;
        System.out.println("block comment extracted: " + inputString.substring(index, i));
        return i;
    }

    private int extractLineComment(int index) {
        int i = index;
        while(str[i] != '\n') i++;
        i++;
        System.out.println("Line comment extracted: " + inputString.substring(index, i));
        return i;
    }

    private boolean isNewLine(int index) {
        return  (str[index] == '\n');
    }

    private int extractNewLine(int index) {
        int i = index;
        Lexem lexem = new Lexem(4, 0, "\n");
        output.add(lexem);
        i++;
        System.out.println("New line extracted: " + inputString.substring(index, i));
        return i;
    }

    private boolean isOperator(int index) {
        for(int i = 0; i < tokenList.size(); i++) {
            Lexem lexem = tokenList.get(i);
            int lexLength = lexem.name.length();
            if(index + lexLength > inputString.length()) continue;
            System.out.println("Comparing : " + inputString.substring(index, index + lexLength) + " and " + lexem.name + " result = " + inputString.substring(index, index + lexLength).equals(lexem.name));
            if(inputString.substring(index, index + lexLength).equals(lexem.name)) return true;
        }
        return false;
    }

    private int extractOperator(int index) {
        for(int i = 0; i < tokenList.size(); i++) {
            Lexem lexem = tokenList.get(i);
            int lexLength = lexem.name.length();
            if(index + lexLength > inputString.length()) continue;
            if(inputString.substring(index, index + lexLength).equals(lexem.name)) {
                output.add(lexem);
                System.out.println("New operator extracted: " + inputString.substring(index, index+lexLength));
                return index + lexLength;
            }
        }
        return index;
    }

    private boolean isQuote(int index) {
        return (str[index] == '"');
    }

    private int extractString(int index) {
        int i = index + 1;
        int j = i + 1;
        while (str[j] != '"') j++;
        Lexem lexem = new Lexem(5, 0, inputString.substring(i, j));
        output.add(lexem);
        System.out.println("New string extracted: " + inputString.substring(i, j));
        return j + 1;
    }

    private boolean isNumber(int index) {
        return Character.isDigit(str[index]);
    }

    private int extractNumber(int index) {
        int i = index;
        boolean isDouble = false;
        while (true) {
            if(str[i] == '.') isDouble = false;
            else if(!Character.isDigit(str[i])) break;
            i++;
        }
        int type = 6;
        if(isDouble) type = 7;
        Lexem lexem = new Lexem(type, 0, inputString.substring(index, i));
        output.add(lexem);
        System.out.println("New number extracted: " + inputString.substring(index, i));
        return i;
    }

    private boolean isBoolean(int index) {
        return inputString.substring(index, index + 4).equals("true") || inputString.substring(index, index + 5).equals("false");
    }

    private int extractBoolean(int index) {
        if(inputString.substring(index, index + 4).equals("true")) {
            Lexem lexem = new Lexem(2, 0, "true");
            output.add(lexem);
            System.out.println("New boolean extracted: " + inputString.substring(index, index + 4));
            return index + 4;
        }
        else {
            Lexem lexem = new Lexem(3, 0, "false");
            output.add(lexem);
            System.out.println("New boolean extracted: " + inputString.substring(index, index + 5));
            return index + 5;
        }
    }

    private boolean isSpace(int index) {
        return str[index] == ' ';
    }

    private int extractSpace(int index) {
        Lexem lexem = new Lexem(1, 0, "<space>");
        output.add(lexem);
        System.out.println("New space extracted: " + inputString.substring(index, index + 1));
        return index + 1;
    }

    private int extractId(int index) {
        int i = index;
        while(true) {
            if(str[i] == ';' || str[i] == ' ') break;
            i++;
        }
        Lexem lexem = new Lexem(8, 0, inputString.substring(index, i));
        output.add(lexem);
        System.out.println("New id extracted: " + inputString.substring(index, i));
        return i;
    }

    public void parse() {
        int pCurr = 0;
        int pEnd = inputString.length();
        str = inputString.toCharArray();

        while (pCurr != pEnd) {
            char c0 = str[pCurr];
            System.out.println("Current symbol is :" + c0);
            char c1 = ' ';
            if(pCurr != pEnd - 1) c1 = str[pCurr + 1];
            if(c0 == '/' && c1 == '*')
                pCurr = extractBlockComment(pCurr);
            else if(c0 == '/' && c1 == '/')
                pCurr = extractLineComment(pCurr);
            else if(isNewLine(pCurr))
                pCurr = extractNewLine(pCurr);
            else if(isQuote(pCurr))
                pCurr = extractString(pCurr);
            else if(isNumber(pCurr))
                pCurr = extractNumber(pCurr);
            else if(isSpace(pCurr))
                pCurr = extractSpace(pCurr);
            else if(isOperator(pCurr))
                pCurr = extractOperator(pCurr);
            else if(isBoolean(pCurr))
                pCurr = extractBoolean(pCurr);
            else pCurr = extractId(pCurr);
        }
    }

    public ArrayList<Lexem> getOutput() throws SQLException, ClassNotFoundException {
        fillFromDatabase("base.db");

        parse();
        return output;
    }
}
