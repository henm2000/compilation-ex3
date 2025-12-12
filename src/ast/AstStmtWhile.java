package ast;

import types.*;
import symboltable.SymbolTable;
import exceptions.SemanticErrorException;
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
    
    public Type semantMe()
    {
        /****************************/
        /* [0] Semant the Condition */
        /*     Must be of type int   */
        /****************************/
        if (cond == null || cond.semantMe() != TypeInt.getInstance())
        {
            throw new SemanticErrorException(line);
        }
        
        /*************************/
        /* [1] Begin While Scope */
        /*************************/
        SymbolTable.getInstance().beginScope();

        /***************************/
        /* [2] Semant Body         */
        /***************************/
        if (body != null) {
            body.semantMe();
        }

        /*****************/
        /* [3] End Scope */
        /*****************/
        SymbolTable.getInstance().endScope();

        /***************************************************/
        /* [4] Return value is irrelevant for while statement */
        /***************************************************/
        return null;		
    }
}