package tardis.common.tileents.components;

import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.FoodStats;
import tardis.api.IArtronEnergyProvider;
import tardis.common.core.Helper;
import tardis.common.entities.particles.ParticleType;
import tardis.common.tileents.ComponentTileEntity;

public class ComponentNanogene extends AbstractComponent
{

	public ComponentNanogene(ComponentTileEntity parent)
	{
		parentObj = parent;
	}

	protected ComponentNanogene(){}

	@Override
	public ITardisComponent create(ComponentTileEntity parent)
	{
		return new ComponentNanogene(parent);
	}

	private boolean isPlayerInNeedOfHelp(EntityLivingBase ent)
	{
		if(ent.getHealth() < ent.getMaxHealth())
			return true;
		if(nanogeneFeed && (ent instanceof EntityPlayerMP))
		{
			EntityPlayerMP pl = (EntityPlayerMP)ent;
			FoodStats fs = pl.getFoodStats();
			if(fs.needFood())
				return true;
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
				if(isPlayerInNeedOfHelp(ent))
				{
					double dist = ((ent.posX - x) * (ent.posX - x));
					dist += ((ent.posY - y) * (ent.posY - y));
					dist += ((ent.posZ - z) * (ent.posZ - z));
					if(dist <= nanogeneRange)
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
		if((parentObj == null) || !ServerHelper.isServer())
			return;
		if(((tt % nanogeneTimer) == 0) && ServerHelper.isServer())
		{
			ArrayList<EntityLivingBase> ents = getNearbyPlayers();
			for(EntityLivingBase ent : ents)
			{
				IArtronEnergyProvider core = getArtronEnergyProvider();
				if((core != null) && core.takeArtronEnergy(1, false))
				{
					Helper.spawnParticle(ParticleType.NANOGENE, WorldHelper.getWorldID(parentObj), ent.posX , ent.posY+1, ent.posZ,20,true);
					if(ent.isBurning())
						ent.extinguish();
					ent.heal(nanogeneHealAmount);
					if(nanogeneFeed && (ent instanceof EntityPlayerMP))
					{
						FoodStats fs = ((EntityPlayerMP)ent).getFoodStats();
						fs.addStats(1, 1);
					}
				}
			}
		}
	}
}
