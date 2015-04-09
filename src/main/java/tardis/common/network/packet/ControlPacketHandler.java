package tardis.common.network.packet;

import io.darkcraft.darkcore.mod.abstracts.AbstractTileEntity;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import io.darkcraft.darkcore.mod.interfaces.IDataPacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tardis.api.IControlMatrix;

public class ControlPacketHandler implements IDataPacketHandler
{
	@Override
	public void handleData(NBTTagCompound data)
	{
		if(data != null)
		{
			String playerName = data.getString("pl");
			EntityPlayer player = ServerHelper.getPlayer(playerName);
			if(player != null)
			{
				int dim = data.getInteger("dim");
				int x = data.getInteger("x");
				int y = data.getInteger("y");
				int z = data.getInteger("z");
				World w = WorldHelper.getWorld(dim);
				if(w != null)
				{
					TileEntity te = w.getTileEntity(x, y, z);
					if(te instanceof IControlMatrix)
					{
						int controlID = data.getInteger("cID");
						((IControlMatrix)te).activateControl(player, controlID);
						if(te instanceof AbstractTileEntity)
							((AbstractTileEntity)te).sendUpdate();
					}
				}
			}
		}
	}
}
