package tardis.common.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class TardisConfigFile
{
	File confFile;
	HashMap<String,Integer> containedInt	= new HashMap<String,Integer>();
	HashMap<String,Double>  containedDbl	= new HashMap<String,Double>();
	HashMap<String,Boolean> containedBln	= new HashMap<String,Boolean>();
	HashMap<String,String>  containedStr	= new HashMap<String,String>();
	
	public TardisConfigFile(File fileLoc)
	{
		confFile = fileLoc;
		readFile();
	}
	
	private void readFile()
	{
		BufferedReader fileReader = null;
		try
		{
			fileReader = new BufferedReader(new FileReader(confFile));
			String line = null;
			while((line = fileReader.readLine()) != null)
			{
				String[] data = line.split(":");
				if(data.length == 3)
				{
					if(data[0].equals("I"))
						containedInt.put(data[1], Integer.parseInt(data[2]));
					else if(data[0].equals("D"))
						containedDbl.put(data[1], Double.parseDouble(data[2]));
					else if(data[0].equals("B"))
						containedBln.put(data[1], Boolean.parseBoolean(data[2]));
					else if(data[0].equals("S"))
						containedStr.put(data[1], data[2]);
					TardisOutput.print("CF", "DI",line);
				}
			}
		}
		catch(Exception e)
		{
			TardisOutput.print("CF", confFile, e.getMessage());
		}
		finally
		{
			try
			{
				if(fileReader != null)
					fileReader.close();
			}
			catch(IOException e)
			{
				TardisOutput.print("CF", confFile, e.getMessage());
			}
		}
	}
	
	private void writeHashMap(PrintWriter writer, String prefix,HashMap map) throws IOException
	{
		for(Object key : map.keySet())
		{
			writer.println(prefix + ":" + key + ":" + map.get(key));
		}
	}
	
	private void writeFile()
	{
		PrintWriter writer = null;
		try
		{
			writer = new PrintWriter(confFile);
			writeHashMap(writer,"I",containedInt);
			writeHashMap(writer,"D",containedDbl);
			writeHashMap(writer,"B",containedBln);
			writeHashMap(writer,"S",containedStr);
		}
		catch(IOException e)
		{
			TardisOutput.print("CF", confFile, e.getMessage());
		}
		finally
		{
			if(writer != null)
				writer.close();
		}
	}
	
	public int getInt(String name,int defaultValue)
	{
		try
		{
			if(containedInt.containsKey(name))
				return containedInt.get(name);
			
			containedInt.put(name, defaultValue);
			writeFile();
		}
		catch(Exception e)
		{
			TardisOutput.print("CF", confFile, e.getMessage());
		}
		return defaultValue;
	}
	
	public double getDouble(String name,double defaultValue)
	{
		try
		{
			if(containedInt.containsKey(name))
				return containedDbl.get(name);
			
			containedDbl.put(name, defaultValue);
			writeFile();
		}
		catch(Exception e)
		{
			TardisOutput.print("CF", confFile, e.getMessage());
		}
		return defaultValue;
	}
	
	public boolean getBoolean(String name,boolean defaultValue)
	{
		try
		{
			if(containedInt.containsKey(name))
				return containedBln.get(name);
			
			containedBln.put(name, defaultValue);
			writeFile();
		}
		catch(Exception e)
		{
			TardisOutput.print("CF", confFile, e.getMessage());
		}
		return defaultValue;
	}
	
	public String getString(String name,String defaultValue)
	{
		try
		{
			if(containedInt.containsKey(name))
				return containedStr.get(name);
			
			containedStr.put(name, defaultValue);
			writeFile();
		}
		catch(Exception e)
		{
			TardisOutput.print("CF", confFile, e.getMessage());
		}
		return defaultValue;
	}
}
