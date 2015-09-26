package tardis.common.tileents.extensions.chameleon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IChameleon extends Comparable<IChameleon>
{
	public String getName();

	@SideOnly(Side.CLIENT)
	public void registerClientResources();
}
