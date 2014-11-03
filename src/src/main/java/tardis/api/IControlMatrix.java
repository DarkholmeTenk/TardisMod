package tardis.api;

public interface IControlMatrix
{
	public double getControlState(int controlID,boolean wobble);
	
	public double getControlState(int controlID);
	
	public double getControlHighlight(int controlID);
	
	public boolean hasScrewdriver(int slot);
	
	public double[] getColorRatio(int controlID);
	
	public TardisScrewdriverMode getScrewMode(int slot);
}
