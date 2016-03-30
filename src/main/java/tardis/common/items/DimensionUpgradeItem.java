package tardis.common.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.darkcraft.darkcore.mod.DarkcoreMod;
import io.darkcraft.darkcore.mod.abstracts.AbstractItem;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.DimensionManager;
import tardis.TardisMod;

public class DimensionUpgradeItem extends AbstractItem {

	private final int 		dimID;
	private IIcon			iconBuffer;

	public DimensionUpgradeItem(int id) {
		super(TardisMod.modName);
		setUnlocalizedName("DimUnlock" + id);
		setCreativeTab(TardisMod.cTab);
		dimID = id;
		this.setTextureName("tardismod:" + "UpgradeDim");
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void addInfo(ItemStack is, EntityPlayer player, List infoList)
	{
		if(is == null) return;
		if(DimensionManager.getWorld(dimID) == null) return;
		infoList.add("Unlocks " + DimensionManager.getWorld(dimID).provider.getDimensionName() + " (" + dimID + ")");
	}
	
	public String getItemStackDisplayName(ItemStack p_77653_1_)
    {
		return "Dimensional key";
    }
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister ir)
	{
			iconBuffer = ir.registerIcon("tardismod:" + "UpgradeDim");
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int damage)
	{
		return iconBuffer;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
    protected String getIconString()
    {
        return "tardismod:" + "UpgradeDim";
    }
	
	@Override
	public void initRecipes() {

	}

}
