package tardis.common.tileents.extensions.upgrades;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import tardis.api.TardisUpgradeMode;
import tardis.client.renderer.model.StickModel;
import tardis.common.dimension.TardisDataStore;
import tardis.common.dimension.damage.TardisDamageType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class AbstractUpgrade
{
	public SimpleCoordStore enginePos;
	public static final ResourceLocation brokenTexture = new ResourceLocation("tardismod","textures/models/upgrades/broken.png");
	StickModel sm = new StickModel();
	@SideOnly(Side.CLIENT)
	public abstract ResourceLocation getTexture();

	@SideOnly(Side.CLIENT)
	public void render()
	{
		ResourceLocation rl = getTexture();
		if(rl != null)
			Minecraft.getMinecraft().getTextureManager().bindTexture(getTexture());
		sm.render(null, 0, 0, 0,0,0, 0.06125F);
	}

	/**
	 * Handles installing upgrades to absorb amounts of damage
	 * @param dam		The type of damage to absorb
	 * @param amount	The amount of damage to absorb
	 * @return			The amount of damage which has not been absorbed
	 */
	public int takeDamage(TardisDamageType dam, int amount)
	{
		return amount;
	}

	/**
	 * Handles installing upgrades to increase the level of an upgrade mode
	 * @param mode	the mode to upgrade
	 * @param ds	the tardis data store (in case you want to do any extra stuff)
	 * @return		the number to modify the upgrade mode's level by
	 */
	public int getUpgradeEffect(TardisUpgradeMode mode, TardisDataStore ds)
	{
		return 0;
	}

	public void tick(int tt){}

	public abstract boolean isValid(AbstractUpgrade[] currentUpgrades);

	/**
	 * @return an itemstack representing this upgrade instance
	 */
	public abstract ItemStack getIS();

	public void setEnginePos(SimpleCoordStore scs)
	{
		enginePos = scs;
	}

	public abstract void writeToNBT(NBTTagCompound nbt);

	public abstract void readFromNBT(NBTTagCompound nbt);

	public abstract String[] getExtraInfo();
}
