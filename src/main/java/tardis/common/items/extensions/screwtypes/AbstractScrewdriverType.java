package tardis.common.items.extensions.screwtypes;

import net.minecraft.nbt.NBTTagCompound;
import tardis.common.core.helpers.ScrewdriverHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class AbstractScrewdriverType implements Comparable<AbstractScrewdriverType>
{
	@SideOnly(Side.CLIENT)
	abstract public void registerClientResources();

	@SideOnly(Side.CLIENT)
	abstract public void render(ScrewdriverHelper helper);

	abstract public String getName();

	@Override
	public int compareTo(AbstractScrewdriverType other)
	{
		return getName().compareTo(other.getName());
	}

	public boolean equals(AbstractScrewdriverType other)
	{
		return getName().equals(other.getName());
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("tname", getName());
	}
}
