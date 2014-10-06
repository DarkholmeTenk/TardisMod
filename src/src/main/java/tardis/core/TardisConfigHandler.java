package tardis.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import tardis.TardisMod;

public class TardisConfigHandler
{
	private File tardisConfigDir;
	private File tardisSchemaDir;
	
	private HashMap<String,TardisConfigFile> cachedConfigs = new HashMap<String,TardisConfigFile>();
	
	public TardisConfigHandler(File baseConfigDir) throws IOException
	{
		tardisConfigDir = new File(baseConfigDir.toString() + "/tardis/");
		if((!tardisConfigDir.exists()) || (!tardisConfigDir.isDirectory()))
		{
			if(!tardisConfigDir.mkdirs())
				throw new IOException("Couldn't create " + tardisConfigDir.toString());
		}
		tardisSchemaDir = new File(tardisConfigDir.toString() + "/schema/");
		if((!tardisSchemaDir.exists()) || (!tardisSchemaDir.isDirectory()))
		{
			if(!tardisSchemaDir.mkdirs())
				throw new IOException("Couldn't create " + tardisSchemaDir.toString());
		}
	}
	
	public File getSchemaFile(String name)
	{
		File schema = new File(tardisSchemaDir.toString() + "/" + name + ".schema");
		if(!schema.exists())
		{
			try
			{
				InputStream fis = TardisMod.class.getResourceAsStream("/assets/tardismod/schema/"+name+".schema");
				FileOutputStream os = new FileOutputStream(schema);
				
				byte[] buffer = new byte[1024];
				int len;
				while ((len = fis.read(buffer)) != -1)
				{
				    os.write(buffer, 0, len);
				}
				fis.close();
				os.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return schema;
	}
	
	public String[] getSchemas()
	{
		String[] fA = new String[0];
		ArrayList<String> found = new ArrayList<String>();
		try
		{
			InputStream is = TardisMod.class.getResourceAsStream("/assets/tardismod/schema/");
			BufferedReader r = new BufferedReader(new InputStreamReader(is));
			String l;
			while((l = r.readLine())!=null)
			{
				TardisOutput.print("TCH", "TCHSS:"+l);
				if(l.endsWith(".schema") && !l.startsWith("tardis"))
					found.add(l.replace(".schema", ""));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		String[] files = tardisSchemaDir.list();
		for(String s:files)
		{
			TardisOutput.print("TCH", "TCHSS:"+s);
			if(s.endsWith(".schema") && !s.startsWith("tardis"))
				found.add(s.replace(".schema", ""));
		}	
		return found.toArray(fA);
	}
	
	public TardisConfigFile getConfigFile(String name)
	{
		if(cachedConfigs.containsKey(name))
			return cachedConfigs.get(name);
		
		TardisConfigFile tmp = new TardisConfigFile(new File(tardisConfigDir.toString() + "//Tardis" + name + ".cfg"));
		cachedConfigs.put(name, tmp);
		return tmp;
	}
}
