package ast;

import java.util.List;
import java.util.HashMap;
import types.*;
import symboltable.SymbolTable;
import exceptions.SemanticErrorException;

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
        /************************************************/
        /* [0] Check that we're in global scope         */
        /*     Classes can only be defined in global scope */
        /************************************************/
        if (!SymbolTable.getInstance().isInGlobalScope()) {
            throw new SemanticErrorException(line);
        }
        
        /************************************************/
        /* [1] Check if class name is a reserved keyword */
        /************************************************/
        if (SymbolTable.getInstance().isReservedKeyword(name)) {
            throw new SemanticErrorException(line);
        }
        
        /************************************************/
        /* [1a] Check if class name already exists       */
        /************************************************/
        if (SymbolTable.getInstance().find(name) != null) {
            throw new SemanticErrorException(line);
        }
        
        /************************************************/
        /* [2] Check if superclass exists and get it    */
        /*     Must be a type definition, not a variable */
        /************************************************/
        TypeClass superClass = null;
        if (extendsName != null) {
            Type superType = SymbolTable.getInstance().findTypeDefinition(extendsName);
            if (superType == null || !superType.isClass()) {
                throw new SemanticErrorException(line);
            }
            superClass = (TypeClass) superType;
            
            /************************************************/
            /* [1a] Check for circular inheritance           */
            /************************************************/
            TypeClass current = superClass;
            while (current != null) {
                if (current.name.equals(name)) {
                    throw new SemanticErrorException(line);
                }
                current = current.father;
            }
        }
        
        /*************************/
        /* [3] Begin Class Scope */
        /*************************/
        SymbolTable.getInstance().beginScope();
        
        /************************************************/
        /* [3a] Create TypeClass first (without dataMembers) */
        /*      so we can set it as current class       */
        /************************************************/
        TypeClass t = new TypeClass(superClass, name, null);
        SymbolTable.getInstance().setCurrentClass(t);

        /************************************************/
        /* [4] Process fields/methods in order         */
        /*     Build TypeList and check for errors      */
        /************************************************/
        TypeList dataMembers = null;
        HashMap<String, Type> definedMembers = new HashMap<>(); // Track defined members for shadowing check
        // Also track members in order for incremental updates to t.dataMembers
        java.util.ArrayList<Type> memberTypesList = new java.util.ArrayList<>();
        
        // First pass: collect all member names from superclass for shadowing check
        HashMap<String, Type> superMembers = new HashMap<>();
        if (superClass != null) {
            TypeClass current = superClass;
            while (current != null) {
                for (TypeList it = current.dataMembers; it != null; it = it.tail) {
                    if (it.head != null && it.head.name != null) {
                        superMembers.put(it.head.name, it.head);
                    }
                }
                current = current.father;
            }
        }
        
        // Process each field/method and collect types        
        for (AstDec field : fields) {
            String memberName = null;
            Type memberType = null;
            
            if (field instanceof AstDecVar) {
                AstDecVar varDec = (AstDecVar) field;
                memberName = varDec.id;
                
                // Check shadowing: field cannot shadow field or method in superclass
                if (superMembers.containsKey(memberName)) {
                    throw new SemanticErrorException(line);
                }
                
                // Check shadowing: field cannot shadow field or method already defined in this class
                if (definedMembers.containsKey(memberName)) {
                    throw new SemanticErrorException(line);
                }
                
                // Semant the variable declaration
                field.semantMe();
                
                // Get the declared type (must be a type definition, not a variable)
                Type baseType = SymbolTable.getInstance().findTypeDefinition(varDec.typeName);
                if (baseType == null) {
                    throw new SemanticErrorException(line);
                }
                
                // Create a TypeClassField wrapper for the field
                // This allows lookup by field name while preserving the actual type
                memberType = new TypeClassField(memberName, baseType);
                
            } else if (field instanceof AstDecFunc) {
                AstDecFunc funcDec = (AstDecFunc) field;
                memberName = funcDec.name;
                
                // Check shadowing: method cannot shadow field in superclass or this class
                if (superMembers.containsKey(memberName)) {
                    Type existing = superMembers.get(memberName);
                    // If it's a field (not a function), shadowing is illegal
                    if (!(existing instanceof TypeFunction)) {
                        throw new SemanticErrorException(line);
                    }
                }
                
                // Check for method overloading in this class (same name, different signature)
                if (definedMembers.containsKey(memberName)) {
                    Type existing = definedMembers.get(memberName);
                    if (existing instanceof TypeFunction) {
                        // Method overloading is illegal
                        throw new SemanticErrorException(line);
                    } else {
                        // Method shadowing field is illegal
                        throw new SemanticErrorException(line);
                    }
                }
                
                // Semant the function declaration
                field.semantMe();
                
                // Get the function type from symbol table
                memberType = SymbolTable.getInstance().find(memberName);
                
                // Check method overriding: if method exists in superclass, signatures must match exactly
                if (superMembers.containsKey(memberName)) {
                    TypeFunction newFunc = (TypeFunction) memberType;
                    Type existing = superMembers.get(memberName);
                    if (existing instanceof TypeFunction) {
                        TypeFunction superFunc = (TypeFunction) existing;
                        
                        // Check return type matches
                        if (newFunc.returnType == null || superFunc.returnType == null ||
                            !newFunc.returnType.name.equals(superFunc.returnType.name)) {
                            throw new SemanticErrorException(line);
                        }
                        
                        // Check parameter types match exactly
                        TypeList newParams = newFunc.params;
                        TypeList superParams = superFunc.params;
                        while (newParams != null && superParams != null) {
                            if (newParams.head == null || superParams.head == null ||
                                !newParams.head.name.equals(superParams.head.name)) {
                                throw new SemanticErrorException(line);
                            }
                            newParams = newParams.tail;
                            superParams = superParams.tail;
                        }
                        if (newParams != null || superParams != null) {
                            // Different number of parameters
                            throw new SemanticErrorException(line);
                        }
                    }
                }
            }
            
            // Add to defined members and collect type
            if (memberName != null && memberType != null) {
                definedMembers.put(memberName, memberType);
                memberTypesList.add(memberType);
                
                // Update t.dataMembers incrementally so later fields can reference earlier ones
                // Prepend to maintain reverse order (will be reversed at the end)
                t.dataMembers = new TypeList(memberType, t.dataMembers);
            }
        }
        
        // Build TypeList in correct order (reverse the list since we prepended)
        dataMembers = null;
        for (int i = memberTypesList.size() - 1; i >= 0; i--) {
            dataMembers = new TypeList(memberTypesList.get(i), dataMembers);
        }
        
        /************************************************/
        /* [5] Update TypeClass with final dataMembers */
        /************************************************/
        t.dataMembers = dataMembers;

        /*****************/
        /* [6] Clear current class */
        /*****************/
        SymbolTable.getInstance().clearCurrentClass();

        /*****************/
        /* [7] End Scope */
        /*****************/
        SymbolTable.getInstance().endScope();

        /************************************************/
        /* [8] Enter the Class Type to the Symbol Table */
        /************************************************/
        SymbolTable.getInstance().enter(name, t);

        /*********************************************************/
        /* [9] Return value is irrelevant for class declarations */
        /*********************************************************/
        return null;		
    }
}