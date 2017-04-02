package tardis.common.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.oredict.ShapedOreRecipe;

import io.darkcraft.darkcore.mod.abstracts.AbstractItem;
import io.darkcraft.darkcore.mod.datastore.SimpleDoubleCoordStore;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import tardis.Configs;
import tardis.TardisMod;
import tardis.api.TardisPermission;
import tardis.common.TMRegistry;
import tardis.common.core.helpers.Helper;
import tardis.common.dimension.TardisDataStore;
import tardis.common.tileents.CoreTileEntity;
import tardis.common.tileents.extensions.CraftingComponentType;

public class KeyItem extends AbstractItem
{

	public KeyItem()
	{
		super(TardisMod.modName);
		setUnlocalizedName("TardisKey");
		setCreativeTab(TardisMod.cTab);
		setMaxStackSize(1);
	}

	public static String getOwnerName(ItemStack is)
	{
		if (is != null)
		{
			if (is.getItem() instanceof KeyItem)
			{
				NBTTagCompound data = is.stackTagCompound;
				if (data != null) return data.getString("keyOwner");
			}
		}
		return null;
	}

	public static void setOwnerName(ItemStack is, String ownerName)
	{
		if (is.stackTagCompound == null) is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setString("keyOwner", ownerName);
	}

	@Override
	public void addInfo(ItemStack is, EntityPlayer player, List infoList)
	{
		if (is != null)
		{
			String owner = getOwnerName(is);
			if (owner != null)
				infoList.add("Owner: " + owner);
			else
			{
				infoList.add("No owner assigned");
				infoList.add("Right click to set");
			}
		}
	}

	@Override
	public boolean doesSneakBypassUse(World w, int x, int y, int z, EntityPlayer player)
	{
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player)
	{
		if ((is != null) && (getOwnerName(is) == null) && !(player instanceof FakePlayer)) setOwnerName(is, ServerHelper.getUsername(player));

		if(ServerHelper.isServer())
		{
			if (!(player instanceof FakePlayer) && !TardisMod.plReg.hasTardis(getOwnerName(is)) && ServerHelper.getUsername(player).equals(getOwnerName(is)))
			{
				if(Helper.summonNewTardis(player))
					player.addChatMessage(new ChatComponentText("[TARDIS KEY]The key feels warm"));
			}
			else
			{
				String on = getOwnerName(is);
				if((on != null) && !on.isEmpty())
				{
					Integer dimensionID = TardisMod.plReg.getDimension(on);
					if(dimensionID == null) return is;
					CoreTileEntity core = Helper.getTardisCore(dimensionID);
					TardisDataStore ds = Helper.getDataStore(dimensionID);
					if ((core != null) && (ds != null))
					{
						if(!Helper.isTardisWorld(world))
						{
							if (ServerHelper.getUsername(player).equals(on) && !ds.hasValidExterior() && !core.inFlight())
							{
									Helper.summonOldTardis(player);
									player.addChatMessage(new ChatComponentText("[TARDIS KEY]The key feels warm"));
									return is;
							}
							if(ds.hasPermission(player, TardisPermission.FLY) && player.isSneaking())
							{
								SimpleDoubleCoordStore extPos = core.getPosition().getCenter();
								if(extPos.distance(player) < 8)
								{
									if(!core.inFlight())
											core.takeOffUncoordinated(player);
									else if(core.inAbortableFlight() && !core.inCoordinatedFlight())
											core.attemptToLand();
								}
							}
						}
					}
				}
			}
		}
		return is;
	}

	@Override
	public void initRecipes()
	{
		if (Configs.keyCraftable)
		{
			if (Configs.keyReqKontron)
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TMRegistry.keyItem, 1), true, " i ", " ik", " ii", 'i', Items.iron_ingot, 'k', CraftingComponentType.KONTRON.getIS(1)));
			else
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TMRegistry.keyItem, 1), true, " i ", " i ", " ii", 'i', Items.iron_ingot));
		}
	}

}
