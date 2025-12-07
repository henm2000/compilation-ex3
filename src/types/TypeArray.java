package types;

public class TypeArray extends Type
{
	/*************************************/
	/* The element type of the array     */
	/*************************************/
	public Type elementType;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TypeArray(Type elementType, String name)
	{
		this.elementType = elementType;
		this.name = name;
	}
	
	/*************/
	/* isArray() */
	/*************/
	@Override
	public boolean isArray() { return true; }
}

