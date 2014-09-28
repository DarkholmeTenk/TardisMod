package tardis.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class TardisConfigHandler
{
	private File tardisConfigDir;
	private File tardisSchemaDir;
	
	private HashMap<String,TardisConfigFile> cachedConfigs = new HashMap<String,TardisConfigFile>();
	
	public TardisConfigHandler(File baseConfigDir) throws IOException
	{
		tardisConfigDir = new File(baseConfigDir.toString() + "\\tardis\\");
		if((!tardisConfigDir.exists()) || (!tardisConfigDir.isDirectory()))
		{
			if(!tardisConfigDir.mkdirs())
				throw new IOException("Couldn't create " + tardisConfigDir.toString());
		}
		tardisSchemaDir = new File(tardisConfigDir.toString() + "\\schema\\");
		if((!tardisSchemaDir.exists()) || (!tardisSchemaDir.isDirectory()))
		{
			if(!tardisSchemaDir.mkdirs())
				throw new IOException("Couldn't create " + tardisSchemaDir.toString());
		}
	}
	
	public File getSchemaFile(String name)
	{
		return new File(tardisSchemaDir.toString() + "\\" + name + ".schema");
	}
	
	public TardisConfigFile getConfigFile(String name)
	{
		if(cachedConfigs.containsKey(name))
			return cachedConfigs.get(name);
		
		TardisConfigFile tmp = new TardisConfigFile(new File(tardisConfigDir.toString() + "\\Tardis" + name + ".cfg"));
		cachedConfigs.put(name, tmp);
		return tmp;
	}
}
