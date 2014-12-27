package tardis.common.network.packet;

import java.io.IOException;

import tardis.common.core.Helper;
import net.minecraft.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class TardisAbstractPacket extends FMLProxyPacket
{
	public enum PacketType {
		DIMREG, SOUND, CONTROL, PARTICLE;
		
		public static PacketType find(int i)
		{
			PacketType[] vals = PacketType.values();
			if(i >= 0 && i < vals.length)
				return vals[i];
			return null;
		}
	}
	
	public ByteBuf buffer;

	public TardisAbstractPacket(ByteBuf payload, String channel)
	{
		super(payload, channel);
		buffer = payload;
	}
	
	public TardisAbstractPacket(ByteBuf payload, NBTTagCompound nbt, byte discriminator)
	{
		super(payload, "tardis");
		payload.writeByte(discriminator);
		payload.writerIndex(1);
		try
		{
			ByteBufOutputStream stream = new ByteBufOutputStream(payload);
			Helper.writeNBT(nbt, stream);
			stream.close();
		}
		catch(IOException e){}
	}
	
	public TardisAbstractPacket(ByteBuf payload)
	{
		super(payload,"tardis");
		buffer = payload;
	}

	public NBTTagCompound getNBT()
	{
		ByteBufInputStream in = new ByteBufInputStream(buffer);
		NBTTagCompound nbt = Helper.readNBT(in);
		try
		{
			in.close();
		} catch (IOException e){e.printStackTrace();}
		return nbt;
	}

}
