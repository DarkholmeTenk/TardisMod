package tardis.common.network;

import io.darkcraft.darkcore.mod.DarkcoreMod;
import io.darkcraft.darkcore.mod.interfaces.IDataPacketHandler;
import tardis.common.network.packet.ControlPacketHandler;
import tardis.common.network.packet.DimRegPacketHandler;
import tardis.common.network.packet.ParticlePacketHandler;

public class TardisPacketHandler
{
	public static final byte				controlFlag		= 10;
	public static final byte				dimRegFlag		= 11;
	public static final byte				particleFlag	= 12;
	public static final IDataPacketHandler	controlHandler	= new ControlPacketHandler();
	public static final IDataPacketHandler	dimRegHandler	= new DimRegPacketHandler();
	public static final IDataPacketHandler	particleHandler	= new ParticlePacketHandler();

	public static void registerHandlers()
	{
		DarkcoreMod.packetHandler.registerHandler(controlFlag, controlHandler);
		DarkcoreMod.packetHandler.registerHandler(dimRegFlag, dimRegHandler);
		DarkcoreMod.packetHandler.registerHandler(particleFlag, particleHandler);
	}
}
