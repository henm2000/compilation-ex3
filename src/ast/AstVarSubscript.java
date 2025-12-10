package ast;

import types.*;
import SemanticErrorException;

public class AstVarSubscript extends AstVar
{
	public AstVar var;
	public AstExp subscript;
	public int line;
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstVarSubscript(AstVar var, AstExp subscript, int line)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== var -> var [ exp ]\n");

		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
		this.var = var;
		this.subscript = subscript;
		this.line = line;
	}

	/*****************************************************/
	/* The printing message for a subscript var AST node */
	/*****************************************************/
	public void printMe()
	{
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE SUBSCRIPT VAR\n");

		/****************************************/
		/* RECURSIVELY PRINT VAR + SUBSCRIPT ... */
		/****************************************/
		if (var != null) var.printMe();
		if (subscript != null) subscript.printMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
				serialNumber,
			"SUBSCRIPT\nVAR\n...[...]");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (var       != null) AstGraphviz.getInstance().logEdge(serialNumber,var.serialNumber);
		if (subscript != null) AstGraphviz.getInstance().logEdge(serialNumber,subscript.serialNumber);
	}
	
	public Type semantMe()
	{
		Type varType = null;
		Type subscriptType = null;
		
		/******************************/
		/* [1] Recursively semant var */
		/******************************/
		if (var != null) {
			varType = var.semantMe();
		} else {
			throw new SemanticErrorException(line);
		}
		
		/*********************************/
		/* [2] Make sure var type is an array or nil */
		/*     Nil is allowed for array types, but we can't access elements on nil */
		/*********************************/
		if (isNilType(varType)) {
			// Nil is allowed for array types, but we can't access elements on nil
			// This is a semantic error - attempting to access an array element on nil
			throw new SemanticErrorException(line);
		}
		
		if (!varType.isArray()) {
			throw new SemanticErrorException(line);
		}
		
		TypeArray arrayType = (TypeArray) varType;
		
		/******************************/
		/* [3] Semant subscript expression */
		/******************************/
		if (subscript != null) {
			subscriptType = subscript.semantMe();
		} else {
			throw new SemanticErrorException(line);
		}
		
		/*********************************/
		/* [4] Subscript must be of type int */
		/*********************************/
		if (subscriptType != TypeInt.getInstance()) {
			throw new SemanticErrorException(line);
		}
		
		/*********************************/
		/* [5] If subscript is constant, must be >= 0 */
		/*********************************/
		if (subscript instanceof AstExpInt) {
			AstExpInt intExp = (AstExpInt) subscript;
			if (intExp.value < 0) {
				throw new SemanticErrorException(line);
			}
		}
		
		/*********************************/
		/* [6] Return the array's element type */
		/*********************************/
		return arrayType.elementType;
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
