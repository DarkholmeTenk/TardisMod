package tardis.common.integration.waila;

import net.minecraft.nbt.NBTTagCompound;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class WailaArtronProvider extends AbstractWailaProvider {

	@Override
	public String[] extraInfo(IWailaDataAccessor accessor, int control) {
		NBTTagCompound nbt = accessor.getNBTData();
		String[] data = new String[2];
		if(nbt.hasKey("ae"))
			data[0] = "Charge: " + nbt.getInteger("ae");
		else
		data[0] = "Unknown charge";
		int m = nbt.getInteger("m");
		switch(m)
		{
			case 1: data[1] = "Mode: Uncoordinated Flight"; break;
			case 2: data[1] = "Mode: Coordinated Flight"; break;
			default: data[1] = "Mode: Landed"; break;
		}
		return data;
	}

	@Override
	public int getControlHit(IWailaDataAccessor accessor) {
		// TODO Auto-generated method stub
		return 0;
	}

}
