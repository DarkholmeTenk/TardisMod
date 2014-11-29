package tardis.common.dimension;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import tardis.TardisMod;
import tardis.api.IChunkLoader;
import tardis.common.core.store.SimpleCoordStore;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public class TardisChunkLoadingManager implements LoadingCallback
{
	HashMap<SimpleCoordStore,Ticket> monitorableChunkLoaders = new HashMap<SimpleCoordStore,Ticket>();
	private int tickCount=0;
	private boolean forceCheck = false;

	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world)
	{
		for(Ticket t : tickets)
		{
			if(t.getModData().hasKey("coords"))
			{
				SimpleCoordStore pos = SimpleCoordStore.readFromNBT(t.getModData().getCompoundTag("coords"));
				if(monitorableChunkLoaders.containsKey(pos))
				{
					Ticket oT = monitorableChunkLoaders.get(pos);
					if(oT == null)
					{
						monitorableChunkLoaders.put(pos, t);
						continue;
					}
				}
				else
				{
					monitorableChunkLoaders.put(pos, t);
					continue;
				}
			}
			ForgeChunkManager.releaseTicket(t);
		}
	}
	
	public void loadMe(IChunkLoader chunkLoader)
	{
		if(!monitorableChunkLoaders.containsKey(chunkLoader.coords()))
			monitorableChunkLoaders.put(chunkLoader.coords(), null);
	}
	
	private void loadLoadables(Ticket t, IChunkLoader te)
	{
		if(t == null || te == null)
			return;
		ChunkCoordIntPair[] loadable = te.loadable();
		if(loadable != null)
		{
			for(ChunkCoordIntPair load : loadable)
				ForgeChunkManager.forceChunk(t, load);
			NBTTagCompound nbt = t.getModData();
			if(nbt != null)
			{
				SimpleCoordStore coords = te.coords();
				if(coords != null)
					nbt.setTag("coords", te.coords().writeToNBT());
			}
		}
	}
	
	private Ticket getTicket(IChunkLoader te,World world)
	{
		Ticket t = ForgeChunkManager.requestTicket(TardisMod.i, world, ForgeChunkManager.Type.NORMAL);
		if(t != null)
			loadLoadables(t,te);
		return t;
	}
	
	private void validateChunkLoaders()
	{
		Iterator<SimpleCoordStore> keyIter = monitorableChunkLoaders.keySet().iterator();
		while(keyIter.hasNext())
		{
			SimpleCoordStore pos = keyIter.next();
			World w = pos.getWorldObj();
			TileEntity te = w.getTileEntity(pos.x, pos.y, pos.z);
			if(te instanceof IChunkLoader)
			{
				Ticket t = monitorableChunkLoaders.get(pos);
				if(t != null && !((IChunkLoader)te).shouldChunkload())
					ForgeChunkManager.releaseTicket(t);
				else if(t == null && ((IChunkLoader)te).shouldChunkload())
					monitorableChunkLoaders.put(pos, getTicket(((IChunkLoader)te),w));
				else if(t != null && ((IChunkLoader)te).shouldChunkload())
					loadLoadables(t, (IChunkLoader)te);
			}
			else
			{
				if(monitorableChunkLoaders.get(pos) != null)
					ForgeChunkManager.releaseTicket(monitorableChunkLoaders.get(pos));
				keyIter.remove();
			}
		}
	}

	@SubscribeEvent
	public void handleTick(ServerTickEvent event)
	{
		//TardisOutput.print("TCLM", "Server tick");
		if(event.side.equals(Side.SERVER) && event.phase.equals(TickEvent.Phase.END))
			tickEnd();
	}
	
	private void tickEnd()
	{
		if(((tickCount++%10) == 1) || forceCheck)
		{
			//TardisOutput.print("TCLM", "Handling chunks");
			forceCheck = false;
			validateChunkLoaders();
		}
	}

}
