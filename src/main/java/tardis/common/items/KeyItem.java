package tardis.common.items;

import io.darkcraft.darkcore.mod.abstracts.AbstractItem;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.dimension.TardisDataStore;
import tardis.common.tileents.CoreTileEntity;
import tardis.common.tileents.extensions.CraftingComponentType;
import cpw.mods.fml.common.registry.GameRegistry;

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
				if (data != null)
					return data.getString("keyOwner");
			}
		}
		return null;
	}

	public static void setOwnerName(ItemStack is, String ownerName)
	{
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
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
		if ((is != null) && (getOwnerName(is) == null))
			setOwnerName(is, ServerHelper.getUsername(player));

		if (ServerHelper.isServer() && !TardisMod.plReg.hasTardis(getOwnerName(is))
				&& ServerHelper.getUsername(player).equals(getOwnerName(is)))
		{
			Helper.summonNewTardis(player);
			player.addChatMessage(new ChatComponentText("[TARDIS KEY]The key feels warm"));
		}
		else if (ServerHelper.isServer() && ServerHelper.getUsername(player).equals(getOwnerName(is)))
		{
			CoreTileEntity core = TardisMod.plReg.getCore(player);
			TardisDataStore ds = TardisMod.plReg.getDataStore(player);
			if ((core != null) && (ds != null))
			{
				if (!ds.hasValidExterior() && !core.inFlight())
				{
					if(!Helper.isTardisWorld(world))
					{
						Helper.summonOldTardis(player);
						player.addChatMessage(new ChatComponentText("[TARDIS KEY]The key feels warm"));
					}
				}
			}
		}
		return is;
	}

	@Override
	public void initRecipes()
	{
		if(TardisMod.keyCraftable)
		{
			if(TardisMod.keyReqKontron)
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TardisMod.keyItem, 1), true, " i ", " ik", " ii", 'i',
						Items.iron_ingot, 'k', CraftingComponentType.KONTRON.getIS(1)));
			else
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TardisMod.keyItem, 1), true, " i ", " i ", " ii", 'i',
						Items.iron_ingot));
		}
	}

}
