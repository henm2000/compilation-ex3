package types;

import java.util.HashMap;

public class TypeClass extends Type
{
	/*********************************************************************/
	/* If this class does not extend a father class this should be null  */
	/*********************************************************************/
	public TypeClass father;
	public static HashMap<String, TypeClass> classes = new HashMap<>();

	/**************************************************/
	/* Gather up all data members in one place        */
	/* Note that data members coming from the AST are */
	/* packed together with the class methods         */
	/**************************************************/
	public TypeList dataMembers;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TypeClass(TypeClass father, String name, TypeList dataMembers)
	{
		this.name = name;
		this.father = father;
		this.dataMembers = dataMembers; 
		this.classes.put(name, this);
	}
	
	/*************/
	/* isClass() */
	/*************/
	@Override
	public boolean isClass() { return true; }
}
