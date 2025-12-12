package ast;
import types.*;
import exceptions.SemanticErrorException;

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
        
        // Handle equality operator (=)
        if (this.op == 6) {
            if (areTypesComparableForEquality(t1, t2)) {
                return TypeInt.getInstance(); // Equality returns int
            } else {
                throw new SemanticErrorException(this.line);
            }
        }
        
        // Handle PLUS operator (+)
        if (this.op == 0) {
            // String concatenation
            if (t1 == TypeString.getInstance() && t2 == TypeString.getInstance()) {
                return TypeString.getInstance();
            }
            // Integer addition
            if (t1 == TypeInt.getInstance() && t2 == TypeInt.getInstance()) {
                return TypeInt.getInstance();
            }
            throw new SemanticErrorException(this.line);
        }
        
        // Handle MINUS, TIMES, DIVIDE, LT, GT (only for integers)
        if (this.op >= 1 && this.op <= 5) {
            if (t1 == TypeInt.getInstance() && t2 == TypeInt.getInstance()) {
                // Check division by zero for constant expressions
                if (this.op == 3) { // DIVIDE
                    checkDivisionByZero(right);
                }
                return TypeInt.getInstance();
            }
            throw new SemanticErrorException(this.line);
        }
        
        throw new SemanticErrorException(this.line);
    }

    /**
     * Check if two types are comparable for equality
     */
    private boolean areTypesComparableForEquality(Type t1, Type t2) {
        // Check for nil comparisons
        boolean t1IsNil = isNilType(t1);
        boolean t2IsNil = isNilType(t2);
        
        // Both are nil - not allowed (though this case might not occur)
        if (t1IsNil && t2IsNil) {
            return false; // or true, depending on your language spec
        }
        
        // One is nil, check if the other is array or class
        if (t1IsNil) {
            return t2.isArray() || t2.isClass();
        }
        if (t2IsNil) {
            return t1.isArray() || t1.isClass();
        }
        
        // Both are primitives - must be exactly the same type
        if (t1 == TypeInt.getInstance() || t1 == TypeString.getInstance()) {
            return t1 == t2;
        }
        
        // Both are arrays - must be exactly the same array type (same name)
        if (t1.isArray() && t2.isArray()) {
            TypeArray arr1 = (TypeArray) t1;
            TypeArray arr2 = (TypeArray) t2;
            return arr1.name.equals(arr2.name); // Arrays are non-interchangeable
        }
        
        // Both are classes - same type or one is subclass of the other
        if (t1.isClass() && t2.isClass()) {
            TypeClass c1 = (TypeClass) t1;
            TypeClass c2 = (TypeClass) t2;
            return areClassesComparable(c1, c2);
        }
        
        // Different type categories - not comparable
        return false;
    }

    /**
     * Check if two classes are comparable (same type or one is subclass of the other)
     */
    private boolean areClassesComparable(TypeClass c1, TypeClass c2) {
        // Same class
        if (c1.name.equals(c2.name)) {
            return true;
        }
        
        // Check if c1 is a subclass of c2
        TypeClass current = c1.father;
        while (current != null) {
            if (current.name.equals(c2.name)) {
                return true;
            }
            current = current.father;
        }
        
        // Check if c2 is a subclass of c1
        current = c2.father;
        while (current != null) {
            if (current.name.equals(c1.name)) {
                return true;
            }
            current = current.father;
        }
        
        return false;
    }

    /**
     * Check if a type is nil
     */
    private boolean isNilType(Type t) {
        // TypeNil should be a singleton like TypeInt/TypeString
        // You'll need to create TypeNil class if it doesn't exist
        if (t == null) return false;
        return t.name != null && t.name.equals("nil");
    }

    /**
     * Check for division by zero (for constant expressions)
     */
    private void checkDivisionByZero(AstExp divisor) {
        // If divisor is a constant integer expression with value 0, throw error
        if (divisor instanceof AstExpInt) {
            AstExpInt intExp = (AstExpInt) divisor;
            if (intExp.value == 0) {
                throw new SemanticErrorException(this.line);
            }
        }
    }   
}
