package tardis.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import tardis.TardisMod;
import tardis.api.TardisFunction;
import tardis.client.renderer.model.TardisConsoleModel;
import tardis.common.blocks.TardisAbstractBlock;
import tardis.common.tileents.TardisConsoleTileEntity;
import tardis.common.tileents.TardisCoreTileEntity;

public class TardisConsoleRenderer extends TardisAbstractBlockRenderer
{
	TardisConsoleModel model = new TardisConsoleModel();
	TardisControlRenderer compRender;
	
	@Override
	public TardisAbstractBlock getBlock()
	{
		return TardisMod.tardisConsoleBlock;
	}
	
	private void renderDimControls(Tessellator tess, TardisConsoleTileEntity tce)
	{
		compRender.renderLever(tess,tce,60, 1.3,-0.23,-0.45, -45, 90, 0, 0.4,0.6,0.6);
	}
	
	private void renderXControls(Tessellator tess, TardisConsoleTileEntity tce)
	{
		compRender.renderLever(tess, tce, 10, -0.4, -0.28, -1.23, -45, 180, 0, 0.6,0.6,0.6);	//x1
		compRender.renderLever(tess, tce, 11, 0, -0.28, -1.23, -45, 180, 0, 0.6,0.6,0.6);			//x2
		compRender.renderLever(tess, tce, 12, 0.4, -0.28, -1.23, -45, 180, 0, 0.6,0.6,0.6);	//x3
		compRender.renderLever(tess, tce, 13, 0, -0.65, -0.82, -45, 180, 0, 0.6,0.6,0.6);		//x4
		compRender.renderWheel(tess, tce, 14, -0.35, -0.67, -0.81, -45, 180, 0, 0.3,0.3,0.3);	//x5
		compRender.renderWheel(tess, tce, 15, 0.35, -0.67, -0.81, -45, 180, 0, 0.3,0.3,0.3);	//x6
	}
	
	private void renderZControls(Tessellator tess, TardisConsoleTileEntity tce)
	{
		compRender.renderLever(tess, tce, 20, 0.3, -0.65, 0.78,   -45, 0, 0, 0.3,0.6,0.6);
		compRender.renderLever(tess, tce, 21, 0.1, -0.65, 0.78,   -45, 0, 0, 0.3,0.6,0.6);
		compRender.renderLever(tess, tce, 22, -0.1, -0.65, 0.78,  -45, 0, 0, 0.3,0.6,0.6);
		compRender.renderLever(tess, tce, 23, -0.3, -0.65, 0.78,  -45, 0, 0, 0.3,0.6,0.6);
		compRender.renderWheel(tess, tce, 24, 0.70, -0.28, 1.25,  -45, 0, 0, 0.35,0.3,0.35);
		compRender.renderWheel(tess, tce, 25, 0.10, -0.28, 1.25, -45, 0, 0,  0.35,0.3,0.35);
	}
	
	private void renderYControls(Tessellator tess, TardisConsoleTileEntity tce)
	{
		compRender.renderLever(tess, tce, 30, 0.78, -0.65, 0.3,	-45, 90, 0, 0.3,0.6,0.6);
		compRender.renderLever(tess, tce, 31, 0.78, -0.65, 0.15,	-45, 90, 0, 0.3,0.6,0.6);
		compRender.renderLever(tess, tce, 32, 0.78, -0.65, 0,		-45, 90, 0, 0.3,0.6,0.6);
		compRender.renderLever(tess, tce, 33, 0.78, -0.65, -0.15,	-45, 90, 0, 0.3,0.6,0.6);
		compRender.renderPushSwitch(tess,tce, 34, 0.81, -0.68, -0.3,-45,90, 0, 0.5,  0.5,  0.5);
	}
	
	private void renderFlightControls(Tessellator tess, TardisConsoleTileEntity tce)
	{
		compRender.renderButton(tess,tce, 40,       0.425,-0.23,1.3, 45,180,0, 1,1,1);
		compRender.renderLever(tess,tce,  41,       1.3,-0.23,-0.8, -45,90,0, 0.8,0.6,0.6);
		compRender.renderSpecialLever(tess,tce, 42, -1.16,-0.35,0.8, 0,180,-45, 0.8,0.8,0.8);
	}
	
	private void renderSchemaChooser(Tessellator tess, TardisConsoleTileEntity tce)
	{
		compRender.renderTextScreen(tess,tce,tce.schemaChooserString,52, 1.1,-0.4,-0.1, -45,-90,0, 0.4,0.4,0.2);
		compRender.renderButton(tess,tce,50, 1.05,-0.42,0.8, 45,-90,0,0.5,0.5,0.5);
		compRender.renderButton(tess,tce,51, 1.19,-0.29,0.8, 45,-90,0,0.5,0.5,0.5);
	}
	
	private void renderFrontPanel(Tessellator tess, TardisConsoleTileEntity tce, TardisCoreTileEntity core)
	{
		compRender.renderButton(tess,tce,5, -1,-0.53,-0.64, 45,90,0, 0.5,0.5,0.5);					//ID 5 Screwdriver generator
		compRender.renderScrewdriverHolder(tess,tce,-1,-0.55,-0.45, 0,0,-45, 0.5,0.5,0.5);			//ID 6 Screwdriver holder
		compRender.renderScrewdriver(tess,tce,0, -1,-0.55,-0.45, 0,0,-45, 0.5,0.5,0.5);				//ID 6 Screwdriver
		compRender.renderGauge (tess, tce, 0, -0.70, -0.68, 0.15, 45, -90, 0, 0.75, 0.75, 0.75);		//ID 0 Energy gauge
		compRender.renderGauge (tess, tce, 1, -0.70, -0.68, -0.125, 45, -90, 0, 0.75, 0.75, 0.75);	//ID 1 Rooms gauge
		compRender.renderGauge (tess, tce, 2, -0.70, -0.68, -0.4,   45, -90, 0, 0.75, 0.75, 0.75);	//ID 2 Speed gauge
		compRender.renderGauge (tess, tce, 8, -0.56, -0.82 , 0.015, 45, -90, 0, 0.75, 0.75, 0.75);	//ID 8 XP gauge
		compRender.renderPushSwitch(tess, tce, 53,-0.7, -0.78 , 0.36,45, 90, 0, 0.6 , 0.6 , 0.6 );
		compRender.renderWheel (tess, tce, 3, -1.25, -0.3, -0.4, 45, 90, -0, 0.5, 0.5, 0.5);			//ID 3 Facing wheel
		compRender.renderLever (tess, tce, 4, -1.23, -0.28, 0.4, -45, -90, 0, 0.6,0.6,0.6);			//ID 4 Speed lever
	}
	
	private void renderRightPanel(Tessellator tess, TardisConsoleTileEntity tce, TardisCoreTileEntity core)
	{
		renderXControls(tess,tce);																	//ID 10-16 X Controls
		compRender.renderScreen(tess,tce, 100, "main", -0.7, -0.325, -1.20, -45,180,0,0.6,0.6,0.6);	//ID 100 Coord guesser
		compRender.renderButton(tess, tce, 901, -1.1, -0.175, -1.35, -45, 180, 0, 0.5, 0.5, 0.5);	//ID 901 RemButton
		compRender.renderPushSwitch(tess, tce, 55, 0.75, -0.28, -1.25, -45, 180, 0, 0.5, 0.5, 0.5);
		if(core != null && core.hasFunction(TardisFunction.STABILISE))
			compRender.renderPushSwitch(tess, tce, 56, 0.75, -0.18, -1.36 , -45, 180, 0, 0.5, 0.5, 0.5);
	}
	
	private void renderLeftPanel(Tessellator tess, TardisConsoleTileEntity tce, TardisCoreTileEntity core)
	{
		renderZControls(tess,tce);																	//ID 20-26 Z Controls
		for(int i=0;i<4;i++)
		{
			for(int j=0;j<5;j++)
			{
				compRender.renderButton(tess, tce, 1019-(5*i) - (4-j), -0.30-(0.125*j), -0.2-(0.08*i), 1.35-(0.1*i), -39, 0, 0, 0.6,0.6,0.6);
			}
		}
		compRender.renderPushSwitch(tess, tce, 900, -0.95, -0.2, 1.37, -39, 0, 0, 0.5, 0.5, 0.5);				//ID 900 save/load switch
		if(core!=null && core.hasFunction(TardisFunction.SENSORS))
			compRender.renderScreen(tess, tce, 54, "scanner", 0.47, -0.38, 1.1, 43, 180, 0, 0.3, 0.3, 0.3);
		compRender.renderButton(tess, tce, 902, -0.95,-0.29,1.26,  -39,0,0,  0.5,0.5,0.5);
		compRender.renderButton(tess, tce, 903, -0.95,-0.38,1.15,  -39,0,0,  0.5,0.5,0.5);
	}
	
	private void renderBackPanel(Tessellator tess, TardisConsoleTileEntity tce, TardisCoreTileEntity core)
	{
		renderSchemaChooser(tess,tce);
		renderDimControls(tess,tce);
		renderYControls(tess,tce);
		compRender.renderScrewdriverHolder(tess, tce,1.13,-0.38,-0.225, 0,0,45, 0.5,0.5,0.5);
		compRender.renderScrewdriver(tess, tce, 1, 1.13,-0.38,-0.225, 0, 0, 45, 0.5, 0.5, 0.5);
		compRender.renderLever(tess, tce, 1020, 1.36, -0.15, -0.1, 0, 0, 45, 0.5, 0.5, 0.5);
		compRender.renderLever(tess, tce, 1021, 1.36, -0.15, 0.4, 0, 0, 45, 0.5, 0.5, 0.5);
		compRender.renderLever(tess, tce, 1022, 1.36, -0.15, 0.9, 0, 0, 45, 0.5, 0.5, 0.5);
		compRender.renderButton(tess, tce, 52, 1.36, -0.15, -1.15, 0, 0, 45, 0.6, 0.6, 0.6);
		compRender.renderPushSwitch(tess,tce, 904, 0.81, -0.68, -0.45,-45,90, 0, 0.5,  0.5,  0.5);
	}
	
	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z)
	{
		if(compRender == null)
			compRender = new TardisControlRenderer(func_147498_b(),field_147501_a.field_147553_e);
		
		
		GL11.glPushMatrix();
		//This line actually rotates the renderer.
		GL11.glTranslatef(0.5F, 0, 0.5F);
		GL11.glRotatef(180F, 0F, 0, 1F);
		GL11.glTranslatef(0F, -0.5F, 0F);
		bindTexture(new ResourceLocation("tardismod","textures/models/TardisConsole.png"));
		//GL11.glColor4f(1F, 1F, 1F, tte.getTransparency());
		GL11.glPushMatrix();
		GL11.glTranslatef(0, -0.5F, 0);
		model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		if(te != null && te instanceof TardisConsoleTileEntity)
		{
			TardisConsoleTileEntity tce = (TardisConsoleTileEntity)te;
			TardisCoreTileEntity core = tce.getCore();
			renderFrontPanel(tess,tce,core);
			renderRightPanel(tess,tce,core);
			renderBackPanel( tess,tce,core);
			renderLeftPanel( tess,tce,core);
			renderFlightControls(tess,tce);
		}
		GL11.glPopMatrix();
	}

}
