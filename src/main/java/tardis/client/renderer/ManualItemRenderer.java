package tardis.client.renderer;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import tardis.common.integration.waila.AbstractWailaProvider;
import tardis.common.integration.waila.DummyWailaAccessor;
import tardis.common.integration.waila.WailaCallback;

public class ManualItemRenderer implements IItemRenderer
{
	private static IModelCustom scr;
	private static ResourceLocation scrTex = new ResourceLocation("tardismod","textures/models/screen.png");
	private static DummyWailaAccessor acc = new DummyWailaAccessor();

	public ManualItemRenderer()
	{
		if(scr == null)
			scr = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/handscreen.obj"));
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

	private void renderText()
	{
		List<String> data = new ArrayList<String>();
		{
			EntityPlayer pl = Minecraft.getMinecraft().thePlayer;
			MovingObjectPosition mop = pl.rayTrace(3, 1);
			Block b = pl.worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ);
			if(b != null)
			{
				int meta = pl.worldObj.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ);
				AbstractWailaProvider provider= WailaCallback.getProvider(b, meta);
				if(provider != null)
				{
					acc.update(pl, mop, b, meta);
					provider.getWailaBody(null, data, acc, null);
				}
			}
		}
		if(data.size() > 0)
		{
			double sx = 0.01;
			int maxSize = 0;
			for(String s : data)
				maxSize = Math.max(maxSize, s.length());
			//sx = 0.25 / maxSize;
			FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
			GL11.glDepthMask(false);
			GL11.glRotated(180, 0, 0, 1);
			GL11.glRotated(90, 0, 1, 0);
			GL11.glTranslated(-0.775, -0.5, -0.1407);
			GL11.glScaled(sx, sx, sx);
			int y = 0;
			for(String s : data)
			{
				List<String> inData = fr.listFormattedStringToWidth(s, 160);
				for(String text : inData)
				{
					fr.drawString(text, 0, y, 16579836);
					y += fr.FONT_HEIGHT;
				}
			}
			GL11.glDepthMask(true);
		}
	}

	private void renderScreen()
	{
		scr.renderAll();
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		GL11.glPushMatrix();
		GL11.glScaled(0.5, 0.5, 0.5);
		if(type.equals(ItemRenderType.EQUIPPED))
		{
			GL11.glRotated(45, 0, 1, 0);
			GL11.glRotated(-65, 1, 0, 0);
			GL11.glRotated(-90, 0, 1, 0);
			GL11.glTranslated(1.45, -0.8, 1);
			GL11.glScaled(1.65, 1.65, 1.65);
		}
		else if(type.equals(ItemRenderType.EQUIPPED_FIRST_PERSON))
		{
			GL11.glRotated(220, 0, 1, 0);
			GL11.glTranslated(1, 2.2, 0);
			GL11.glScaled(1.65, 1.65, 1.65);
		}
		else if(type.equals(ItemRenderType.INVENTORY))
		{
			GL11.glScaled(1.75, 1.75, 1.75);
		}
		GL11.glPushMatrix();
		Minecraft.getMinecraft().renderEngine.bindTexture(scrTex);
		renderScreen();
		GL11.glPopMatrix();
		if(type == ItemRenderType.EQUIPPED_FIRST_PERSON)
		{
			GL11.glPushMatrix();
			renderText();
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();
	}

}
