package tardis.common.network.packet;

import net.minecraft.nbt.NBTTagCompound;
import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.core.TardisDimensionRegistry;
import io.netty.buffer.ByteBuf;

public class DimRegPacket extends AbstractPacket
{

	public DimRegPacket(ByteBuf payload, NBTTagCompound nbt)
	{
		super(payload, nbt, (byte) PacketType.DIMREG.ordinal());
	}
	
	public DimRegPacket(ByteBuf payload)
	{
		super(payload);
	}

	public void registerDims()
	{
		NBTTagCompound nbt = getNBT();
		if(nbt != null && !Helper.isServer())
		{
			if(TardisMod.dimReg == null)
				TardisMod.dimReg = TardisDimensionRegistry.load();
			TardisMod.dimReg.readFromNBT(nbt);
			TardisMod.dimReg.registerDims();
		}
	}

}
