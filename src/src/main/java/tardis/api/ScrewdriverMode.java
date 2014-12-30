package tardis.api;

public enum ScrewdriverMode
{
	Reconfigure		(0.2,0.4,1,		null),
	Locate			(0.2,1,0.5,		TardisFunction.LOCATE),
	Schematic		(1,1,0.3,		null),
	Dismantle		(0.7,0.1,0.1,	null),
	Transmat		(0.8,0.3,1.0,	TardisFunction.TRANSMAT),
	Recall			(1.0,0.6,0.1,	TardisFunction.RECALL);
	
	public TardisFunction requiredFunction;
	public final double[] c;
	
	ScrewdriverMode(double r, double g, double b, TardisFunction req)
	{
		requiredFunction = req;
		c = new double[3];
		c[0] = r;
		c[1] = g;
		c[2] = b;
	}
}
