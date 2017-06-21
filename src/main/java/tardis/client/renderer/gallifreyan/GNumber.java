package tardis.client.renderer.gallifreyan;

import java.util.ArrayList;
import java.util.List;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import io.darkcraft.darkcore.mod.datastore.Colour;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.RenderHelper;

import tardis.client.renderer.gallifreyan.Letter.LinePoint;
import tardis.client.renderer.gallifreyan.Word.LineSet;

public class GNumber
{
	private final float rStep;

	private final LineSet[] lines;
	private final LinePoint[] circles;

	private final boolean negative;
	private final int beforeDec;
	private final int afterDec;

	private Colour colour = Colour.white;

	private GNumber(String number)
	{
		negative = number.startsWith("-");
		if(negative)
			number = number.substring(1);
		int beforeDec = 0;
		int afterDec = 0;
		int total = 0;
		boolean dec = false;
		for(int i = 0; i < number.length(); i++)
		{
			if(number.charAt(i) == '.')
			{
				dec = true;
				continue;
			}
			if(dec)
				afterDec++;
			else
				beforeDec++;
			total++;
		}
		this.beforeDec = beforeDec;
		this.afterDec = afterDec;
		rStep = 1f/(total+1);

		List<LinePoint> circles = new ArrayList<>();
		List<LineSet> lines = new ArrayList<>();
		boolean a = false;
		for(int i = 0; i < number.length(); i++)
		{
			char c = number.charAt(i);
			if(c == '.')
			{
				a = true;
				continue;
			}
			int num = c - '0';
			float r = ((total) - i) * rStep;
			if(a)
				r+=rStep;
			float r2 = r + rStep;
			boolean circ = num >= 5;
			num %= 5;
			if((num == 1) || (num == 3))
				lines.add(new LineSet(new LinePoint(0,circ?-r:r), new LinePoint(0,circ?-r2:r2)));
			if((num == 2) || (num == 3) || (num == 4))
			{
				float xr = (float) MathHelper.sin(30);
				float yr = (float) MathHelper.cos(30);
				float nr = circ ? -r: r;
				float nr2 = circ ? -r2: r2;
				lines.add(new LineSet(new LinePoint(xr*nr,yr*nr), new LinePoint(xr*nr2,yr*nr2)));
				lines.add(new LineSet(new LinePoint(-xr*nr,yr*nr), new LinePoint(-xr*nr2,yr*nr2)));
				if(num == 4)
				{
					lines.add(new LineSet(new LinePoint(xr*nr,-yr*nr), new LinePoint(xr*nr2,-yr*nr2)));
					lines.add(new LineSet(new LinePoint(-xr*nr,-yr*nr), new LinePoint(-xr*nr2,-yr*nr2)));
				}
			}
			if(circ)
				circles.add(new LinePoint(0,(r+r2)/2));
		}
		this.lines = lines.toArray(new LineSet[0]);
		this.circles = circles.toArray(new LinePoint[0]);
	}

	private void renderCircle(int i, boolean d)
	{
		float r = rStep * (i+1);
		GallifreyanHelper.arc(0, 0, r, 0, 360, d ? 3 : 1, false);
		if(negative && (i == 0))
		{
			GallifreyanHelper.line(r,0,-r,0);
		}
	}

	public void setColour(Colour colour)
	{
		this.colour = colour;
	}

	public void render(float scale, float xo, float yo)
	{
		GallifreyanHelper.pre(scale, xo, yo);
		RenderHelper.colour(colour);
		for(int i = 0; i < afterDec; i++)
			renderCircle(i, false);
		renderCircle(afterDec, true);
		for(int i = 0; i < beforeDec; i++)
			renderCircle(i+afterDec+1, false);
		for(LinePoint p : circles)
			GallifreyanHelper.arc(p.x, p.y, rStep/2, 0, 360);
		for(LineSet s : lines)
			GallifreyanHelper.line(s.a.x, s.a.y, s.b.x, s.b.y);
		GallifreyanHelper.post();
	}

	private static final LoadingCache<String,GNumber> cache = CacheBuilder.newBuilder()
			.build(CacheLoader.from(GNumber::new));

	public static GNumber get(int integer)
	{
		return cache.getUnchecked(String.valueOf(integer));
	}
}
