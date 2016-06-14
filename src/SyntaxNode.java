import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: dreamer
 * Date: 22.06.13
 * Time: 8:10
 * To change this template use File | Settings | File Templates.
 */
public class SyntaxNode {
    String name;
    int id;
    int typeId;
    ArrayList<SyntaxNode> children;

    public SyntaxNode(String name, int id, int typeId) {
        this.name = name;
        this.id = id;
        this.typeId = typeId;
        this.children = new ArrayList<SyntaxNode>();
    }

    public String print() {
        String result = "";
        for(int i = 0; i < children.size(); i++)
            result += children.get(i).print() + " ";
        return result;
    }
}
