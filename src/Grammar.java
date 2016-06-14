import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 07.05.13
 * Time: 14:31
 * To change this template use File | Settings | File Templates.
 */
public class Grammar {
    String name;
    String expression;
    boolean containsOther;
    LexAnalizator analizator;
    ArrayList<Grammar> otherGrammas;
    ArrayList<String> grammas;
    ArrayList<String> lexems;

    public Grammar(LexAnalizator analizator, String name, String expression) {
        this.analizator = analizator;
        this.otherGrammas = analizator.gramas;
        this.expression = expression;
        this.name = name;
        this.containsOther = false;
        this.grammas = new ArrayList<String>();
        this.lexems = new ArrayList<String>();
        this.parse();
    }


    public void checkOther() {
        char[] exp = expression.toCharArray();
        for(int i = 0; i < exp.length; i++)
            if(exp[i] == '<') containsOther = true;
    }

    public int findGrammar(int start) {
        int end = start + 1;
        String gram = "";
        while(expression.charAt(end) != '>') {
            gram += expression.charAt(end);
            end++;
        }
        grammas.add(gram);
        return end;
    }

    public void parse() {
        this.checkOther();
        if(this.containsOther) {
            int position = 0;
            while (position < expression.length()) position = findGrammar(position);
        }   else {
            String[] lexms = expression.split(",");
            for(int i = 0; i < lexms.length; i++) lexems.add(lexms[i]);
        }
    }

    public Grammar searchGrammarByName(String name) {
        for(int i = 0; i < otherGrammas.size(); i++)
            if(otherGrammas.get(i).name.equals(name) == true) return otherGrammas.get(i);
        return null;
    }

    public int check(String input, int position) {
        int result = position;
        if(this.containsOther) {
            for(int i = 0; i < this.grammas.size(); i++) {
                Grammar grammar = searchGrammarByName(this.grammas.get(i));
                result = grammar.check(input, result);
            }
        }   else {
            int maxResult = 0;
            for(int i = 0; i < this.lexems.size(); i++) {
                String lexem = this.lexems.get(i);
                String subString = input.substring(result, lexem.length());
                if(lexem.equals(subString) == true)
                    if(lexem.length() > maxResult)
                        maxResult = lexem.length();
            }
            result = result + maxResult;
        }
        return result;
    }


}
