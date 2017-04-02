package tardis.client.renderer.tileents;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractObjRenderer;
import io.darkcraft.darkcore.mod.helpers.MathHelper;

import tardis.common.TMRegistry;
import tardis.common.tileents.BatteryTileEntity;

public class BatteryRenderer extends AbstractObjRenderer implements IItemRenderer
{
	private static IModelCustom box = null;
	private static IModelCustom ring = null;
	private static ResourceLocation boxModel = new ResourceLocation("tardismod","models/hollowbox.obj");
	private static ResourceLocation ringModel = new ResourceLocation("tardismod","models/ring.obj");
	private static ResourceLocation boxTexOne = new ResourceLocation("tardismod","textures/models/battery/one.png");
	private static ResourceLocation boxTexTwo = new ResourceLocation("tardismod","textures/models/battery/two.png");
	private static ResourceLocation boxTexThr = new ResourceLocation("tardismod","textures/models/battery/thr.png");
	private static ResourceLocation ringTex = new ResourceLocation("tardismod","textures/models/battery/ring.png");
	private static double[] offsets = new double[360];

	{
		if(box == null)
		{
			box = AdvancedModelLoader.loadModel(boxModel);
			ring = AdvancedModelLoader.loadModel(ringModel);
			for(int i = 0;i<360;i++)
				offsets[i] = Math.sin(Math.PI *(((double)i) / 180));
		}
	}

	@Override
	public AbstractBlock getBlock()
	{
		return TMRegistry.battery;
	}

	private int pr(int in)
	{
		switch(in)
		{
			case 4 : return 11;
			case 3 : return 7;
			case 2 : return 5;
			case 1 : return 3;
			case 0 : return 1;
			default : return 1;
		}
	}

	private void magicFunct(double angle, int mode, int ringNum)
	{
		double baseSC = 0.85;
		double sc = Math.pow(baseSC,ringNum+1);
		if(mode == 0)
		{
			for(int i = 0; i <= ringNum; i++)
			{
				GL11.glPushMatrix();
				if((i%2) == 0)
					GL11.glRotated(angle * pr(i), 1, 0, 0);
				else
					GL11.glRotated(angle * pr(i), 0, 0, 1);
			}
		}
		else if(mode == 1)
		{
			GL11.glPushMatrix();
			int thing = (int)Math.round(angle - (15 * ringNum));
			if(thing < 0)
				thing += 360;
			thing = MathHelper.clamp(thing, 0, 359);
			double am = offsets[thing] * baseSC;
			GL11.glTranslated(0, am, 0);
		}
		else if(mode == 2)
		{
			GL11.glPushMatrix();
			GL11.glRotated(90, 1, 0, 0);
			GL11.glRotated(angle*pr(ringNum), 1, 0, 0);
		}
		GL11.glScaled(sc,sc,sc);
		ring.renderAll();
		if(mode == 0)
		{
			for(int i = ringNum;i >= 0;i--)
				GL11.glPopMatrix();
		}
		else if((mode == 1) || (mode == 2))
			GL11.glPopMatrix();
	}

	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z)
	{
		if(!(te instanceof BatteryTileEntity))
			return;
		BatteryTileEntity bat = (BatteryTileEntity)te;
		int lev = bat.getLevel();
		GL11.glPushMatrix();
			if(lev == 3)
				bindTexture(boxTexThr);
			else if(lev == 2)
				bindTexture(boxTexTwo);
			else
				bindTexture(boxTexOne);
			box.renderAll();
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		bindTexture(ringTex);
		double angle = bat.getAngle();
		int mode = bat.getMode();
		magicFunct(angle,mode,0);
		magicFunct(angle,mode,1);
		if(lev > 1)
			magicFunct(angle,mode,2);
		if(lev > 2)
			magicFunct(angle,mode,3);
		GL11.glPopMatrix();
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
		int lev = item.getItemDamage();
		if(lev == 0)
			Minecraft.getMinecraft().renderEngine.bindTexture(boxTexOne);
		else if(lev == 1)
			Minecraft.getMinecraft().renderEngine.bindTexture(boxTexTwo);
		else
			Minecraft.getMinecraft().renderEngine.bindTexture(boxTexThr);
		GL11.glScaled(0.5,0.5,0.5);
		box.renderAll();
		GL11.glPushMatrix();
		Minecraft.getMinecraft().renderEngine.bindTexture(ringTex);
		magicFunct(0,0,0);
		magicFunct(0,0,1);
		if(lev > 1)
			magicFunct(0,1,2);
		if(lev > 2)
			magicFunct(0,1,3);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}
