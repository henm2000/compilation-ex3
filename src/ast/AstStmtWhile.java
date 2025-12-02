package ast;

public class AstStmtWhile extends AstStmt
{
    public AstExp cond;
    public AstStmtList body;
    public int line;
    public AstStmtWhile(AstExp cond, AstStmtList body, int line)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        System.out.print("====================== stmt -> WHILE\n");

        this.cond = cond;
        this.body = body;
        this.line = line;
    }

    public void printMe()
    {
        System.out.print("AST NODE WHILE\n");
        AstGraphviz.getInstance().logNode(serialNumber, "WHILE");

        if (cond != null) {
            cond.printMe();
            AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);
        }

        if (body != null) {
            body.printMe();
            AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);
        }
    }
}