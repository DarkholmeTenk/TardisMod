package tardis.api;

public enum TardisScrewdriverMode
{
	Reconfigure		(0.2,0.4,1,		null),
	Locate			(0.2,1,0.5,		TardisFunction.LOCATE),
	Schematic		(1,1,0.3,		null),
	Dismantle		(0.7,0.1,0.1,	null);
	
	public TardisFunction requiredFunction;
	public final double[] c;
	
	TardisScrewdriverMode(double r, double g, double b, TardisFunction req)
	{
		requiredFunction = req;
		c = new double[3];
		c[0] = r;
		c[1] = g;
		c[2] = b;
	}
}
