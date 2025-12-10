package ast;

import types.*;
import symboltable.SymbolTable;
import SemanticErrorException;

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
        /* [1] Begin Then Scope */
        /*************************/
        SymbolTable.getInstance().beginScope();

        /***************************/
        /* [2] Semant Then Body    */
        /***************************/
        if (thenBody != null) {
            thenBody.semantMe();
        }

        /*****************/
        /* [3] End Then Scope */
        /*****************/
        SymbolTable.getInstance().endScope();
        
        /*************************/
        /* [4] Handle Else Body  */
        /*     (if it exists)     */
        /*************************/
        if (elseBody != null) {
            /*************************/
            /* [4a] Begin Else Scope */
            /*************************/
            SymbolTable.getInstance().beginScope();

            /***************************/
            /* [4b] Semant Else Body    */
            /***************************/
            elseBody.semantMe();

            /*****************/
            /* [4c] End Else Scope */
            /*****************/
            SymbolTable.getInstance().endScope();
        }

        /***************************************************/
        /* [5] Return value is irrelevant for if statement */
        /***************************************************/
        return null;		
    }	
}
