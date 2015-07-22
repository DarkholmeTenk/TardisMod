package tardis.client.renderer.tileents;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractObjRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import tardis.TardisMod;
import tardis.client.TardisClientProxy;
import tardis.client.renderer.model.TardisModel;
import tardis.common.TardisProxy;
import tardis.common.tileents.TardisTileEntity;

public class TardisRenderer extends AbstractObjRenderer implements IItemRenderer
{
	TardisModel model;
	private static IModelCustom tardis;


	public TardisRenderer()
	{
		model = new TardisModel();
	}

	@Override
	public void renderBlock(Tessellator tesselator, TileEntity te, int x, int y, int z)
	{
		if(tardis == null)
			tardis = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/tardis.obj"));
		World world = te.getWorldObj();
		TardisTileEntity tte = null;
		if((te != null) && (te instanceof TardisTileEntity))
			tte = (TardisTileEntity) te;
		int dir = world.getBlockMetadata(x,y,z);
		GL11.glScaled(1.1, 0.95, 1.1);
		if((dir < 4) && (tte != null))
		{
			GL11.glPushMatrix();
			//This line actually rotates the renderer.
			//GL11.glTranslatef(0.5F, 0, 0.5F);
			GL11.glRotatef((dir * (-90F)) + 90, 0F, 1F, 0F);
			//GL11.glRotatef(180F, 0F, 0, 1F);
			GL11.glTranslatef(0F, 1F, 0F);
			if(TardisMod.proxy instanceof TardisClientProxy)
				bindTexture(((TardisClientProxy)TardisMod.proxy).getSkin(field_147501_a.field_147553_e,tte));
			GL11.glColor4f(1F, 1F, 1F, tte.getTransparency());
			//model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			tardis.renderAll();
			GL11.glPopMatrix();
		}
	}

	@Override
	public AbstractBlock getBlock()
	{
		return TardisMod.tardisBlock;
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
		//GL11.glRotatef(180F, 0F, 0, 1F);
		GL11.glScaled(0.35, 0.35, 0.35);
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
			GL11.glScaled(0.8, 0.8, 0.8);
			GL11.glTranslated(0, -0.5, 0);
		}
		render(item);
		GL11.glPopMatrix();
	}

	public void render(ItemStack item)
	{
		if(tardis == null)
			tardis = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/tardis.obj"));
		GL11.glPushMatrix();
		Minecraft.getMinecraft().renderEngine.bindTexture(TardisProxy.defaultSkin);
		//model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		tardis.renderAll();
		GL11.glPopMatrix();
	}
}
