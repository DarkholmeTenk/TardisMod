package tardis.common.network.packet;

import net.minecraft.nbt.NBTTagCompound;

import io.darkcraft.darkcore.mod.interfaces.IDataPacketHandler;

import tardis.common.core.helpers.ScrewdriverHelperFactory;

public class ScrewdriverHelperPacketHandler implements IDataPacketHandler
{

	@Override
	public void handleData(NBTTagCompound data)
	{
		ScrewdriverHelperFactory.get(data);
	}

}
