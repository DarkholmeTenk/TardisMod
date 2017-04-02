package tardis.client.renderer.tileents;

import java.util.EnumSet;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractObjRenderer;
import io.darkcraft.darkcore.mod.datastore.Colour;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.RenderHelper;

import tardis.common.TMRegistry;
import tardis.common.recipes.LabRecipeRegistry;
import tardis.common.tileents.AdvancedLab;
import tardis.common.tileents.extensions.LabFlag;
import tardis.common.tileents.extensions.LabRecipe;

public class AdvancedLabRenderer extends AbstractObjRenderer
{
	private static ResourceLocation tex	= new ResourceLocation("tardismod","textures/models/lab/newlab.png");
	private RenderItem ri = new RenderItem();
	private FontRenderer fr;
	private Colour red = new Colour(0.9f, 0, 0);
	private Colour gre = new Colour(0, 0.9f, 0);

	@Override
	public AbstractBlock getBlock()
	{
		return TMRegistry.advLab;
	}

	private void renderEI(float yOff, int num, EntityItem ei)
	{
		if(ei == null) return;
		GL11.glPushMatrix();
		GL11.glTranslatef(0, yOff, 0);
		GL11.glRotatef(72*num, 0, 1, 0);
		GL11.glTranslatef(0.5f, -0.2f, 0);
		RenderManager.instance.renderEntityWithPosYaw(ei, 0, 0, 0, 0, ptt);
		GL11.glPopMatrix();
	}

	private void renderItems(AdvancedLab al)
	{
		float yO = 0.25f;
		for(int i = 0; i < 5; i++)
		{
			renderEI(yO, i, al.getEntityItem(i));
			renderEI(-yO, i, al.getEntityItem(5+i));
		}
	}

	private void renderRecipes(AdvancedLab al)
	{
		ri.zLevel = -50.1f;
		GL11.glScalef(1/3f, 1/3f, 1);
		List<LabRecipe> recipes = LabRecipeRegistry.getRecipes();
		int min = 6 * al.page;
		int max = 6 * (al.page + 1);
		for(int i = min; (i < max) && (i < recipes.size()); i++)
		{
			int slot = i - min;
			int row = (i % 6) / 3;
			int col = i % 3;
			LabRecipe rec = recipes.get(i);
			ri.renderItemAndEffectIntoGUI(RenderHelper.getFontRenderer(), RenderManager.instance.renderEngine, rec.displayIS.is(), col*16, row*16);
			//RenderManager.instance.renderEntityWithPosYaw(rec.displayIS.ei(), 0, 0, 0, 45, 0);
		}
		GL11.glPushMatrix();
		GL11.glScalef(0.5f, 0.5f, 0);
		fr.drawString("Prev", 4, 74, Colour.white.asInt, false);
		fr.drawString("Next", 70, 74, Colour.white.asInt, false);
		fr.drawString(String.format("%d/%d", al.page+1, MathHelper.ceil(recipes.size()/6.0)), 40, 74, Colour.white.asInt, false);
		GL11.glPopMatrix();
	}

	private void renderIS(ItemStack is, int x, int y, int count, boolean drawCount)
	{
		if(is == null) return;
		GL11.glPushMatrix();
		ri.renderItemAndEffectIntoGUI(fr, RenderManager.instance.renderEngine, is, x, y);
		if((is.stackSize > 1) || drawCount)
		{
			String str = drawCount ? count +"/"+is.stackSize : ""+is.stackSize;
			Colour c = Colour.white;
			if(drawCount)
				c = count >= is.stackSize ? gre : red;
			GL11.glScalef(0.5f, 0.5f, 1);
			GL11.glTranslatef(0, 0, -5f);
			fr.drawString(str, 1+(x*2), (2*((y+16))) - fr.FONT_HEIGHT, c.asInt, true);
		}
		GL11.glPopMatrix();
	}

	private void renderFlags(EnumSet<LabFlag> flags)
	{
		int i = 0;
		RenderHelper.bindTexture(LabFlag.rl);
		GL11.glColor3f(1, 1, 1);
		int y = 33;
		for(LabFlag flag : flags)
		{
			int x = 16 + ((i++) * 16);
			RenderHelper.uiFace(x, y, 10, 10, 0, flag.uv, true);
		}
	}

	private void renderRecipe(AdvancedLab al, LabRecipe lr)
	{
		GL11.glScalef(0.2f, 0.2f, 1);
		fr.drawString("Out:", 2, 2, Colour.white.asInt, false);
		fr.drawString("In:", 2, 34, Colour.white.asInt, false);
		for(int i = 0; i < lr.dest.length; i++)
			renderIS(lr.dest[i],16 * i, 16, 0, false);
		for(int i = 0; i < lr.source.length; i++)
			renderIS(lr.source[i],16*i,48, al.getStackCount(lr.source[i]), true);
		renderFlags(lr.flags);
		fr.drawString("Back", 2, 66, Colour.white.asInt);
		fr.drawString(al.active ? "Stop" : "Start", 48,66, Colour.white.asInt, false);
		GL11.glScalef(0.75f, 0.75f, 1);
		fr.drawString("AE: " + (lr.energyCost - al.energyBuildup), 38, 3, Colour.white.asInt, false);
	}

	private void renderScreen(AdvancedLab al)
	{
		GL11.glRotatef(270, 0, 1, 0);
		GL11.glRotatef(180, 0, 0, 1);
		GL11.glTranslatef(-0.76f, -0.75f, -1.005f);
		GL11.glScalef(0.09375f, 0.09375f, 0.00005f);
		if(al.selectedRecipe == null)
			renderRecipes(al);
		else
			renderRecipe(al, al.selectedRecipe);
	}

	private static IModelCustom model;
	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z)
	{
		if(fr == null) fr = RenderHelper.getFontRenderer();
		if(model == null) model = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/newlab.obj"));
		RenderHelper.bindTexture(tex);
		int meta = 0;
		if(te != null) meta = te.getWorldObj().getBlockMetadata(x, y, z);
		GL11.glRotatef(90-(90 * meta), 0, 1, 0);
		model.renderAll();
		if(te instanceof AdvancedLab)
		{
			AdvancedLab al = (AdvancedLab) te;
			renderItems(al);
			renderScreen(al);
		}
	}

}
