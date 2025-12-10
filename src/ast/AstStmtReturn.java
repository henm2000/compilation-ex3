package ast;

import types.*;
import symboltable.SymbolTable;
import SemanticErrorException;

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
        if (!isReturnTypeCompatible(returnType, retExpType)) {
            throw new SemanticErrorException(line);
        }
        
        /************************************************/
        /* [6] Return value is irrelevant for statements */
        /************************************************/
        return null;
    }
    
    /**
     * Check if return expression type is compatible with function return type
     * Uses the same rules as assignment compatibility
     */
    private boolean isReturnTypeCompatible(Type returnType, Type expType)
    {
        /************************************************/
        /* Handle nil: can be returned for arrays/classes */
        /* but not for primitives (int/string)            */
        /************************************************/
        if (isNilType(expType)) {
            return returnType.isArray() || returnType.isClass();
        }
        
        /************************************************/
        /* Primitives: exact type match required         */
        /************************************************/
        if (returnType == TypeInt.getInstance() || returnType == TypeString.getInstance()) {
            return returnType == expType;
        }
        
        /************************************************/
        /* Arrays: exact type match required (same name) */
        /************************************************/
        if (returnType.isArray()) {
            if (!expType.isArray()) {
                return false;
            }
            TypeArray returnArray = (TypeArray) returnType;
            TypeArray expArray = (TypeArray) expType;
            return returnArray.name.equals(expArray.name);
        }
        
        /************************************************/
        /* Classes: same type OR expType is subclass    */
        /* of returnType (polymorphism)                  */
        /************************************************/
        if (returnType.isClass()) {
            if (!expType.isClass()) {
                return false;
            }
            
            TypeClass returnClass = (TypeClass) returnType;
            TypeClass expClass = (TypeClass) expType;
            
            // Same class
            if (returnClass.name.equals(expClass.name)) {
                return true;
            }
            
            // Check if expClass is a subclass of returnClass
            TypeClass current = expClass.father;
            while (current != null) {
                if (current.name.equals(returnClass.name)) {
                    return true;
                }
                current = current.father;
            }
            
            return false;
        }
        
        /************************************************/
        /* If we get here, types are incompatible       */
        /************************************************/
        return false;
    }
    
    /**
     * Check if a type is nil
     */
    private boolean isNilType(Type t)
    {
        if (t == null) return false;
        return t.name != null && t.name.equals("nil");
    }
}