package tardis.common.tileents;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import io.darkcraft.darkcore.mod.abstracts.AbstractTileEntity;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.api.IControlMatrix;
import tardis.client.renderer.tileents.ManualRenderer;
import tardis.common.TMRegistry;
import tardis.common.core.helpers.Helper;
import tardis.common.core.helpers.ScrewdriverHelper;
import tardis.common.tileents.extensions.ManualPage;
import tardis.common.tileents.extensions.ManualPageTree;

public class ManualTileEntity extends AbstractTileEntity implements IControlMatrix
{
	private ManualPage selected = ManualPage.MAIN;

	private List<String> pageListCache = null;

	public ManualTileEntity()
	{

	}

	public ManualTileEntity(World w)
	{
		// TODO Auto-generated constructor stub
	}

	public ManualPage getPage()
	{
		return selected;
	}

	public List<String> getPageList()
	{
		if(pageListCache == null)
			return pageListCache = ManualPageTree.topTree.getString(getPage(), 0);
		return pageListCache;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if((tt % 80) == 0)
		{
			for(int i = -2; i <= 2; i++)
			{
				for(int j = -1; j <= 1; j++)
				{
					if((i == 0) && (j == 0))
						continue;
					worldObj.setBlock(xCoord+(dX()?i:0), yCoord+j, zCoord+(!dX()?i:0), TMRegistry.manualHelperBlock,meta(),3);
				}
			}
		}
	}

	private int meta()
	{
		return worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
	}

	private boolean dX()
	{
		return (meta() % 2) == 0;
	}

	public static boolean isRightSide(int s, int meta)
	{
		switch(meta)
		{
			case 0: return s==2;
			case 1: return s==5;
			case 2: return s==3;
			case 3: return s==4;
			default: return false;
		}
	}

	public boolean activate(int x, int y, int z, EntityPlayer pl, int s, float t, float j, float k)
	{
		if((dX()?z:x) == (dX()?zCoord:xCoord))
		{
			if(isRightSide(s,meta()))
			{
				if(ServerHelper.isServer()) return true;
				boolean flip = meta() < 2;
				double oX;
				if(!flip)
					oX = ((dX()?(x-xCoord)+t:(z-zCoord)+k)+2);
				else
					oX = ((dX()?(xCoord-x)-t:(zCoord-z)-k)+3);
				double oY = (y-yCoord)+1+j;
				double start = 0.1;
				double end = 1.5;
				if((oX >= start) && (oX < end) && (oY >= start))
				{
					double row = (3 - oY - start) / ((ManualRenderer.textSize * 10) / 2);
					if((row >= 0) && (row < ManualPage.values().length))
					{
						int rowInt = MathHelper.floor(row);
						List<String> rows = getPageList();
						if(rowInt < rows.size())
						{
							String d = rows.get(rowInt);
							selected = ManualPage.get(d);
							Helper.activateControl(this, pl, selected.ordinal());
						}
					}
				}
				pageListCache = null;
				return true;
			}
		}
		return false;
	}

	@Override
	public void activateControl(EntityPlayer player, int controlID)
	{
		selected = ManualPage.get(controlID);
		sendUpdate();
	}

	@Override
	public void writeTransmittable(NBTTagCompound nbt)
	{
		nbt.setInteger("page", selected.ordinal());
	}

	@Override
	public void readTransmittable(NBTTagCompound nbt)
	{
		selected = ManualPage.get(nbt.getInteger("page"));
	}

	@Override
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
		if(dX())
			return AxisAlignedBB.getBoundingBox(xCoord-2, yCoord-1, zCoord, xCoord+3, yCoord+1, zCoord+1);
		else
			return AxisAlignedBB.getBoundingBox(xCoord, yCoord-1, zCoord-2, xCoord+1, yCoord+1, zCoord+3);
    }

	@Override
	public double getControlState(int controlID, boolean wobble){return 0;}

	@Override
	public double getControlState(int controlID){return 0;}

	@Override
	public double getControlHighlight(int controlID){return 0;}

	@Override
	public ScrewdriverHelper getScrewHelper(int slot){return null;}

	@Override
	public double[] getColorRatio(int controlID){return null;}
}
