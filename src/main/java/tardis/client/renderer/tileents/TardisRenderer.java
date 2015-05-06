package tardis.client.renderer.tileents;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractBlockRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import tardis.TardisMod;
import tardis.client.TardisClientProxy;
import tardis.client.renderer.model.TardisModel;
import tardis.common.tileents.TardisTileEntity;

public class TardisRenderer extends AbstractBlockRenderer
{
	TardisModel model;
	
	public TardisRenderer()
	{
		model = new TardisModel();
	}
	
	public void renderBlock(Tessellator tesselator, TileEntity te, int x, int y, int z)
	{
		World world = te.getWorldObj();
		TardisTileEntity tte = null;
		if(te != null && te instanceof TardisTileEntity)
			tte = (TardisTileEntity) te;
		int dir = world.getBlockMetadata(x,y,z);
		if(dir < 4 && tte != null)
		{
			GL11.glPushMatrix();
			//This line actually rotates the renderer.
			GL11.glTranslatef(0.5F, 0, 0.5F);
			GL11.glRotatef(dir * (-90F), 0F, 1F, 0F);
			GL11.glRotatef(180F, 0F, 0, 1F);
			GL11.glTranslatef(0F, -1.5F, 0F);
			if(TardisMod.proxy instanceof TardisClientProxy)
				bindTexture(((TardisClientProxy)TardisMod.proxy).getSkin(this.field_147501_a.field_147553_e,tte));
			GL11.glColor4f(1F, 1F, 1F, tte.getTransparency());
			model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			GL11.glPopMatrix();
		}
	}

	@Override
	public AbstractBlock getBlock()
	{
		return TardisMod.tardisBlock;
	}
}
