package tardis.common.core;

public class HitPosition
{
	public int side;
	public float posZ;
	public float posY;
	
	public HitPosition(float height, float pos, int s)
	{
		posY = height;
		posZ = pos;
		side = s;
	}
	
	public boolean within(int sideIn, double zMin, double yMin,double zMax,double yMax)
	{
		if(side != sideIn)
			return false;
		if(posZ < zMin || posZ > zMax)
			return false;
		if(posY < yMin || posY > yMax)
			return false;
		return true;
	}
	
	public String toString()
	{
		return "[Hit s:" + side + " ["+posZ+","+posY+"]]";
	}
}
