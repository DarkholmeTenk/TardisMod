package tardis.common.dimension;

import tardis.TardisMod;
import tardis.api.TardisFunction;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.items.TardisSonicScrewdriverItem;
import tardis.common.tileents.TardisCoreTileEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class TardisDimensionEventHandler
{
	@ForgeSubscribe
	public void damageHandler(LivingHurtEvent event)
	{
		if(!Helper.isServer())
			return;
		EntityLivingBase ent = event.entityLiving;
		DamageSource source = event.source;
		boolean cancel = true;
		if(source == DamageSource.starve || source == DamageSource.onFire || source == DamageSource.wither)
			cancel = false;
		if(ent instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)ent;
			TardisOutput.print("TDEH", "Handling hurt event");
			if(player.getHealth() <= event.ammount)
			{
				World w = player.worldObj;
				handleDead(w,player,event,cancel);
			}
		}
	}
	
	private void handleDead(World w, EntityPlayer player, LivingHurtEvent event,boolean cancel)
	{
		if(w != null)
		{
			if(!(w.provider instanceof TardisWorldProvider))
			{
				Integer tardisDim = TardisMod.plReg.getDimension(player);
				if(tardisDim != null)
				{
					if(savePlayer(player,tardisDim))
					{
						event.setCanceled(cancel && TardisMod.deathTransmatLive);
						return;
					}
				}
				
				InventoryPlayer inv = player.inventory;
				for(ItemStack is: inv.mainInventory)
				{
					if(is != null)
					{
						Item i = is.getItem();
						if(i instanceof TardisSonicScrewdriverItem)
						{
							int linkedDim = TardisSonicScrewdriverItem.getLinkedDim(is);
							if(linkedDim != 0)
							{
								if(savePlayer(player,linkedDim))
								{
									event.setCanceled(cancel && TardisMod.deathTransmatLive);
									return;
								}
							}
						}
					}
				}
			}
		}
	}
	
	private boolean savePlayer(EntityPlayer player,int dim)
	{
		TardisCoreTileEntity core = Helper.getTardisCore(dim);
		if(core != null && core.hasFunction(TardisFunction.TRANSMAT))
		{
			if(core.transmatEntity(player))
			{
				return true;
			}
		}
		return false;
	}
}
