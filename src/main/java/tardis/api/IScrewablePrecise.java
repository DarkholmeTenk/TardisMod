package tardis.api;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import net.minecraft.entity.player.EntityPlayer;

public interface IScrewablePrecise
{
	public boolean screw(ScrewdriverMode mode, EntityPlayer player, SimpleCoordStore s);
}
