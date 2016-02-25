package tardis.common.dimension;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenPlains;

public class BiomeGenConsoleRoom extends BiomeGenPlains{

	public BiomeGenConsoleRoom(int id) {
		super(id);
        this.biomeName = "TARDIS";
        this.color = 4396444;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public int getBiomeGrassColor(int x, int y, int z){
		return 4396444;
	}
	
	@Override
	public BiomeGenBase createMutation() {
		return null;
	}

}
