package tardis.common.tileents.components;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;
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
				ChatMessageComponent c = new ChatMessageComponent();
				if(core.canModify(player))
				{
					if(core.isTransmatPoint(myCoord))
					{
						core.setTransmatPoint(null);
						c.addText("TARDIS transmat point set to default location");
					}
					else
					{
						if(parentObj.worldObj.isAirBlock(xCoord, yCoord+1, zCoord)&&parentObj.worldObj.isAirBlock(xCoord, yCoord+2, zCoord))
						{
							core.setTransmatPoint(myCoord);
							c.addText("TARDIS transmat point set to " + myCoord.toSimpleString());
						}
						else
							c.addText("Not enough room for transmat");
					}
				}
				else
					c.addText("You don't have permission to modify this TARDIS");
				player.sendChatToPlayer(c);
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
