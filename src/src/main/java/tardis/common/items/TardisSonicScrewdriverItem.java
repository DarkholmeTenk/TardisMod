package tardis.common.items;

import java.util.ArrayList;
import java.util.List;

import buildcraft.api.tools.IToolWrench;

import cofh.api.block.IDismantleable;
import cofh.api.tileentity.IReconfigurableFacing;

import tardis.TardisMod;
import tardis.api.TardisFunction;
import tardis.api.TardisScrewdriverMode;
import tardis.common.core.Helper;
import tardis.common.core.store.SimpleCoordStore;
import tardis.common.tileents.TardisCoreTileEntity;
import tardis.common.tileents.TardisTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class TardisSonicScrewdriverItem extends TardisAbstractItem implements IToolWrench
{

	public TardisSonicScrewdriverItem(int par1)
	{
		super(par1);
		setUnlocalizedName("SonicScrewdriver");
		setMaxDamage(64);
		setMaxStackSize(1);
	}
	
	public static TardisScrewdriverMode getMode(int i)
	{
		TardisScrewdriverMode[] modes = TardisScrewdriverMode.values();
		if(i < 0 || i >= modes.length)
			return modes[0];
		return modes[i];
	}
	
	public static TardisScrewdriverMode getMode(ItemStack is)
	{
		if(is == null)
			return TardisScrewdriverMode.Dismantle;
		
		NBTTagCompound isTag = is.stackTagCompound;
		if(isTag == null)
		{
			is.stackTagCompound = isTag = new NBTTagCompound();
			isTag.setInteger("screwdriverMode", 0);
		}
		
		return getMode(isTag.getInteger("screwdriverMode"));
	}
	
	public static String getSchema(ItemStack is)
	{
		if(is == null)
			return "";
		
		NBTTagCompound isTag = is.stackTagCompound;
		if(isTag != null)
			return isTag.getString("schemaName");
		return "";
	}
	
	public static TardisCoreTileEntity getLinkedCore(ItemStack is)
	{
		if(is.stackTagCompound != null)
		{
			int dim = is.stackTagCompound.getInteger("linkedTardis");
			return Helper.getTardisCore(dim);
		}
		return null;
	}
	
	public static SimpleCoordStore getStoredCoords(ItemStack is)
	{
		if(is != null)
		{
			NBTTagCompound nbt = is.stackTagCompound;
			if(nbt != null && nbt.hasKey("coordStore"))
			{
				return SimpleCoordStore.readFromNBT(nbt.getCompoundTag("coordStore"));
			}
		}
		return null;
	}
	
	public static void setStoredCoords(ItemStack is,SimpleCoordStore toStore)
	{
		if(is != null && (is.getItem() instanceof TardisSonicScrewdriverItem))
		{
			NBTTagCompound nbt = is.stackTagCompound;
			if(nbt == null)
				nbt = is.stackTagCompound = new NBTTagCompound();
			NBTTagCompound storeNBT = toStore.writeToNBT();
			nbt.setCompoundTag("coordStore", storeNBT);
		}
	}
	
	public static double[] getColors(TardisScrewdriverMode m)
	{
		double[] colors = new double[3];
		colors[0] = 0;
		colors[1] = 0;
		colors[2] = 1;
		if(m != null)
			return m.c;
		return colors;
	}
	
	public static double[] getColors(ItemStack is)
	{
		TardisScrewdriverMode mode = getMode(is);
		return getColors(mode);
	}
	
	@Override
	public void addInfo(ItemStack is, EntityPlayer player, List infoList)
	{
		if(is != null)
		{
			TardisScrewdriverMode mode = getMode(is);
			infoList.add("Mode: " + mode.toString());
			if(mode.equals(TardisScrewdriverMode.Schematic))
			{
				String schemaName = getSchema(is);
				if(schemaName == null || schemaName.equals(""))
					infoList.add("Schematic: --None--");
				else
					infoList.add("Schematic: " + schemaName);
			}
		}
	}
	
	public void notifyMode(ItemStack is, EntityPlayer player)
	{
		getMode(is);
		ArrayList<Object> list = new ArrayList<Object>();
		addInfo(is,player,list);
		for(Object o: list)
		{
			if(o instanceof String)
			{
				ChatMessageComponent c = new ChatMessageComponent();
				c.setColor(EnumChatFormatting.AQUA);
				c.addText("[Sonic Screwdriver]" + (String)o);
				player.sendChatToPlayer(c);
			}
		}
	}
	
	private boolean rightClickBlock(ItemStack is, TardisScrewdriverMode mode, EntityPlayer player, World w)
	{
		if(Helper.isServer())
		{
			MovingObjectPosition hitPos = getMovingObjectPositionFromPlayer(w, player, true);
			if(mode.equals(TardisScrewdriverMode.Dismantle))
			{
				if(hitPos != null)
				{
					Block b = Helper.getBlock(w, hitPos.blockX, hitPos.blockY, hitPos.blockZ);
					if(b != null && b instanceof IDismantleable)
					{
						IDismantleable dis = (IDismantleable) b;
						if(dis.canDismantle(player, w, hitPos.blockX, hitPos.blockY, hitPos.blockZ))
						{
							ItemStack s = dis.dismantleBlock(player, w, hitPos.blockX, hitPos.blockY, hitPos.blockZ, false);
							if(s != null)
								Helper.giveItemStack(player, s);
							wrenchUsed(player,hitPos.blockX, hitPos.blockY, hitPos.blockZ);
							return true;
						}
					}
				}
			}
			else if(mode.equals(TardisScrewdriverMode.Reconfigure))
			{
				if(hitPos != null)
				{
					if(w.getBlockId(hitPos.blockX, hitPos.blockY, hitPos.blockZ) == TardisMod.decoBlock.blockID)
					{
						int m = w.getBlockMetadata(hitPos.blockX, hitPos.blockY, hitPos.blockZ);
						if(m == 2 || m == 4)
						{
							TardisCoreTileEntity core = Helper.getTardisCore(w);
							if(core.canModify(player))
							{
								w.setBlock(hitPos.blockX, hitPos.blockY, hitPos.blockZ, TardisMod.componentBlock.blockID, m==2?0:1, 3);
								wrenchUsed(player,hitPos.blockX, hitPos.blockY, hitPos.blockZ);
								return true;
							}
							else
								player.sendChatToPlayer(new ChatMessageComponent().addText("You do not have permission to modify this TARDIS"));
						}
					}
					else
					{
						TileEntity te = w.getBlockTileEntity(hitPos.blockX, hitPos.blockY, hitPos.blockZ);
						if(te instanceof IReconfigurableFacing)
						{
							if(((IReconfigurableFacing)te).rotateBlock())
							{
								wrenchUsed(player,hitPos.blockX, hitPos.blockY, hitPos.blockZ);
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player)
    {
		TardisScrewdriverMode mode = getMode(is);
		if(!world.isRemote && player.isSneaking())
		{
			boolean valid = false;
			int newValue = mode.ordinal();
			if(!rightClickBlock(is,mode,player,world))
			{
				while(!valid)
				{
					newValue = (newValue + 1) % TardisScrewdriverMode.values().length;
					TardisScrewdriverMode m = getMode(newValue);
					if(m.requiredFunction != null)
					{
						TardisCoreTileEntity te = getLinkedCore(is);
						if(te != null && te.hasFunction(m.requiredFunction))
							valid = true;
					}
					else
					{
						valid = true;
					}
				}
				is.stackTagCompound.setInteger("screwdriverMode", newValue);
				notifyMode(is,player);
			}
		}
		else if(Helper.isServer())
		{
			TardisCoreTileEntity core = getLinkedCore(is);
			if(mode.equals(TardisScrewdriverMode.Locate))
			{
				if(core != null)
				{
					if(Helper.getWorldID(core.worldObj) == Helper.getWorldID(player.worldObj))
					{
						player.addChatMessage("[Sonic Screwdriver]You are in the TARDIS");
					}
					else
					{
						if(core != null)
						{
							TardisTileEntity ext = core.getExterior();
							if(ext != null)
							{
								if(ext.worldObj.provider.dimensionId != player.worldObj.provider.dimensionId)
									player.addChatMessage("[Sonic Screwdriver]The TARDIS does not appear to be in this dimension");
								else
									player.addChatMessage("[Sonic Screwdriver]The TARDIS is at ["+ext.xCoord+","+ext.yCoord+","+ext.zCoord+"]");
							}
						}
					}
				}
				else
					player.addChatMessage("[Sonic Screwdriver]The TARDIS could not be located");
			}
			else if(mode.equals(TardisScrewdriverMode.Transmat))
			{
				if(core.hasFunction(TardisFunction.TRANSMAT))
					core.transmatEntity(player);
			}
			else if(mode.equals(TardisScrewdriverMode.Dismantle) || mode.equals(TardisScrewdriverMode.Reconfigure))
			{
				rightClickBlock(is,mode,player,world);
			}
		}
        return is;
    }
	
	@Override
	public boolean hitEntity(ItemStack is, EntityLivingBase hit, EntityLivingBase hitter)
    {
		TardisScrewdriverMode mode = getMode(is);
		if(TardisScrewdriverMode.Transmat.equals(mode) && !(hit instanceof EntityPlayer))
		{
			TardisCoreTileEntity core = getLinkedCore(is);
			core.transmatEntity(hit);
			return true;
		}
        return false;
    }

	@Override
	public void initRecipes()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canWrench(EntityPlayer player, int x, int y, int z)
	{
		ItemStack is = player.getHeldItem();
		if(is != null)
		{
			TardisScrewdriverMode mode = getMode(is);
			if(mode.equals(TardisScrewdriverMode.Dismantle) || mode.equals(TardisScrewdriverMode.Reconfigure))
				return true;
		}
		return false;
	}

	@Override
	public void wrenchUsed(EntityPlayer player, int x, int y, int z)
	{
		player.swingItem();
	}

}
