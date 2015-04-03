package tardis.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IScrewablePrecise
{
	public boolean screw(ScrewdriverMode mode, EntityPlayer player, World w, int x, int y, int z);
}
