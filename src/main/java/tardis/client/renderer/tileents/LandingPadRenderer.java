package tardis.client.renderer.tileents;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;

public class LandingPadRenderer extends ComponentRenderer
{
	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z, float ptt)
	{
		super.renderBlock(tess, te, x, y, z, ptt);
		
	}
}
