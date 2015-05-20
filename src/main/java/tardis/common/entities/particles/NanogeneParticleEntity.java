package tardis.common.entities.particles;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class NanogeneParticleEntity extends AbstractParticleEntity
{
	public static final ResourceLocation res = new ResourceLocation("tardismod","textures/particles/nanogene.png");

	public NanogeneParticleEntity(World w, double x, double y, double z)
	{
		super(w, x, y, z);
	}

	@Override
	public ResourceLocation getTexture()
	{
		particleScale = 2;
		particleAlpha = 100;
		return res;
	}

}
