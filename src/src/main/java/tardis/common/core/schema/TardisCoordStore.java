package tardis.common.core.schema;

import tardis.common.core.TardisOutput;

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

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TardisCoordStore))
			return false;
		TardisCoordStore other = (TardisCoordStore) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

}
