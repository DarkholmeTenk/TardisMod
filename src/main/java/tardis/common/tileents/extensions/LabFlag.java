package tardis.common.tileents.extensions;

import io.darkcraft.darkcore.mod.datastore.UVStore;
import net.minecraft.util.ResourceLocation;

public enum LabFlag
{
	NOTINFLIGHT(new UVStore(0,0.25,0,1)), //Recipe will only run while TARDIS is not in flight.
	INFLIGHT(new UVStore(0.25,0.5,0,1)), //Recipe will only run while TARDIS is in flight (any flight)
	INCOORDINATEDFLIGHT(new UVStore(0.5,0.75,0,1)),
	INUNCOORDINATEDFLIGHT(new UVStore(0.75,1,0,1)); // Recipe will only run while TARDIS is in uncoordinated flight.

	public final static ResourceLocation rl = new ResourceLocation("tardismod","textures/labflags.png");
	public final UVStore uv;

	private LabFlag(UVStore _uv)
	{
		uv = _uv;
	}
}
