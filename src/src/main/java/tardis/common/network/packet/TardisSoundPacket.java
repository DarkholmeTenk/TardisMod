package tardis.common.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;

public class TardisSoundPacket extends TardisAbstractPacket
{
	public TardisSoundPacket(ByteBuf payload)
	{
		super(payload);
	}
	
	public TardisSoundPacket(NBTTagCompound nbt)
	{
		this(Unpooled.buffer(),nbt);
	}
	
	public TardisSoundPacket(ByteBuf payload, NBTTagCompound nbt)
	{
		super(payload,nbt,(byte) TardisAbstractPacket.PacketType.SOUND.ordinal());
	}
	
	public void play()
	{
		if(Helper.isServer())
			return;
		TardisOutput.print("TSP", "Attempting to play sound packet");
		NBTTagCompound data = getNBT();
		if(data != null && data.hasKey("sound"))
		{
			String sound = data.getString("sound");
			int dim = data.getInteger("world");
			int x = data.getInteger("x");
			int y = data.getInteger("y");
			int z = data.getInteger("z");
			float vol = data.getFloat("vol");
			float speed = 1;
			if(data.hasKey("spe"))
				speed = data.getFloat("spe");
			World w = Helper.getWorld(dim);
			if(w != null)
			{
				w.playSound(x, y, z, sound, vol, 1f, true);
			}
		}
	}
}
