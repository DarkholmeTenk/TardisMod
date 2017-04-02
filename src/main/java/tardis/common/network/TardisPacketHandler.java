package tardis.common.network;

import io.darkcraft.darkcore.mod.DarkcoreMod;
import io.darkcraft.darkcore.mod.interfaces.IDataPacketHandler;

import tardis.common.network.packet.ControlPacketHandler;
import tardis.common.network.packet.DimRegPacketHandler;
import tardis.common.network.packet.ParticlePacketHandler;
import tardis.common.network.packet.ScrewdriverHelperPacketHandler;

public class TardisPacketHandler
{
	public static final String				controlFlag		= "tm.control";
	public static final String				dimRegFlag		= "tm.dimreg";
	public static final String				particleFlag	= "tm.particle";
	public static final String				screwFlag		= "tm.screw";
	public static final IDataPacketHandler	controlHandler	= new ControlPacketHandler();
	public static final IDataPacketHandler	dimRegHandler	= new DimRegPacketHandler();
	public static final IDataPacketHandler	particleHandler	= new ParticlePacketHandler();
	public static final IDataPacketHandler	screwHandler	= new ScrewdriverHelperPacketHandler();

	public static void registerHandlers()
	{
		DarkcoreMod.packetHandler.registerHandler(controlFlag, controlHandler);
		DarkcoreMod.packetHandler.registerHandler(dimRegFlag, dimRegHandler);
		DarkcoreMod.packetHandler.registerHandler(particleFlag, particleHandler);
		DarkcoreMod.packetHandler.registerHandler(screwFlag, screwHandler);
	}
}
