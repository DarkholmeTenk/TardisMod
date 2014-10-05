package tardis.api;

import net.minecraft.entity.player.EntityPlayer;

public interface IActivatable
{
	public boolean activate(EntityPlayer pl,int side);
}
