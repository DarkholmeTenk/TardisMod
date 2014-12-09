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
import tardis.common.core.TardisOutput;
import tardis.common.core.store.SimpleCoordStore;
import tardis.common.dimension.TardisWorldProvider;
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
	public static final int maxPerms = 0xFF;
	public static final int minPerms = 0xCD;

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
			isTag.setInteger("scMo", 0);
		}
		
		return getMode(isTag.getInteger("scMo"));
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
	
	public static int getLinkedDim(NBTTagCompound nbt)
	{
		if(nbt != null)
		{
			int dim = nbt.getInteger("linkedTardis");
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
	
	public boolean isValidMode(EntityPlayer pl, ItemStack is, TardisScrewdriverMode mode)
	{
		if(!hasPermission(is,mode))
			return false;
		if(mode.requiredFunction == null)
			return true;
		TardisOutput.print("TSSI", "HasP");
		TardisCoreTileEntity core = getLinkedCore(is);
		if(core == null)
			return false;
		if(!core.hasFunction(mode.requiredFunction))
			return false;
		if(pl != null)
		{
			boolean isInTardis = false;
			isInTardis = pl.worldObj.provider instanceof TardisWorldProvider;
			if(isInTardis && mode.equals(TardisScrewdriverMode.Locate))
				return false;
			if(!isInTardis && mode.equals(TardisScrewdriverMode.Schematic))
				return false;
		}
		return true;
	}
	
	public void notifyMode(ItemStack is, EntityPlayer player, boolean override)
	{
		TardisScrewdriverMode mode = getMode(is);
		if(override || isValidMode(player, is,mode))
		{
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
		else
			switchMode(is, player.worldObj, player, mode);
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
	
	public static boolean hasPermission(ItemStack is, TardisScrewdriverMode mode)
	{
		if(is == null)
			return false;
		if(is.stackTagCompound == null)
			return false;
		return hasPermission(is.stackTagCompound, mode);
	}
	
	public static boolean hasPermission(NBTTagCompound nbt, TardisScrewdriverMode mode)
	{
		int permissions = maxPerms;
		if(nbt.hasKey("perm"))
			permissions = nbt.getInteger("perm");
		else
			nbt.setInteger("perm", permissions);
		int toCheck = (int) Math.pow(2, mode.ordinal());
		//TardisOutput.print("TSSI", String.format("Checking perm %2X : %2X = %b",permissions,toCheck, (permissions & toCheck) == toCheck));
		return (permissions & toCheck) == toCheck;
	}
	
	public static void setPermission(ItemStack is, TardisScrewdriverMode mode, boolean value)
	{
		if(is == null)
			return;
		if(is.stackTagCompound == null)
			return;
		setPermission(is.stackTagCompound, mode, value);
	}
	
	public static void setPermission(NBTTagCompound nbt, TardisScrewdriverMode mode, boolean value)
	{
		TardisOutput.print("TSSI", "Setting permission " + mode.name()  +" to "+ value);
		int permissions = maxPerms;
		if(nbt.hasKey("perm"))
			permissions = nbt.getInteger("perm");
		int toCheck = (int) Math.pow(2, mode.ordinal());
		if(!value)
			permissions -= permissions & toCheck;
		else
			permissions += toCheck & (~permissions);
		nbt.setInteger("perm", permissions);
	}
	
	public static void togglePermission(NBTTagCompound nbt, TardisScrewdriverMode mode)
	{
		TardisOutput.print("TSSI", "Toggling permission " + mode.name());
		setPermission(nbt,mode,!hasPermission(nbt,mode));
	}
	
	public static NBTTagCompound getNewNBT()
	{
		NBTTagCompound temp = new NBTTagCompound();
		temp.setInteger("scMo", 0);
		temp.setInteger("perm", maxPerms);
		return temp;
	}
	
	private void switchMode(ItemStack is, World world, EntityPlayer player, TardisScrewdriverMode mode)
	{
		boolean valid = false;
		boolean first = false;
		int newValue = mode.ordinal();
		while(((!valid) && (newValue != 0)) || (!first))
		{
			first = true;
			newValue = (newValue + 1) % TardisScrewdriverMode.values().length;
			TardisScrewdriverMode m = getMode(newValue);
			valid = isValidMode(player, is,m);
			TardisOutput.print("TSSI", "V:"+valid);
		}
		is.stackTagCompound.setInteger("scMo", newValue);
		notifyMode(is,player,true);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player)
    {
		TardisScrewdriverMode mode = getMode(is);
		if(!world.isRemote && player.isSneaking())
		{
			if(!rightClickBlock(is,mode,player,world))
				switchMode(is,world,player,mode);
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
					if(con.setControls(Helper.getWorldID(player.worldObj), (int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ), false))
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
		float speed = (float)(player.getRNG().nextDouble() * 0.5) + 0.75f;
		Helper.playSound(Helper.getWorldID(player.worldObj), x, y, z, "tardismod:sonic", 0.25F,speed);
		player.swingItem();
	}

}
