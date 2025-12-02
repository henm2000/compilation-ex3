package ast;

public class AstStmtReturn extends AstStmt
{
    public AstExp retExp; // may be null
    public int line;
    public AstStmtReturn(AstExp retExp, int line)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        System.out.print("====================== stmt -> RETURN\n");
        this.retExp = retExp;
        this.line = line;
    }

    public void printMe()
    {
        System.out.print("AST NODE RETURN\n");
        AstGraphviz.getInstance().logNode(serialNumber, "RETURN");
        if (retExp != null) {
            retExp.printMe();
            AstGraphviz.getInstance().logEdge(serialNumber, retExp.serialNumber);
        }
    }
}