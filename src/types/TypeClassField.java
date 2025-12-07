package types;

/**
 * Represents a class field in the type system.
 * This allows storing fields in TypeClass.dataMembers with the field name
 * for lookup, while preserving the actual field type.
 */
public class TypeClassField extends Type
{
	public Type fieldType; // The actual type of the field
	
	public TypeClassField(String fieldName, Type fieldType)
	{
		this.name = fieldName;
		this.fieldType = fieldType;
	}
	
	/**
	 * Returns the actual type of the field (not the field itself)
	 */
	public Type getFieldType()
	{
		return fieldType;
	}
}

