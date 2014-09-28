package tardis.core.schema;

import tardis.core.TardisOutput;

public class TardisCoordStore
{
	public final int x;
	public final int y;
	public final int z;
	
	public TardisCoordStore(int xIn, int yIn, int zIn)
	{
		x = xIn;
		y = yIn;
		z = zIn;
	}
	
	public TardisCoordStore rotateRight()
	{
		return new TardisCoordStore(z,y,-x);
	}
	
	public TardisCoordStore rotateLeft()
	{
		return new TardisCoordStore(-z,y,x);
	}
	
	public TardisCoordStore rotate()
	{
		return new TardisCoordStore(-x,y,-z);
	}
	
	public boolean equals(TardisCoordStore other)
	{
		if(x == other.x && y == other.y && z == other.z)
			return true;
		return false;
	}
	
	public int hashCode()
	{
		String temp = "" + x + "," + y + "," + z;
		return temp.hashCode();
	}
	
	public String toString()
	{
		return x + "," + y + "," + z;
	}
	
	public static TardisCoordStore fromString(String from)
	{
		try
		{
			String[] splitString = from.split(",");
			int x = Integer.parseInt(splitString[0]);
			int y = Integer.parseInt(splitString[1]);
			int z = Integer.parseInt(splitString[2]);
			return new TardisCoordStore(x,y,z);
		}
		catch(Exception e)
		{
			TardisOutput.print("TCS", "Trying to load coord " + from + " failed: " + e.getMessage(),TardisOutput.Priority.WARNING);
		}
		return null;
	}

}
