package tardis.common.items;

import io.darkcraft.darkcore.mod.abstracts.AbstractItem;
import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.SoundHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.api.IScrewable;
import tardis.api.ScrewdriverMode;
import tardis.api.TardisFunction;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.dimension.TardisWorldProvider;
import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.CoreTileEntity;
import tardis.common.tileents.TardisTileEntity;
import cofh.api.block.IDismantleable;
import cofh.api.item.IToolHammer;
import cofh.api.tileentity.IReconfigurableFacing;

public class SonicScrewdriverItem extends AbstractItem implements IToolHammer
{
	public static final int maxPerms = 0xFF;
	public static final int minPerms = 0xCD;

	public SonicScrewdriverItem()
	{
		super(TardisMod.modName);
		setUnlocalizedName("SonicScrewdriver");
		setMaxDamage(64);
		setMaxStackSize(1);
	}
	
	public static ScrewdriverMode getMode(int i)
	{
		ScrewdriverMode[] modes = ScrewdriverMode.values();
		if(i < 0 || i >= modes.length)
			return modes[0];
		return modes[i];
	}
	
	public static ScrewdriverMode getMode(ItemStack is)
	{
		if(is == null)
			return ScrewdriverMode.Dismantle;
		
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

	public static CoreTileEntity getLinkedCore(ItemStack is)
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
		if(is != null && (is.getItem() instanceof SonicScrewdriverItem))
		{
			NBTTagCompound nbt = is.stackTagCompound;
			if(nbt == null)
				nbt = is.stackTagCompound = new NBTTagCompound();
			NBTTagCompound storeNBT = toStore.writeToNBT();
			nbt.setTag("coordStore", storeNBT);
		}
	}
	
	public static double[] getColors(ScrewdriverMode m)
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
		ScrewdriverMode mode = getMode(is);
		return getColors(mode);
	}
	
	@Override
	public void addInfo(ItemStack is, EntityPlayer player, List infoList)
	{
		if(is != null)
		{
			ScrewdriverMode mode = getMode(is);
			infoList.add("Mode: " + mode.toString());
			if(mode.equals(ScrewdriverMode.Schematic))
			{
				String schemaName = getSchema(is);
				if(schemaName == null || schemaName.equals(""))
					infoList.add("Schematic: --None--");
				else
					infoList.add("Schematic: " + schemaName);
			}
		}
	}
	
	public boolean isValidMode(EntityPlayer pl, ItemStack is, ScrewdriverMode mode)
	{
		if(!hasPermission(is,mode))
			return false;
		if(mode.requiredFunction == null)
			return true;
		TardisOutput.print("TSSI", "HasP");
		CoreTileEntity core = getLinkedCore(is);
		if(core == null)
			return false;
		if(!core.hasFunction(mode.requiredFunction))
			return false;
		if(pl != null)
		{
			boolean isInTardis = false;
			isInTardis = pl.worldObj.provider instanceof TardisWorldProvider;
			if(isInTardis && mode.equals(ScrewdriverMode.Locate))
				return false;
			if(!isInTardis && mode.equals(ScrewdriverMode.Schematic))
				return false;
		}
		return true;
	}
	
	public static boolean isPlayerHoldingScrewdriver(EntityPlayer pl)
	{
		ItemStack is = pl.getHeldItem();
		if(is!= null)
		{
			if(is.getItem() == TardisMod.screwItem)
				return true;
		}
		return false;
	}
	
	public void notifyMode(ItemStack is, EntityPlayer player, boolean override)
	{
		ScrewdriverMode mode = getMode(is);
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
	
	private boolean dismantle(IDismantleable dis, SimpleCoordStore pos, EntityPlayer player, ItemStack is)
	{
		if(dis.canDismantle(player, pos.getWorldObj(), pos.x, pos.y, pos.z))
		{
			ArrayList<ItemStack> s = dis.dismantleBlock(player, pos.getWorldObj(), pos.x, pos.y, pos.z, false);
			for(ItemStack tis : s)
				if(tis != null)
					WorldHelper.giveItemStack(player, tis);
			toolUsed(is,player, pos.x, pos.y, pos.z);
			return true;
		}
		return false;
	}
	
	private boolean screwScrewable(Object screw, ScrewdriverMode mode, EntityPlayer player)
	{
		if(screw instanceof IScrewable)
			return ((IScrewable)screw).screw(mode, player);
		return false;
	}
	
	public boolean rightClickBlock(EntityPlayer pl, SimpleCoordStore pos)
	{
		if(!isPlayerHoldingScrewdriver(pl))
			return false;
		return screwScrewable(pos.getTileEntity(),getMode(pl.getHeldItem()),pl);
	}
	
	private boolean rightClickBlock(ItemStack is, ScrewdriverMode mode, EntityPlayer player, World w)
	{
		System.out.println("T");
		if(ServerHelper.isServer())
		{
			MovingObjectPosition hitPos = getMovingObjectPositionFromPlayer(w, player, true);
			if(hitPos == null)
				return false;
			
			TileEntity te = w.getTileEntity(hitPos.blockX, hitPos.blockY, hitPos.blockZ);
			Block b = w.getBlock(hitPos.blockX, hitPos.blockY, hitPos.blockZ);
			if(screwScrewable(te,mode,player) || screwScrewable(b,mode,player))
				return true;
			if(mode.equals(ScrewdriverMode.Dismantle))
			{
				if(b instanceof IDismantleable)
					if(dismantle((IDismantleable)b,new SimpleCoordStore(w,hitPos),player,is))
						return true;
				if(te instanceof IDismantleable)
					if(dismantle((IDismantleable)te,new SimpleCoordStore(w,hitPos),player,is))
						return true;
			}
			else if(mode.equals(ScrewdriverMode.Reconfigure))
			{
				if(b == TardisMod.decoBlock || b == TardisMod.darkDecoBlock)
				{
					int m = w.getBlockMetadata(hitPos.blockX, hitPos.blockY, hitPos.blockZ);
					if(m == 2 || m == 4)
					{
						CoreTileEntity core = Helper.getTardisCore(w);
						if(core == null || core.canModify(player))
						{
							w.setBlock(hitPos.blockX, hitPos.blockY, hitPos.blockZ, TardisMod.componentBlock, m==2?0:1, 3);
							toolUsed(is,player,hitPos.blockX, hitPos.blockY, hitPos.blockZ);
							return true;
						}
						else
							player.addChatMessage(CoreTileEntity.cannotModifyMessage);
					}
				}
				else
				{
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
		return false;
	}
	
	public static boolean hasPermission(ItemStack is, ScrewdriverMode mode)
	{
		if(is == null)
			return false;
		if(is.stackTagCompound == null)
			return false;
		return hasPermission(is.stackTagCompound, mode);
	}
	
	public static boolean hasPermission(NBTTagCompound nbt, ScrewdriverMode mode)
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
	
	public static void setPermission(ItemStack is, ScrewdriverMode mode, boolean value)
	{
		if(is == null)
			return;
		if(is.stackTagCompound == null)
			return;
		setPermission(is.stackTagCompound, mode, value);
	}
	
	public static void setPermission(NBTTagCompound nbt, ScrewdriverMode mode, boolean value)
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
	
	public static void togglePermission(NBTTagCompound nbt, ScrewdriverMode mode)
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
	
	private void switchMode(ItemStack is, World world, EntityPlayer player, ScrewdriverMode mode)
	{
		boolean valid = false;
		boolean first = false;
		int newValue = mode.ordinal();
		while(((!valid) && (newValue != 0)) || (!first))
		{
			first = true;
			newValue = (newValue + 1) % ScrewdriverMode.values().length;
			ScrewdriverMode m = getMode(newValue);
			valid = isValidMode(player, is,m);
			TardisOutput.print("TSSI", "V:"+valid);
		}
		is.stackTagCompound.setInteger("scMo", newValue);
		notifyMode(is,player,true);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player)
    {
		System.out.println("T!");
		ScrewdriverMode mode = getMode(is);
		if(ServerHelper.isServer() && player.isSneaking())
		{
			if(!rightClickBlock(is,mode,player,world))
				switchMode(is,world,player,mode);
		}
		else if(ServerHelper.isServer())
		{
			CoreTileEntity core = getLinkedCore(is);
			if(mode.equals(ScrewdriverMode.Locate))
			{
				if(core != null)
				{
					if(WorldHelper.getWorldID(core.getWorldObj()) == WorldHelper.getWorldID(player.worldObj))
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
								if(WorldHelper.getWorldID(ext) != player.worldObj.provider.dimensionId)
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
			else if(mode.equals(ScrewdriverMode.Transmat))
			{
				if(core.hasFunction(TardisFunction.TRANSMAT))
					core.transmatEntity(player);
			}
			else if(mode.equals(ScrewdriverMode.Recall))
			{
				ConsoleTileEntity con = core.getConsole();
				if(con != null && !core.inFlight())
				{
					if(con.setControls(WorldHelper.getWorldID(player.worldObj), (int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ), false))
					{
						if(core.takeOff(true,player))
							player.addChatMessage(new ChatComponentText("[Sonic Screwdriver]TARDIS inbound"));
					}
					else
						player.addChatMessage(new ChatComponentText("[Sonic Screwdriver]TARDIS recall failed"));
				}
			}
			else if(mode.equals(ScrewdriverMode.Dismantle) || mode.equals(ScrewdriverMode.Reconfigure))
			{
				rightClickBlock(is,mode,player,world);
			}
		}
        return is;
    }
	
	@Override
	public boolean hitEntity(ItemStack is, EntityLivingBase hit, EntityLivingBase hitter)
    {
		if(!ServerHelper.isServer())
			return false;
		ScrewdriverMode mode = getMode(is);
		if(ScrewdriverMode.Transmat.equals(mode) && !(hit instanceof EntityPlayer))
		{
			CoreTileEntity core = getLinkedCore(is);
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
			ScrewdriverMode mode = getMode(is);
			if(mode.equals(ScrewdriverMode.Dismantle) || mode.equals(ScrewdriverMode.Reconfigure))
				return true;
		}
		return false;
	}

	@Override
	public void toolUsed(ItemStack is, EntityLivingBase player, int x, int y, int z)
	{
		float speed = (float)(player.getRNG().nextDouble() * 0.5) + 0.75f;
		SoundHelper.playSound(WorldHelper.getWorldID(player.worldObj), x, y, z, "tardismod:sonic", 0.25F,speed);
		player.swingItem();
	}
	
	@Override
	public void registerIcons(IIconRegister register)
	{
		
	}

}
