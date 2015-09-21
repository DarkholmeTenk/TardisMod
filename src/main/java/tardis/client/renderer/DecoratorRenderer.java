package tardis.client.renderer;

import io.darkcraft.darkcore.mod.datastore.Pair;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import tardis.common.items.extensions.DecoratorToolTypes;

public class DecoratorRenderer implements IItemRenderer
{
	private HashMap<Pair<Integer,DecoratorToolTypes>, EntityItem> entMap = new HashMap();
	private IModelCustom decorator;
	private ResourceLocation tex = new ResourceLocation("tardismod","textures/models/decorator.png");

	{
		decorator = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/decorator.obj"));
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

	private EntityItem getEnt(World w, DecoratorToolTypes t)
	{
		Pair p = new Pair(WorldHelper.getWorldID(w), t);
		if(entMap.containsKey(p))
			return entMap.get(p);
		EntityItem ent = new EntityItem(w,0,0,0,t.getIS());
		entMap.put(p, ent);
		return ent;
	}

	private void renderBlock(ItemStack is)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(0, 0.9, 0.125);
		DecoratorToolTypes toolType = DecoratorToolTypes.get(is.getItemDamage());
		Entity ent = getEnt(Minecraft.getMinecraft().theWorld, toolType);
		long time = System.currentTimeMillis();
		int rotTime = 6000;
		int rot = (int) (time % rotTime) / (rotTime / 360);
		RenderManager.instance.renderEntityWithPosYaw(ent, 0, 0, 0, 0, rot);
		GL11.glColor3d(1, 1, 1);
		GL11.glPopMatrix();
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		GL11.glPushMatrix();
		if(type.equals(ItemRenderType.EQUIPPED))
		{
			GL11.glRotated(45, 0, 1, 0);
			GL11.glRotated(-70, 1, 0, 0);
			GL11.glTranslated(0, -1.1, 0.2);
		}
		else if(type.equals(ItemRenderType.EQUIPPED_FIRST_PERSON))
		{
			GL11.glRotated(-45, 0, 1, 0);
			GL11.glTranslated(0, 0.5, 0);
		}
		else if(type.equals(ItemRenderType.INVENTORY))
		{
			GL11.glScaled(0.8, 0.8, 0.8);
			GL11.glTranslated(0, -0.3, 0);
		}
		GL11.glPushMatrix();
			double scale = 0.5;
			GL11.glScaled(scale, scale, scale);
			Minecraft.getMinecraft().renderEngine.bindTexture(tex);
			decorator.renderOnly("Cube_Cube.001");
			GL11.glPushMatrix();
				GL11.glEnable(GL11.GL_BLEND);
				decorator.renderOnly("Cube.001_Cube.000");
				GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
		GL11.glPopMatrix();
		if(type != ItemRenderType.INVENTORY)
			renderBlock(item);
		GL11.glPopMatrix();
	}
}
