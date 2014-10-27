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
	
	public ControlStateStore(int facing, int dimControl, int[] xControls, int[] yControls, int[] zControls, boolean landGroundControl,boolean rel)
	{
		this.facing = facing;
		this.dimControl= dimControl;
		this.xControls = xControls.clone();
		this.yControls = yControls.clone();
		this.zControls = zControls.clone();
		this.landGroundControl = landGroundControl;
		this.relative = rel;
	}
	
	public boolean isValid()
	{
		if(xControls!= null && xControls.length == 6)
			if(yControls!= null && yControls.length == 4)
				if(zControls!= null && zControls.length == 6)
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
