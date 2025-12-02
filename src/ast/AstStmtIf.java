package ast;

public class AstStmtIf extends AstStmt
{
    public AstExp cond;
    public AstStmtList thenBody;
    public AstStmtList elseBody; // may be null
    public int line;
    public AstStmtIf(AstExp cond, AstStmtList thenBody, AstStmtList elseBody, int line)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        
        if (elseBody == null) {
            System.out.print("====================== stmt -> IF (exp) {stmtList}\n");
        } else {
            System.out.print("====================== stmt -> IF (exp) {stmtList} ELSE {stmtList}\n");
        }

        this.cond = cond;
        this.thenBody = thenBody;
        this.elseBody = elseBody;
        this.line = line;
    }

    public void printMe()
    {
        System.out.print("AST NODE IF STMT\n");

        AstGraphviz.getInstance().logNode(serialNumber, "IF");

        // print condition
        if (cond != null) {
            cond.printMe();
            AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);
        }

        // print 'then' body
        if (thenBody != null) {
            thenBody.printMe();
            AstGraphviz.getInstance().logEdge(serialNumber, thenBody.serialNumber);
        }

        // print 'else' body (if exists)
        if (elseBody != null) {
            elseBody.printMe();
            AstGraphviz.getInstance().logEdge(serialNumber, elseBody.serialNumber);
        }
    }
}
