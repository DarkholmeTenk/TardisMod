package tardis.client.renderer.tileents;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractBlockRenderer;
import io.darkcraft.darkcore.mod.helpers.RenderHelper;

import tardis.client.renderer.gallifreyan.Word;
import tardis.client.renderer.model.ConsoleModel;
import tardis.client.renderer.model.console.ButtonModel;
import tardis.common.TMRegistry;
import tardis.common.core.HitPosition;
import tardis.common.core.helpers.Helper;
import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.CoreTileEntity;
import tardis.core.console.panel.ConsolePanel;

public class ConsoleRenderer extends AbstractBlockRenderer
{
	private static ButtonModel button = new ButtonModel();
	private static final IModelCustom newModel = RenderHelper.getModel(new ResourceLocation("tardismod", "models/console/console.obj"));
	private static final ResourceLocation baseTexture = new ResourceLocation("tardismod", "models/console/console.png");
	private static final ResourceLocation panelTexture = new ResourceLocation("tardismod", "models/console/console_face.png");
	private static final ResourceLocation emptyTexture = new ResourceLocation("tardismod", "models/console/console_empty.png");

	public static HitPosition hp = null;
	ConsoleModel model = new ConsoleModel();
//	ControlRenderer compRender;

	@Override
	public AbstractBlock getBlock()
	{
		return TMRegistry.tardisConsoleBlock;
	}

//	private void renderDimControls(Tessellator tess, ConsoleTileEntity tce)
//	{
//		compRender.renderLever(tess,tce,60, 1.3,-0.23,-0.45, -45, 90, 0, 0.4,0.6,0.6);
//	}
//
//	private void renderXControls(Tessellator tess, ConsoleTileEntity tce)
//	{
//		compRender.renderLever(tess, tce, 10, -0.4, -0.28, -1.23, -45, 180, 0, 0.6,0.6,0.6);	//x1
//		compRender.renderLever(tess, tce, 11, 0, -0.28, -1.23, -45, 180, 0, 0.6,0.6,0.6);			//x2
//		compRender.renderLever(tess, tce, 16, 0.4, -0.28, -1.23, -45, 180, 0, 0.6,0.6,0.6);	//x3
//		compRender.renderLever(tess, tce, 12, -0.09, -0.65, -0.82, -45, 180, 0, 0.35,0.5,0.45);		//x4
//		compRender.renderWheel(tess, tce, 14, -0.35, -0.67, -0.81, -45, 180, 0, 0.3,0.3,0.3);	//x5
//		compRender.renderWheel(tess, tce, 15, 0.35, -0.67, -0.81, -45, 180, 0, 0.3,0.3,0.3);	//x6
//		compRender.renderLever(tess, tce, 13, 0.09, -0.65, -0.82, -45, 180, 0, 0.35,0.5,0.45); //x7
//	}
//
//	private void renderZControls(Tessellator tess, ConsoleTileEntity tce)
//	{
//		compRender.renderLever(tess, tce, 20, 0.4, -0.65, 0.78,   -45, 0, 0, 0.3,0.6,0.6);
//		compRender.renderLever(tess, tce, 21, 0.2, -0.65, 0.78,   -45, 0, 0, 0.3,0.6,0.6);
//		compRender.renderLever(tess, tce, 26, 0, -0.65, 0.78,  -45, 0, 0, 0.3,0.6,0.6);
//		compRender.renderLever(tess, tce, 22, -0.2, -0.65, 0.78,  -45, 0, 0, 0.3,0.6,0.6);
//		compRender.renderLever(tess, tce, 23, -0.4, -0.65, 0.78,	 -45, 0, 0, 0.3,0.6,0.6);
//		compRender.renderWheel(tess, tce, 24, 0.70, -0.28, 1.25,  -45, 0, 0, 0.35,0.3,0.35);
//		compRender.renderWheel(tess, tce, 25, 0.10, -0.28, 1.25, -45, 0, 0,  0.35,0.3,0.35);
//	}
//
//	private void renderYControls(Tessellator tess, ConsoleTileEntity tce)
//	{
//		compRender.renderLever(tess, tce, 30, 0.78, -0.65, 0.3,	-45, 90, 0, 0.3,0.6,0.6);
//		compRender.renderLever(tess, tce, 31, 0.78, -0.65, 0.15,	-45, 90, 0, 0.3,0.6,0.6);
//		compRender.renderLever(tess, tce, 32, 0.78, -0.65, 0,		-45, 90, 0, 0.3,0.6,0.6);
//		compRender.renderLever(tess, tce, 33, 0.78, -0.65, -0.15,	-45, 90, 0, 0.3,0.6,0.6);
//		compRender.renderPushSwitch(tess,tce, 34, 0.81, -0.68, -0.3,-45,90, 0, 0.5,  0.5,  0.5);
//	}
//
//	private void renderFlightControls(Tessellator tess, ConsoleTileEntity tce)
//	{
//		compRender.renderButton(tess,tce, 40,       0.425,-0.23,1.3, 45,180,0, 1,1,1);
//		compRender.renderLever(tess,tce,  41,       1.3,-0.23,-0.8, -45,90,0, 0.8,0.6,0.6);
//		compRender.renderSpecialLever(tess,tce, 42, -1.16,-0.35,0.8, 0,180,-45, 0.8,0.8,0.8);
//	}
//
//	private void renderSchemaChooser(Tessellator tess, ConsoleTileEntity tce)
//	{
//		compRender.renderTextScreen(tess,tce,tce.schemaCategoryString,52, 1.05,-0.42,-0.1, -49,-90,0, 0.4,0.4,0.2);
//		compRender.renderTextScreen(tess,tce,tce.schemaChooserString,52, 1.26,-0.22,-0.1, -49,-90,0, 0.4,0.4,0.2);
//		compRender.renderButton(tess,tce,57, 1.05,-0.42,0.8, 41,-90,0,0.5,0.5,0.5);
//		compRender.renderButton(tess,tce,58, 1.15,-0.33,0.8, 41,-90,0,0.5,0.5,0.5);
//		compRender.renderButton(tess,tce,50, 1.26,-0.23,0.8, 41,-90,0,0.5,0.5,0.5);
//		compRender.renderButton(tess,tce,51, 1.36,-0.14,0.8, 41,-90,0,0.5,0.5,0.5);
//	}
//
//	private void renderFrontPanel(Tessellator tess, ConsoleTileEntity tce, CoreTileEntity core)
//	{
//		compRender.renderButton(tess,tce,5, -1,-0.53,-0.64, 45,90,0, 0.5,0.5,0.5);					//ID 5 Screwdriver generator
//		compRender.renderScrewdriverHolder(tess,tce,-1,-0.55,-0.45, 0,0,-45, 0.5,0.5,0.5);			//ID 6 Screwdriver holder
//		compRender.renderScrewdriver(tess,tce,0, -1,-0.55,-0.45, 0,0,-45, 0.5,0.5,0.5);				//ID 6 Screwdriver
//		compRender.renderGauge (tess, tce, 0, -0.70, -0.68, 0.15, 45, -90, 0, 0.75, 0.75, 0.75);		//ID 0 Energy gauge
//		compRender.renderGauge (tess, tce, 1, -0.70, -0.68, -0.125, 45, -90, 0, 0.75, 0.75, 0.75);	//ID 1 Rooms gauge
//		compRender.renderGauge (tess, tce, 2, -0.70, -0.68, -0.4,   45, -90, 0, 0.75, 0.75, 0.75);	//ID 2 Speed gauge
//		compRender.renderGauge (tess, tce, 8, -0.56, -0.82 , 0.015, 45, -90, 0, 0.75, 0.75, 0.75);	//ID 8 XP gauge
//		compRender.renderGauge (tess, tce, 9, -0.56, -0.82 , -0.285, 45, -90, 0, 0.75, 0.75, 0.75);	//ID 9 Shields gauge
//		compRender.renderPushSwitch(tess, tce, 53,-0.7, -0.78 , 0.36,45, 90, 0, 0.6 , 0.6 , 0.6 );
//		compRender.renderWheel (tess, tce, 3, -1.25, -0.3, -0.4, 45, 90, -0, 0.5, 0.5, 0.5);			//ID 3 Facing wheel
//		compRender.renderLever (tess, tce, 4, -1.23, -0.28, 0.4, -45, -90, 0, 0.6,0.6,0.6);			//ID 4 Speed lever
//		compRender.renderPushSwitch(tess, tce, 1030, -1.08, -0.43, 0, 43, 90, 0, 0.5, 0.5, 0.5);
//		compRender.renderPushSwitch(tess, tce, 1031, -1.18, -0.34, 0, 43, 90, 0, 0.5, 0.5, 0.5);
//		compRender.renderPushSwitch(tess, tce, 1032, -1.28, -0.25, 0, 43, 90, 0, 0.5, 0.5, 0.5);
//		compRender.renderButton (tess, tce, 43, -1.25, -0.3, -0.9, 45, 90, -0, 0.5, 0.5, 0.5);			//ID 43 Randomizer
//	}
//
//	private void renderRightPanel(Tessellator tess, ConsoleTileEntity tce, CoreTileEntity core)
//	{
//		renderXControls(tess,tce);																	//ID 10-16 X Controls
//		compRender.renderScreen(tess,tce, 100, "main", -0.7, -0.325, -1.20, -45,180,0,0.6,0.6,0.6);	//ID 100 Coord guesser
//		compRender.renderButton(tess, tce, 901, -1.1, -0.175, -1.35, -45, 180, 0, 0.5, 0.5, 0.5);	//ID 901 RemButton
//		compRender.renderPushSwitch(tess, tce, 55, 0.75, -0.28, -1.25, -45, 180, 0, 0.5, 0.5, 0.5);
//		if((core != null) && core.hasFunction(TardisFunction.STABILISE))
//			compRender.renderPushSwitch(tess, tce, 56, 0.75, -0.18, -1.36 , -45, 180, 0, 0.5, 0.5, 0.5);
//		compRender.renderLever(tess, tce, 1022, 0.75, -0.46, -1.02, 90, 45, 90, 0.3, 0.4, 0.4);
//		compRender.renderLever(tess, tce, 1023, -0.75, -0.46, -1.02, 90, 45, 90, 0.3, 0.4, 0.4);
//		compRender.renderLever(tess, tce, 1020, 0.9, -0.19, -1.30, 45, 0, 0, 0.3, 0.4, 0.4);
//		compRender.renderLever(tess, tce, 1021, 1.07, -0.19, -1.30, 45, 0, 0, 0.3, 0.4, 0.4);
//	}
//
//	private void renderLeftPanel(Tessellator tess, ConsoleTileEntity tce, CoreTileEntity core)
//	{
//		renderZControls(tess,tce);																	//ID 20-26 Z Controls
//		for(int i=0;i<4;i++)
//		{
//			for(int j=0;j<5;j++)
//			{
//				compRender.renderButton(tess, tce, 1019-(5*i) - (4-j), -0.30-(0.125*j), -0.2-(0.08*i), 1.35-(0.1*i), -39, 0, 0, 0.6,0.6,0.6);
//			}
//		}
//		compRender.renderPushSwitch(tess, tce, 900, -0.95, -0.2, 1.37, -39, 0, 0, 0.5, 0.5, 0.5);				//ID 900 save/load switch
//		if((core!=null) && core.hasFunction(TardisFunction.SENSORS))
//			compRender.renderScreen(tess, tce, 54, "scanner", 0.47, -0.38, 1.1, 43, 180, 0, 0.3, 0.3, 0.3);
//		compRender.renderButton(tess, tce, 902, -0.95,-0.29,1.26,  -39,0,0,  0.5,0.5,0.5);
//		compRender.renderButton(tess, tce, 903, -0.95,-0.38,1.15,  -39,0,0,  0.5,0.5,0.5);
//		compRender.renderLever(tess, tce, 1027, 0.1, -0.42, 1.05, -90, -45, 90, 0.2, 0.3, 0.3);
//		compRender.renderLever(tess, tce, 1028, 0.7, -0.42, 1.05, -90, -45, 90, 0.2, 0.3, 0.3);
//		compRender.renderLever(tess, tce, 1029, 0.7, -0.49, 0.95, -90, -45, 90, 0.2, 0.3, 0.3);
//	}
//
//	private void renderBackPanel(Tessellator tess, ConsoleTileEntity tce, CoreTileEntity core)
//	{
//		renderSchemaChooser(tess,tce);
//		renderDimControls(tess,tce);
//		renderYControls(tess,tce);
//		compRender.renderScrewdriverHolder(tess, tce,1.20,-0.32,-0.225, 0,0,45, 0.5,0.5,0.5);
//		compRender.renderScrewdriver(tess, tce, 1, 1.20,-0.32,-0.225, 0, 0, 45, 0.5, 0.5, 0.5);
//		//compRender.renderLever(tess, tce, 1020, 1.36, -0.15, -0.1, 0, 0, 45, 0.5, 0.5, 0.5);
//		//compRender.renderLever(tess, tce, 1021, 1.36, -0.15, 0.4, 0, 0, 45, 0.5, 0.5, 0.5);
//		//compRender.renderLever(tess, tce, 1022, 1.36, -0.15, 0.9, 0, 0, 45, 0.5, 0.5, 0.5);
//		compRender.renderButton(tess, tce, 52, 1.36, -0.15, -1.15, 0, 0, 45, 0.6, 0.6, 0.6);
//		compRender.renderPushSwitch(tess,tce, 904, 0.81, -0.68, -0.45,-45,90, 0, 0.5,  0.5,  0.5);
//		compRender.renderLever(tess, tce, 1024, 0.97, -0.49, -0.6, 0, 0, 45, 0.3, 0.4, 0.4);
//		compRender.renderLever(tess, tce, 1025, 1.02, -0.43, -0.32, 0, 0, 45, 0.3, 0.4, 0.4);
//		compRender.renderButton(tess, tce, 1026, 0.91, -0.58, -0.3, -45, 90, 0, 0.5, 0.5, 0.5);
//	}

	private final Word word1 = new Word("foxpotato");
	private final Word word2 = new Word("darkholme");
	private final Word word3 = new Word("ohmygodhireddit");
	private final Word word4 = new Word("shale");
	private final Word word5 = new Word("tabefilokuaeiou");

	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z, float ptt)
	{
//		if(compRender == null)
//			compRender = new ControlRenderer(func_147498_b(),field_147501_a.field_147553_e);
		ConsoleTileEntity tce = null;
		if((te != null) && (te instanceof ConsoleTileEntity))
			tce = (ConsoleTileEntity) te;

		GL11.glPushMatrix();
		//GL11.glColor4f(1F, 1F, 1F, tte.getTransparency());
		GL11.glPushMatrix();
		GL11.glScaled(0.5, 0.5, 0.5);
		GL11.glTranslated(1, -1, 1);
		bindTexture(baseTexture);
		newModel.renderOnly("Cube_Base");
		if(tce != null)
		{
			ConsolePanel[] panels = tce.getPanels();
			for(int i = 0; (i < 4) && (i < panels.length); i++)
			{
				GL11.glPushMatrix();
				GL11.glRotated((90*(i))-90, 0, 1, 0);
				if(panels[i] != null)
				{
					bindTexture(panelTexture);
					newModel.renderOnly("Face_FilledFace");
				}
				else
				{
					bindTexture(emptyTexture);
					newModel.renderOnly("Inset_EmptyFace");
				}
				GL11.glPopMatrix();
			}
		}
		GL11.glTranslated(4, 4, 0);
//		word1.render();
//		word2.render();
//		word3.render();
//		word4.render();
//		word5.render();
		GL11.glPopMatrix();
		if(tce != null)
		{
			GL11.glRotated(180, 1, 0, 0);
			GL11.glRotated(180, 0, 1, 0);
			GL11.glTranslated(-0.5, -0.5, 0.5);
			CoreTileEntity core = Helper.getTardisCore(te);
			ConsolePanel[] panels = tce.getPanels();
			for(int i = 0; (i < 4) && (i < panels.length); i++)
			{
				ConsolePanel panel = panels[i];
				GL11.glPushMatrix();
				GL11.glRotated(90*(i-2), 0, 1, 0);
				if(panel != null)
					panel.render(ptt);
				GL11.glPopMatrix();
			}
		}
		GL11.glPopMatrix();
		if(hp != null)
		{
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glPushMatrix();
			GL11.glTranslated(0.5, 0, 0.5);
			GL11.glRotated(90*(2-hp.side), 0, 1, 0);
			GL11.glPointSize(2.0f);
			GL11.glDepthFunc(GL11.GL_ALWAYS);
			GL11.glColor3f(0, 1, 0);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBegin(GL11.GL_POINTS);
			GL11.glVertex3d(-0.5-hp.posY, 1.5-hp.posY, -1.5+hp.posZ);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}
	}

}
