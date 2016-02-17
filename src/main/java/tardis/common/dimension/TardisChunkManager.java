package tardis.common.dimension;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import tardis.TardisMod;

public class TardisChunkManager extends WorldChunkManager
{
	private static final BiomeGenBase[] bgbArr = { TardisMod.consoleBiome };

    public TardisChunkManager(World p_i1976_1_)
    {
        super(p_i1976_1_.getSeed(), p_i1976_1_.getWorldInfo().getTerrainType());
    }
	
    @Override
	public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] par1ArrayOfBiomeGenBase, int par2, int par3, int par4, int par5)
	{
		return bgbArr;
	}
	
	@Override
	public BiomeGenBase getBiomeGenAt(int par1, int par2)
    {
        return TardisMod.consoleBiome;
    }
	
//	@Override
//	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] par1ArrayOfBiomeGenBase, int par2, int par3, int par4, int par5, boolean par6)
//    {
//        return bgbArr;
//    }
}
