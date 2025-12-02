package ast;

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

    public void printMe()
    {
        System.out.print("AST NODE ARRAY TYPEDEF ( " + id + " : " + baseType + " )\n");
        AstGraphviz.getInstance().logNode(serialNumber, "ARRAYTYPE\\n(" + id + ")");
    }
}