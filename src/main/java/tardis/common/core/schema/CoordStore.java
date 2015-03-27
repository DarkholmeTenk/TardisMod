package tardis.common.core.schema;

import tardis.common.core.TardisOutput;

public class CoordStore
{
	public final int x;
	public final int y;
	public final int z;
	
	public CoordStore(int xIn, int yIn, int zIn)
	{
		x = xIn;
		y = yIn;
		z = zIn;
	}
	
	public CoordStore rotateRight()
	{
		return new CoordStore(z,y,-x);
	}
	
	public CoordStore rotateLeft()
	{
		return new CoordStore(-z,y,x);
	}
	
	public CoordStore rotate()
	{
		return new CoordStore(-x,y,-z);
	}
	
	public String toString()
	{
		return x + "," + y + "," + z;
	}
	
	public static CoordStore fromString(String from)
	{
		try
		{
			String[] splitString = from.split(",");
			int x = Integer.parseInt(splitString[0]);
			int y = Integer.parseInt(splitString[1]);
			int z = Integer.parseInt(splitString[2]);
			return new CoordStore(x,y,z);
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
		if (!(obj instanceof CoordStore))
			return false;
		CoordStore other = (CoordStore) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

}
