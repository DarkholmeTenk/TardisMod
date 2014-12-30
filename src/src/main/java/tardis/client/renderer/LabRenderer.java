package tardis.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import tardis.TardisMod;
import tardis.client.renderer.model.StickModel;
import tardis.common.blocks.AbstractBlock;
import tardis.common.tileents.LabTileEntity;

public class LabRenderer extends AbstractObjRenderer implements IItemRenderer
{
	private static IModelCustom model = null;
	private static StickModel stickModel = new StickModel();
	private static ResourceLocation texOff	= new ResourceLocation("tardismod","textures/models/lab/off.png");
	private static ResourceLocation texOn	= new ResourceLocation("tardismod","textures/models/lab/on.png");
	private static ResourceLocation stickOff = new ResourceLocation("tardismod","textures/models/lab/stickOff.png"); 
	private static ResourceLocation stickOn  = new ResourceLocation("tardismod","textures/models/lab/stickOn.png"); 
	
	{
		if(model == null)
		{
			ResourceLocation loc = new ResourceLocation("tardismod","models/lab.obj");
			model = AdvancedModelLoader.loadModel(loc);
		}
	}
	
	@Override
	public AbstractBlock getBlock()
	{
		return TardisMod.labBlock;
	}

	private void renderStick(LabTileEntity lab, double rot)
	{
		if(lab == null)
			return;
		double[] stickPos = lab.getStick();
		GL11.glPushMatrix();
		bindTexture(lab.isGeneratingEnergy(null, null) ? stickOn : stickOff);
		double scaler = 0.9;
		GL11.glRotated(rot, 0, 1, 0);
		GL11.glTranslated(stickPos[0]*scaler, -0.2, stickPos[1]*scaler);
		GL11.glTranslated(-0.025, 0, -0.025);
		stickModel.render(null, 0, 0, 0, 0, 0, 0.06125F);
		GL11.glPopMatrix();
	}

	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z)
	{
		if(model == null)
			return;
		if(te instanceof LabTileEntity && ((LabTileEntity)te).isGeneratingEnergy(null, null))
			bindTexture(texOn);
		else
			bindTexture(texOff);
		model.renderAll();
		if(te instanceof LabTileEntity)
		{
			renderStick((LabTileEntity)te,0);
			renderStick((LabTileEntity)te,90);
			renderStick((LabTileEntity)te,180);
			renderStick((LabTileEntity)te,270);
		}
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return true;
	}
	
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		GL11.glPushMatrix();
		//This line actually rotates the renderer.
		GL11.glRotated(180, 0,0,1);
		if(type.equals(ItemRenderType.EQUIPPED))
		{
			GL11.glRotatef(90F,1F,0F,1F);
			GL11.glRotatef(-25F,1F,0F,1F);
			GL11.glRotatef(45F,0F,1F,0F);
			GL11.glTranslatef(-0.75F, 0.8F, -0F);
		}
		else if(type.equals(ItemRenderType.EQUIPPED_FIRST_PERSON))
		{
			GL11.glRotatef(-35F,0F,1F,0F);
			GL11.glRotatef(-15F,0F,0F,1F);
			GL11.glTranslatef(0F, -1F, -0F);
		}
		GL11.glRotated(180, 1, 0, 0);
		Minecraft.getMinecraft().renderEngine.bindTexture(texOn);
		GL11.glScaled(0.5,0.5,0.5);
		model.renderAll();
		GL11.glPopMatrix();
	}

}
