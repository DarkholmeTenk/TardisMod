package tardis.common.tileents.extensions.chameleon.tardis;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import tardis.common.items.extensions.screwtypes.AbstractScrewdriverType;
import tardis.common.tileents.TardisTileEntity;
import tardis.common.tileents.extensions.chameleon.IChameleon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class AbstractTardisChameleon implements IChameleon
{
	@Override
	public abstract String getName();

	public String getTextureDir()
	{
		return getName();
	}

	@SideOnly(Side.CLIENT)
	public abstract void render(TardisTileEntity te);

	@Override
	public abstract void registerClientResources();

	@Override
	public int compareTo(IChameleon other)
	{
		return getName().compareTo(other.getName());
	}

	public boolean equals(AbstractScrewdriverType other)
	{
		return getName().equals(other.getName());
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("ct", getName());
	}

	protected void bindTexture(ResourceLocation res)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(res);
	}
}
