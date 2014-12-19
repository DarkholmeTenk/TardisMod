package tardis.common.core.store;

import net.minecraft.nbt.NBTTagCompound;

public class ControlStateStore
{
	public final int facing;
	public final int dimControl;
	public final int[] xControls;
	public final int[] yControls;
	public final int[] zControls;
	public final boolean landGroundControl;
	public final boolean relative;
	
	public static int[] fixControls(int[] toFix)
	{
		if(toFix == null || (toFix.length != 6 && toFix.length != 7))
			return new int[] {0,0,0,0,0,0,0};
		if(toFix.length == 7)
			return toFix;
		int[] fixed = new int[7];
		for(int i = 0;i<6;i++)
			fixed[i] = toFix[i];
		fixed[6] = 0;
		return fixed;
	}
	
	public ControlStateStore(int facing, int dimControl, int[] xControls, int[] yControls, int[] zControls, boolean landGroundControl,boolean rel)
	{
		this.facing = facing;
		this.dimControl= dimControl;
		this.xControls = fixControls(xControls.clone());
		this.yControls = yControls.clone();
		this.zControls = fixControls(zControls.clone());
		this.landGroundControl = landGroundControl;
		this.relative = rel;
	}
	
	public boolean isValid()
	{
		if(xControls!= null && xControls.length == 7)
			if(yControls!= null && yControls.length == 4)
				if(zControls!= null && zControls.length == 7)
					return true;
		return false;
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setInteger("facing", facing);
		tag.setInteger("dimControl",dimControl);
		tag.setIntArray("xControls", xControls);
		tag.setIntArray("yControls", yControls);
		tag.setIntArray("zControls", zControls);
		tag.setBoolean("relativeCoords", relative);
		tag.setBoolean("landGroundControl", landGroundControl);
	}
	
	public static ControlStateStore readFromNBT(NBTTagCompound tag)
	{
		int facing = tag.getInteger("facing");
		int dim = tag.getInteger("dimControl");
		int[] x = tag.getIntArray("xControls");
		int[] y = tag.getIntArray("yControls");
		int[] z = tag.getIntArray("zControls");
		boolean rel = tag.getBoolean("relativeCoords");
		boolean lgc = tag.getBoolean("landGroundControl");
		return new ControlStateStore(facing,dim,x,y,z,lgc,rel);
	}
}
