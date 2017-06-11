package tardis.common.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import io.darkcraft.darkcore.mod.DarkcoreMod;
import io.darkcraft.darkcore.mod.abstracts.AbstractTileEntity;
import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.handlers.containers.PlayerContainer;
import io.darkcraft.darkcore.mod.interfaces.IDataPacketHandler;
import io.darkcraft.darkcore.mod.nbt.Mapper;
import io.darkcraft.darkcore.mod.nbt.NBTConstructor;
import io.darkcraft.darkcore.mod.nbt.NBTHelper;
import io.darkcraft.darkcore.mod.nbt.NBTProperty;
import io.darkcraft.darkcore.mod.nbt.NBTProperty.SerialisableType;
import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;
import io.darkcraft.darkcore.mod.network.DataPacket;

import tardis.api.IControlMatrix;
import tardis.common.core.HitPosition;
import tardis.common.network.TardisPacketHandler;
import tardis.core.console.control.AbstractControl;

public class ControlPacketHandler implements IDataPacketHandler
{
	public static void sendPacket(HitPosition hp, TileEntity te, PlayerContainer pc)
	{
		HitDataStore ds = new HitDataStore(hp, new SimpleCoordStore(te), pc);
		DarkcoreMod.networkChannel.sendToServer(new DataPacket(mapper.writeToNBT(ds), TardisPacketHandler.controlFlag));
	}

	@Override
	public void handleData(NBTTagCompound data)
	{
		HitDataStore hds = mapper.createFromNBT(data);
		TileEntity baseTE = hds.te.getTileEntity();
		AbstractTileEntity te = baseTE instanceof AbstractTileEntity ? (AbstractTileEntity) baseTE : null;
		EntityPlayer pl = hds.pc.getEntity();
		if((pl == null) || (te == null))
			return;
		if(te instanceof IControlMatrix)
		{
			AbstractControl control = ((IControlMatrix)te).getControl(hds.pc, hds.hp);
			if(control != null)
			{
				if(control.activate(hds.pc, pl.isSneaking()))
				{
					te.queueUpdate();
					te.markDirty();
				}
			}
			else
				((IControlMatrix)te).activatedWithoutControl(hds.pc, hds.hp);
		}

	}

	private final static Mapper<HitDataStore> mapper = NBTHelper.getMapper(HitDataStore.class, SerialisableType.TRANSMIT);
	@NBTSerialisable
	public static class HitDataStore
	{
		@NBTProperty
		private final HitPosition hp;
		@NBTProperty
		private final SimpleCoordStore te;
		@NBTProperty
		private final PlayerContainer pc;

		@NBTConstructor({"hp", "te", "pc"})
		public HitDataStore(HitPosition hp, SimpleCoordStore te, PlayerContainer pc)
		{
			this.hp = hp;
			this.te = te;
			this.pc = pc;
		}
	}
}
