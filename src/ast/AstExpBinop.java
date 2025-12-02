package ast;
//TODO: we should implement the boolean = sign according to class logic. 

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
    /* Mapping used in CUP actions:
         * 0 -> PLUS
         * 1 -> MINUS
         * 2 -> TIMES
         * 3 -> DIVIDE
         * 4 -> LT
         * 5 -> GT
         * 6 -> EQ
         */
        Type t1 = null;
        Type t2 = null;
        
        if (left  != null) t1 = left.semantMe();
        if (right != null) t2 = right.semantMe();
        /*
        TODO: we should implement the boolean = sign according to class logic. 
        */
        if (this.op == 0 && t1.getInstance() == t2.getInstance()){
            //case = (boolean opeartor, compares two comparable variables \ classes)
            return t1.getInstance();
        }
        if (this.op == 1 && t1.getInstance() == t2.getInstance() && t1.getInstance().name == "string"){
            // case str concatenation 
            return t1.getInstance();
        }
        if (t1.getInstance == t2.getInstance && t1.getInstance().name == "int"){
            // case int operators.
            return t1.getInstance();
        }
        throw SemanticError(this.line);
        System.exit(0);
        return null;
    }

}
