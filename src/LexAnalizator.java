import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 07.05.13
 * Time: 14:20
 * To change this template use File | Settings | File Templates.
 */
public class LexAnalizator {
    ArrayList<Lexem> lexems;
    ArrayList<Grammar> gramas;

    public LexAnalizator(Java2ObjCConverter converter) {
        this.lexems = converter.lexems;
        gramas = new ArrayList<Grammar>();
    }

    public boolean hasAlternatives(String grammar) {
        for(int i = 0; i < grammar.length(); i++)
            if(grammar.charAt(i) == '|') return true;
        return false;
    }

    public void fillGrammas(String input) {
        String[] grStrings = input.split("\n");
        for(int i = 0; i < grStrings.length; i++) {
            String grString = grStrings[i];
            if(hasAlternatives(grString)) {
                String[] grParts = grString.split("::=");
                String[] grAlterns = grParts[1].split("|");
                for(int j = 0; j < grAlterns.length; j++) {
                    Grammar grammar = new Grammar(this, grParts[0], grAlterns[j]);
                    gramas.add(grammar);
                }
            }   else {
                String[] grParts = grString.split("::=");
                Grammar grammar = new Grammar(this, grParts[0], grParts[1]);
                gramas.add(grammar);
            }
        }
    }


}
