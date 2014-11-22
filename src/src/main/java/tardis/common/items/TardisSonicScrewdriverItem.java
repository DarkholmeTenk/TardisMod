package tardis.common.items;

import java.util.ArrayList;
import java.util.List;

import cofh.api.block.IDismantleable;
import cofh.api.item.IToolHammer;
import cofh.api.tileentity.IReconfigurableFacing;

import tardis.TardisMod;
import tardis.api.TardisFunction;
import tardis.api.TardisScrewdriverMode;
import tardis.common.core.Helper;
import tardis.common.core.store.SimpleCoordStore;
import tardis.common.tileents.TardisConsoleTileEntity;
import tardis.common.tileents.TardisCoreTileEntity;
import tardis.common.tileents.TardisTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class TardisSonicScrewdriverItem extends TardisAbstractItem implements IToolHammer
{

	public TardisSonicScrewdriverItem()
	{
		super();
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
	
	public static int getLinkedDim(ItemStack is)
	{
		if(is.stackTagCompound != null)
		{
			int dim = is.stackTagCompound.getInteger("linkedTardis");
			return dim;
		}
		return 0;
	}

	public static TardisCoreTileEntity getLinkedCore(ItemStack is)
	{
		if(is.stackTagCompound != null && is.stackTagCompound.hasKey("linkedTardis"))
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
			nbt.setTag("coordStore", storeNBT);
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
				ChatComponentText c = new ChatComponentText("");
				c.getChatStyle().setColor(EnumChatFormatting.AQUA);
				c.appendText("[Sonic Screwdriver]" + (String)o);
				player.addChatMessage(c);
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
					Block b = w.getBlock(hitPos.blockX, hitPos.blockY, hitPos.blockZ);
					if(b != null && b instanceof IDismantleable)
					{
						IDismantleable dis = (IDismantleable) b;
						if(dis.canDismantle(player, w, hitPos.blockX, hitPos.blockY, hitPos.blockZ))
						{
							ArrayList<ItemStack> s = dis.dismantleBlock(player, w, hitPos.blockX, hitPos.blockY, hitPos.blockZ, false);
							for(ItemStack tis : s)
								if(tis != null)
									Helper.giveItemStack(player, tis);
							toolUsed(is,player,hitPos.blockX, hitPos.blockY, hitPos.blockZ);
						}
					}
					else
					{
						TileEntity te = w.getTileEntity(hitPos.blockX, hitPos.blockY, hitPos.blockZ);
						return te != null;
					}
				}
			}
			else if(mode.equals(TardisScrewdriverMode.Reconfigure))
			{
				if(hitPos != null)
				{
					if(w.getBlock(hitPos.blockX, hitPos.blockY, hitPos.blockZ) == TardisMod.decoBlock)
					{
						int m = w.getBlockMetadata(hitPos.blockX, hitPos.blockY, hitPos.blockZ);
						if(m == 2 || m == 4)
						{
							TardisCoreTileEntity core = Helper.getTardisCore(w);
							if(core == null || core.canModify(player))
							{
								w.setBlock(hitPos.blockX, hitPos.blockY, hitPos.blockZ, TardisMod.componentBlock, m==2?0:1, 3);
								toolUsed(is,player,hitPos.blockX, hitPos.blockY, hitPos.blockZ);
								return true;
							}
							else
								player.addChatMessage(TardisCoreTileEntity.cannotModifyMessage);
						}
					}
					else
					{
						TileEntity te = w.getTileEntity(hitPos.blockX, hitPos.blockY, hitPos.blockZ);
						if(te instanceof IReconfigurableFacing)
						{
							if(((IReconfigurableFacing)te).rotateBlock())
							{
								toolUsed(is,player,hitPos.blockX, hitPos.blockY, hitPos.blockZ);
								return true;
							}
						}
						return te != null;
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
					if(Helper.getWorldID(core.getWorldObj()) == Helper.getWorldID(player.worldObj))
					{
						player.addChatMessage(new ChatComponentText("[Sonic Screwdriver]You are in the TARDIS"));
					}
					else
					{
						if(core != null)
						{
							TardisTileEntity ext = core.getExterior();
							if(ext != null)
							{
								if(Helper.getWorldID(ext) != player.worldObj.provider.dimensionId)
									player.addChatMessage(new ChatComponentText("[Sonic Screwdriver]The TARDIS does not appear to be in this dimension"));
								else
									player.addChatMessage(new ChatComponentText("[Sonic Screwdriver]The TARDIS is at ["+ext.xCoord+","+ext.yCoord+","+ext.zCoord+"]"));
							}
						}
					}
				}
				else
					player.addChatMessage(new ChatComponentText("[Sonic Screwdriver]The TARDIS could not be located"));
			}
			else if(mode.equals(TardisScrewdriverMode.Transmat))
			{
				if(core.hasFunction(TardisFunction.TRANSMAT))
					core.transmatEntity(player);
			}
			else if(mode.equals(TardisScrewdriverMode.Recall))
			{
				TardisConsoleTileEntity con = core.getConsole();
				if(con != null && !core.inFlight())
				{
					if(con.setControls(Helper.getWorldID(player.worldObj), (int) player.posX, (int) player.posY, (int) player.posZ, false))
					{
						if(core.takeOff(true,player))
							player.addChatMessage(new ChatComponentText("[Sonic Screwdriver]TARDIS inbound"));
					}
					else
						player.addChatMessage(new ChatComponentText("[Sonic Screwdriver]TARDIS recall failed"));
				}
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
		if(!Helper.isServer())
			return false;
		TardisScrewdriverMode mode = getMode(is);
		if(TardisScrewdriverMode.Transmat.equals(mode) && !(hit instanceof EntityPlayer))
		{
			TardisCoreTileEntity core = getLinkedCore(is);
			if(core != null)
				core.transmatEntity(hit);
			return true;
		}
        return false;
    }
	
	@Override
	public boolean doesSneakBypassUse(World w, int x, int y, int z,EntityPlayer player)
	{
		return true;
	}

	@Override
	public void initRecipes()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isUsable(ItemStack is,EntityLivingBase player, int x, int y, int z)
	{
		if(is != null)
		{
			TardisScrewdriverMode mode = getMode(is);
			if(mode.equals(TardisScrewdriverMode.Dismantle) || mode.equals(TardisScrewdriverMode.Reconfigure))
				return true;
		}
		return false;
	}

	@Override
	public void toolUsed(ItemStack is, EntityLivingBase player, int x, int y, int z)
	{
		float speed = (float)(player.getRNG().nextDouble() * 0.5) + 1;
		Helper.playSound(Helper.getWorldID(player.worldObj), x, y, z, "tardismod:sonic", 0.5F,speed);
		player.swingItem();
	}

}
