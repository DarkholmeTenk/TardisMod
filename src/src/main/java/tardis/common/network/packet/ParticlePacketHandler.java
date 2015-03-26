package tardis.common.network.packet;

import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import io.darkcraft.darkcore.mod.interfaces.IDataPacketHandler;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tardis.common.entities.particles.NanogeneParticleEntity;
import tardis.common.entities.particles.ParticleType;

public class ParticlePacketHandler implements IDataPacketHandler
{
	private static Random rand = new Random();
	private double pO(boolean r)
	{
		if(r)
			return (rand.nextGaussian() / 3);
		return 0;
	}
	
	public void handleData(NBTTagCompound nbt)
	{
		if(nbt != null && !ServerHelper.isServer())
		{
			int dim = nbt.getInteger("dim");
			double x = nbt.getDouble("x");
			double y = nbt.getDouble("y");
			double z = nbt.getDouble("z");
			ParticleType type = ParticleType.get(nbt.getInteger("type"));
			int c = nbt.hasKey("c") ? nbt.getInteger("c") : 1;
			boolean r = nbt.hasKey("r") && nbt.getBoolean("r");
			EntityFX[] fx = new EntityFX[c];
			World w = WorldHelper.getWorld(dim);
			if(w == null)
				return;
			for(int i = 0;i<c;i++)
			{
				if(type == ParticleType.NANOGENE)
					fx[i] = new NanogeneParticleEntity(WorldHelper.getWorld(dim),x+pO(r),y+pO(r),z+pO(r));
			}
			
			for(EntityFX f : fx)
				if(f != null)
					Minecraft.getMinecraft().effectRenderer.addEffect(f);
		}
	}
}
