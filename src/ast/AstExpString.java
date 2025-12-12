package ast;
import types.*;

public class AstExpString extends AstExp
{
    public String value;
    public int line;
    public AstExpString(String value, int line)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        System.out.print("====================== exp -> STRING\n");
        this.value = value;
        this.line = line;
    }

    public void printMe()
    {
        System.out.print("AST NODE STRING EXP ( " + value + " )\n");
        AstGraphviz.getInstance().logNode(serialNumber, "STRING\\n(" + value + ")");
    }
    public Type semantMe()
    {
        return TypeString.getInstance();
    }
}