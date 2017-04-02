package tardis.common.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

import io.darkcraft.darkcore.mod.abstracts.AbstractItem;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import tardis.TardisMod;
import tardis.common.tileents.extensions.CraftingComponentType;
import tardis.common.tileents.extensions.chameleon.tardis.AbstractTardisChameleon;

public class UpgradeChameleonItem extends AbstractItem
{
	private int size;
	public final AbstractTardisChameleon[] types;

	public UpgradeChameleonItem()
	{
		super(TardisMod.modName);
		setUnlocalizedName("ChameleonUpgrade");
		setCreativeTab(TardisMod.cTab);
		size = TardisMod.tardisChameleonReg.size();
		String[] type = new String[size];
		types = new AbstractTardisChameleon[size];
		for(int i = 0; i < size; i++)
		{
			types[i] = TardisMod.tardisChameleonReg.get(i);
			type[i] = types[i].getName();
		}
		setSubNames(type);
	}

	@Override
	public void initRecipes()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this, 1), false, "ici", "rgb", "ici",
				'i', "ingotIron",
				'c', CraftingComponentType.CHRONOSTEEL.getIS(1),
				'r', "dyeRed",
				'g', "dyeGreen",
				'b', "dyeBlue"));
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer pl)
    {
		if(pl.isSneaking())
		{
			int ord = is.getItemDamage();
			ord = (ord + 1) % size;
			is.setItemDamage(ord);
			if(ServerHelper.isClient())
				ServerHelper.sendString(pl, "Mode: " + StatCollector.translateToLocal(types[ord].getName()));
		}
		return is;
    }

	@Override
	public String getUnlocalizedName(ItemStack is)
	{
		return getUnlocalizedName();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void addInfo(ItemStack is, EntityPlayer player, List infoList)
	{
		if(is == null) return;
		int meta = is.getItemDamage();
		AbstractTardisChameleon cham = (((meta >= 0) && (meta < types.length)) ? types[meta] : TardisMod.tardisChameleonReg.getDefault());
		infoList.add("Mode: " + StatCollector.translateToLocal(cham.getName()));
	}

	@Override
	public String[] getSubNamesForIcons()
	{
		return null;
	}

	@Override
	public String[] getSubNamesForNEI()
	{
		return null;
	}

}
