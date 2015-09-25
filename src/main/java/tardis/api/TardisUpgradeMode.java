package tardis.api;

public enum TardisUpgradeMode
{
	ENERGY	("Energy"),
	REGEN	("Energy regeneration"),
	SHIELDS	("Shields"),
	ROOMS	("Max rooms"),
	SPEED	("Speed");

	public final String name;
	TardisUpgradeMode(String _name)
	{
		name = _name;
	}

	public static TardisUpgradeMode getUpgradeMode(int i)
	{
		TardisUpgradeMode[] ums = values();
		if((i >= 0) && (i < ums.length))
			return ums[i];
		return null;
	}
}
