package tardis.client.renderer.tileents;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractObjRenderer;

import java.util.HashMap;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import tardis.TardisMod;
import tardis.common.tileents.ManualTileEntity;
import tardis.common.tileents.extensions.ManualPageTree;

public class ManualRenderer extends AbstractObjRenderer
{
	private static IModelCustom							model		= AdvancedModelLoader.loadModel(new ResourceLocation("tardismod", "models/manualscreen.obj"));
	private static HashMap<String, ResourceLocation>	resources	= new HashMap();

	public static final double textSize = 0.03;

	@Override
	public AbstractBlock getBlock()
	{
		return TardisMod.manualBlock;
	}

	private ResourceLocation getRL(String s)
	{
		if(resources.containsKey(s))
			return resources.get(s);
		resources.put(s, new ResourceLocation("tardismod","textures/models/manual/"+s+".png"));
		return resources.get(s);
	}

	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z)
	{
		if(!(te instanceof ManualTileEntity)) return;
		ManualTileEntity mte = (ManualTileEntity)te;
		World w = mte.getWorldObj();
		int meta = w.getBlockMetadata(x, y, z);
		GL11.glPushMatrix();
		GL11.glRotated(-90*meta, 0, 1, 0);
		bindTexture(getRL(mte.getPage().tex));
		model.renderAll();
		renderText(mte);
		GL11.glPopMatrix();
	}

	private void renderText(ManualTileEntity mte)
	{
		GL11.glPushMatrix();
		GL11.glDepthMask(false);
		GL11.glTranslated(0, 0, -1.001);

		List<String> strings = ManualPageTree.topTree.getString(mte.getPage(), 0);
		GL11.glTranslated(4.75, 2.75, 0);
		GL11.glScaled(textSize, textSize, textSize);
		GL11.glRotated(180, 0, 0, 1);
		for(String s : strings)
		{
			fr.drawString(s, 0, 0, 16579836);
			GL11.glTranslated(0, 10, 0);
		}

		GL11.glDepthMask(true);
		GL11.glPopMatrix();
	}

}
