package tardis.common.items;

import java.util.EnumSet;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import io.darkcraft.darkcore.mod.abstracts.AbstractItem;

import cpw.mods.fml.common.registry.GameRegistry;
import tardis.Configs;
import tardis.TardisMod;
import tardis.common.TMRegistry;
import tardis.common.recipes.LabRecipeRegistry;
import tardis.common.tileents.extensions.CraftingComponentType;
import tardis.common.tileents.extensions.LabFlag;
import tardis.common.tileents.extensions.LabRecipe;

public class CraftingComponentItem extends AbstractItem
{

	private static String[] names;

	static
	{
		names = new String[CraftingComponentType.values().length];
		CraftingComponentType[] types = CraftingComponentType.values();
		for(int i = 0;i<names.length;i++)
			names[i] = types[i].name;
	}

	public CraftingComponentItem()
	{
		super(TardisMod.modName);
		setUnlocalizedName("CraftingComponent");
		setSubNames(names);
		setCreativeTab(TardisMod.cTab);
	}

	@Override
	public void initRecipes()
	{
		OreDictionary.registerOre("ingotChronosteel", CraftingComponentType.CHRONOSTEEL.getIS(1));
		OreDictionary.registerOre("ingotDalekanium", CraftingComponentType.DALEKANIUM.getIS(1));
		OreDictionary.registerOre("gemKontron", CraftingComponentType.KONTRON.getIS(1));

		if(Configs.kontronCraftable)
			GameRegistry.addRecipe(new ShapedOreRecipe(CraftingComponentType.KONTRON.getIS(1),false,"gdg","ded","gdg",
					'g', Blocks.glass,
					'd', Items.diamond,
					'e', Items.ender_eye));

		LabRecipe currentRecipe = new LabRecipe("tm.chronosteel",
				new ItemStack[] { new ItemStack(Items.iron_ingot)},
				new ItemStack[]{CraftingComponentType.CHRONOSTEEL.getIS(1)},
				EnumSet.of(LabFlag.INFLIGHT),
				100);
		LabRecipeRegistry.addRecipe(currentRecipe);
		currentRecipe = new LabRecipe("tm.dalekanium",
				new ItemStack[] { new ItemStack(Items.gold_ingot) },
				new ItemStack[] { CraftingComponentType.DALEKANIUM.getIS(1) },
				EnumSet.noneOf(LabFlag.class),
				100);
		LabRecipeRegistry.addRecipe(currentRecipe);
		currentRecipe = new LabRecipe("tm.kontron",
				new ItemStack[] { new ItemStack(Items.diamond), new ItemStack(Items.ender_pearl) },
				new ItemStack[] { CraftingComponentType.KONTRON.getIS(1) },
				EnumSet.noneOf(LabFlag.class),
				100);
		LabRecipeRegistry.addRecipe(currentRecipe);

		GameRegistry.addRecipe(new ShapedOreRecipe(CraftingComponentType.UPGRADE.getIS(1), false, "gcg", "gig", "gcg",
				'g', "nuggetGold",
				'i', "ingotIron",
				'c', CraftingComponentType.CHRONOSTEEL.getIS(1)));

		LabRecipeRegistry.addRecipe(new LabRecipe("tm.upgrade",
				new ItemStack[] { new ItemStack(TMRegistry.upgradeItem,1,0), CraftingComponentType.CHRONOSTEEL.getIS(1) },
				new ItemStack[] { CraftingComponentType.UPGRADE.getIS(1) },
				EnumSet.noneOf(LabFlag.class),
				100));

		if(Configs.kontronRarity > 0)
		{
			ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(CraftingComponentType.KONTRON.getIS(1),1,1,Configs.kontronRarity));
			ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(new WeightedRandomChestContent(CraftingComponentType.KONTRON.getIS(1),1,1,Configs.kontronRarity));
			ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(new WeightedRandomChestContent(CraftingComponentType.KONTRON.getIS(1),1,1,Configs.kontronRarity));
		}
	}

}
