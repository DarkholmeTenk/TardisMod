package tardis.api;

import net.minecraft.entity.player.EntityPlayer;
import tardis.common.core.helpers.ScrewdriverHelper;

public interface IScrewable
{
	public boolean screw(ScrewdriverHelper helper, ScrewdriverMode mode, EntityPlayer player);
}
