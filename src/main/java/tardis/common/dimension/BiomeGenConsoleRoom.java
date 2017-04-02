package tardis.common.dimension;

import net.minecraft.world.biome.BiomeGenPlains;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BiomeGenConsoleRoom extends BiomeGenPlains{

	public BiomeGenConsoleRoom(int id) {
		super(id);
        biomeName = "TARDIS";
        color = 4396444;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public int getBiomeGrassColor(int x, int y, int z){
		return 4396444;
	}
}
