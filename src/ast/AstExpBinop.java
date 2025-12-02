package ast;

public class AstExpBinop extends AstExp
{
    int op;
    public AstExp left;
    public AstExp right;    
    public int line;
    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AstExpBinop(AstExp left, AstExp right, int op, int line)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        serialNumber = AstNodeSerialNumber.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.print("====================== exp -> exp BINOP exp\n");

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.left = left;
        this.right = right;
        this.op = op;
        this.line = line;
    }
    
    /*************************************************/
    /* The printing message for a binop exp AST node */
    /*************************************************/
    public void printMe()
    {
        String sop="";
        
        /*********************************/
        /* CONVERT op to a printable sop */
        /*********************************/
        /* Mapping used in CUP actions:
         * 0 -> PLUS
         * 1 -> MINUS
         * 2 -> TIMES
         * 3 -> DIVIDE
         * 4 -> LT
         * 5 -> GT
         * 6 -> EQ
         */
        if (op == 0) { sop = "+"; }
        else if (op == 1) { sop = "-"; }
        else if (op == 2) { sop = "*"; }
        else if (op == 3) { sop = "/"; }
        else if (op == 4) { sop = "<"; }
        else if (op == 5) { sop = ">"; }
        else if (op == 6) { sop = "="; }
        else { sop = "?"; }
        
        /*************************************/
        /* AST NODE TYPE = AST BINOP EXP */
        /*************************************/
        System.out.print("AST NODE BINOP EXP ( " + sop + " )\n");

        /**************************************/
        /* RECURSIVELY PRINT left + right ... */
        /**************************************/
        if (left != null) left.printMe();
        if (right != null) right.printMe();
        
        /**************************************/
        /* PRINT to AST GRAPHVIZ DOT file */
        /**************************************/
        // the helper AstGraphviz is assumed to exist in the project
        AstGraphviz.getInstance().logNode(
            serialNumber,
            "BINOP\\n(" + sop + ")"
        );
        
        /* print edges to children */
        if (left != null)
            AstGraphviz.getInstance().logEdge(serialNumber, left.serialNumber);
        if (right != null)
            AstGraphviz.getInstance().logEdge(serialNumber, right.serialNumber);
    }
    public Type semantMe()
    {
        Type t1 = null;
        Type t2 = null;
        
        if (left  != null) t1 = left.semantMe();
        if (right != null) t2 = right.semantMe();
        
        if ((t1 == TypeInt.getInstance()) && (t2 == TypeInt.getInstance()))
        {
            return TypeInt.getInstance();
        }
        System.exit(0);
        return null;
    }
}
