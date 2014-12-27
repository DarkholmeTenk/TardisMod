package tardis.common.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tardis.common.core.Helper;
import tardis.common.entities.particles.NanogeneParticleEntity;
import tardis.common.entities.particles.ParticleType;

public class ParticlePacket extends TardisAbstractPacket
{
	public ParticlePacket(ByteBuf payload, NBTTagCompound nbt)
	{
		super(payload, nbt, (byte) PacketType.PARTICLE.ordinal());
	}
	
	public ParticlePacket(ByteBuf payload)
	{
		super(payload);
	}
	
	public void spawn()
	{
		NBTTagCompound nbt = getNBT();
		if(nbt != null && !Helper.isServer())
		{
			int dim = nbt.getInteger("dim");
			double x = nbt.getInteger("x");
			double y = nbt.getInteger("y");
			double z = nbt.getInteger("z");
			ParticleType type = ParticleType.get(nbt.getInteger("type"));
			EntityFX fx = null;
			World w = Helper.getWorld(dim);
			if(w == null)
				return;
			if(type == ParticleType.NANOGENE)
				fx = new NanogeneParticleEntity(Helper.getWorld(dim),x,y,z);
			
			if(fx != null)
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}
}
