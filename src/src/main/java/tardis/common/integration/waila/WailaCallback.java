package tardis.common.integration.waila;

import tardis.common.blocks.EngineBlock;
import tardis.common.blocks.SchemaComponentBlock;
import tardis.common.core.TardisOutput;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;

public class WailaCallback
{
	private static WailaConsoleProvider consoleProv = new WailaConsoleProvider();
	private static WailaEngineProvider engineProv = new WailaEngineProvider();
	
	private static void registerProvider(IWailaRegistrar registrar, IWailaDataProvider prov,Class block)
	{
		registrar.registerHeadProvider(prov, block);
		registrar.registerBodyProvider(prov, block);
		registrar.registerTailProvider(prov, block);
	}
	
	public static void wailaRegister(IWailaRegistrar registrar)
	{
		TardisOutput.print("TWC", "Waila callback");
		registerProvider(registrar,consoleProv,SchemaComponentBlock.class);
		registerProvider(registrar,engineProv,SchemaComponentBlock.class);
		registerProvider(registrar,engineProv,EngineBlock.class);
	}
}
