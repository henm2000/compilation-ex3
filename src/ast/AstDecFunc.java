package ast;

import java.util.List;
import types.*;
import exceptions.SemanticErrorException;
import symboltable.SymbolTable;

public class AstDecFunc extends AstDec
{
    public String returnType;
    public String name;
    public List<AstParam> params;
    public AstStmtList body;
    public int line;
    
    public AstDecFunc(String returnType, String name, List<AstParam> params, AstStmtList body, int line)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        System.out.print("====================== dec -> funcDec\n");
        this.returnType = returnType;
        this.name = name;
        this.params = params;
        this.body = body;
        this.line = line;
    }

    public void printMe()
    {
        System.out.print("AST NODE FUNC DEC ( " + returnType + " " + name + " )\n");
        AstGraphviz.getInstance().logNode(serialNumber, "FUNC\\n(" + name + ")");
        if (params != null) {
            for (AstParam p : params) {
                p.printMe();
                AstGraphviz.getInstance().logEdge(serialNumber, p.serialNumber);
            }
        }
        if (body != null) {
            body.printMe();
            AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);
        }
    }
    
    public Type semantMe()
    {
        Type t;
        Type returnTypeObj = null;
        TypeList type_list = null;

        /*******************/
        /* [0] Check if function name is a reserved keyword */
        /*******************/
        if (SymbolTable.getInstance().isReservedKeyword(name)) {
            throw new SemanticErrorException(line);
        }
        
        /*******************/
        /* [0a] Check return type exists */
        /*     Must be a type definition, not a variable */
        /*******************/
        returnTypeObj = SymbolTable.getInstance().findTypeDefinition(this.returnType);
        if (returnTypeObj == null)
        {
            throw new SemanticErrorException(line);
        }
    
        /****************************/
        /* [1] Begin Function Scope */
        /****************************/
        SymbolTable.getInstance().beginScope();

        /***************************/
        /* [2] Semant Input Params */
        /***************************/
        if (params != null) {
            for (int i = params.size() - 1; i >= 0; i--)
            {
                AstParam param = params.get(i);
                
                /************************************************/
                /* [2a] Check if parameter name is a reserved keyword */
                /************************************************/
                if (SymbolTable.getInstance().isReservedKeyword(param.id)) {
                    throw new SemanticErrorException(line);
                }
                
                /**************************************/
                /* [2b] Check That Name does NOT exist */
                /**************************************/
                if (SymbolTable.getInstance().findInCurrentScope(param.id) != null)
                {
                    throw new SemanticErrorException(line);
                }
                
                t = SymbolTable.getInstance().findTypeDefinition(param.typeName);
                if (t == null)
                {
                    throw new SemanticErrorException(line);
                }
                
                /************************************************/
                /* [2c] Check that parameter type is not void    */
                /************************************************/
                if (t == TypeVoid.getInstance() || param.typeName.equals("void"))
                {
                    throw new SemanticErrorException(line);
                }
                
                type_list = new TypeList(t, type_list);
                SymbolTable.getInstance().enter(param.id, t);
            }
        }

        TypeFunction funcType = new TypeFunction(returnTypeObj, name, type_list);
        SymbolTable.getInstance().enter(name, funcType);

        /*******************/
        /* [3] Set return type for return statement validation */
        /*******************/
        SymbolTable.getInstance().setReturnType(returnTypeObj);

        /*******************/
        /* [4] Semant Body */
        /*******************/
        if (body != null) {
            body.semantMe();
        }

        /*******************/
        /* [5] Clear return type */
        /*******************/
        SymbolTable.getInstance().clearReturnType();

        /*****************/
        /* [6] End Scope */
        /*****************/
        SymbolTable.getInstance().endScope();

        /***************************************************/
        /* [7] Enter the Function Type to the Symbol Table */
        /***************************************************/
        SymbolTable.getInstance().enter(name, funcType);

        /************************************************************/
        /* [8] Return value is irrelevant for function declarations */
        /************************************************************/
        return null;		
    }
}