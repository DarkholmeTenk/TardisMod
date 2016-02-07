package tardis.common.tileents.components;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import tardis.api.IScrewable;
import tardis.api.ScrewdriverMode;
import tardis.api.TardisPermission;
import tardis.common.core.helpers.Helper;
import tardis.common.core.helpers.ScrewdriverHelper;
import tardis.common.dimension.TardisDataStore;
import tardis.common.tileents.ComponentTileEntity;
import tardis.common.tileents.CoreTileEntity;

public class ComponentTransmat extends AbstractComponent implements IScrewable
{
	protected ComponentTransmat()
	{
	}

	public ComponentTransmat(ComponentTileEntity parent)
	{
	}


	@Override
	public boolean screw(ScrewdriverHelper helper, ScrewdriverMode mode, EntityPlayer player)
	{
		if(mode.equals(ScrewdriverMode.Reconfigure))
		{
			TardisDataStore ds = Helper.getDataStore(world);
			CoreTileEntity core = Helper.getTardisCore(world);
			if((core != null) && (ds != null))
			{
				ChatComponentText c = new ChatComponentText("");
				if(ds.hasPermission(player, TardisPermission.ROUNDEL) && ds.hasPermission(player, TardisPermission.TRANSMAT))
				{
					SimpleCoordStore transPoint = new SimpleCoordStore(player);
					if(core.isTransmatPoint(transPoint))
					{
						core.setTransmatPoint(null);
						c.appendText("TARDIS transmat point set to default location");
					}
					else
					{
						if(parentObj.getWorldObj().isAirBlock((int)player.posX, (int)player.posY, (int)player.posZ)&&parentObj.getWorldObj().isAirBlock((int)player.posX, (int)player.posY+1, (int)player.posZ))
						{
							core.setTransmatPoint(transPoint);
							c.appendText("TARDIS transmat point set to " + core.getTransmatPoint().toSimpleString());
						}
						else
							c.appendText("Not enough room for transmat");
					}
				}
				else
					c.appendText("You don't have permission to modify this TARDIS");
				player.addChatMessage(c);
				return true;
			}
		}
		return false;
	}

	@Override
	protected void parentAdded(ComponentTileEntity parent)
	{
		super.parentAdded(parent);
		new SimpleCoordStore(world,xCoord,yCoord,zCoord);
	}

	@Override
	public ITardisComponent create(ComponentTileEntity parent)
	{
		return new ComponentTransmat(parent);
	}
}
