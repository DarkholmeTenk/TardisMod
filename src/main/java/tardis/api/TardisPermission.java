package tardis.api;

public enum TardisPermission
{
	PERMISSIONS("Modify permissions"),
	ROOMS("Modify rooms"),
	ROUNDEL("Modify roundels"),
	TRANSMAT("Transmat"),
	RECALL("Recall"),
	RECOLOUR("Recolour"),
	FLY("Fly"),
	POINTS("Spend points");

	public final String name;
	public final int mask;

	private TardisPermission(String _name)
	{
		name = _name;
		mask = 1 << ordinal();
	}

	public boolean isIn(int data)
	{
		return (data & mask) == mask;
	}

	public int toggle(int data)
	{
		return data ^ mask;
	}
}
