package tardis.common.dimension;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenPlains;

public class BiomeGenConsoleRoom extends BiomeGenPlains{

	public BiomeGenConsoleRoom(int id) {
		super(id);
        this.biomeName = "Console Room";
        this.color = 7746177;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public int getBiomeGrassColor(int x, int y, int z){
//		if(System.currentTimeMillis() % 350 == 0)
//		{
//			setColor(new Random().nextInt(16777215));
//			System.out.println(color);
//			System.out.println(System.currentTimeMillis() + " true");
//
//		}

		return this.color;
	}
	
	@Override
	public BiomeGenBase createMutation() {
		return null;
	}

}
