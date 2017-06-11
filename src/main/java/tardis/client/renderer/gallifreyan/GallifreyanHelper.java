package tardis.client.renderer.gallifreyan;

import org.lwjgl.opengl.GL11;

import io.darkcraft.darkcore.mod.helpers.MathHelper;

public class GallifreyanHelper
{
	private static float getLW()
	{
		return 3;
	}

	public static void arc(double x, double y, double r, double startAngle, double endAngle)
	{
		double degSeg = MathHelper.clamp(1/r, 4, 90);
		GL11.glLineWidth(getLW());
		GL11.glBegin(GL11.GL_LINE_STRIP);
		double delta = endAngle - startAngle;
		double angle = startAngle;
		for(int i = 0; i < (delta / degSeg); i++)
		{
			//if(angle > 90) continue;
			GL11.glVertex3d(x+(r*MathHelper.sin(angle)), y+(r*MathHelper.cos(angle)), 0);
			angle += degSeg;
		}
		angle = endAngle;
		GL11.glVertex3d(x+(r*MathHelper.sin(angle)), y+(r*MathHelper.cos(angle)), 0);
		GL11.glEnd();
	}

	public static void line(double x1, double y1, double x2, double y2)
	{
		GL11.glLineWidth(getLW());
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(x1, y1, 0);
		GL11.glVertex3d(x2, y2, 0);
		GL11.glEnd();
	}
}
