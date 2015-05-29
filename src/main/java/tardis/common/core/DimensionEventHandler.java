package tardis.common.core;

import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.SoundHelper;

import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import tardis.TardisMod;
import tardis.api.ScrewdriverMode;
import tardis.api.TardisFunction;
import tardis.common.dimension.TardisDataStore;
import tardis.common.items.SonicScrewdriverItem;
import tardis.common.tileents.CoreTileEntity;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.relauncher.Side;

public class DimensionEventHandler
{
	private HashMap<String, Integer> playersToSave = new HashMap<String,Integer>();
	private HashMap<String, DamageSource> sources = new HashMap<String, DamageSource>();

	@SubscribeEvent
	public void damageHandler(LivingHurtEvent event)
	{
		if(ServerHelper.isClient())
			return;
		EntityLivingBase ent = event.entityLiving;
		World w = ent.worldObj;
		DamageSource source = event.source;
		if((!handleTranquility(w,event,ent,source)) && (ent instanceof EntityPlayer))
		{
			float damAmount = event.ammount;
			EntityPlayer player = (EntityPlayer)ent;
			damAmount = (damAmount * (25 - player.getTotalArmorValue())) / 25.0f;
			//TardisOutput.print("TDEH", "Handling hurt event");
			if(player.getHealth() <= damAmount)
			{
				handleDead(w,player,event,source);
			}
		}
	}


	private boolean handleTranquility(World w, LivingHurtEvent event, EntityLivingBase ent, DamageSource source)
	{
		if(Helper.isTardisWorld(w))
		{
			TardisDataStore store = Helper.getDataStore(w);
			if(store.hasFunction(TardisFunction.TRANQUILITY))
			{
				if((source == DamageSource.wither) || (source == DamageSource.magic) || (source == DamageSource.generic))
				{
					event.setCanceled(true);
					return true;
				}
			}
		}
		return false;
	}

	private void handleDead(World w, EntityPlayer player, LivingHurtEvent event,DamageSource source)
	{
		String name = ServerHelper.getUsername(player);
		synchronized(playersToSave)
		{
			if(playersToSave.keySet().contains(name))
			{
				event.setCanceled(true);
				return;
			}
		}

		if(w != null)
		{
			if(!Helper.isTardisWorld(w))
			{
				Integer tardisDim = TardisMod.plReg.getDimension(player);
				if(tardisDim != null)
				{
					if(savePlayer(player,tardisDim, source))
					{
						event.setCanceled(true);
						return;
					}
				}

				InventoryPlayer inv = player.inventory;
				for(ItemStack is: inv.mainInventory)
				{
					if(is != null)
					{
						Item i = is.getItem();
						if(i instanceof SonicScrewdriverItem)
						{
							if(SonicScrewdriverItem.hasPermission(is, ScrewdriverMode.Transmat))
							{
								int linkedDim = SonicScrewdriverItem.getLinkedDim(is);
								if(linkedDim != 0)
								{
									if(savePlayer(player,linkedDim, source))
									{
										event.setCanceled(true);
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean savePlayer(EntityPlayer player,int dim, DamageSource source)
	{
		CoreTileEntity core = Helper.getTardisCore(dim);
		if((core != null) && core.hasFunction(TardisFunction.TRANSMAT))
		{
			if(core.canTransmatEntity(player))
			{
				synchronized(playersToSave)
				{
					sources.put(ServerHelper.getUsername(player), source);
					playersToSave.put(ServerHelper.getUsername(player), dim);
				}
				return true;
			}
			else
				SoundHelper.playSound(player, "tardismod:transmatFail", 0.6F, 1);
		}
		return false;
	}

	@SubscribeEvent
	public void handleTick(ServerTickEvent event)
	{
		if(event.side.equals(Side.SERVER) && event.phase.equals(TickEvent.Phase.END))
		{
			synchronized(playersToSave)
			{
				Iterator<String> plNameIter = playersToSave.keySet().iterator();
				while(plNameIter.hasNext())
				{
					String plName = plNameIter.next();
					EntityPlayer pl = ServerHelper.getPlayer(plName);
					if(pl != null)
					{
						int dim = playersToSave.get(plName);
						DamageSource source = sources.remove(plName);
						CoreTileEntity core = Helper.getTardisCore(dim);
						if(core != null)
							core.transmatEntity(pl);
						if((source == DamageSource.starve) || (source == DamageSource.onFire) || (source == DamageSource.wither) || !TardisMod.deathTransmatLive)
							pl.attackEntityFrom(source, 200);
					}
					plNameIter.remove();
				}
			}
		}
	}
}
