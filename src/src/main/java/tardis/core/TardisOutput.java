package tardis.core;

import tardis.TardisMod;

public class TardisOutput
{
	public enum Priority { NONE, ERROR, WARNING, INFO, DEBUG, OLDDEBUG }
	public static Priority defaultPriority = Priority.INFO;
	
	public static void print(String descriptor,String message,Priority prio)
	{
		if(prio.ordinal() <= TardisMod.priorityLevel.ordinal())
			System.out.println("[TM][" + descriptor + "]" + message);
	}
	
	public static void print(String descriptor,Object toStr, String message,Priority prio)
	{
		if(prio.ordinal() <= TardisMod.priorityLevel.ordinal())
			System.out.println("[TM][" + descriptor + "][" + toStr.toString() + "]" + message);
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
