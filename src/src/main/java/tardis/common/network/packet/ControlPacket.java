package tardis.common.network.packet;

import tardis.api.IControlMatrix;
import tardis.common.core.Helper;
import tardis.common.tileents.AbstractTileEntity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ControlPacket extends AbstractPacket
{
	public ControlPacket(ByteBuf payload)
	{
		super(payload);
	}
	
	public ControlPacket(NBTTagCompound nbt)
	{
		this(Unpooled.buffer(),nbt);
	}
	
	public ControlPacket(ByteBuf payload, NBTTagCompound nbt)
	{
		super(payload,nbt,(byte) AbstractPacket.PacketType.CONTROL.ordinal());
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
						if(te instanceof AbstractTileEntity)
							((AbstractTileEntity)te).sendUpdate();
						//else
						//	w.markBlockForUpdate(x, y, z);
					}
				}
			}
		}
	}
}
