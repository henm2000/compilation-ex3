package ast;
import types.*;

public class AstExpNil extends AstExp
{
    public int line;
    public AstExpNil(int line)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        System.out.print("====================== exp -> NIL\n");
        this.line = line;
    }
    public Type semantMe(){
        return TypeNil.getInstance();
    }

    public void printMe()
    {
        System.out.print("AST NODE NIL\n");
        AstGraphviz.getInstance().logNode(serialNumber, "NIL");
    }
}