package tardis.common.dimension;

import java.nio.IntBuffer;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IRenderHandler;

public class TardisWorldSkyRenderer extends IRenderHandler {

	private static final ResourceLocation locationMoonPhasesPng = new ResourceLocation(
			"textures/environment/moon_phases.png");
	private static final ResourceLocation locationSunPng = new ResourceLocation("tardismod", "textures/environment/sun.png");
	private static final ResourceLocation locationOrangeSunPng = new ResourceLocation("tardismod", "textures/environment/OrangeSun.png");

	IntBuffer occlusionResult = GLAllocation.createDirectIntBuffer(64);
	@Override
	public void render(float p_72714_1_, WorldClient theWorld, Minecraft mc) {

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Vec3 vec3 = theWorld.getSkyColor(mc.renderViewEntity, p_72714_1_);
		float f1 = (float) vec3.xCoord;
		float f2 = (float) vec3.yCoord;
		float f3 = (float) vec3.zCoord;
		float f6;

		if (mc.gameSettings.anaglyph) {
			float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
			float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
			f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
			f1 = f4;
			f2 = f5;
			f3 = f6;
		}

		GL11.glColor3f(f1, f2, f3);
		Tessellator tessellator1 = Tessellator.instance;
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		float f7;
		float f8;
		float f9;
		float f10;
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1);
		OpenGlHelper.glBlendFunc(770, 1, 1, 0);
		GL11.glPushMatrix();
		GL11.glRotatef(((TardisWorldProvider) theWorld.provider).calculateRealCelestialAngle(p_72714_1_) * 360.0F, 0.0F, 1.0F, 0.0F);
		renderStars();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		f6 = 1.0F - 0;
		f7 = 0.0F;
		f8 = 0.0F;
		f9 = 0.0F;
		f10 = 25.0F;

		Minecraft.getMinecraft().renderEngine.bindTexture(locationOrangeSunPng);

		GL11.glTranslatef(f7, f8, f9);
		GL11.glRotatef(-120.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(((TardisWorldProvider) theWorld.provider).calculateRealCelestialAngle(p_72714_1_) * 360.0F, 1.0F, 0.0F, 0.0F);
		tessellator1.startDrawingQuads();
		tessellator1.addVertexWithUV((double) (-f10), 100.0D, (double) (-f10), 0.0D, 0.0D);
		tessellator1.addVertexWithUV((double) f10, 100.0D, (double) (-f10), 1.0D, 0.0D);
		tessellator1.addVertexWithUV((double) f10, 100.0D, (double) f10, 1.0D, 1.0D);
		tessellator1.addVertexWithUV((double) (-f10), 100.0D, (double) f10, 0.0D, 1.0D);
		tessellator1.draw();
		
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		Minecraft.getMinecraft().renderEngine.bindTexture(locationSunPng);
		Tessellator tessellator2 = Tessellator.instance;
		GL11.glRotatef(-((((TardisWorldProvider) theWorld.provider).calculateRealCelestialAngle(p_72714_1_) * 4) * 360.0F), 1F, 2F, 1.5F);
		f10 = 15.0F;
		tessellator2.startDrawingQuads();
		tessellator2.addVertexWithUV((double) (-f10) / 2, 110.0D, (double) (-f10) / 2, 0.0D, 0.0D);
		tessellator2.addVertexWithUV((double) f10 / 2, 110.0D, (double) (-f10) / 2, 1.0D, 0.0D);
		tessellator2.addVertexWithUV((double) f10 / 2, 110.0D, (double) f10 / 2, 1.0D, 1.0D);
		tessellator2.addVertexWithUV((double) (-f10) / 2, 110.0D, (double) f10 / 2, 0.0D, 1.0D);
		tessellator2.draw();
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		
		GL11.glRotatef(-(((TardisWorldProvider) theWorld.provider).calculateRealCelestialAngle(p_72714_1_) * 360.0F), 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(-(((TardisWorldProvider) theWorld.provider).calculateRealCelestialAngle(p_72714_1_) * 2) * 360.0F, 0.2F, 0.5F, 1.3F);

		f10 = 10.0F;
		Minecraft.getMinecraft().renderEngine.bindTexture(locationMoonPhasesPng);
		float f14 = (float) 0;
		float f15 = (float) 0;
		float f16 = (float) 0.25;
		float f17 = (float) 0.5;
		GL11.glRotatef(-90, 1f, 0f, 0f);
		GL11.glRotatef(-90, 0f, 0f, 1f);

		GL11.glRotatef(-(((TardisWorldProvider) theWorld.provider).calculateRealCelestialAngle(p_72714_1_) * 360.0F), 0f, 1f, 1f);
	
		tessellator1.startDrawingQuads();
		tessellator1.setColorRGBA(111, 150, 255, 255);
		tessellator1.addVertexWithUV((double) (-f10) / 2, -100.0D, (double) f10 / 2, (double) f16, (double) f17);
		tessellator1.addVertexWithUV((double) f10 / 2, -100.0D, (double) f10 / 2, (double) f14, (double) f17);
		tessellator1.addVertexWithUV((double) f10 / 2, -100.0D, (double) (-f10) / 2, (double) f14, (double) f15);
		tessellator1.addVertexWithUV((double) (-f10) / 2, -100.0D, (double) (-f10) / 2, (double) f16, (double) f15);
		tessellator1.draw();

		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glPopMatrix();
		GL11.glDepthMask(true);

	}

	private void renderStars() {
		Random random = new Random(10842L);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		for (int i = 0; i < 1500; ++i) {
			double d0 = (double) (random.nextFloat() * 2.0F - 1.0F);
			double d1 = (double) (random.nextFloat() * 2.0F - 1.0F);
			double d2 = (double) (random.nextFloat() * 2.0F - 1.0F);
			double d3 = (double) (0.15F + random.nextFloat() * 0.1F);
			double d4 = d0 * d0 + d1 * d1 + d2 * d2;

			if (d4 < 1.0D && d4 > 0.01D) {
				d4 = 1.0D / Math.sqrt(d4);
				d0 *= d4;
				d1 *= d4;
				d2 *= d4;
				double d5 = d0 * 100.0D;
				double d6 = d1 * 100.0D;
				double d7 = d2 * 100.0D;
				double d8 = Math.atan2(d0, d2);
				double d9 = Math.sin(d8);
				double d10 = Math.cos(d8);
				double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
				double d12 = Math.sin(d11);
				double d13 = Math.cos(d11);
				double d14 = random.nextDouble() * Math.PI * 2.0D;
				double d15 = Math.sin(d14);
				double d16 = Math.cos(d14);

				for (int j = 0; j < 4; ++j) {
					double d17 = 5.0D;
					double d18 = (double) ((j & 2) - 1) * d3;
					double d19 = (double) ((j + 1 & 2) - 1) * d3;
					double d20 = d18 * d16 - d19 * d15;
					double d21 = d19 * d16 + d18 * d15;
					double d22 = d20 * d12 + d17 * d13;
					double d23 = d17 * d12 - d20 * d13;
					double d24 = d23 * d9 - d21 * d10;
					double d25 = d21 * d9 + d23 * d10;
					tessellator.addVertex(d5 + d24, d6 + d22, d7 + d25);
				}
			}
		}

		tessellator.draw();
	}
}
