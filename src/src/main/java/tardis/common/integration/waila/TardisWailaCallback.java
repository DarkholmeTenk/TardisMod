package tardis.common.integration.waila;

import tardis.common.blocks.TardisSchemaComponentBlock;
import tardis.common.core.TardisOutput;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;

public class TardisWailaCallback
{
	private static TardisWailaConsoleProvider consoleProv = new TardisWailaConsoleProvider();
	
	private static void registerProvider(IWailaRegistrar registrar, IWailaDataProvider prov,Class block)
	{
		registrar.registerHeadProvider(prov, block);
		registrar.registerBodyProvider(prov, block);
		registrar.registerTailProvider(prov, block);
	}
	
	public static void wailaRegister(IWailaRegistrar registrar)
	{
		TardisOutput.print("TWC", "Waila callback");
		registerProvider(registrar,consoleProv,TardisSchemaComponentBlock.class);
	}
}
