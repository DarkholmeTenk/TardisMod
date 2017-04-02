package tardis.api;

import net.minecraft.entity.player.EntityPlayer;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;

import tardis.common.core.helpers.ScrewdriverHelper;

public interface IScrewablePrecise
{
	public boolean screw(ScrewdriverHelper helper, ScrewdriverMode mode, EntityPlayer player, SimpleCoordStore s);
}
