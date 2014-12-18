package tardis.client.renderer;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;

public class LandingPadRenderer extends TardisComponentRenderer
{
	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z)
	{
		super.renderBlock(tess, te, x, y, z);
		
	}
}
