package tardis.common.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import tardis.TardisMod;

public class ConfigHandler
{
	private File tardisConfigDir;
	private File tardisSchemaDir;
	
	private HashMap<String,ConfigFile> cachedConfigs = new HashMap<String,ConfigFile>();
	
	public ConfigHandler(File baseConfigDir) throws IOException
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
				if(fis != null)
				{
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
		return getSchemas(false);
	}
	
	public String[] getSchemas(boolean consoleSchemas)
	{
		String[] fA = new String[0];
		ArrayList<String> found = new ArrayList<String>();
		try
		{
			InputStream is = TardisMod.class.getResourceAsStream("/assets/tardismod/schema/schemaList");
			BufferedReader r = new BufferedReader(new InputStreamReader(is));
			String l;
			while((l = r.readLine())!=null)
			{
				TardisOutput.print("TCH", "TCHSS:"+l);
				if(l.endsWith(".schema") && (l.startsWith("tardis") == consoleSchemas))
				{
					String q = l.replace(".schema", "");
					if(!found.contains(q))
						found.add(q);
				}
			}
			TardisOutput.print("TCH","Read classpath");
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
			{
				String q = s.replace(".schema", "");
				if(!found.contains(q))
					found.add(q);
			}
		}
		Collections.sort(found,String.CASE_INSENSITIVE_ORDER);
		return found.toArray(fA);
	}
	
	public ConfigFile getConfigFile(String name)
	{
		if(cachedConfigs.containsKey(name))
			return cachedConfigs.get(name);
		
		ConfigFile tmp = new ConfigFile(new File(tardisConfigDir.toString() + "//Tardis" + name + ".cfg"));
		cachedConfigs.put(name, tmp);
		return tmp;
	}
}
