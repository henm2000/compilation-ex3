package types;

public class TypeVoid extends Type
{
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static TypeVoid instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected TypeVoid() {
		name = "void";
	}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static Type getInstance()
	{
		if (instance == null)
		{
			instance = new TypeVoid();
		}
		return instance;
	}
}
