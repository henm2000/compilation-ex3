package ast;

import java.util.List;
import types.*;
import symboltable.SymbolTable;
import exceptions.SemanticErrorException;

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

    public Type semantMe()
    {
        TypeFunction funcType = null;
        
        /************************************************/
        /* [1] Determine if this is a method or function call */
        /************************************************/
        if (receiver == null) {
            // Direct function call - look up in symbol table
            Type t = SymbolTable.getInstance().find(methodName);
            if (t == null || !(t instanceof TypeFunction)) {
                throw new SemanticErrorException(line);
            }
            funcType = (TypeFunction) t;
        } else {
            // Method call on a class instance
            Type receiverType = receiver.semantMe();
            
            // Receiver must be a class type (or nil, which is allowed for class types)
            // However, we can't actually call methods on nil, so check for nil first
            if (isNilType(receiverType)) {
                // Nil is allowed for class types, but we can't call methods on nil
                // This is a semantic error - attempting to call a method on nil
                throw new SemanticErrorException(line);
            }
            
            if (!receiverType.isClass()) {
                throw new SemanticErrorException(line);
            }
            
            TypeClass receiverClass = (TypeClass) receiverType;
            
            // Look up method in class hierarchy
            funcType = findMethodInClass(receiverClass, methodName);
            if (funcType == null) {
                throw new SemanticErrorException(line);
            }
        }
        
        /************************************************/
        /* [2] Check argument count matches parameter count */
        /************************************************/
        int argCount = (args != null) ? args.size() : 0;
        int paramCount = countParameters(funcType.params);
        
        if (argCount != paramCount) {
            throw new SemanticErrorException(line);
        }
        
        /************************************************/
        /* [3] Check each argument type is compatible */
        /************************************************/
        if (args != null && funcType.params != null) {
            TypeList currentParam = funcType.params;
            int argIndex = 0;
            
            for (AstExp arg : args) {
                Type argType = arg.semantMe();
                Type paramType = currentParam.head;
                
                // Check type compatibility
                if (!isAssignmentCompatible(paramType, argType, arg)) {
                    throw new SemanticErrorException(line);
                }
                
                currentParam = currentParam.tail;
                argIndex++;
            }
        }
        
        /************************************************/
        /* [4] Return the function's return type */
        /************************************************/
        return funcType.returnType;
    }
    
    /**
     * Find a method in a class or its superclasses
     */
    private TypeFunction findMethodInClass(TypeClass cls, String methodName)
    {
        TypeClass current = cls;
        
        // Search through the class hierarchy
        while (current != null) {
            // Search in dataMembers
            for (TypeList it = current.dataMembers; it != null; it = it.tail) {
                if (it.head != null && it.head.name != null && it.head.name.equals(methodName)) {
                    // Found it - check if it's a function (method)
                    if (it.head instanceof TypeFunction) {
                        return (TypeFunction) it.head;
                    }
                    // If it's a field, that's an error (can't call a field)
                    throw new SemanticErrorException(line);
                }
            }
            
            // Move to superclass
            current = current.father;
        }
        
        return null;
    }
    
    /**
     * Count the number of parameters in a TypeList
     */
    private int countParameters(TypeList params)
    {
        int count = 0;
        for (TypeList it = params; it != null; it = it.tail) {
            count++;
        }
        return count;
    }
}