package tardis.dimension;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;

public class TardisWorldProvider extends WorldProvider
{

	public TardisWorldProvider()
	{
	}

	@Override
	public String getDimensionName()
	{
		return "Tardis Interior";
	}
	
    @Override
	public String getSaveFolder()
    {
        return (dimensionId == 0 ? null : "tardis\\DIM" + dimensionId);
    }
    
    @Override
    public IChunkProvider createChunkGenerator()
    {
        return new TardisChunkProvider(worldObj);
    }

}
