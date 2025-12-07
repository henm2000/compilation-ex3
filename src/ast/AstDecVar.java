package ast;

import types.*;
import symboltable.SymbolTable;

public class AstDecVar extends AstDec
{
    public String typeName;
    public String id;
    public AstExp init; // may be null
    public int line;

    public AstDecVar(String typeName, String id, AstExp init, int line)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        System.out.print("====================== dec -> varDec\n");
        this.typeName = typeName;
        this.id = id;
        this.init = init;   
        this.line = line;
    }

    public void printMe()
    {
        System.out.print("AST NODE DEC VAR ( " + typeName + " " + id + " )\n");
        AstGraphviz.getInstance().logNode(serialNumber, "DEC_VAR\\n(" + typeName + " " + id + ")");
        if (init != null) {
            init.printMe();
            AstGraphviz.getInstance().logEdge(serialNumber, init.serialNumber);
        }
    }
    public Type semantMe()
    {
        Type t;
    
        /****************************/
        /* [1] Check If Type exists  */
        /*     Must be a type definition, not a variable */
        /****************************/
        t = SymbolTable.getInstance().findTypeDefinition(typeName);
        if (t == null)
        {
            throw new SemanticErrorException(line);
        }
        
        /**************************************/
        /* [2] Check That type is not void    */
        /**************************************/
        if (t == TypeVoid.getInstance() || typeName.equals("void"))
        {
            throw new SemanticErrorException(line);
        }
        
        /**************************************/
        /* [3] Check That Name does NOT exist within the scope */
        /**************************************/
        if (SymbolTable.getInstance().findInCurrentScope(id) != null) {
            throw new SemanticErrorException(line);
        }
  
        /************************************************/
        /* [4] Enter the Identifier to the Symbol Table */
        /************************************************/
        SymbolTable.getInstance().enter(id, t);
        
        /************************************************/
        /* [5] Check initialization expression type     */
        /************************************************/
        if (init != null) {
            Type initType = init.semantMe();
            // Note: Assignment compatibility will be checked later in AstStmtAssign
            // For now, we just semant the expression to ensure it's valid
        }
  
        /************************************************************/
        /* [6] Return value is irrelevant for variable declarations */
        /************************************************************/
        return null;		
    }  
}