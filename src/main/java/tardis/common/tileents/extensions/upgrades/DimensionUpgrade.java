package tardis.common.tileents.extensions.upgrades;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import tardis.TardisMod;

public class DimensionUpgrade extends AbstractUpgrade {

	private int dimID;

	public DimensionUpgrade(int id)
	{
		dimID = id;
	}
	
	private static final ResourceLocation tex = new ResourceLocation("tardismod","textures/models/upgrades/dim.png");
	@Override
	public ResourceLocation getTexture()
	{
		return tex;
	}

	@Override
	public boolean isValid(AbstractUpgrade[] currentUpgrades) {
		for(AbstractUpgrade up : currentUpgrades)
			if(up instanceof DimensionUpgrade)
				if(dimID == ((DimensionUpgrade) up).getDimID())
					return false;
			
		return true;
	}

	@Override
	public ItemStack getIS() {
		return new ItemStack(TardisMod.dimensionUpgradeItems.get(dimID), 1);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setString("id", "dim");
		nbt.setInteger("dimID", dimID);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		dimID = nbt.getInteger("dimID");
	}

	private String[] info;
	@Override
	public String[] getExtraInfo()
	{
		if(info == null)
			info = new String[]{"Dimension ID: " + dimID};
		return info;
	}
	
	public int getDimID(){
		return dimID;
	}
	
	

}
