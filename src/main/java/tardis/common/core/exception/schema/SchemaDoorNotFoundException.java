package tardis.common.core.exception.schema;

public class SchemaDoorNotFoundException extends SchemaException
{
	private static final long serialVersionUID = -7826456634154394145L;
	private int[] b;
	private int xIn;
	private int yIn;
	private int zIn;
	
	public SchemaDoorNotFoundException(int[] bounds, int x, int y, int z)
	{
		b = bounds;
		xIn = x;
		yIn = y;
		zIn = z;
	}
	
	@Override
	public String getMessage()
	{
		if(b!= null && b.length == 5)
			return "No primary door found @["+xIn+","+yIn+","+zIn+"] within ["+b[0]+","+b[1]+","+b[2]+","+b[3]+","+b[4]+"]";
		return "No primary door found: invalid boundaries";
	}

}
