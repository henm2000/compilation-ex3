package ast;

import types.*;
import symboltable.SymbolTable;
import exceptions.SemanticErrorException;
public class AstVarSimple extends AstVar
{
	/************************/
	/* simple variable name */
	/************************/
	public String name;
	public int line;
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstVarSimple(String name, int line)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();
	
		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== var -> ID( %s )\n",name);

		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
		this.name = name;
		this.line = line;
	}

	/**************************************************/
	/* The printing message for a simple var AST node */
	/**************************************************/
	public void printMe()
	{
		/**********************************/
		/* AST NODE TYPE = AST SIMPLE VAR */
		/**********************************/
		System.out.format("AST NODE SIMPLE VAR( %s )\n",name);

		/*********************************/
		/* Print to AST GRAPHVIZ DOT file */
		/*********************************/
		AstGraphviz.getInstance().logNode(
				serialNumber,
			String.format("SIMPLE\nVAR\n(%s)",name));
	}
	public Type semantMe()
	{
		/************************************************/
		/* Use class scope resolution if in a class,    */
		/* otherwise use regular scope resolution        */
		/************************************************/
		Type result = SymbolTable.getInstance().findInClassScope(name);
		if (result == null) {
			throw new SemanticErrorException(line);
		}
		return result;
	}
}
