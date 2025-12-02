package ast;

import java.util.List;

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
        Type returnType = null;
        TypeList type_list = null;

        /*******************/
        /* [0] return type */
        /*******************/
        returnType = SymbolTable.getInstance().find(returnTypeName);
        if (returnType == null)
        {
            System.out.format(">> ERROR [%d:%d] non existing return type %s\n",6,6,returnType);				
        }
    
        /****************************/
        /* [1] Begin Function Scope */
        /****************************/
        SymbolTable.getInstance().beginScope();

        /***************************/
        /* [2] Semant Input Params */
        /***************************/
        for (AstTypeNameList it = params; it  != null; it = it.tail)
        {
            t = SymbolTable.getInstance().find(it.head.type);
            if (t == null)
            {
                System.out.format(">> ERROR [%d:%d] non existing type %s\n",2,2,it.head.type);				
            }
            else
            {
                type_list = new TypeList(t,type_list);
                SymbolTable.getInstance().enter(it.head.name,t);
            }
        }

        /*******************/
        /* [3] Semant Body */
        /*******************/
        body.semantMe();

        /*****************/
        /* [4] End Scope */
        /*****************/
        SymbolTable.getInstance().endScope();

        /***************************************************/
        /* [5] Enter the Function Type to the Symbol Table */
        /***************************************************/
        SymbolTable.getInstance().enter(name,new TypeFunction(returnType,name,type_list));

        /************************************************************/
        /* [6] Return value is irrelevant for function declarations */
        /************************************************************/
        return null;		
    }
}