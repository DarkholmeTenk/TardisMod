package tardis.common.core.exception.schema;

public class SchemaCoreNotFoundException extends SchemaException
{
	private static final long serialVersionUID = -3620121195082168059L;

	int x;
	int y;
	int z;
	public SchemaCoreNotFoundException(int xIn, int yIn, int zIn)
	{
		x = xIn;
		y = yIn;
		z = zIn;
	}
	
	@Override
	public String getMessage()
	{
		return "Core not found @["+x+","+y+","+z+"]";
	}
}
