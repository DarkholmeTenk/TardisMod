package tardis.api;

import net.minecraft.entity.player.EntityPlayer;

public interface IControlMatrix
{
	public double getControlState(int controlID,boolean wobble);
	
	public double getControlState(int controlID);
	
	public double getControlHighlight(int controlID);
	
	public boolean hasScrewdriver(int slot);
	
	public double[] getColorRatio(int controlID);
	
	public void activateControl(EntityPlayer player, int controlID);
	
	public TardisScrewdriverMode getScrewMode(int slot);
}
