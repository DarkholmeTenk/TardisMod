package tardis.common.integration.waila;

import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.block.Block;
import tardis.TardisMod;
import tardis.common.blocks.BatteryBlock;
import tardis.common.blocks.EngineBlock;
import tardis.common.blocks.SchemaComponentBlock;
import tardis.common.blocks.TardisBlock;
import tardis.common.blocks.TopBlock;
import tardis.common.core.TardisOutput;

public class WailaCallback {
	private static WailaConsoleProvider consoleProv = new WailaConsoleProvider();
	private static WailaEngineProvider engineProv = new WailaEngineProvider();
	private static WailaArtronProvider artronProv = new WailaArtronProvider();
	private static WailaTardisProvider tardisProv = new WailaTardisProvider();

	private static void registerProvider(IWailaRegistrar registrar,
			IWailaDataProvider prov, Class block) {
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
		registerProvider(registrar,artronProv,BatteryBlock.class);
		registerProvider(registrar,tardisProv,TardisBlock.class);
		registerProvider(registrar,tardisProv,TopBlock.class);
	}

	public static AbstractWailaProvider getProvider(Block b, int meta)
	{
		if(b == TardisMod.schemaComponentBlock)
		{
			if((meta == 3) || (meta == 6))
				return consoleProv;
			else if(meta == 7)
				return engineProv;
		}
		else if(b == TardisMod.tardisConsoleBlock)
			return consoleProv;
		else if(b == TardisMod.tardisEngineBlock)
			return engineProv;
		else if(b == TardisMod.battery)
			return artronProv;
		else if(b == TardisMod.tardisBlock)
			return tardisProv;
		else if(b == TardisMod.tardisTopBlock)
			return tardisProv;
		return null;
	}
}
