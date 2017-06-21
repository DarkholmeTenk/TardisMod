package tardis.client.renderer.gallifreyan;

import org.lwjgl.opengl.GL11;

import io.darkcraft.darkcore.mod.helpers.MathHelper;

public class GallifreyanHelper
{
	private static float getLW()
	{
		return 1.5f;
	}

	public static void arc(double x, double y, double r, double startAngle, double endAngle, float w, boolean f)
	{
		double degSeg = MathHelper.clamp(1/(r*0.5), 8, 90);
		GL11.glLineWidth(getLW() * w);
		GL11.glBegin(f ? GL11.GL_TRIANGLE_STRIP : GL11.GL_LINE_STRIP);
		double delta = endAngle - startAngle;
		double angle = startAngle;
		for(int i = 0; i < (delta / degSeg); i++)
		{
			//if(angle > 90) continue;
			GL11.glVertex3d(x+(r*MathHelper.sin(angle)), y+(r*MathHelper.cos(angle)), 0);
			if(f)
				GL11.glVertex3d(x, y, 0);
			angle += degSeg;
		}
		angle = endAngle;
		GL11.glVertex3d(x+(r*MathHelper.sin(angle)), y+(r*MathHelper.cos(angle)), 0);
		GL11.glEnd();
	}

	public static void arc(double x, double y, double r, double startAngle, double endAngle)
	{
		arc(x,y,r,startAngle,endAngle,1,false);
	}

	public static void line(double x1, double y1, double x2, double y2)
	{
		GL11.glLineWidth(getLW());
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(x1, y1, 0);
		GL11.glVertex3d(x2, y2, 0);
		GL11.glEnd();
	}

	public static void pre(float scale, float xo, float yo)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef(xo, yo, 0);
		GL11.glScalef(scale, -scale, scale);
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

	public static void post()
	{
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
}
