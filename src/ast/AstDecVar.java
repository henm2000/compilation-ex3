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
}