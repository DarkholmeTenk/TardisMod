package tardis.common.core.flight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FlightModifierRegistry
{
	private static HashMap<String,IFlightModifier> map = new HashMap();
	private static List<String> ids = new ArrayList();

	public static boolean registerFlightModifier(IFlightModifier modifier)
	{
		String id = modifier.getID();
		if(map.containsKey(id)) return false;
		map.put(id, modifier);
		ids.add(id);
		Collections.sort(ids);
		return true;
	}

	public static List<String> getIDs()
	{
		return ids;
	}

	public static IFlightModifier getFlightModifier(String id)
	{
		if((id == null) || id.isEmpty()) return null;
		return map.get(id);
	}

	/**
	 * @param ids a semicolon separated list of modifier ids
	 * @return a list of IFlightModifiers
	 */
	public static List<IFlightModifier> getFlightModifierList(String ids)
	{
		List<IFlightModifier> list = new ArrayList();
		String[] data = ids.split(";");
		for(String d : data)
		{
			IFlightModifier m = getFlightModifier(d);
			if(m != null)
				list.add(m);
		}
		return list;
	}

	public static String getString(List<IFlightModifier> mods)
	{
		String s = "";
		for(IFlightModifier m : mods)
		{
			if(m == null) continue;
			s += m.getID() + ";";
		}
		if(s.endsWith(";"))
			s = s.substring(0, s.length()-1);
		return s;
	}
}
