package tardis.api;

import net.minecraft.entity.player.EntityPlayer;
import tardis.common.core.helpers.ScrewdriverHelper;

public interface IControlMatrix
{
	public double getControlState(int controlID,boolean wobble);

	public double getControlState(int controlID);

	public double getControlHighlight(int controlID);

	public double[] getColorRatio(int controlID);

	public void activateControl(EntityPlayer player, int controlID);

	public ScrewdriverHelper getScrewHelper(int slot);
}
