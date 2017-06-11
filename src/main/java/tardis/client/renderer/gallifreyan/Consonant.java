package tardis.client.renderer.gallifreyan;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import io.darkcraft.darkcore.mod.helpers.MathHelper;

import tardis.client.renderer.gallifreyan.Letter.LinePoint;

public class Consonant
{
	private final CircleType type;
	private final Decor decor;

	private Consonant(CircleType type, Decor decor)
	{
		this.type = type;
		this.decor = decor;
	}

	private static enum CircleType
	{
		PROTRUDE,
		ABOVE,
		SEMI,
		THROUGH
	}

	private static enum Decor
	{
		NONE,
		DOUBLEDOT,
		TRIPLEDOT,
		TRIPLELINE,
		SINGLELINE,
		DOUBLELINE
	}

	public static Map<String, Consonant> map = new HashMap<>();

	static
	{
		String[] blobs = new String[] {
				"B", "CH", "D", "F", "G", "H",
				"J", "K", "L", "M", "N", "P",
				"T", "SH", "R", "S", "V", "W",
				"TH", "Y", "Z", "NG", "QU", "X"
		};

		int i = 0;
		for(CircleType t : CircleType.values())
			for(Decor d : Decor.values())
				map.put(blobs[i++], new Consonant(t,d));
	}

	public void render(double xC, double yC, double pX, double pY, double pR, double sA, double eA, double mA, Vowel v)
	{
		double theta = eA-sA;
		double w = pR*MathHelper.sin(theta/2);
		double newRad = pR;
		if((type == CircleType.THROUGH) || (type == CircleType.ABOVE))
			GallifreyanHelper.arc(0, 0, pR, sA, eA);
		if(type == CircleType.THROUGH)
			GallifreyanHelper.arc(xC, yC, w, 0, 360);
		if(type == CircleType.SEMI)
		{
			newRad = pR * MathHelper.cos(theta / 2);
			xC = pX+(newRad * MathHelper.sin(mA));
			yC = pY+(newRad * MathHelper.cos(mA));
			GallifreyanHelper.arc(xC, yC, w, mA+90, mA+270);
		}
		if(type == CircleType.PROTRUDE)
		{
			newRad = (pR * MathHelper.cos(theta / 2)) - w;
			xC = pX+(newRad * MathHelper.sin(mA));
			yC = pY+(newRad * MathHelper.cos(mA));
			w = (2*w)/(2*MathHelper.sin(45));
			GallifreyanHelper.arc(xC, yC, w, mA+45, mA+315);
		}
		if(type == CircleType.ABOVE)
		{
			newRad = (pR - w) * 0.9;
			xC = pX+(newRad * MathHelper.sin(mA));
			yC = pY+(newRad * MathHelper.cos(mA));
			GallifreyanHelper.arc(xC, yC, w, 0, 360);
		}

		if(decor == Decor.DOUBLEDOT)
		{
			double dw = 0.1 * w;
			double aw = w * 0.8;
			GallifreyanHelper.arc(xC+(aw*MathHelper.sin(mA+195)),yC+(aw*MathHelper.cos(mA+195)),dw,0,360);
			GallifreyanHelper.arc(xC+(aw*MathHelper.sin(mA+165)),yC+(aw*MathHelper.cos(mA+165)),dw,0,360);
		}

		if(decor == Decor.TRIPLEDOT)
		{
			double dw = 0.1 * w;
			double aw = w * 0.8;
			GallifreyanHelper.arc(xC+(aw*MathHelper.sin(mA+210)),yC+(aw*MathHelper.cos(mA+210)),dw,0,360);
			GallifreyanHelper.arc(xC+(aw*MathHelper.sin(mA+180)),yC+(aw*MathHelper.cos(mA+180)),dw,0,360);
			GallifreyanHelper.arc(xC+(aw*MathHelper.sin(mA+150)),yC+(aw*MathHelper.cos(mA+150)),dw,0,360);
		}

		if(v != null)
		{
			double dR = w / 5;
			if(v.type == Vowel.Type.OUTER)
			{
				double nR = pR + (w / 3);
				GallifreyanHelper.arc(pX+(nR*MathHelper.sin(mA)), pY+(nR*MathHelper.cos(mA)), dR, 0, 360);
			}
			if(v.type == Vowel.Type.INNER)
			{
				double dX = xC + (w * MathHelper.sin(mA + 100));
				double dY = yC + (w * MathHelper.cos(mA + 100));
				GallifreyanHelper.arc(dX, dY, dR, 0, 360);
			}
			if(v.type == Vowel.Type.THROUGH)
			{
				GallifreyanHelper.arc(xC, yC, dR, 0, 360);
			}
		}
	}

	public Set<LinePoint> getLinePoints(double xC, double yC, double pX, double pY, double pR, double sA, double eA, double mA, Vowel v)
	{
		Set<LinePoint> points = new LinkedHashSet<>();
		double theta = eA-sA;
		double w = pR*MathHelper.sin(theta/2);
		double newRad = pR;
		if(type == CircleType.SEMI)
		{
			newRad = pR * MathHelper.cos(theta / 2);
			xC = pX+(newRad * MathHelper.sin(mA));
			yC = pY+(newRad * MathHelper.cos(mA));
		}
		if(type == CircleType.PROTRUDE)
		{
			newRad = (pR * MathHelper.cos(theta / 2)) - w;
			xC = pX+(newRad * MathHelper.sin(mA));
			yC = pY+(newRad * MathHelper.cos(mA));
			w = (2*w)/(2*MathHelper.sin(45));
		}
		if(type == CircleType.ABOVE)
		{
			newRad = (pR - w) * 0.9;
			xC = pX+(newRad * MathHelper.sin(mA));
			yC = pY+(newRad * MathHelper.cos(mA));
		}

		if((decor == Decor.SINGLELINE) || (decor == Decor.DOUBLELINE) || (decor == Decor.TRIPLELINE))
			points.add(new LinePoint(xC+(w*MathHelper.sin(mA+155)),yC+(w*MathHelper.cos(mA+155))));
		if((decor == Decor.DOUBLELINE) || (decor == Decor.TRIPLELINE))
			points.add(new LinePoint(xC+(w*MathHelper.sin(mA+170)),yC+(w*MathHelper.cos(mA+170))));
		if(decor == Decor.TRIPLELINE)
			points.add(new LinePoint(xC+(w*MathHelper.sin(mA+195)),yC+(w*MathHelper.cos(mA+195))));

		if((v != null) && (v.line != Vowel.Line.NONE))
		{
			double dR = w / 5;
			if(v.type == Vowel.Type.INNER)
			{
				double dX = xC + (w * MathHelper.sin(mA + 100));
				double dY = yC + (w * MathHelper.cos(mA + 100));
				points.add(new LinePoint(dX+(dR*MathHelper.sin(mA+180)),dY+(dR*MathHelper.cos(mA+180))));
			}
			if(v.type == Vowel.Type.THROUGH)
				points.add(new LinePoint(xC+(dR*MathHelper.sin(mA+180)),yC+(dR*MathHelper.cos(mA+180))));
		}
		if(points.isEmpty())
			return null;
		return points;
	}
}
