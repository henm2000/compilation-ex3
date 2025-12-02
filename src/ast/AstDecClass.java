package ast;

import java.util.List;

public class AstDecClass extends AstDec
{
    public String name;
    public String extendsName; // may be null
    public List<AstDec> fields;
    public int line;

    public AstDecClass(String name, String extendsName, List<AstDec> fields, int line)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        System.out.print("====================== dec -> classDec\n");
        this.name = name;
        this.extendsName = extendsName;
        this.fields = fields;
        this.line = line;
    }

    public void printMe()
    {
        System.out.print("AST NODE CLASS DEC ( " + name + " )\n");
        AstGraphviz.getInstance().logNode(serialNumber, "CLASS\\n(" + name + ")");
        if (fields != null) {
            for (AstDec f : fields) {
                f.printMe();
                AstGraphviz.getInstance().logEdge(serialNumber, f.serialNumber);
            }
        }
    }
    public Type semantMe()
    {	
        /*************************/
        /* [1] Begin Class Scope */
        /*************************/
        SymbolTable.getInstance().beginScope();

        /***************************/
        /* [2] Semant Data Members */
        /***************************/
        TypeClass t = new TypeClass(null,name, dataMembers.semantMe());

        /*****************/
        /* [3] End Scope */
        /*****************/
        SymbolTable.getInstance().endScope();

        /************************************************/
        /* [4] Enter the Class Type to the Symbol Table */
        /************************************************/
        SymbolTable.getInstance().enter(name,t);

        /*********************************************************/
        /* [5] Return value is irrelevant for class declarations */
        /*********************************************************/
        return null;		
    }
}