package tardis.common.core;

import io.darkcraft.darkcore.mod.nbt.NBTConstructor;
import io.darkcraft.darkcore.mod.nbt.NBTProperty;
import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;

@NBTSerialisable
public class HitPosition
{
	@NBTProperty
	public final int side;
	@NBTProperty
	public final float posZ;
	@NBTProperty
	public final float posY;
	public float depth;

	@NBTConstructor({"posY", "posZ", "side"})
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
				default: posZ = 0;
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

	public static class HitRegion
	{
		public final double zMin;
		public final double zMax;
		public final double yMin;
		public final double yMax;

		public HitRegion(double zMin, double yMin, double zMax, double yMax)
		{
			this.zMin = zMin;
			this.zMax = zMax;
			this.yMin = yMin;
			this.yMax = yMax;
		}

		public boolean contains(int side, HitPosition pos)
		{
			if(pos == null)
				return false;
			return pos.within(side, zMin, yMin, zMax, yMax);
		}
	}
}
