package ast;

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
        /* [1] Check If Type exists */
        /****************************/
        t = SymbolTable.getInstance().find(type);
        if (t == null)
        {
            System.out.format(">> ERROR [%d:%d] non existing type %s\n",2,2,type);
            System.exit(0);
        }
        
        /**************************************/
        /* [2] Check That Name does NOT exist */
        /**************************************/
        if (SymbolTable.getInstance().find(name) != null)
        {
            System.out.format(">> ERROR [%d:%d] variable %s already exists in scope\n",2,2,name);				
        }
  
        /************************************************/
        /* [3] Enter the Identifier to the Symbol Table */
        /************************************************/
        SymbolTable.getInstance().enter(name,t);
  
        /************************************************************/
        /* [4] Return value is irrelevant for variable declarations */
        /************************************************************/
        return null;		
    }  
}