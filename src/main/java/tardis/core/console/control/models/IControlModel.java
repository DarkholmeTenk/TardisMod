package tardis.core.console.control.models;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IControlModel
{
	public double regularX();

	public double regularY();

	public double xAngle();

	@SideOnly(Side.CLIENT)
	public void render(float state);
}
