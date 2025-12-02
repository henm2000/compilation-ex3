package ast;

public class AstExpNew extends AstExp
{
    public String typeName;
    public AstExp sizeExpr; // for array creation; null if simple new
    public int line;
    public AstExpNew(String typeName, int line)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        System.out.print("====================== exp -> NEW type\n");
        this.typeName = typeName;
        this.line = line;
    }

    public AstExpNew(String typeName, AstExp sizeExpr, int line)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        System.out.print("====================== exp -> NEW type [ exp ]\n");
        this.typeName = typeName;
        this.sizeExpr = sizeExpr;
        this.line = line;
    }

    public void printMe()
    {
        System.out.print("AST NODE NEW EXP ( " + typeName + " )\n");
        AstGraphviz.getInstance().logNode(serialNumber, "NEW\\n(" + typeName + ")");
        if (sizeExpr != null) {
            sizeExpr.printMe();
            AstGraphviz.getInstance().logEdge(serialNumber, sizeExpr.serialNumber);
        }
    }
}