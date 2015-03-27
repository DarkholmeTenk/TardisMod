package tardis.common.tileents.components;

import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import java.util.ArrayList;
import java.util.List;

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
	
	private boolean isPlayerInNeedOfHelp(EntityPlayerMP pl)
	{
		if(pl.getHealth() < pl.getMaxHealth())
			return true;
		if(nanogeneFeed)
		{
			FoodStats fs = pl.getFoodStats();
			if(fs.needFood())
				return true;
		}
		return false;
	}
	
	private ArrayList<EntityPlayerMP> getNearbyPlayers()
	{
		ArrayList<EntityPlayerMP> list = new ArrayList<EntityPlayerMP>();
		List worldList = parentObj.getWorldObj().playerEntities;
		double x = parentObj.xCoord + 0.5;
		double y = parentObj.yCoord + 0.5;
		double z = parentObj.zCoord + 0.5;
		for(Object o : worldList)
		{
			if(o instanceof EntityPlayerMP)
			{
				EntityPlayerMP pl = (EntityPlayerMP)o;
				if(isPlayerInNeedOfHelp(pl))
				{
					double dist = ((pl.posX - x) * (pl.posX - x));
					dist += ((pl.posY - y) * (pl.posY - y));
					dist += ((pl.posZ - z) * (pl.posZ - z));
					if(dist <= nanogeneRange)
						list.add(pl);
				}
			}
		}
		return list;
	}
	
	@Override
	public void updateTick()
	{
		super.updateTick();
		if(parentObj == null || !ServerHelper.isServer())
			return;
		if(tt % nanogeneTimer == 0 && ServerHelper.isServer())
		{
			ArrayList<EntityPlayerMP> players = getNearbyPlayers();
			for(EntityPlayerMP pl : players)
			{
				IArtronEnergyProvider core = getArtronEnergyProvider();
				if(core != null && core.takeArtronEnergy(1, false))
				{
					Helper.spawnParticle(ParticleType.NANOGENE, WorldHelper.getWorldID(parentObj), pl.posX , pl.posY+1, pl.posZ,20,true);
					if(pl.isBurning())
						pl.extinguish();
					pl.heal(nanogeneHealAmount);
					if(nanogeneFeed)
					{
						FoodStats fs = pl.getFoodStats();
						fs.addStats(1, 1);
					}
				}
			}
		}
	}
}
