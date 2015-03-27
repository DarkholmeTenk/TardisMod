package tardis.common.core.exception.schema;

import tardis.common.core.schema.PartBlueprint;

public class UnmatchingSchemaException extends SchemaException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 309171836392147983L;
	
	private boolean dF = false;
	private boolean dB = false;
	private boolean dD = false;
	private PartBlueprint t;
	private PartBlueprint f;
	
	public UnmatchingSchemaException(PartBlueprint to,PartBlueprint from,boolean diffFacing, boolean diffBounds, boolean diffDoor)
	{
		dF = diffFacing;
		dB = diffBounds;
		dD = diffDoor;
		t = to;
		f = from;
	}
	
	@Override
	public String getMessage()
	{
		return "USE:" + dF +":" + dB + ":" + dD + " | " + t.myName +"<-" + f.myName;
	}
}
