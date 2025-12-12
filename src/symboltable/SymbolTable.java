/***********/
/* PACKAGE */
/***********/
package symboltable;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.PrintWriter;
import java.util.HashMap;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import types.*;
import exceptions.SemanticErrorException;
/****************/
/* SYMBOL TABLE */
/****************/
public class SymbolTable
{
	private int hashArraySize = 13;
	
	/**********************************************/
	/* The actual symbol table data structure ... */
	/**********************************************/
	private SymbolTableEntry[] table = new SymbolTableEntry[hashArraySize];
	private SymbolTableEntry top;
	private int topIndex = 0;
	
	/**************************************************************/
	/* A very primitive hash function for exposition purposes ... */
	/**************************************************************/
	private int hash(String s)
	{
		if (s.charAt(0) == 'l') {return 1;}
		if (s.charAt(0) == 'm') {return 1;}
		if (s.charAt(0) == 'r') {return 3;}
		if (s.charAt(0) == 'i') {return 6;}
		if (s.charAt(0) == 'd') {return 6;}
		if (s.charAt(0) == 'k') {return 6;}
		if (s.charAt(0) == 'f') {return 6;}
		if (s.charAt(0) == 'S') {return 6;}
		return 12;
	}

	/*******************************************************************/
	/* Helper method to determine classification from Type ... */
	/*******************************************************************/
	private String getClassification(Type t)
	{
		if (t instanceof TypeFunction) {
			return "FUNCTION";
		}
		if (t instanceof TypeClass) {
			return "CLASS";
		}
		if (t instanceof TypeArray) {
			return "ARRAY_TYPE";
		}
		if (t instanceof TypeForScopeBoundaries) {
			return "SCOPE_BOUNDARY";
		}
		if (t instanceof TypeInt || t instanceof TypeString || t instanceof TypeVoid) {
			return "PRIMITIVE_TYPE";
		}
		return "VARIABLE";
	}

	/****************************************************************************/
	/* Enter a variable, function, class type or array type to the symbol table */
	/****************************************************************************/
	public void enter(String name, Type t)
	{
		/*************************************************/
		/* [1] Compute the hash value for this new entry */
		/*************************************************/
		int hashValue = hash(name);

		/******************************************************************************/
		/* [2] Extract what will eventually be the next entry in the hashed position  */
		/*     NOTE: this entry can very well be null, but the behaviour is identical */
		/******************************************************************************/
		SymbolTableEntry next = table[hashValue];
	
		/**************************************************************************/
		/* [3] Prepare a new symbol table entry with name, type, next and prevtop */
		/**************************************************************************/
		SymbolTableEntry e = new SymbolTableEntry(name, t, getClassification(t), hashValue, next, top, topIndex++);

		/**********************************************/
		/* [4] Update the top of the symbol table ... */
		/**********************************************/
		top = e;
		
		/****************************************/
		/* [5] Enter the new entry to the table */
		/****************************************/
		table[hashValue] = e;
		
		/**************************/
		/* [6] Print Symbol Table */
		/**************************/
		printMe();
	}

	/***********************************************/
	/* Find the inner-most scope element with name */
	/***********************************************/
	public Type find(String name)
	{
		SymbolTableEntry e;
				
		for (e = table[hash(name)]; e != null; e = e.next)
		{
			if (name.equals(e.name))
			{
				return e.type;
			}
		}
		
		return null;
	}
	
	/***********************************************/
	/* Find a type definition (not a variable)     */
	/* This ensures we get a type, not a variable */
	/* or function with the same name              */
	/***********************************************/
	public Type findTypeDefinition(String name)
	{
		SymbolTableEntry e;
		
		// First check if it's a primitive type
		if (name.equals("int") || name.equals("string") || name.equals("void")) {
			Type t = find(name);
			if (t != null && (t == TypeInt.getInstance() || t == TypeString.getInstance() || t == TypeVoid.getInstance())) {
				return t;
			}
		}
		
		// Search for the entry
		for (e = table[hash(name)]; e != null; e = e.next)
		{
			if (name.equals(e.name))
			{
				Type t = e.type;
				
				// Reject functions - they're not type definitions
				if (t instanceof TypeFunction) {
					return null;
				}
				
				// For TypeClass, verify it's a type definition
				// (entry name matches type name, not a variable of that type)
				if (t instanceof TypeClass) {
					TypeClass tc = (TypeClass) t;
					if (name.equals(tc.name)) {
						return t; // It's a type definition
					}
					return null; // It's a variable of that class type
				}
				
				// For TypeArray, verify it's a type definition
				if (t instanceof TypeArray) {
					TypeArray ta = (TypeArray) t;
					if (name.equals(ta.name)) {
						return t; // It's a type definition
					}
					return null; // It's a variable of that array type
				}
				
				// For primitives, if name matches, it's a type definition
				if (t == TypeInt.getInstance() && name.equals("int")) {
					return t;
				}
				if (t == TypeString.getInstance() && name.equals("string")) {
					return t;
				}
				
				// Otherwise, it's likely a variable with a primitive type
				return null;
			}
		}
		
		return null;
	}
	
	/***********************************************/
	/* Check if we're currently in global scope   */
	/* Returns true if no SCOPE-BOUNDARY found      */
	/***********************************************/
	public boolean isInGlobalScope()
	{
		// Traverse backwards from top to check for SCOPE-BOUNDARY
		SymbolTableEntry e;
		for (e = top; e != null; e = e.prevtop)
		{
			if (e.name != null && e.name.equals("SCOPE-BOUNDARY"))
			{
				return false; // Found a scope boundary, we're in a nested scope
			}
		}
		return true; // No scope boundaries found, we're in global scope
	}

	/***************************************************************************/
	/* begine scope = Enter the <SCOPE-BOUNDARY> element to the data structure */
	/***************************************************************************/
	public void beginScope()
	{
		/************************************************************************/
		/* Though <SCOPE-BOUNDARY> entries are present inside the symbol table, */
		/* they are not really types. In order to be able to debug print them,  */
		/* a special TYPE_FOR_SCOPE_BOUNDARIES was developed for them. This     */
		/* class only contain their type name which is the bottom sign: _|_     */
		/************************************************************************/
		enter(
			"SCOPE-BOUNDARY",
			new TypeForScopeBoundaries("NONE"));

		/*********************************************/
		/* Print the symbol table after every change */
		/*********************************************/
		printMe();
	}

	/********************************************************************************/
	/* end scope = Keep popping elements out of the data structure,                 */
	/* from most recent element entered, until a <NEW-SCOPE> element is encountered */
	/********************************************************************************/
	public void endScope()
	{
		/**************************************************************************/
		/* Pop elements from the symbol table stack until a SCOPE-BOUNDARY is hit */		
		/**************************************************************************/
		while (top.name != "SCOPE-BOUNDARY")
		{
			table[top.index] = top.next;
			topIndex = topIndex -1;
			top = top.prevtop;
		}
		/**************************************/
		/* Pop the SCOPE-BOUNDARY sign itself */		
		/**************************************/
		table[top.index] = top.next;
		topIndex = topIndex -1;
		top = top.prevtop;

	/*********************************************/
	/* Print the symbol table after every change */		
	/*********************************************/
	printMe();
	}
	
	/***********************************************/
	/* Variable to track current function return type */
	/* (No stack needed since functions can't nest) */
	/***********************************************/
	private Type currentReturnType = null;
	
	/***********************************************/
	/* Set current function return type            */
	/***********************************************/
	public void setReturnType(Type returnType)
	{
		currentReturnType = returnType;
	}
	
	/***********************************************/
	/* Clear current function return type          */
	/***********************************************/
	public void clearReturnType()
	{
		currentReturnType = null;
	}
	
	/***********************************************/
	/* Get current function return type            */
	/***********************************************/
	public Type getCurrentReturnType()
	{
		return currentReturnType;
	}
	
	/***********************************************/
	/* Variable to track current class being processed */
	/* (No stack needed since classes can't nest)  */
	/***********************************************/
	private TypeClass currentClass = null;
	
	/***********************************************/
	/* Set current class                           */
	/***********************************************/
	public void setCurrentClass(TypeClass cls)
	{
		currentClass = cls;
	}
	
	/***********************************************/
	/* Clear current class                         */
	/***********************************************/
	public void clearCurrentClass()
	{
		currentClass = null;
	}
	
	/***********************************************/
	/* Get current class                           */
	/***********************************************/
	public TypeClass getCurrentClass()
	{
		return currentClass;
	}
	
	/***********************************************/
	/* Check if a name is a reserved keyword      */
	/***********************************************/
	public boolean isReservedKeyword(String name)
	{
		if (name == null) {
			return false;
		}
		
		// Language keywords
		if (name.equals("array") || name.equals("class") || 
		    name.equals("return") || name.equals("while") ||
		    name.equals("if") || name.equals("else") ||
		    name.equals("new") || name.equals("extends") ||
		    name.equals("nil")) {
			return true;
		}
		
		// Primitive type names (also reserved)
		if (name.equals("int") || name.equals("string") || name.equals("void")) {
			return true;
		}
		
		// Library function names (also reserved)
		if (name.equals("PrintInt") || name.equals("PrintString")) {
			return true;
		}
		
		return false;
	}
	
	/***********************************************/
	/* Find identifier in class scope              */
	/* Search order: class → superclass chain → global */
	/***********************************************/
	public Type findInClassScope(String name)
	{
		// If not in a class scope, use regular find
		if (currentClass == null) {
			return find(name);
		}
		
		// Search in current class and superclass chain
		TypeClass cls = currentClass;
		while (cls != null) {
			// Search in class dataMembers
			for (TypeList it = cls.dataMembers; it != null; it = it.tail) {
				if (it.head != null && it.head.name != null && it.head.name.equals(name)) {
					// Found in class - return the type
					// If it's a TypeClassField, return the actual field type
					if (it.head instanceof TypeClassField) {
						return ((TypeClassField) it.head).getFieldType();
					}
					// If it's a TypeFunction (method), return the function type
					if (it.head instanceof TypeFunction) {
						return it.head;
					}
					// Otherwise return the type as-is
					return it.head;
				}
			}
			// Move to superclass
			cls = cls.father;
		}
		
		// Not found in class hierarchy, search global scope
		return find(name);
	}
	
	public static int n=0;
	
	public Type findInCurrentScope(String name){
		SymbolTableEntry e;
		SymbolTableEntry scopeBoundary = null;

		for (e = top ; e!= null; e = e.prevtop){ // goes backward up to {
			if (e.name.equals("SCOPE-BOUNDARY")){
				scopeBoundary = e;
				break;
			}
		}
		
		for (e = top; e != null && e != scopeBoundary; e = e.prevtop){
			if (name.equals(e.name)){
				return e.type;
			}
		}
		return null;
	}

	public void printMe()
	{
		int i=0;
		int j=0;
		String dirname="./output/";
		String filename=String.format("SYMBOL_TABLE_%d_IN_GRAPHVIZ_DOT_FORMAT.txt",n++);

		try
		{
			/*******************************************/
			/* [1] Open Graphviz text file for writing */
			/*******************************************/
			PrintWriter fileWriter = new PrintWriter(dirname+filename);

			/*********************************/
			/* [2] Write Graphviz dot prolog */
			/*********************************/
			fileWriter.print("digraph structs {\n");
			fileWriter.print("rankdir = LR\n");
			fileWriter.print("node [shape=record];\n");

			/*******************************/
			/* [3] Write Hash Table Itself */
			/*******************************/
			fileWriter.print("hashTable [label=\"");
			for (i=0;i<hashArraySize-1;i++) { fileWriter.format("<f%d>\n%d\n|",i,i); }
			fileWriter.format("<f%d>\n%d\n\"];\n",hashArraySize-1,hashArraySize-1);
		
			/****************************************************************************/
			/* [4] Loop over hash table array and print all linked lists per array cell */
			/****************************************************************************/
			for (i=0;i<hashArraySize;i++)
			{
				if (table[i] != null)
				{
					/*****************************************************/
					/* [4a] Print hash table array[i] -> entry(i,0) edge */
					/*****************************************************/
					fileWriter.format("hashTable:f%d -> node_%d_0:f0;\n",i,i);
				}
				j=0;
				for (SymbolTableEntry it = table[i]; it!=null; it=it.next)
				{
					/*******************************/
					/* [4b] Print entry(i,it) node */
					/*******************************/
					fileWriter.format("node_%d_%d ",i,j);
					fileWriter.format("[label=\"<f0>%s|<f1>%s|<f2>prevtop=%d|<f3>next\"];\n",
						it.name,
						it.type.name,
						it.prevtopIndex);

					if (it.next != null)
					{
						/***************************************************/
						/* [4c] Print entry(i,it) -> entry(i,it.next) edge */
						/***************************************************/
						fileWriter.format(
							"node_%d_%d -> node_%d_%d [style=invis,weight=10];\n",
							i,j,i,j+1);
						fileWriter.format(
							"node_%d_%d:f3 -> node_%d_%d:f0;\n",
							i,j,i,j+1);
					}
					j++;
				}
			}
			fileWriter.print("}\n");
			fileWriter.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static SymbolTable instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected SymbolTable() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static SymbolTable getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new SymbolTable();

			/*****************************************/
			/* [1] Enter primitive types int, string */
			/*****************************************/
			instance.enter("int",   TypeInt.getInstance());
			instance.enter("string", TypeString.getInstance());
			instance.enter("void", TypeVoid.getInstance());

			/*************************************/
			/* [2] How should we handle void ??? */
			/* ANSWER: We handle it above ^^^ */
			/*************************************/
			/***************************************/
			/* [3] Enter library function PrintInt */
			/***************************************/
			instance.enter(
				"PrintInt",
				new TypeFunction(
					TypeVoid.getInstance(),
					"PrintInt",
					new TypeList(
						TypeInt.getInstance(),
						null)));
			
			/***************************************/
			/* [4] Enter library function PrintString */
			/***************************************/
			instance.enter(
				"PrintString",
				new TypeFunction(
					TypeVoid.getInstance(),
					"PrintString",
					new TypeList(
						TypeString.getInstance(),
						null)));
			
		}
		return instance;
	}
}
