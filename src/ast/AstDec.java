package ast;
import types.*;  

public abstract class AstDec extends AstNode
{
    public AstDec() { /* common base for declarations */ }
    public Type semantMe()
	{
		return null;
	}
}