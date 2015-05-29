package tardis.common.tileents.extensions;

public enum ManualPage
{
	MAIN("Menu","Main"),
	FLIGHT("Flight","Flight"),
	COORDS("Coordinates","Coords"),
	TAKEOFF("Takeoff","Takeoff"),
	UNCOORDINATED("Drifting","Uncoord"),
	CRAFTING("Crafting","Craft"),
	LAB("Lab","Lab"),
	CONSOLE("Console","Console"),
	CONSOLEF("Front","ConsoleF"),
	CONSOLER("Right","ConsoleR"),
	CONSOLEB("Back","ConsoleB"),
	CONSOLEL("Left","ConsoleL"),
	BATTERY("Battery","Battery"),
	LRCHRONO("Chronosteel","LRChrono"),
	LRDALEK("Dalekanium","LRDalek"),
	LRKONTRON("Kontron","LRKontron"),
	LRTEMPDIRT("Temporal Dirt","LRDirt"),
	TOOLS("Tools","Tools"),
	ROUNDELS("Roundels","Roundels"),
	LANDPAD("Landing Pad","LandPad"),
	GRAVLIFT("Gravity Lift","GravLift"),
	ENGINE("Engine","Engine"),
	SONIC("Screwdriver","Sonic"),
	CREDITS("Credits","Credits");

	public final String title;
	public final String tex;

	private ManualPage(String _title, String _tex)
	{
		title = _title;
		tex = _tex;
	}

	public static ManualPage get(int i)
	{
		if((i >= 0) && (i < values().length))
			return values()[i];
		return MAIN;
	}

	public static ManualPage get(String s)
	{
		s = s.trim();
		if(s.startsWith("+") || s.startsWith("-"))
			s = s.substring(1);
		for(ManualPage p : values())
			if(p.title.equals(s))
				return p;
		return MAIN;
	}
}
