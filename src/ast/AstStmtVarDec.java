package ast;
import types.*;

public class AstStmtVarDec extends AstStmt
{
    public AstDec dec;
    public int line;
    public AstStmtVarDec(AstDec dec, int line) 
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        System.out.print("====================== stmt -> varDec\n");
        this.dec = dec;
        this.line = line;
    }

    public void printMe() {
        System.out.print("AST NODE STMT VAR DEC\n");
        AstGraphviz.getInstance().logNode(serialNumber, "VARDECL STMT");
        if (dec != null) {
            dec.printMe();
            AstGraphviz.getInstance().logEdge(serialNumber, dec.serialNumber);
        }
    }
    public Type semantMe()
	{
		return dec.semantMe();
	}
}