package tardis.client.renderer.tileents;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractObjRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import tardis.TardisMod;
import tardis.client.renderer.model.TardisModel;
import tardis.common.tileents.TardisTileEntity;
import tardis.common.tileents.extensions.chameleon.tardis.AbstractTardisChameleon;

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
		World world = te.getWorldObj();
		if((te == null) || !(te instanceof TardisTileEntity)) return;
		TardisTileEntity tte = (TardisTileEntity) te;
		AbstractTardisChameleon cham = tte.getChameleon();
		int dir = world.getBlockMetadata(x,y,z);
		GL11.glScaled(1.1, 0.95, 1.1);
		if((dir < 4) && (tte != null))
		{
			GL11.glPushMatrix();
			GL11.glRotatef((dir * (-90F)) + 90, 0F, 1F, 0F);
			GL11.glTranslatef(0F, 0.95F, 0F);
			GL11.glColor4f(1F, 1F, 1F, tte.getTransparency());
			cham.render(tte);
			//tardis.renderAll();
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
		GL11.glPushMatrix();
		TardisMod.tardisChameleonReg.getDefault().render(null);
		GL11.glPopMatrix();
	}
}
