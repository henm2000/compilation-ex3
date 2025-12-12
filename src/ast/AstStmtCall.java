package ast;
import types.*;

public class AstStmtCall extends AstStmt {
    public AstExp call;
    public int line;
    public AstStmtCall(AstExp call, int line) {
        serialNumber = AstNodeSerialNumber.getFresh();
        System.out.print("====================== stmt -> callExp SEMICOLON\n");
        this.call = call;
        this.line = line;
    }

    public void printMe() {
        System.out.print("AST NODE STMT CALL\n");
        AstGraphviz.getInstance().logNode(serialNumber, "CALL_STMT");

        if (call != null) {
            call.printMe();
            AstGraphviz.getInstance().logEdge(serialNumber, call.serialNumber);
        }
    }

    public Type semantMe() {
        if (call != null) {
            call.semantMe();
        }
        return null;
    }
}