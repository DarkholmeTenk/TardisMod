package tardis.common.tileents.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayerMP;

import tardis.common.core.Helper;
import tardis.common.entities.particles.ParticleType;
import tardis.common.tileents.TardisComponentTileEntity;
import tardis.common.tileents.TardisCoreTileEntity;

public class TardisComponentNanogene extends TardisAbstractComponent
{

	public TardisComponentNanogene(TardisComponentTileEntity parent)
	{
		parentObj = parent;
	}
	
	protected TardisComponentNanogene(){}

	@Override
	public ITardisComponent create(TardisComponentTileEntity parent)
	{
		return new TardisComponentNanogene(parent);
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
				if(pl.getHealth() < pl.getMaxHealth())
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
		if(parentObj == null || !Helper.isServer())
			return;
		if(tt % nanogeneTimer == 0 && Helper.isServer())
		{
			ArrayList<EntityPlayerMP> players = getNearbyPlayers();
			for(EntityPlayerMP pl : players)
			{
				TardisCoreTileEntity core = getCore();
				if(core != null && core.takeEnergy(1, false))
				{
					Helper.spawnParticle(ParticleType.NANOGENE, Helper.getWorldID(parentObj), pl.posX , pl.posY+1, pl.posZ,12,true);
					if(pl.isBurning())
						pl.extinguish();
					pl.heal(nanogeneHealAmount);
				}
			}
		}
	}
}
