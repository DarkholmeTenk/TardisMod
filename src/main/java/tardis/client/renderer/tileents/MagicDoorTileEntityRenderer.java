package tardis.client.renderer.tileents;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractObjRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import tardis.TardisMod;
import tardis.client.renderer.portal.PortalWorld;
import tardis.client.renderer.portal.PortalWorldRegistry;
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
		MagicDoorTileEntity mdte = (MagicDoorTileEntity) te;
		PortalWorld w = PortalWorldRegistry.getWorld(mdte);
		w.render();
	}

}
