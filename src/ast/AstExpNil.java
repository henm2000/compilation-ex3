package ast;

public class AstExpNil extends AstExp
{
    public int line;
    public AstExpNil(int line)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        System.out.print("====================== exp -> NIL\n");
        this.line = line;
    }

    public void printMe()
    {
        System.out.print("AST NODE NIL\n");
        AstGraphviz.getInstance().logNode(serialNumber, "NIL");
    }
}