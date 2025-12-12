package ast;
import types.*;

public abstract class AstNode
{
	/*******************************************/
	/* The serial number is for debug purposes */
	/* In particular, it can help in creating  */
	/* a graphviz dot format of the AST ...    */
	/*******************************************/
	public int serialNumber;
	/***********************************************/
	/* The default message for an unknown AST node */
	/***********************************************/
	public void printMe()
	{
		System.out.print("AST NODE UNKNOWN\n");
	}
	public Type semantMe(){
		return null;
	}

	/**
	 * Check if expression type is compatible with variable type for assignment
	 */
	protected boolean isAssignmentCompatible(Type varType, Type expType, AstExp exp)
	{
		/************************************************/
		/* Handle nil: can be assigned to arrays/classes */
		/* but not to primitives (int/string)            */
		/************************************************/
		if (isNilType(expType)) {
			return varType.isArray() || varType.isClass();
		}
		
		/************************************************/
		/* Primitives: exact type match required         */
		/************************************************/
		if (varType == TypeInt.getInstance() || varType == TypeString.getInstance()) {
			return varType == expType;
		}
		
		/************************************************/
		/* Arrays: special handling for new T           */
		/************************************************/
		if (varType.isArray()) {
			TypeArray varArray = (TypeArray) varType;
			
			// Check if expression is "new T[e]" (array allocation)
			if (exp instanceof AstExpNew) {
				AstExpNew newExp = (AstExpNew) exp;
				
				if (newExp.sizeExpr == null) {
					// Expression is "new T" - this is for class allocation, not array
					return false;
				} else {
					// Expression is "new T[e]" - expType is the element type T
					return varArray.elementType == expType;
				}
			}
			
			// For non-new expressions, arrays must have exactly the same type (same name)
			if (expType.isArray()) {
				TypeArray expArray = (TypeArray) expType;
				return varArray.name.equals(expArray.name);
			}
			
			return false;
		}
		
		/************************************************/
		/* Classes: same type OR expType is subclass    */
		/* of varType (polymorphism)                     */
		/************************************************/
		if (varType.isClass()) {
			if (!expType.isClass()) {
				return false;
			}
			
			TypeClass varClass = (TypeClass) varType;
			TypeClass expClass = (TypeClass) expType;
			
			// Same class
			if (varClass.name.equals(expClass.name)) {
				return true;
			}
			
			// Check if expClass is a subclass of varClass
			TypeClass current = expClass.father;
			while (current != null) {
				if (current.name.equals(varClass.name)) {
					return true;
				}
				current = current.father;
			}
			
			return false;
		}
		
		/************************************************/
		/* If we get here, types are incompatible       */
		/************************************************/
		return false;
	}
	
	/**
	 * Check if a type is nil
	 */
	protected boolean isNilType(Type t)
	{
		if (t == null) return false;
		return t.name != null && t.name.equals("nil");
	}
}
