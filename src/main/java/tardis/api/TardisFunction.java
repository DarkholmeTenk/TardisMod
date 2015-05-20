package tardis.api;

public enum TardisFunction
{
	TRANSMAT ("Transmat"),
	LOCATE ("Locate"),
	SENSORS ("Exterior sensors"),
	STABILISE ("Blue stabilizers"),
	RECALL ("Remote recall"),
	TRANQUILITY ("Tranquility zone");

	public final String name;

	TardisFunction(String fName)
	{
		name = fName;
	}

}
