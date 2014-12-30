package tardis.common.network.packet;

import tardis.api.IControlMatrix;
import tardis.common.core.Helper;
import tardis.common.tileents.TardisAbstractTileEntity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TardisControlPacket extends TardisAbstractPacket
{
	public TardisControlPacket(ByteBuf payload)
	{
		super(payload);
	}
	
	public TardisControlPacket(NBTTagCompound nbt)
	{
		this(Unpooled.buffer(),nbt);
	}
	
	public TardisControlPacket(ByteBuf payload, NBTTagCompound nbt)
	{
		super(payload,nbt,(byte) TardisAbstractPacket.PacketType.CONTROL.ordinal());
	}
	
	public void activate()
	{
		if(!Helper.isServer())
			return;
		NBTTagCompound data = getNBT();
		if(data != null)
		{
			String playerName = data.getString("pl");
			EntityPlayer player = Helper.getPlayer(playerName);
			if(player != null)
			{
				int dim = data.getInteger("dim");
				int x = data.getInteger("x");
				int y = data.getInteger("y");
				int z = data.getInteger("z");
				World w = Helper.getWorld(dim);
				if(w != null)
				{
					TileEntity te = w.getTileEntity(x, y, z);
					if(te instanceof IControlMatrix)
					{
						int controlID = data.getInteger("cID");
						((IControlMatrix)te).activateControl(player, controlID);
						if(te instanceof TardisAbstractTileEntity)
							((TardisAbstractTileEntity)te).sendUpdate();
						//else
						//	w.markBlockForUpdate(x, y, z);
					}
				}
			}
		}
	}
}
