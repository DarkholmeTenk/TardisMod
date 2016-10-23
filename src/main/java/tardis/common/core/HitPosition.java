package tardis.common.core;

public class HitPosition
{
	public int side;
	public float posZ;
	public float posY;
	public float depth;

	public HitPosition(float height, float pos, int s)
	{
		posY = height;
		posZ = pos;
		side = s;
	}

	public HitPosition(int side, float x, float y, float z)
	{
		this.side = side;
		if((side == 0) || (side == 1))
		{
			depth = side == 0 ? y : 1 - y;
			posZ = z;
			posY = x;
		}
		else
		{
			posY = y;
			switch(side)
			{
				case 2: posZ = 1 - x; depth = z; break;
				case 3: posZ = x; depth = 1 - z; break;
				case 4: posZ = z; depth = x; break;
				case 5: posZ = 1 - z; depth = 1 - x; break;
			}
		}
	}

	public boolean within(int sideIn, double zMin, double yMin,double zMax,double yMax)
	{
		if(side != sideIn)
			return false;
		if((posZ < zMin) || (posZ > zMax))
			return false;
		if((posY < yMin) || (posY > yMax))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "[Hit s:" + side + " ["+posZ+","+posY+"]]";
	}
}
