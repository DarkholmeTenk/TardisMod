package tardis.common.network.packet;

import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.interfaces.IDataPacketHandler;
import net.minecraft.nbt.NBTTagCompound;
import tardis.TardisMod;
import tardis.common.core.TardisDimensionRegistry;

public class DimRegPacketHandler implements IDataPacketHandler
{
	@Override
	public void handleData(NBTTagCompound nbt)
	{
		if((nbt != null) && ServerHelper.isClient())
		{
			System.out.println("Recieved new dimensions list from server");
			if(TardisMod.dimReg == null)
				TardisDimensionRegistry.loadAll();
			TardisMod.dimReg.readFromNBT(nbt);
			TardisMod.dimReg.registerDims();
		}
	}

}
