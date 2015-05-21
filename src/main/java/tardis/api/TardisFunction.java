package tardis.api;

public enum TardisFunction
{
	TRANSMAT ("Transmat"),
	LOCATE ("Locate"),
	SENSORS ("Exterior sensors"),
	STABILISE ("Blue stabilizers"),
	RECALL ("Remote recall"),
	TRANQUILITY ("Tranquility zone"),
	CLARITY ("Perfect destination guesser");

	public final String name;

	TardisFunction(String fName)
	{
		name = fName;
	}

}
