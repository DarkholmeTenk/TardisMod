package tardis.client.renderer;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import tardis.TardisMod;
import tardis.common.blocks.TardisAbstractBlock;
import tardis.common.core.TardisOutput;
import tardis.common.tileents.LabTileEntity;

public class LabRenderer extends TardisAbstractObjRenderer
{
	private static IModelCustom model = null;
	private static ResourceLocation texOff	= new ResourceLocation("tardismod","textures/models/lab/off.png");
	private static ResourceLocation texOn	= new ResourceLocation("tardismod","textures/models/lab/on.png");
	
	{
		if(model == null)
		{
			ResourceLocation loc = new ResourceLocation("tardismod","models/lab.obj");
			model = AdvancedModelLoader.loadModel(loc);
		}
	}
	
	@Override
	public TardisAbstractBlock getBlock()
	{
		return TardisMod.labBlock;
	}

	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z)
	{
		if(model == null)
			return;
		if(!(te instanceof LabTileEntity))
			return;
		LabTileEntity lab = (LabTileEntity)te;
		bindTexture(texOff);
		model.renderAll();
	}

}
