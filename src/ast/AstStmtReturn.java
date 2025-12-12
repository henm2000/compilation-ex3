package ast;
import types.*;

import types.*;
import symboltable.SymbolTable;
import exceptions.SemanticErrorException;
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
    
    public Type semantMe()
    {
        Type returnType = null;
        Type retExpType = null;
        
        /************************************************/
        /* [1] Get current function return type        */
        /************************************************/
        returnType = SymbolTable.getInstance().getCurrentReturnType();
        if (returnType == null) {
            // Return statement outside a function - this shouldn't happen syntactically
            // but we check for safety
            throw new SemanticErrorException(line);
        }
        
        /************************************************/
        /* [2] Handle void return type                */
        /************************************************/
        if (returnType == TypeVoid.getInstance() || returnType.name.equals("void")) {
            // For void functions, return statement must be empty
            if (retExp != null) {
                throw new SemanticErrorException(line);
            }
            return null;
        }
        
        /************************************************/
        /* [3] Handle non-void return type            */
        /************************************************/
        // For non-void functions, return statement must have an expression
        if (retExp == null) {
            throw new SemanticErrorException(line);
        }
        
        /************************************************/
        /* [4] Semant return expression                */
        /************************************************/
        retExpType = retExp.semantMe();
        
        /************************************************/
        /* [5] Check return expression type compatibility */
        /*     Uses same rules as assignment            */
        /************************************************/
        if (!isAssignmentCompatible(returnType, retExpType, retExp)) {
            throw new SemanticErrorException(line);
        }
        
        /************************************************/
        /* [6] Return value is irrelevant for statements */
        /************************************************/
        return null;
    }
}