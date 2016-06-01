package tardis.common.tileents.components;

import java.util.ArrayList;
import java.util.List;

import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tardis.Configs;
import tardis.api.IArtronEnergyProvider;
import tardis.common.core.helpers.Helper;
import tardis.common.entities.particles.ParticleType;
import tardis.common.integration.ae.AEHelper;
import tardis.common.integration.other.CofHCore;
import tardis.common.integration.other.IC2;
import tardis.common.tileents.ComponentTileEntity;

public class ComponentRestorationField extends AbstractComponent
{
	
	public ComponentRestorationField(ComponentTileEntity parent)
	{
		parentObj = parent;
	}

	protected ComponentRestorationField(){}
	
	@Override
	public ITardisComponent create(ComponentTileEntity parent)
	{
		return new ComponentRestorationField(parent);
	}

	private boolean hasBrokenItems(EntityLivingBase ent)
	{
		if(ent instanceof EntityPlayerMP)
		{
			EntityPlayerMP pl = (EntityPlayerMP)ent;
			if(pl.inventory.mainInventory != null){
				for(ItemStack s : pl.inventory.mainInventory)
				{
					if(s != null && s.isItemStackDamageable() && s.isItemDamaged())
						return true;
				}
			}
			if(pl.inventory.armorInventory != null){
				for(ItemStack s : pl.inventory.armorInventory)
				{
					if(s != null && s.isItemStackDamageable() && s.isItemDamaged())
						return true;
				}
			}
			
		}
		return false;
	}
	
	private ArrayList<EntityLivingBase> getNearbyPlayers()
	{
		ArrayList<EntityLivingBase> list = new ArrayList<EntityLivingBase>();
		List worldList = parentObj.getWorldObj().loadedEntityList;
		double x = parentObj.xCoord + 0.5;
		double y = parentObj.yCoord + 0.5;
		double z = parentObj.zCoord + 0.5;
		for(Object o : worldList)
		{
			if(o instanceof EntityLivingBase)
			{
				EntityLivingBase ent = (EntityLivingBase)o;
				if(hasBrokenItems(ent))
				{
					double dist = ((ent.posX - x) * (ent.posX - x));
					dist += ((ent.posY - y) * (ent.posY - y));
					dist += ((ent.posZ - z) * (ent.posZ - z));
					if(dist <= Configs.restorationFieldRange)
						list.add(ent);
				}
			}
		}
		return list;
	}
	
	@Override
	public void updateTick()
	{
		super.updateTick();
		if((parentObj == null) || ServerHelper.isClient())
			return;
		if(((tt % Configs.restorationFieldTimer) == 0) && ServerHelper.isServer())
		{
			ArrayList<EntityLivingBase> ents = getNearbyPlayers();
			for(EntityLivingBase ent : ents)
			{
				IArtronEnergyProvider core = getArtronEnergyProvider();
				if((core != null))
				{
					if((ent instanceof EntityPlayerMP))
					{
						EntityPlayerMP pl = (EntityPlayerMP)ent;
						for(ItemStack s : pl.inventory.mainInventory){
							if(s != null){
								if(IC2.isItemElectric(s) || CofHCore.isItemElectric(s) || AEHelper.isItemElectric(s))
									continue;
								
								if(s.isItemStackDamageable())
								{
									if( s.isItemDamaged())
									{
										Item i = s.getItem();
										if(i.getDamage(s) > (i.getMaxDamage() * ((double) (100 - Configs.restorationFieldPercentage) / 100)) && core.takeArtronEnergy(Configs.restorationFieldCost, false))
										{
											i.setDamage(s, i.getDamage(s) - 1);
											Helper.spawnParticle(ParticleType.RESTORATIONFIELD, WorldHelper.getWorldID(parentObj), ent.posX , ent.posY+1, ent.posZ,20,true);
										}
									}
								}
							}
						}
						
						for(ItemStack s : pl.inventory.armorInventory){
							if(s != null){
								if(IC2.isItemElectric(s) || CofHCore.isItemElectric(s) || AEHelper.isItemElectric(s))
									return;
								
								if(s.isItemStackDamageable())
								{
									if( s.isItemDamaged())
									{
										Item i = s.getItem();
										if(i.getDamage(s) > (i.getMaxDamage() * ((double) (100 - Configs.restorationFieldPercentage) / 100)) && core.takeArtronEnergy(Configs.restorationFieldCost, false))
										{
											i.setDamage(s, i.getDamage(s) - 1);
											Helper.spawnParticle(ParticleType.RESTORATIONFIELD, WorldHelper.getWorldID(parentObj), ent.posX , ent.posY+1, ent.posZ,20,true);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
