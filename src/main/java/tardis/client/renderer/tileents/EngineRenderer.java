package tardis.client.renderer.tileents;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractObjRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import tardis.TardisMod;
import tardis.api.TardisPermission;
import tardis.client.renderer.ControlRenderer;
import tardis.common.core.Helper;
import tardis.common.tileents.CoreTileEntity;
import tardis.common.tileents.EngineTileEntity;

public class EngineRenderer extends AbstractObjRenderer
{
	ControlRenderer comps = null;
	IModelCustom engine;

	{
		engine = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/engine.obj"));
	}

	@Override
	public AbstractBlock getBlock()
	{
		return TardisMod.tardisEngineBlock;
	}

	private void renderRight(Tessellator tess, EngineTileEntity eng, CoreTileEntity core)
	{
		if(core != null)
		{
			comps.renderTextScreen(tess, eng, core.getOwner(), 0,  0.8, 2.4, -0.02,  0, 0, 180,  0.3, 0.3, 0.3);
		}
		comps.renderTextScreen(tess, eng, eng.currentPerson, 3,  0.65, 0.6, -0.02, 0, 0, 180,  0.3, 0.3, 0.3);
		comps.renderButton(tess,eng,4, 0.72,0.625,-0.0125, 90,0,0, 0.3,0.3,0.3);
		comps.renderButton(tess,eng,5, 0.72,0.535,-0.0125, 90,0,0, 0.3,0.3,0.3);
		comps.renderLight( tess,eng,6, 0.82,0.6155,-0.0125, 90,0,0, 0.3,0.3,0.3);
		comps.renderButton(tess,eng,7, 0.81,0.535,-0.0125, 90,0,0, 0.3,0.3,0.3);

		comps.renderTextScreen(tess, eng, eng.getConsoleSetting(), 70,  0.65, 0.8, -0.02, 0, 0, 180,  0.3, 0.3, 0.3);
		comps.renderButton(tess,eng,71, 0.72,0.825,-0.0125, 90,0,0, 0.3,0.3,0.3);
		comps.renderButton(tess,eng,72, 0.72,0.735,-0.0125, 90,0,0, 0.3,0.3,0.3);
		comps.renderButton(tess,eng,73, 0.81,0.780,-0.0125, 90,0,0, 0.3,0.3,0.3);
		for(TardisPermission p : TardisPermission.values())
		{
			double d = p.ordinal()/13.0;
			comps.renderPushSwitch(tess, eng, 80+p.ordinal(), 0.625-d,0.47,-0.02, 90,0,0,0.3,0.3,0.3);
		}
	}

	private void renderFront(Tessellator tess, EngineTileEntity eng, CoreTileEntity core)
	{
		double base  = 0.10625;
		double delta = 0.2;
		comps.renderGauge(tess,eng,30,  0.995,0.875,base+(3*delta), 180,-90,0, 0.6,0.6,0.6);
		comps.renderGauge(tess,eng,23,	0.995,0.7,base, 180,-90,0, 0.6,0.6,0.6);
		comps.renderGauge(tess,eng,22,	0.995,0.7,base+delta, 180,-90,0, 0.6,0.6,0.6);
		comps.renderGauge(tess,eng,21,	0.995,0.7,base+(2*delta), 180,-90,0, 0.6,0.6,0.6);
		comps.renderGauge(tess,eng,20,	0.995,0.7,base+(3*delta), 180,-90,0, 0.6,0.6,0.6);

		base += 0.07375;
		comps.renderButton(tess,eng,13,	1.035,0.45,base,			0,0,90, 0.6,0.6,0.6);
		comps.renderButton(tess,eng,12,	1.035,0.45,base+delta,		0,0,90, 0.6,0.6,0.6);
		comps.renderButton(tess,eng,11,	1.035,0.45,base+(2*delta),	0,0,90, 0.6,0.6,0.6);
		comps.renderButton(tess,eng,10,	1.035,0.45,base+(3*delta),	0,0,90, 0.6,0.6,0.6);
	}

	private void renderLeft(Tessellator tess, EngineTileEntity eng, CoreTileEntity core)
	{
		comps.renderPushSwitch(tess, eng, 60, 0.3, 0.788, 1.045, -90, 0, 0, 0.5, 0.5, 0.5);
		comps.renderScrewdriverHolder(tess, eng, 0.6, 0.5, 1.05, -90, 0, 0, 0.5, 0.5, 0.5);
		comps.renderScrewdriver(tess, eng, 0, 0.6, 0.5, 1.1, -90, 0, 0, 0.5, 0.5, 0.5);
		comps.renderButton(tess,eng, 41, 0.3, 0.388, 1.02, -90, 0, 0, 0.3, 0.3, 0.3);
		comps.renderButton(tess,eng, 44, 0.3, 0.488, 1.02, -90, 0, 0, 0.3, 0.3, 0.3);
		comps.renderButton(tess,eng, 45, 0.3, 0.588, 1.02, -90, 0, 0, 0.3, 0.3, 0.3);
		comps.renderLight(tess, eng, 51, 0.4, 0.4, 1.02, -90, 0, 0, 0.3, 0.3, 0.3);
		comps.renderLight(tess, eng, 54, 0.4, 0.5, 1.02, -90, 0, 0, 0.3, 0.3, 0.3);
		comps.renderLight(tess, eng, 55, 0.4, 0.6, 1.02, -90, 0, 0, 0.3, 0.3, 0.3);
	}

	private void renderBack(Tessellator tess, EngineTileEntity eng, CoreTileEntity core)
	{

	}

	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z)
	{
		if(comps == null)
			comps = new ControlRenderer(func_147498_b(),field_147501_a.field_147553_e);
		if(te instanceof EngineTileEntity)
		{
			CoreTileEntity core = Helper.getTardisCore(te.getWorldObj());
			EngineTileEntity eng = (EngineTileEntity)te;
			GL11.glScaled(2, 2, 2);
			/**/
			GL11.glPushMatrix();
			GL11.glTranslated(0, -1.5, 0);
			bindTexture(new ResourceLocation("tardismod","textures/models/engine.png"));
			engine.renderAll();
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glTranslated(-0.5, -0.5, -0.5);
			renderRight(tess,eng,core);
			renderFront(tess,eng,core);
			renderLeft (tess,eng,core);
			renderBack (tess,eng,core);
			GL11.glPopMatrix();
		}
	}

}
