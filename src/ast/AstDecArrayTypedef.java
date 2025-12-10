package ast;

import types.*;
import symboltable.SymbolTable;

public class AstDecArrayTypedef extends AstDec
{
    public String id;
    public String baseType;
    public int line;

    public AstDecArrayTypedef(String id, String baseType, int line)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        System.out.print("====================== dec -> arrayTypedef\n");
        this.id = id;
        this.baseType = baseType;
        this.line = line;
    }

    public Type semantMe(){
        /************************************************/
        /* [0] Check that we're in global scope        */
        /*     Arrays can only be defined in global scope */
        /************************************************/
        if (!SymbolTable.getInstance().isInGlobalScope()) {
            throw new SemanticErrorException(line);
        }
        
        /************************************************/
        /* [1] Check if base type exists (non-void)    */
        /*     Must be a type definition, not a variable */
        /************************************************/
        Type baseTypeObj = SymbolTable.getInstance().findTypeDefinition(baseType);
        if (baseTypeObj == null) {
            throw new SemanticErrorException(line);
        }
        
        /************************************************/
        /* [2] Check that base type is not void        */
        /************************************************/
        if (baseTypeObj == TypeVoid.getInstance() || baseType.equals("void")) {
            throw new SemanticErrorException(line);
        }
        
        /************************************************/
        /* [2] Check if array type name is a reserved keyword */
        /************************************************/
        if (SymbolTable.getInstance().isReservedKeyword(id)) {
            throw new SemanticErrorException(line);
        }
        
        /************************************************/
        /* [2a] Check that name doesn't exist in scope  */
        /************************************************/
        if (SymbolTable.getInstance().findInCurrentScope(id) != null) {
            throw new SemanticErrorException(line);
        }
        
        /************************************************/
        /* [3] Create array type and enter to table    */
        /************************************************/
        TypeArray arrayType = new TypeArray(baseTypeObj, id);
        SymbolTable.getInstance().enter(id, arrayType);
        
        /************************************************/
        /* [4] Return value is irrelevant               */
        /************************************************/
        return null;
    }

    public void printMe()
    {
        System.out.print("AST NODE ARRAY TYPEDEF ( " + id + " : " + baseType + " )\n");
        AstGraphviz.getInstance().logNode(serialNumber, "ARRAYTYPE\\n(" + id + ")");
    }
}