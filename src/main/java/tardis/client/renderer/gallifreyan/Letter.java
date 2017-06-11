package tardis.client.renderer.gallifreyan;

import java.util.LinkedHashSet;
import java.util.Set;

import io.darkcraft.darkcore.mod.helpers.MathHelper;

public class Letter
{
	private final Consonant consonant;
	private final Vowel vowel;

	public Letter(Consonant consonant, Vowel vowel)
	{
		this.consonant = consonant;
		this.vowel = vowel;
	}

	public Set<LinePoint> getLinePoints(double xC, double yC, double pX, double pY, double pR, double sA, double eA, double mA)
	{
		if(consonant != null)
			return consonant.getLinePoints(xC, yC, pX, pY, pR, sA, eA, mA, vowel);
		else
		{
			if(vowel.line == Vowel.Line.NONE)
				return null;
			Set<LinePoint> points = new LinkedHashSet<>();
			GallifreyanHelper.arc(pX, pY, pR, sA, eA);
			double theta = eA-sA;
			double w = pR*MathHelper.sin(theta/2);
			double dR = w / 2;
			if(vowel.type == Vowel.Type.INNER)
			{
				double nR = pR - w;
				double dX = pX+(nR*MathHelper.sin(mA));
				double dY = pY+(nR*MathHelper.cos(mA));
				points.add(new LinePoint(dX+(dR*MathHelper.sin(mA+180)),dY+(dR*MathHelper.cos(mA+180))));
			}
			if(vowel.type == Vowel.Type.THROUGH)
				points.add(new LinePoint(xC+(dR*MathHelper.sin(mA+180)),yC+(dR*MathHelper.cos(mA+180))));
			return points;
		}
	}

	public void render(double xC, double yC, double pX, double pY, double pR, double sA, double eA, double mA)
	{
		if(consonant != null)
		{
			consonant.render(xC, yC, pX, pY, pR, sA, eA, mA, vowel);
		}
		else
		{
			GallifreyanHelper.arc(pX, pY, pR, sA, eA);
			double theta = eA-sA;
			double w = pR*MathHelper.sin(theta/2);
			double dR = w / 2;
			if(vowel.type == Vowel.Type.OUTER)
			{
				double nR = pR + (w);
				GallifreyanHelper.arc(pX+(nR*MathHelper.sin(mA)), pY+(nR*MathHelper.cos(mA)), dR, 0, 360);
			}
			if(vowel.type == Vowel.Type.INNER)
			{
				double nR = (pR - (w));
				GallifreyanHelper.arc(pX+(nR*MathHelper.sin(mA)), pY+(nR*MathHelper.cos(mA)), dR, 0, 360);
			}
			if(vowel.type == Vowel.Type.THROUGH)
			{
				GallifreyanHelper.arc(xC, yC, dR, 0, 360);
			}
		}
	}

	public static class LinePoint
	{
		public final double x;
		public final double y;

		public LinePoint(double x, double y)
		{
			this.x = x;
			this.y = y;
		}
	}
}
