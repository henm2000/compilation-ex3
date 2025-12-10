package ast;

import types.*;
import SemanticErrorException;

public class AstStmtAssign extends AstStmt
{
	/***************/
	/*  var := exp */
	/***************/
	public AstVar var;
	public AstExp exp;
	public int line;
	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtAssign(AstVar var, AstExp exp, int line)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== stmt -> var ASSIGN exp SEMICOLON\n");

		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
		this.var = var;
		this.exp = exp;
		this.line = line;
	}

	/*********************************************************/
	/* The printing message for an assign statement AST node */
	/*********************************************************/
	public void printMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST ASSIGNMENT STATEMENT */
		/********************************************/
		System.out.print("AST NODE ASSIGN STMT\n");

		/***********************************/
		/* RECURSIVELY PRINT VAR + EXP ... */
		/***********************************/
		if (var != null) var.printMe();
		if (exp != null) exp.printMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
				serialNumber,
			"ASSIGN\nleft := right\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AstGraphviz.getInstance().logEdge(serialNumber,var.serialNumber);
		AstGraphviz.getInstance().logEdge(serialNumber,exp.serialNumber);
	}
	public Type semantMe()
	{
		Type varType = null;
		Type expType = null;
		
		/************************************************/
		/* [1] Get types of variable and expression     */
		/************************************************/
		if (var != null) varType = var.semantMe();
		if (exp != null) expType = exp.semantMe();
		
		/************************************************/
		/* [2] Check assignment compatibility           */
		/************************************************/
		if (!isAssignmentCompatible(varType, expType, exp)) {
			throw new SemanticErrorException(line);
		}
		
		/************************************************/
		/* [3] Return value is irrelevant for statements */
		/************************************************/
		return null;
	}
	
	/**
	 * Check if expression type is compatible with variable type for assignment
	 */
	private boolean isAssignmentCompatible(Type varType, Type expType, AstExp exp)
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
					// Rule: if e = new T, then x must be of type array defined over type T
					// Note: T can be a primitive, class, or even an array type (for nested arrays)
					// Example: array IntArray = int[]; array IntMatrix = IntArray[];
					//          IntMatrix m := new IntArray[5];  // T = IntArray (array type)
					// Check if varArray.elementType == expType (where expType is T)
					// This handles both simple arrays (T = int) and nested arrays (T = IntArray)
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
	private boolean isNilType(Type t)
	{
		if (t == null) return false;
		return t.name != null && t.name.equals("nil");
	}
}
