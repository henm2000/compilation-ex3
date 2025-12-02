package ast;

import java.util.List;

public class AstExpCall extends AstExp
{
    public AstVar receiver; // may be null for direct call
    public String methodName;
    public List<AstExp> args;   
    public int line;
    public AstExpCall(AstVar receiver, String methodName, List<AstExp> args, int line)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        System.out.print("====================== exp -> callExp\n");
        this.receiver = receiver;
        this.methodName = methodName;
        this.args = args;
        this.line = line;
    }

    public void printMe()
    {
        System.out.print("AST NODE CALL EXP ( " + methodName + " )\n");
        AstGraphviz.getInstance().logNode(serialNumber, "CALL\\n(" + methodName + ")");
        if (receiver != null) {
            receiver.printMe();
            AstGraphviz.getInstance().logEdge(serialNumber, receiver.serialNumber);
        }
        if (args != null) {
            for (AstExp e : args) {
                if (e != null) {
                    e.printMe();
                    AstGraphviz.getInstance().logEdge(serialNumber, e.serialNumber);
                }
            }
        }
    }
}