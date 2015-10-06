package tardis.client.renderer.tileents;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractObjRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import tardis.TardisMod;
import tardis.common.tileents.MagicDoorTileEntity;

public class MagicDoorTileEntityRenderer extends AbstractObjRenderer
{

	@Override
	public AbstractBlock getBlock()
	{
		return TardisMod.magicDoorBlock;
	}

	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z)
	{
		if(te == null) return;
		MagicDoorTileEntity mte = (MagicDoorTileEntity) te;
		int facing = mte.coords().getMetadata() % 4;
		GL11.glPushMatrix();
		GL11.glRotated(-90 * (facing+1), 0, 1, 0);
		GL11.glScaled(6, 6, 6);
		GL11.glTranslated(-0.5, -0.5, 0);
		tess.startDrawingQuads();
		tess.addVertexWithUV(0, 0, 0, 0, 0);
		tess.addVertexWithUV(0, 1, 0, 0, 1);
		tess.addVertexWithUV(1, 1, 0, 1, 1);
		tess.addVertexWithUV(1, 0, 0, 1, 0);
		tess.draw();
		GL11.glPopMatrix();
	}

}
