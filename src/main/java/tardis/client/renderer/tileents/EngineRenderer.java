package tardis.client.renderer.tileents;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractObjRenderer;

import tardis.api.TardisFunction;
import tardis.api.TardisPermission;
import tardis.client.renderer.ControlRenderer;
import tardis.common.TMRegistry;
import tardis.common.core.helpers.Helper;
import tardis.common.dimension.TardisDataStore;
import tardis.common.dimension.damage.TardisDamageSystem;
import tardis.common.tileents.CoreTileEntity;
import tardis.common.tileents.EngineTileEntity;
import tardis.common.tileents.extensions.upgrades.AbstractUpgrade;

public class EngineRenderer extends AbstractObjRenderer
{
	ControlRenderer comps = null;
	IModelCustom engine;
	ResourceLocation engineTex;
	IModelCustom enginePanel;
	ResourceLocation panelTex;
	IModelCustom bubble;
	ResourceLocation bubbleUp;
	ResourceLocation bubbleDown;

	{
		engine = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/engine.obj"));
		engineTex = new ResourceLocation("tardismod","textures/models/engine.png");
		enginePanel = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/enginepanel.obj"));
		panelTex = new ResourceLocation("tardismod","textures/models/enginepanel.png");
		bubble = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/breakableTemp.obj"));
		bubbleUp = new ResourceLocation("tardismod","textures/models/capup.png");
		bubbleDown = new ResourceLocation("tardismod","textures/models/cap.png");
	}

	@Override
	public AbstractBlock getBlock()
	{
		return TMRegistry.tardisEngineBlock;
	}

	private void renderRight(Tessellator tess, EngineTileEntity eng, CoreTileEntity core, TardisDataStore ds)
	{
		if(core != null)
		{
			comps.renderTextScreen(tess, eng, core.getOwner(), 0,  0.8, 2.4, -0.02,  0, 0, 180,  0.3, 0.3, 0.3);
		}
		comps.renderTextScreen(tess, eng, eng.currentPerson, 3,  0.65, 0.6, -0.02, 0, 0, 180,  0.3, 0.3, 0.3);
		comps.renderButton(tess,eng,4, 0.72,0.625,-0.0125, 90,0,0, 0.3,0.3,0.3);
		comps.renderButton(tess,eng,5, 0.72,0.535,-0.0125, 90,0,0, 0.3,0.3,0.3);
		//comps.renderLight( tess,eng,6, 0.82,0.6155,-0.0125, 90,0,0, 0.3,0.3,0.3);
		//comps.renderButton(tess,eng,7, 0.81,0.535,-0.0125, 90,0,0, 0.3,0.3,0.3);

		comps.renderTextScreen(tess, eng, eng.getConsoleSetting(), 70,  0.65, 0.8, -0.02, 0, 0, 180,  0.3, 0.3, 0.3);
		comps.renderButton(tess,eng,71, 0.72,0.825,-0.0125, 90,0,0, 0.3,0.3,0.3);
		comps.renderButton(tess,eng,72, 0.72,0.735,-0.0125, 90,0,0, 0.3,0.3,0.3);
		comps.renderButton(tess,eng,73, 0.81,0.780,-0.0125, 90,0,0, 0.3,0.3,0.3);
		for(TardisPermission p : TardisPermission.values())
		{
			double d = p.ordinal()/13.0;
			comps.renderLight(tess, eng, 90+p.ordinal(), 0.635-d,0.47,-0.02, 90,0,0,0.3,0.3,0.3);
			comps.renderPushSwitch(tess, eng, 80+p.ordinal(), 0.625-d,0.4,-0.02, 90,0,0,0.3,0.3,0.3);
		}
	}

	private void renderFront(Tessellator tess, EngineTileEntity eng, CoreTileEntity core, TardisDataStore ds)
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

	private void renderLeft(Tessellator tess, EngineTileEntity eng, CoreTileEntity core, TardisDataStore ds)
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
		if(ds.hasFunction(TardisFunction.SPAWNPROT))
			comps.renderLever(tess, eng, 130, 0.5, 0.8, 1, -90, 0, 180, 0.2, 0.3, 0.3);
		comps.renderButton(tess, eng, 131, 0.75, 0.488, 1.02, -90, 0, 0, 0.3, 0.3, 0.3);
		comps.renderButton(tess, eng, 132, 0.65, 0.8, 1.03, -90, 0, 0, 0.4, 0.4, 0.4);
	}

	private void renderBack(Tessellator tess, EngineTileEntity eng, CoreTileEntity core, TardisDataStore ds)
	{
		if(eng.visibility > 0)
		{
			GL11.glPushMatrix();
			GL11.glColor4d(1, 1, 1, eng.visibility);
			GL11.glTranslated(0.49, -1, 0.5);
			bindTexture(panelTex);
			enginePanel.renderAll();
			GL11.glColor4d(1, 1, 1, 1);
			GL11.glPopMatrix();
		}
		if((eng.visibility < 1) && (ds != null))
			renderUnderPanel(tess,eng,core,ds);
		GL11.glPushMatrix();
		comps.renderButton(tess, eng, 100, -0.01, 0.5, 0.04, 0, 0, 270, 0.3, 0.3, 0.3);
		GL11.glPopMatrix();
	}

	private void renderBreakable(Tessellator tess, EngineTileEntity eng, CoreTileEntity core, TardisDataStore ds, int comp)
	{
		double x = 0.5;
		double y = 0.6;
		double z = -0.05;
		double scale = 0.1;
		switch(comp)
		{
			case 0: break;
			case 1: x += 0.2;	y += 0.2;	scale *= 0.5; break;
			case 2: x += 0.25;	y += 0; 	scale *= 0.5; break;
			case 3: x += 0.2;	y -= 0.2;	scale *= 0.5; break;
			case 4: x -= 0.2;	y += 0.2;	scale *= 0.5; break;
			case 5: x -= 0.25;	y += 0; 	scale *= 0.5; break;
			case 6: x -= 0.2;	y -= 0.2;	scale *= 0.5; break;
			case 7: x -= 0.28;	y -= 0.365;	scale *= 0.7; break;
			case 8: x += 0.15;	y -= 0.35; 	scale *= 0.4; break;
			case 9: x -= 0; 	y -= 0.35;	scale *= 0.8; break;
		}
		GL11.glPushMatrix();
		GL11.glRotated(90, 0, 0, 1);
		GL11.glTranslated(y, z, x);
		GL11.glScaled(scale, scale, scale);
		if(ds.damage.isComponentBroken(comp))
			bindTexture(bubbleDown);
		else
			bindTexture(bubbleUp);
		bubble.renderAll();
		GL11.glPopMatrix();
	}

	private void renderUnderPanel(Tessellator tess, EngineTileEntity eng, CoreTileEntity core, TardisDataStore ds)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(0.045, 0.96, 0.148);
		GL11.glScaled(0.4, 0.31, 0.8);
		for(int i = 0; i < ds.upgrades.length; i++)
		{
			AbstractUpgrade up = ds.upgrades[i];
			if(up != null)
			{
				GL11.glPushMatrix();
				GL11.glTranslated(0, 0, i * 0.117);
				up.render();
				GL11.glPopMatrix();
			}
		}
		GL11.glPopMatrix();
		for(int i = 0; i < TardisDamageSystem.numBreakables; i++)
			renderBreakable(tess,eng,core,ds,i);
	}

	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z, float ptt)
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
			bindTexture(engineTex);
			engine.renderAll();
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glTranslated(-0.5, -0.5, -0.5);
			TardisDataStore ds = Helper.getDataStore(eng);
			if(ds != null)
			{
				renderRight(tess,eng,core,ds);
				renderFront(tess,eng,core,ds);
				renderLeft (tess,eng,core,ds);
				renderBack (tess,eng,core,ds);
			}
			GL11.glPopMatrix();
		}
	}

}
