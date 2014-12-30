package tardis.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;

public class LandingPadRenderer extends ComponentRenderer
{
	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z)
	{
		super.renderBlock(tess, te, x, y, z);
		
	}
}
