package tardis.client.renderer.gallifreyan;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import io.darkcraft.darkcore.mod.datastore.Colour;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.RenderHelper;

import tardis.client.renderer.gallifreyan.Letter.LinePoint;

public class Word
{
	private final Letter[] letters;
	private final LineSet[] lines;

	private Colour colour = Colour.white;

	public Word(String word)
	{
		word = word.toUpperCase();
		List<Letter> letters = new ArrayList<>();
		int i = 0;
		while(i < word.length())
		{
			Consonant c = null;
			Vowel v = null;
			if(i < (word.length() - 1))
			{
				String two = word.substring(i, i+2);
				if(Consonant.map.containsKey(two))
				{
					c = Consonant.map.get(two);
					i += 2;
				}
			}
			if(c == null)
			{
				String one = word.substring(i, i+1);
				if(Consonant.map.containsKey(one))
				{
					c = Consonant.map.get(one);
					i += 1;
				}
			}
			if(i < word.length())
			{
				String one = word.substring(i, i+1);
				if(Vowel.map.containsKey(one))
				{
					v = Vowel.map.get(one);
					i += 1;
				}
			}
			if((v == null) && (c == null))
				i++;
			else
				letters.add(new Letter(c,v));
		}
		this.letters = letters.toArray(new Letter[letters.size()]);
		lines = calculateLines(this.letters);
	}

	public Word setColour(Colour c)
	{
		colour = c;
		return this;
	}

	public void render()
	{
		render(1,0,0);
	}

	public void render(float scale, float xo, float yo)
	{
		double part = 1 / Math.pow(letters.length, 0.3);

		GallifreyanHelper.pre(scale, xo, yo);
		RenderHelper.colour(colour);
		double segAngle = 360.0 / letters.length;
		double offset = 180 + (segAngle * ((1 + part) / 2));
		for(int i = 0 ; i < letters.length; i++)
			GallifreyanHelper.arc(0, 0, 1, offset - ((i+part)*segAngle), offset - ((i)*segAngle));
		for(int i = 0; i < letters.length; i++)
		{
//			if(i > 0) continue;
			double eA = offset-((i+part)*segAngle);
			double sA = offset-((i+1)*segAngle);
			double a = (eA + sA) / 2;
			//double a = (i + 0.83333) * segAngle;
			double x = MathHelper.sin(a);
			double y = MathHelper.cos(a);
			letters[i].render(x, y, 0, 0, 1, sA, eA, a);
		}
		for(LineSet l : lines)
			GallifreyanHelper.line(l.a.x, l.a.y, l.b.x, l.b.y);
		GallifreyanHelper.post();
	}

	private static LineSet[] calculateLines(Letter[] letters)
	{
		ListMultimap<Letter, LinePoint> pointMap = MultimapBuilder.hashKeys().linkedListValues().build();
		double part = 1 / Math.pow(letters.length, 0.3);
		double segAngle = 360.0 / letters.length;
		double offset = 180 + (segAngle * ((1 + part) / 2));
		for(int i = 0; i < letters.length; i++)
		{
			double eA = offset-((i+part)*segAngle);
			double sA = offset-((i+1)*segAngle);
			double a = (eA + sA) / 2;
			double x = MathHelper.sin(a);
			double y = MathHelper.cos(a);
			letters[i].render(x, y, 0, 0, 1, sA, eA, a);
			Set<LinePoint> points = letters[i].getLinePoints(x, y, 0, 0, 1, sA, eA, a);
			if(points != null)
				pointMap.putAll(letters[i], points);
		}

		List<LineSet> lines = new ArrayList<>();
		for(int i = 0; i < letters.length; i++)
		{
			int k = 0;
			lp:
			for(LinePoint l : pointMap.get(letters[i]))
			{
				for(int j = letters.length - 1; j > i; j--)
				{
					List<LinePoint> np = pointMap.get(letters[j]);
					if(np.isEmpty())
						continue;
					ListIterator<LinePoint> iter = np.listIterator(np.size());
					lines.add(new LineSet(l, iter.previous()));
					iter.remove();
					continue lp;
				}
				double a = offset-(((i+(letters.length / 2)) +(part/2))*segAngle);
				a += (part * segAngle * (k++ / 6.0));
				lines.add(new LineSet(l, new LinePoint(MathHelper.sin(a), MathHelper.cos(a))));
			}
		}
		return lines.toArray(new LineSet[0]);
	}

	public static class LineSet
	{
		protected final LinePoint a;
		protected final LinePoint b;

		LineSet(LinePoint a, LinePoint b)
		{
			this.a = a;
			this.b = b;
		}
	}
}
