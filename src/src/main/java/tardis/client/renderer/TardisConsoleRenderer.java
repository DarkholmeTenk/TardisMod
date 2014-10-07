package tardis.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import tardis.TardisMod;
import tardis.blocks.TardisAbstractBlock;
import tardis.client.renderer.model.TardisConsoleModel;
import tardis.client.renderer.model.TardisSonicScrewdriverModel;
import tardis.client.renderer.model.console.GaugeDisplayModel;
import tardis.client.renderer.model.console.GaugeNeedleModel;
import tardis.client.renderer.model.console.LeverBaseModel;
import tardis.client.renderer.model.console.LeverModel;
import tardis.client.renderer.model.console.PushLeverModel;
import tardis.client.renderer.model.console.SchemaDisplayModel;
import tardis.client.renderer.model.console.ScreenFrameModel;
import tardis.client.renderer.model.console.ScreenModel;
import tardis.client.renderer.model.console.SonicScrewdriverHolderModel;
import tardis.client.renderer.model.console.SpecialLeverModel;
import tardis.client.renderer.model.console.ValveWheelModel;
import tardis.tileents.TardisConsoleTileEntity;

public class TardisConsoleRenderer extends TardisAbstractBlockRenderer
{
	TardisConsoleModel model;
	TardisSonicScrewdriverModel screw;
	SonicScrewdriverHolderModel holder;
	GaugeDisplayModel gaugeDisplay;
	GaugeNeedleModel gaugeNeedle;
	ValveWheelModel wheel;
	LeverModel lever;
	LeverBaseModel leverBase;
	ScreenFrameModel screenFrame;
	ScreenModel screen;
	PushLeverModel pushSwitch;
	SpecialLeverModel specLever;
	SchemaDisplayModel schemaDisplay;
	
	FontRenderer fontRenderer;
	
	public TardisConsoleRenderer()
	{
		model = new TardisConsoleModel();
		screw = new TardisSonicScrewdriverModel();
		holder = new SonicScrewdriverHolderModel();
		gaugeDisplay = new GaugeDisplayModel();
		gaugeNeedle = new GaugeNeedleModel();
		wheel = new ValveWheelModel();
		lever = new LeverModel();
		leverBase = new LeverBaseModel();
		screenFrame = new ScreenFrameModel();
		screen = new ScreenModel();
		pushSwitch = new PushLeverModel();
		specLever  = new SpecialLeverModel();
		schemaDisplay = new SchemaDisplayModel();
	}

	@Override
	public TardisAbstractBlock getBlock()
	{
		return TardisMod.tardisConsoleBlock;
	}
	
	private void setHighlight(TardisConsoleTileEntity te, int controlID)
	{
		if(te.getControlHighlight(controlID) >= 0)
			GL11.glColor3d(0.2*te.getControlHighlight(controlID), 0.5 * te.getControlHighlight(controlID), te.getControlHighlight(controlID));
		else
			GL11.glColor3d(1, 1, 1);
	}
	
	private void renderScrewdriverHolder(Tessellator tess, TardisConsoleTileEntity te, int x, int y, int z)
	{
		GL11.glPushMatrix();
		float scaleFactor = 0.5F;
		GL11.glRotatef(180F, 0F, 1F, 0F);
		GL11.glTranslatef(1F, -0.55F, 0.45F);
		GL11.glRotatef(45F, 0F, 0, 1F);
		GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
		GL11.glColor3d(1, 1, 1);
		bindTexture(new ResourceLocation("tardismod","textures/models/SonicScrewdriverHolder.png"));
		holder.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
	}
	
	private void renderScrewdriver(Tessellator tess, TardisConsoleTileEntity te, int x, int y, int z)
	{
		if(te.hasScrewdriver())
		{
			GL11.glPushMatrix();
			float scaleFactor = 0.5F;
			GL11.glRotatef(180F, 0F, 1F, 0F);
			GL11.glTranslatef(1F, -0.55F, 0.45F);
			GL11.glRotatef(45F, 0F, 0, 1F);
			GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
			GL11.glColor3d(1, 1, 1);
			bindTexture(new ResourceLocation("tardismod","textures/models/SonicScrewdriver.png"));
			screw.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			GL11.glPopMatrix();
		}
	}
	
	private void renderGauge(Tessellator tess, TardisConsoleTileEntity te, int id, double x, double y, double z,double rX,double rY,double rZ, double sX, double sY, double sZ)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glRotated(rZ, 0, 0, 1);
		GL11.glRotated(rY, 0, 1, 0);
		GL11.glRotated(rX, 1, 0, 0);
		GL11.glScaled(sX, sY, sZ);
		GL11.glColor3d(1, 1, 1);
		GL11.glPushMatrix();
		bindTexture(new ResourceLocation("tardismod","textures/models/TardisConsoleGaugeDisplay.png"));
		gaugeDisplay.render(null, 0F, 0F, 0F, 0F, 0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		bindTexture(new ResourceLocation("tardismod","textures/models/TardisConsoleGaugeNeedle.png"));
		GL11.glTranslated(0.15, 0.175, 0.5/8);
		GL11.glRotated(-90-(te.getControlState(id,true) * 180), 0, 0, 1);
		GL11.glScaled(0.25, 0.40, 0.25);
		gaugeNeedle.render(null, 0F, 0F, 0F, 0F, 0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
	
	private void renderWheel(Tessellator tess, TardisConsoleTileEntity te, int id, double x, double y, double z,double rX,double rY,double rZ, double sX, double sY, double sZ)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glRotated(rZ, 0, 0, 1);
		GL11.glRotated(rY, 0, 1, 0);
		GL11.glRotated(rX, 1, 0, 0);
		GL11.glScaled(sX, sY, sZ);
		GL11.glRotated(te.getControlState(id)*360, 0, 1, 0);
		GL11.glTranslated(-0.03125, 0, -0.03125);
		setHighlight(te,id);
		bindTexture(new ResourceLocation("tardismod","textures/models/TardisValveWheel.png"));
		wheel.render(null, 0F, 0F, 0F, 0F, 0F, 0.0625F);
		GL11.glPopMatrix();
	}
	
	private void renderLever(Tessellator tess, TardisConsoleTileEntity te, int id, double x, double y, double z,double rX,double rY,double rZ, double sX, double sY, double sZ)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glRotated(rZ, 0, 0, 1);
		GL11.glRotated(rY, 0, 1, 0);
		GL11.glRotated(rX, 1, 0, 0);
		GL11.glScaled(sX, sY, sZ);
		GL11.glPushMatrix();
		GL11.glRotated(te.getControlState(id)*140 - 70, 1, 0, 0);
		setHighlight(te,id);
		bindTexture(new ResourceLocation("tardismod","textures/models/TardisConsoleLever.png"));
		lever.render(null, 0F, 0F, 0F, 0F, 0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glColor3d(1, 1, 1);
		bindTexture(new ResourceLocation("tardismod","textures/models/TardisConsoleLeverBase.png"));
		leverBase.render(null,0F,0F,0F,0F,0F,0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
	
	private void renderSpecialLever(Tessellator tess, TardisConsoleTileEntity te, int id, double x, double y, double z,double rX,double rY,double rZ, double sX, double sY, double sZ)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glRotated(rZ, 0, 0, 1);
		GL11.glRotated(rY, 0, 1, 0);
		GL11.glRotated(rX, 1, 0, 0);
		GL11.glScaled(sX, sY, sZ);
		GL11.glPushMatrix();
		GL11.glRotated(70 - te.getControlState(id)*140, 0, 0, 1);
		GL11.glTranslated(-0.03125, -0.15625, 0);
		setHighlight(te,id);
		bindTexture(new ResourceLocation("tardismod","textures/models/SpecialLever.png"));
		specLever.render(null, 0F, 0F, 0F, 0F, 0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glColor3d(1, 1, 1);
		GL11.glTranslated(0, 0, 0.0625);
		bindTexture(new ResourceLocation("tardismod","textures/models/TardisConsoleLeverBase.png"));
		leverBase.render(null,0F,0F,0F,0F,0F,0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
	
	public void renderScreen(Tessellator tess, TardisConsoleTileEntity te, int id, String texture, double x, double y, double z,double rX,double rY,double rZ, double sX, double sY, double sZ)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glRotated(rZ, 0, 0, 1);
		GL11.glRotated(rY, 0, 1, 0);
		GL11.glRotated(rX, 1, 0, 0);
		GL11.glScaled(sX, sY, sZ);
		GL11.glColor3d(1, 1, 1);
		GL11.glPushMatrix();
		GL11.glTranslated(0, -0.03125, -0.0625);
		bindTexture(new ResourceLocation("tardismod","textures/models/ScreenFrame.png"));
		screenFrame.render(null, 0, 0, 0, 0, 0, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		bindTexture(new ResourceLocation("tardismod","textures/models/screen/"+texture+".png"));
		screen.render(null, 0, 0, 0, 0, 0, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
		GL11.glColor3d(1, 1, 1);
	}
	
	public void renderPushSwitch(Tessellator tess, TardisConsoleTileEntity tce, int id, double x, double y, double z,double rX,double rY,double rZ, double sX, double sY, double sZ)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glRotated(rZ, 0, 0, 1);
		GL11.glRotated(rY, 0, 1, 0);
		GL11.glRotated(rX, 1, 0, 0);
		GL11.glScaled(sX, sY, sZ);
		GL11.glPushMatrix();
		GL11.glTranslated(0.03125, 0, 0.03125);
		GL11.glColor3d(1, 1, 1);
		bindTexture(new ResourceLocation("tardismod","textures/models/SonicScrewdriverHolder.png"));
		holder.render(null, 0F, 0F, 0F, 0F, 0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslated(0, -0.03125 - (0.3 * tce.getControlState(id)), 0);
		setHighlight(tce,id);
		bindTexture(new ResourceLocation("tardismod","textures/models/PushLever.png"));
		pushSwitch.render(null,0F,0F,0F,0F,0F,0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
		GL11.glColor3d(1, 1, 1);
	}
	

	
	private void renderButton(Tessellator tess, TardisConsoleTileEntity tce, int id, double x, double y, double z,double rX,double rY,double rZ, double sX, double sY, double sZ)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glRotated(rZ, 0, 0, 1);
		GL11.glRotated(rY, 0, 1, 0);
		GL11.glRotated(rX, 1, 0, 0);
		GL11.glScaled(sX, sY, sZ);
		GL11.glPushMatrix();
		GL11.glTranslated(0.03125, 0, 0.03125);
		GL11.glColor3d(1, 1, 1);
		bindTexture(new ResourceLocation("tardismod","textures/models/SonicScrewdriverHolder.png"));
		holder.render(null, 0F, 0F, 0F, 0F, 0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslated(0, -0.0015 + (0.06 * tce.getControlState(id)), 0);
		setHighlight(tce,id);
		bindTexture(new ResourceLocation("tardismod","textures/models/PushLever.png"));
		pushSwitch.render(null,0F,0F,0F,0F,0F,0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
		GL11.glColor3d(1, 1, 1);
	}
	
	private void renderDimControls(Tessellator tess, TardisConsoleTileEntity tce)
	{
		renderLever(tess,tce,60, 1.3,-0.23,-0.45, -45, 90, 0, 0.4,0.6,0.6);
	}
	
	private void renderXControls(Tessellator tess, TardisConsoleTileEntity tce)
	{
		renderLever(tess, tce, 10, -0.4, -0.28, -1.23, -45, 180, 0, 0.6,0.6,0.6);	//x1
		renderLever(tess, tce, 11, 0, -0.28, -1.23, -45, 180, 0, 0.6,0.6,0.6);			//x2
		renderLever(tess, tce, 12, 0.4, -0.28, -1.23, -45, 180, 0, 0.6,0.6,0.6);	//x3
		renderLever(tess, tce, 13, 0, -0.65, -0.82, -45, 180, 0, 0.6,0.6,0.6);		//x4
		renderWheel(tess, tce, 14, -0.35, -0.67, -0.81, -45, 180, 0, 0.3,0.3,0.3);	//x5
		renderWheel(tess, tce, 15, 0.35, -0.67, -0.81, -45, 180, 0, 0.3,0.3,0.3);	//x6
	}
	
	private void renderZControls(Tessellator tess, TardisConsoleTileEntity tce)
	{
		renderLever(tess, tce, 20, 0.3, -0.65, 0.78,   -45, 0, 0, 0.3,0.6,0.6);
		renderLever(tess, tce, 21, 0.1, -0.65, 0.78,   -45, 0, 0, 0.3,0.6,0.6);
		renderLever(tess, tce, 22, -0.1, -0.65, 0.78,  -45, 0, 0, 0.3,0.6,0.6);
		renderLever(tess, tce, 23, -0.3, -0.65, 0.78,  -45, 0, 0, 0.3,0.6,0.6);
		renderWheel(tess, tce, 24, 0.40, -0.28, 1.25,  -45, 0, 0, 0.3,0.3,0.3);
		renderWheel(tess, tce, 25, -0.40, -0.28, 1.25, -45, 0, 0, 0.3,0.3,0.3);
	}
	
	private void renderYControls(Tessellator tess, TardisConsoleTileEntity tce)
	{
		renderLever(tess, tce, 30, 0.78, -0.65, 0.3,	-45, 90, 0, 0.3,0.6,0.6);
		renderLever(tess, tce, 31, 0.78, -0.65, 0.15,	-45, 90, 0, 0.3,0.6,0.6);
		renderLever(tess, tce, 32, 0.78, -0.65, 0,		-45, 90, 0, 0.3,0.6,0.6);
		renderLever(tess, tce, 33, 0.78, -0.65, -0.15,	-45, 90, 0, 0.3,0.6,0.6);
		renderPushSwitch(tess,tce, 34, 0.81, -0.68, -0.3,-45,90, 0, 0.5,  0.5,  0.5);
	}
	
	private void renderFlightControls(Tessellator tess, TardisConsoleTileEntity tce)
	{
		renderButton(tess,tce, 40, 0,-0.23,1.3, 45,180,0, 1,1,1);
		renderLever(tess,tce,  41, 1.3,-0.23,-0.8, -45,90,0, 0.8,0.6,0.6);
		renderSpecialLever(tess,tce, 42, -1.16,-0.35,0.8, 0,180,-45, 0.8,0.8,0.8);
	}
	
	private void renderSchematicSelector(Tessellator tess, TardisConsoleTileEntity tce,String s, int id, double x, double y, double z,double rX,double rY,double rZ, double sX, double sY, double sZ)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glRotated(rZ, 0, 0, 1);
		GL11.glRotated(rY, 0, 1, 0);
		GL11.glRotated(rX, 1, 0, 0);
		GL11.glScaled(sX, sY, sZ);
		GL11.glPushMatrix();
		GL11.glTranslated(-0.0125, -0.03, 0.0025);
		bindTexture(new ResourceLocation("tardismod","textures/models/SchemaDisplay.png"));
		schemaDisplay.render(null,0F,0F,0F,0F,0F,0.0625F);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glDepthMask(false);
		GL11.glScaled(0.025, 0.025, 0.025);
		fontRenderer.drawString(s.length() > 12 ? s.substring(0,12):s, 0, 0, 16579836);
		GL11.glDepthMask(true);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
		GL11.glColor3d(1, 1, 1);
	}
	
	private void renderSchemaChooser(Tessellator tess, TardisConsoleTileEntity tce)
	{
		renderSchematicSelector(tess,tce,tce.schemaChooserString,52, 1.1,-0.4,-0.1, -45,-90,0, 0.4,0.4,0.2);
		renderButton(tess,tce,50, 1.05,-0.42,0.8, 45,-90,0,0.5,0.5,0.5);
		renderButton(tess,tce,51, 1.19,-0.29,0.8, 45,-90,0,0.5,0.5,0.5);
	}
	
	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z)
	{
		if(fontRenderer == null)
			fontRenderer = getFontRenderer();
		
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
			renderButton(tess,tce,5, -1,-0.53,-0.64, 45,90,0, 0.5,0.5,0.5);
			renderScrewdriverHolder(tess,tce,x,y,z);
			renderScrewdriver(tess,tce,x,y,z);
			renderGauge(tess, tce, 0, -0.68, -0.72, 0.15, 45, -90, 0, 0.75, 0.75, 0.75);
			renderGauge(tess, tce, 1, -0.68, -0.72, -0.125, 45, -90, 0, 0.75, 0.75, 0.75);
			renderGauge(tess, tce, 2, -0.68, -0.72, -0.4, 45, -90, 0, 0.75, 0.75, 0.75);
			renderWheel(tess, tce, 3, -1.25, -0.3, -0.4, 45, 90, -0, 0.5, 0.5, 0.5); //Facing
			renderLever(tess, tce, 4, -1.23, -0.28, 0.4, -45, -90, 0, 0.6,0.6,0.6); //Speed
			renderDimControls(tess,tce);
			renderXControls(tess,tce);
			renderYControls(tess,tce);
			renderZControls(tess,tce);
			renderFlightControls(tess,tce);
			renderSchemaChooser(tess,tce);
			renderScreen(tess,tce, 100, "main", -0.7, -0.325, -1.20, -45,180,0,0.6,0.6,0.6);
		}
		GL11.glPopMatrix();
	}

}
