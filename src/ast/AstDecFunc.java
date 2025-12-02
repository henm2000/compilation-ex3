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
}