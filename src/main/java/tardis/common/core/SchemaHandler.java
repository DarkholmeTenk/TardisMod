package tardis.common.core;

import io.darkcraft.darkcore.mod.config.ConfigHandler;

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
import tardis.common.core.schema.PartBlueprint;

public class SchemaHandler
{
	private File tardisSchemaDir;
	
	public SchemaHandler(ConfigHandler handler) throws IOException
	{
		tardisSchemaDir = new File(handler.getConfigDir(),"schemas/");
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
	
	public boolean addSchema(String l, boolean consoleSchemas)
	{
		return (l.endsWith(".schema") && !l.contains(".diff") && (l.startsWith("tardisConsole") == consoleSchemas) && !l.startsWith("tardisHidden"));
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
				if(addSchema(l,consoleSchemas))
				{
					TardisOutput.print("CH", "Added schema " + l +" " + consoleSchemas);
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
			if(addSchema(s,consoleSchemas))
			{
				String q = s.replace(".schema", "");
				if(!found.contains(q))
					found.add(q);
			}
		}
		Collections.sort(found,String.CASE_INSENSITIVE_ORDER);
		return found.toArray(fA);
	}
	
	private static HashMap<String,PartBlueprint> cachedPBs = new HashMap();

	public void refresh(String myName)
	{
		if(cachedPBs.containsKey(myName))
			cachedPBs.remove(myName);
	}
	
	public void refresh(String name, PartBlueprint newPB)
	{
		if(newPB.myName.equals(name))
			cachedPBs.put(name, newPB);
	}
	
	public void refresh()
	{
		cachedPBs.clear();
	}

	public PartBlueprint getSchema(String name)
	{
		if(cachedPBs.containsKey(name))
			return cachedPBs.get(name);
		PartBlueprint temp = new PartBlueprint(getSchemaFile(name));
		if(temp.myName != null)
		{
			cachedPBs.put(name, temp);
			return temp;
		}
		return null;
	}
}
