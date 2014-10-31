package tardis.common.dimension;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

public class TardisChunkManager extends WorldChunkManager
{
	private static final BiomeGenBase[] bgbArr = { BiomeGenBase.plains };
	public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] par1ArrayOfBiomeGenBase, int par2, int par3, int par4, int par5)
	{
		return bgbArr;
	}
	
	public BiomeGenBase getBiomeGenAt(int par1, int par2)
    {
        return BiomeGenBase.plains;
    }
	
	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] par1ArrayOfBiomeGenBase, int par2, int par3, int par4, int par5, boolean par6)
    {
        return bgbArr;
    }
}
