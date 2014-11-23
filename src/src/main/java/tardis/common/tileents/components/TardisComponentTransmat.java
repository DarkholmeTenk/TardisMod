package tardis.common.tileents.components;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import tardis.api.IScrewable;
import tardis.api.TardisScrewdriverMode;
import tardis.common.core.Helper;
import tardis.common.core.store.SimpleCoordStore;
import tardis.common.tileents.TardisComponentTileEntity;
import tardis.common.tileents.TardisCoreTileEntity;

public class TardisComponentTransmat extends TardisAbstractComponent implements IScrewable
{
	private SimpleCoordStore myCoord;
	
	protected TardisComponentTransmat()
	{
		
	}
	
	public TardisComponentTransmat(TardisComponentTileEntity parent)
	{
	}
	
	
	@Override
	public boolean screw(TardisScrewdriverMode mode, EntityPlayer player)
	{
		if(mode.equals(TardisScrewdriverMode.Reconfigure))
		{
			TardisCoreTileEntity core = Helper.getTardisCore(world);
			if(core != null)
			{
				ChatComponentText c = new ChatComponentText("");
				if(core.canModify(player))
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
	protected void parentAdded(TardisComponentTileEntity parent)
	{
		super.parentAdded(parent);
		myCoord = new SimpleCoordStore(world,xCoord,yCoord,zCoord);
	}

	@Override
	public ITardisComponent create(TardisComponentTileEntity parent)
	{
		return new TardisComponentTransmat(parent);
	}
}
