package tardis.common.blocks;

import java.util.EnumSet;

import cpw.mods.fml.common.registry.GameRegistry;
import tardis.common.tileents.BatteryTileEntity;
import tardis.common.tileents.LabTileEntity;
import tardis.common.tileents.extensions.CraftingComponentType;
import tardis.common.tileents.extensions.LabFlag;
import tardis.common.tileents.extensions.LabRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class BatteryBlock extends AbstractBlockContainer
{
	public BatteryBlock()
	{
		super(false,true);
	}

	@Override
	public TileEntity createNewTileEntity(World w, int m)
	{
		return new BatteryTileEntity(m+1);
	}

	@Override
	public void initData()
	{
		setBlockName("Battery");
		setSubNames("Basic","Advanced","Temporal");
	}
	
	@Override
	public ItemStack getIS(int am, int dam)
	{
		ItemStack is = super.getIS(am, dam);
		NBTTagCompound d = new NBTTagCompound();
		d.setInteger("ae",0);
		is.stackTagCompound = d;
		return is;
	}

	@Override
	public void initRecipes()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(1,0), false, "cdc","dkd", "cdc",
				'c', CraftingComponentType.CHRONOSTEEL.getIS(1),
				'd', CraftingComponentType.DALEKANIUM.getIS(1),
				'k', CraftingComponentType.KONTRON.getIS(1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(1,1), false, "cdc","dkd", "cdc",
				'c', CraftingComponentType.CHRONOSTEEL.getIS(1),
				'd', CraftingComponentType.DALEKANIUM.getIS(1),
				'k', getIS(1,0)));
		LabTileEntity.addRecipe(new LabRecipe(new ItemStack[] { getIS(1,1) }, new ItemStack[] { getIS(1,2) }, EnumSet.of(LabFlag.INFLIGHT),200));
	}

}
