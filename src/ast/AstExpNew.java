package ast;

import types.*;
import symboltable.SymbolTable;
import exceptions.SemanticErrorException;

public class AstExpNew extends AstExp
{// new name  \\ new name [ exp ]
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

    public Type semantMe()
    {
        Type t = null;
        
        /************************************************/
        /* [1] Find type definition (not a variable)   */
        /************************************************/
        t = SymbolTable.getInstance().findTypeDefinition(typeName);
        if (t == null) {
            throw new SemanticErrorException(line);
        }
        
        /************************************************/
        /* [2] Handle array allocation: new T[e]      */
        /************************************************/
        if (sizeExpr != null) {
            /************************************************/
            /* [2a] Type must be non-void                   */
            /************************************************/
            if (t == TypeVoid.getInstance() || typeName.equals("void")) {
                throw new SemanticErrorException(line);
            }
            
            /************************************************/
            /* [2b] Semant size expression                 */
            /************************************************/
            Type sizeType = sizeExpr.semantMe();
            
            /************************************************/
            /* [2c] Size expression must be of type int   */
            /************************************************/
            if (sizeType != TypeInt.getInstance()) {
                throw new SemanticErrorException(line);
            }
            
            /************************************************/
            /* [2d] If size is constant, must be > 0        */
            /************************************************/
            if (sizeExpr instanceof AstExpInt) {
                AstExpInt intExp = (AstExpInt) sizeExpr;
                if (intExp.value <= 0) {
                    throw new SemanticErrorException(line);
                }
            }
            
            /************************************************/
            /* [2e] Return array type over element type T  */
            /*      For new T[e], T is the element type    */
            /************************************************/
            return t; // return the element type T
        }
        
        /************************************************/
        /* [3] Handle class allocation: new T           */
        /************************************************/
        else {
            /************************************************/
            /* [3a] Type must be a class                    */
            /************************************************/
            if (!t.isClass()) {
                throw new SemanticErrorException(line);
            }
            
            /************************************************/
            /* [3b] Return the class type                  */
            /************************************************/
            return t;
        }
    }
}