package tardis.client.renderer.gallifreyan;

import java.util.HashMap;
import java.util.Map;

public class Vowel
{
	public final Type type;
	public final Line line;

	private Vowel(Type type, Line line)
	{
		this.type = type;
		this.line = line;
	}

	public static enum Type
	{
		OUTER,
		INNER,
		THROUGH
	}

	public static enum Line
	{
		NONE,
		ONE,
	}

	public static Map<String, Vowel> map = new HashMap<>();
	static
	{
		map.put("A", new Vowel(Type.OUTER, Line.NONE));
		map.put("O", new Vowel(Type.INNER, Line.NONE));
		map.put("E", new Vowel(Type.THROUGH, Line.NONE));
		map.put("I", new Vowel(Type.THROUGH, Line.ONE));
		map.put("U", new Vowel(Type.INNER, Line.ONE));
	}
}
