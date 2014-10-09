package tardis.common.core;

import tardis.TardisMod;

public class TardisOutput
{
	public enum Priority { NONE, ERROR, WARNING, INFO, DEBUG, OLDDEBUG }
	public static Priority defaultPriority = Priority.INFO;
	
	public static void print(String descriptor,String message,Priority prio)
	{
		String toDisplay = "[TM][" + descriptor + "]" + message;
		if(prio.ordinal() <= TardisMod.priorityLevel.ordinal())
		{
			if(prio.equals(Priority.ERROR))
				System.err.println(toDisplay);
			else
				System.out.println(toDisplay);
		}
	}
	
	public static void print(String descriptor,Object toStr, String message,Priority prio)
	{
		print(descriptor,"["+toStr.toString()+"]"+message,prio);
	}
	
	public static void print(String descriptor,String message)
	{
		print(descriptor,message,defaultPriority);
	}
	
	public static void print(String descriptor,Object toStr, String message)
	{
		print(descriptor,toStr,message,defaultPriority);
	}
}
