package tardis.common.items;

import io.darkcraft.darkcore.mod.abstracts.AbstractItem;
import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.SoundHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import mrtjp.projectred.api.IScrewdriver;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.api.ILinkable;
import tardis.api.IScrewable;
import tardis.api.IScrewablePrecise;
import tardis.api.ITDismantleable;
import tardis.api.ScrewdriverMode;
import tardis.api.TardisFunction;
import tardis.api.TardisPermission;
import tardis.common.core.helpers.Helper;
import tardis.common.core.helpers.ScrewdriverHelper;
import tardis.common.core.helpers.ScrewdriverHelperFactory;
import tardis.common.dimension.TardisDataStore;
import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.CoreTileEntity;
import tardis.common.tileents.TardisTileEntity;
import buildcraft.api.tools.IToolWrench;
import cofh.api.block.IDismantleable;
import cofh.api.item.IToolHammer;
import cofh.api.tileentity.IReconfigurableFacing;

import com.google.common.collect.Multimap;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;

@Optional.InterfaceList(value={
		@Optional.Interface(iface="buildcraft.api.tools.IToolWrench",modid="BuildCraftAPI|core"),
		@Optional.Interface(iface="cofh.api.item.IToolHammer",modid="CoFHCore"),
		@Optional.Interface(iface="mrtjp.projectred.api.IScrewdriver", modid="ProjRed|Core")})
public class SonicScrewdriverItem extends AbstractItem implements IToolHammer, IToolWrench, IScrewdriver
{
	public static final int	maxPerms	= 0xFF;
	public static final int	minPerms	= 0xCD;

	public SonicScrewdriverItem()
	{
		super(TardisMod.modName);
		setUnlocalizedName("SonicScrewdriver");
		setMaxDamage(64);
		setMaxStackSize(1);
	}

	@SuppressWarnings("unchecked")
	private void addModeInfo(ScrewdriverMode mode, ScrewdriverHelper helper, List infoList)
	{
		infoList.add("Mode: " + mode.toString());
		if (mode == ScrewdriverMode.Schematic)
			infoList.add("Schematic: " + helper.getSchemaDisplay());
		if (mode == ScrewdriverMode.Link)
		{
			SimpleCoordStore link = helper.getLinkSCS();
			if(link == null)
				infoList.add("Link: Target not set");
			else
				infoList.add("Link: " + link.toString());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInfo(ItemStack is, EntityPlayer player, List infoList)
	{
		ScrewdriverHelper helper = getHelper(is);
		if (helper != null)
		{
			infoList.add("Owner: " + helper.getOwner());
			for (ScrewdriverMode m : ScrewdriverMode.values())
			{
				if (!helper.hasPermission(m)) infoList.add(m.name() + " - Disabled");
			}
			ScrewdriverMode mode = getMode(is);
			addModeInfo(mode, helper, infoList);
		}
	}

	public static boolean isPlayerHoldingScrewdriver(EntityPlayer pl)
	{
		if(pl != null)
			return isScrewdriver(pl.getHeldItem());
		return false;
	}

	public static boolean isScrewdriver(ItemStack is)
	{
		if (is != null)
		{
			if (is.getItem() == TardisMod.screwItem) return true;
		}
		return false;
	}

	public void notifyMode(ScrewdriverHelper helper, EntityPlayer player, boolean override)
	{
		ScrewdriverMode mode = helper.getMode();
		if (!(override || helper.isModeValid(player, mode)))
			helper.switchMode(player);
		ArrayList<Object> list = new ArrayList<Object>();
		addModeInfo(mode, helper, list);
		for (Object o : list)
		{
			if (o instanceof String)
			{
				ChatComponentText c = new ChatComponentText("");
				c.getChatStyle().setColor(EnumChatFormatting.AQUA);
				c.appendText("[Sonic Screwdriver]" + (String) o);
				player.addChatMessage(c);
			}
		}
	}

	@Optional.Method(modid="CoFHCore")
	private boolean dismantle(Object o, SimpleCoordStore pos, EntityPlayer player)
	{
		if(!(o instanceof IDismantleable)) return false;
		IDismantleable dis = (IDismantleable)o;
		if (dis.canDismantle(player, pos.getWorldObj(), pos.x, pos.y, pos.z))
		{
			if(ServerHelper.isClient()) return true;
			ArrayList<ItemStack> s = dis.dismantleBlock(player, pos.getWorldObj(), pos.x, pos.y, pos.z, false);
			for (ItemStack tis : s)
				if (tis != null) WorldHelper.giveItemStack(player, tis);
			toolUsed(player.getHeldItem(), player, pos.x, pos.y, pos.z);
			return true;
		}
		return false;
	}

	private boolean tardisDismantle(ITDismantleable dis, SimpleCoordStore scs, EntityPlayer pl)
	{
		if(dis.canDismantle(scs, pl))
		{
			if(ServerHelper.isClient()) return true;
			List<ItemStack> s = dis.dismantle(scs, pl);
			for (ItemStack tis : s)
				if (tis != null) WorldHelper.giveItemStack(pl, tis);
			toolUsed(pl.getHeldItem(), pl, scs.x, scs.y, scs.z);
			return true;
		}
		return false;
	}

	private boolean screwScrewable(Object screw, ScrewdriverMode mode, EntityPlayer player, SimpleCoordStore pos)
	{
		if (screw instanceof IScrewable) return ((IScrewable) screw).screw(mode, player);
		if (screw instanceof IScrewablePrecise) return ((IScrewablePrecise)screw).screw(mode,player,pos);
		return false;
	}

	public boolean handleBlock(SimpleCoordStore pos, EntityPlayer pl)
	{
		if (!isPlayerHoldingScrewdriver(pl)) return false;
		ItemStack is = pl.getHeldItem();
		ScrewdriverHelper helper = getHelper(is);
		if(helper == null) return false;
		ScrewdriverMode mode = helper.getMode();
		TileEntity te = pos.getTileEntity();
		Block b = pos.getBlock();
		int m = pos.getMetadata();
		if(mode == ScrewdriverMode.Dismantle)
		{
			if(te instanceof ITDismantleable)
				if(tardisDismantle((ITDismantleable)te,pos,pl)) return true;
			if(Loader.isModLoaded("CoFHCore"))
				if(dismantle(te,pos,pl) || dismantle(b,pos,pl)) return true;
		}
		else if(mode == ScrewdriverMode.Link)
		{
			if(te instanceof ILinkable)
			{
				if(link(pl, helper, pos))
				{
					toolUsed(is,pl,pos.x,pos.y,pos.z);
				}
			}
			else
				helper.setLinkSCS(null);
			return true;
		}
		else if(mode == ScrewdriverMode.Reconfigure)
		{
			if (b == TardisMod.colorableRoundelBlock)
			{
				TardisDataStore ds = Helper.getDataStore(pos.world);
				if ((ds == null) || ds.hasPermission(pl, TardisPermission.ROUNDEL))
				{
					pos.getWorldObj().setBlock(pos.x, pos.y, pos.z, TardisMod.colorableOpenRoundelBlock, m, 3);
					toolUsed(is, pl, pos.x, pos.y, pos.z);
					return true;
				}
				else
					pl.addChatMessage(CoreTileEntity.cannotModifyRoundel);
			}
			else if(Loader.isModLoaded("CoFHCore"))
			{
				if (te instanceof IReconfigurableFacing)
				{
					if (((IReconfigurableFacing) te).rotateBlock())
					{
						toolUsed(is, pl, pos.x, pos.y, pos.z);
						return true;
					}
				}
			}
		}
		if(screwScrewable(te,mode,pl,pos) || screwScrewable(b, mode, pl, pos)) return true;
		return false;
	}

	private boolean rightClickBlock(ItemStack is, ScrewdriverMode mode, EntityPlayer player, World w)
	{
		if (ServerHelper.isServer())
		{
			MovingObjectPosition hitPos = getMovingObjectPositionFromPlayer(w, player, true);
			if (hitPos == null) return false;
			System.out.println("T");
			SimpleCoordStore pos = new SimpleCoordStore(w,hitPos);
			return handleBlock(pos, player);
		}
		return false;
	}

	public static NBTTagCompound getNewNBT()
	{
		NBTTagCompound temp = new NBTTagCompound();
		temp.setInteger("scMo", 0);
		temp.setInteger("perm", maxPerms);
		return temp;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player)
	{
		System.out.println("T!");
		ScrewdriverHelper helper = getHelper(is);
		if(helper == null) return is;
		ScrewdriverMode mode = helper.getMode();
		if (ServerHelper.isServer() && !player.isSneaking())
		{
			CoreTileEntity core = helper.getLinkedCore();
			if (mode.equals(ScrewdriverMode.Locate))
			{
				if (core != null)
				{
					if (helper.getLinkedDimID() == WorldHelper.getWorldID(player.worldObj))
					{
						player.addChatMessage(new ChatComponentText("[Sonic Screwdriver]You are in the TARDIS"));
					}
					else
					{
						TardisDataStore ds = helper.getLinkedDS();
						if (ds != null)
						{
							TardisTileEntity ext = ds.getExterior();
							if (ext != null)
							{
								if (WorldHelper.getWorldID(ext) != player.worldObj.provider.dimensionId)
									player.addChatMessage(new ChatComponentText("[Sonic Screwdriver]The TARDIS does not appear to be in this dimension"));
								else
									player.addChatMessage(new ChatComponentText("[Sonic Screwdriver]The TARDIS is at [" + ext.xCoord + "," + ext.yCoord + "," + ext.zCoord + "]"));
							}
						}
					}
				}
				else
					player.addChatMessage(new ChatComponentText("[Sonic Screwdriver]The TARDIS could not be located"));
			}
			else if (mode.equals(ScrewdriverMode.Transmat))
			{
				if (core.hasFunction(TardisFunction.TRANSMAT)) core.transmatEntity(player);
			}
			else if (mode.equals(ScrewdriverMode.Recall))
			{
				ConsoleTileEntity con = core.getConsole();
				if ((con != null) && !core.inFlight())
				{
					if (con.setControls(WorldHelper.getWorldID(player.worldObj), (int) Math.floor(player.posX + 1), (int) Math.floor(player.posY), (int) Math.floor(player.posZ), false))
					{
						if (core.takeOff(true, player)) player.addChatMessage(new ChatComponentText("[Sonic Screwdriver]TARDIS inbound"));
					}
					else
						player.addChatMessage(new ChatComponentText("[Sonic Screwdriver]TARDIS recall failed"));
				}
			}
			else
			{
				rightClickBlock(is, mode, player, world);
			}
		}
		else if(ServerHelper.isServer() && player.isSneaking())
		{
			if (mode.equals(ScrewdriverMode.Dismantle) || mode.equals(ScrewdriverMode.Reconfigure))
			{
				if(!rightClickBlock(is, mode, player, world))
					helper.switchMode(player);
			}
			else
				helper.switchMode(player);
		}
		if(mode != helper.getMode())
			notifyMode(helper, player, true);
		return is;
	}

	public boolean link(EntityPlayer pl, ScrewdriverHelper helper, SimpleCoordStore toSCS)
	{
		if(helper == null) return false;
		SimpleCoordStore fromSCS = helper.getLinkSCS();
		if(fromSCS == null)
		{
			helper.setLinkSCS(toSCS);
			return true;
		}
		TileEntity from = fromSCS.getTileEntity();
		TileEntity to = toSCS.getTileEntity();
		if(fromSCS.equals(toSCS) && (from instanceof ILinkable))
		{
			helper.setLinkSCS(null);
			return ((ILinkable)from).unlink(pl);
		}
		if((from instanceof ILinkable) && (to instanceof ILinkable))
		{
			Set<SimpleCoordStore> linked = ((ILinkable)from).getLinked();
			if(linked.contains(toSCS))
				return ((ILinkable)from).unlink(pl,toSCS);
			else
				return ((ILinkable)from).link(pl, toSCS);
		}
		return false;
	}

	@Override
	public boolean hitEntity(ItemStack is, EntityLivingBase hit, EntityLivingBase hitter)
	{
		if (ServerHelper.isClient()) return false;
		ScrewdriverHelper helper = getHelper(is);
		if(helper == null) return false;
		if ((helper.getMode() == ScrewdriverMode.Transmat) && !(hit instanceof EntityPlayer))
		{
			TardisDataStore ds = helper.getLinkedDS();
			if((ds != null) && ds.hasPermission(hitter, TardisPermission.TRANSMAT))
			{
				CoreTileEntity core = helper.getLinkedCore();
				if (core != null) core.transmatEntity(hit);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean doesSneakBypassUse(World w, int x, int y, int z, EntityPlayer player)
	{
		return true;
	}

	@Override
	public void initRecipes()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isUsable(ItemStack is, EntityLivingBase player, int x, int y, int z)
	{
		ScrewdriverHelper helper = getHelper(is);
		if (helper != null)
		{
			ScrewdriverMode mode = helper.getMode();
			if ((mode == ScrewdriverMode.Dismantle) || (mode == ScrewdriverMode.Reconfigure)) return true;
		}
		return false;
	}

	@Override
	public void toolUsed(ItemStack is, EntityLivingBase player, int x, int y, int z)
	{
		if(ServerHelper.isClient()) return;
		float speed = (float) (player.getRNG().nextDouble() * 0.5) + 0.75f;
		SoundHelper.playSound(WorldHelper.getWorldID(player.worldObj), x, y, z, "tardismod:sonic", 0.25F, speed);
		player.swingItem();
	}

	@Override
	public void registerIcons(IIconRegister register)
	{

	}

	@Override
	public boolean canWrench(EntityPlayer pl, int x, int y, int z)
	{
		ScrewdriverHelper helper = getHelper(pl.getHeldItem());
		if (helper == null) return false;
		return helper.getMode() == ScrewdriverMode.Reconfigure;
	}

	@Override
	public void wrenchUsed(EntityPlayer pl, int x, int y, int z)
	{
		toolUsed(null, pl, x, y, z);
	}

	/**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
    @SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
	@Override
	public Multimap getItemAttributeModifiers()
    {
        Multimap multimap = super.getItemAttributeModifiers();
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", 0, 0));
        return multimap;
    }

	@Override
	public boolean canUse(EntityPlayer pl, ItemStack is)
	{
		if(getHelper(is).getMode() == ScrewdriverMode.Reconfigure)
			return true;
		return false;
	}

	private static ScrewdriverHelper getHelper(ItemStack is)
	{
		return ScrewdriverHelperFactory.get(is);
	}

	@Override
	public void damageScrewdriver(EntityPlayer pl, ItemStack is){}

	public static ScrewdriverMode getMode(ItemStack is)
	{
		ScrewdriverHelper helper = getHelper(is);
		if(helper != null)
			return helper.getMode();
		return null;
	}

}
