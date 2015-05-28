package tardis.client.renderer;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractObjRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import tardis.TardisMod;

public class SummonerRenderer extends AbstractObjRenderer implements IItemRenderer
{
	private static IModelCustom blob = null;
	private static final ResourceLocation tex = new ResourceLocation("tardismod","textures/models/blob.png");

	{
		if(blob == null)
			blob = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/rounded.obj"));
	}

	@Override
	public AbstractBlock getBlock()
	{
		return TardisMod.summonerBlock;
	}

	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z)
	{
		bindTexture(tex);
		blob.renderAll();
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
		GL11.glRotatef(180F, 0F, 0, 1F);
		GL11.glScaled(0.7, 0.7, 0.7);
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
		else if(type == ItemRenderType.INVENTORY)
		{
			GL11.glScaled(0.75, 0.75, 0.75);
		}
		Minecraft.getMinecraft().renderEngine.bindTexture(tex);
		blob.renderAll();
		GL11.glPopMatrix();
	}

}
