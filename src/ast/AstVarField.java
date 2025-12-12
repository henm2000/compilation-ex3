package ast;

import types.*;
import exceptions.SemanticErrorException;
public class AstVarField extends AstVar
{
	public AstVar var; 
	public String fieldName;
	public int line;
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstVarField(AstVar var, String fieldName, int line)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== var -> var DOT ID( %s )\n",fieldName);

		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
		this.var = var;
		this.fieldName = fieldName;
		this.line = line;
	}

	/*************************************************/
	/* The printing message for a field var AST node */
	/*************************************************/
	public void printMe()
	{
		/*********************************/
		/* AST NODE TYPE = AST FIELD VAR */
		/*********************************/
		System.out.print("AST NODE FIELD VAR\n");

		/**********************************************/
		/* RECURSIVELY PRINT VAR, then FIELD NAME ... */
		/**********************************************/
		if (var != null) var.printMe();
		System.out.format("FIELD NAME( %s )\n",fieldName);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
				serialNumber,
			String.format("FIELD\nVAR\n...->%s",fieldName));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (var != null) AstGraphviz.getInstance().logEdge(serialNumber,var.serialNumber);
	}
	public Type semantMe()
	{
		Type t = null;
		TypeClass tc = null;
		
		/******************************/
		/* [1] Recursively semant var */
		/******************************/
		if (var != null) t = var.semantMe();
		
		/*********************************/
		/* [2] Make sure type is a class */
		/*********************************/
		if (isNilType(t)) {
			throw new SemanticErrorException(line);
		}
		
		if (t.isClass() == false)
		{
			throw new SemanticErrorException(line);
		}
		else
		{
			tc = (TypeClass) t;
		}
		
		/************************************/
		/* [3] Look for fieldName in class hierarchy */
		/************************************/
		TypeClass current = tc;
		
		while (current != null) {
			for (TypeList it = current.dataMembers; it != null; it = it.tail)
			{
				if (it.head != null && it.head.name != null && it.head.name.equals(fieldName))
				{
					// If it's a TypeClassField, return the actual field type
					if (it.head instanceof TypeClassField) {
					return ((TypeClassField) it.head).getFieldType();
				}
					// If it's a method, that's an error (can't access method as field)
					if (it.head instanceof TypeFunction) {
						throw new SemanticErrorException(line);
					}
					return it.head;
				}
			}
			
			current = current.father;
		}
		
		/*********************************************/
		/* [4] fieldName does not exist in class hierarchy */
		/*********************************************/
		throw new SemanticErrorException(line);
	}

}
