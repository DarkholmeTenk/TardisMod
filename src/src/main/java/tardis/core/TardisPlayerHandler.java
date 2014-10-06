package tardis.core;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import tardis.TardisMod;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TardisPlayerHandler
{
	@SideOnly(Side.SERVER)
	@ForgeSubscribe
	public void playerLogon(EntityJoinWorldEvent event)
	{
		Entity e = event.entity;
		if(e != null && e instanceof EntityPlayerMP)
		{
			Packet250CustomPayload packet = new Packet250CustomPayload();
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			DataOutputStream stream = new DataOutputStream(byteStream);
			try
			{
				NBTTagCompound nbt = new NBTTagCompound();
				TardisMod.dimReg.writeToNBT(nbt);
				NBTTagCompound.writeNamedTag(nbt, stream);
				packet.data = byteStream.toByteArray();
				packet.length = byteStream.size();
				packet.channel = "TardisDR";
				PacketDispatcher.sendPacketToAllInDimension(packet, e.worldObj.provider.dimensionId);
			}
			catch(Exception err)
			{
				err.printStackTrace();
			}
		}
	}
}
