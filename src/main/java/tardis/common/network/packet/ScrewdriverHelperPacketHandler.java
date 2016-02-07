package tardis.common.network.packet;

import io.darkcraft.darkcore.mod.interfaces.IDataPacketHandler;
import net.minecraft.nbt.NBTTagCompound;
import tardis.common.core.helpers.ScrewdriverHelperFactory;

public class ScrewdriverHelperPacketHandler implements IDataPacketHandler
{

	@Override
	public void handleData(NBTTagCompound data)
	{
		ScrewdriverHelperFactory.get(data);
	}

}
