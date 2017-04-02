package tardis.common.tileents.components;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.FoodStats;

import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import tardis.Configs;
import tardis.api.IArtronEnergyProvider;
import tardis.api.IScrewable;
import tardis.api.ScrewdriverMode;
import tardis.api.TardisPermission;
import tardis.common.core.helpers.Helper;
import tardis.common.core.helpers.ScrewdriverHelper;
import tardis.common.dimension.TardisDataStore;
import tardis.common.entities.particles.ParticleType;
import tardis.common.tileents.ComponentTileEntity;

public class ComponentNanogene extends AbstractComponent implements IScrewable
{
	/**
	 * 0 = All.
	 * 1 = No mobs.
	 * 2 = No animals.
	 */
	private int state = 0;

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
		if(Configs.nanogeneFeed && (ent instanceof EntityPlayerMP))
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
				if((state >= 1) && (ent instanceof EntityMob)) continue;
				if((state == 2) && !(ent instanceof EntityPlayer)) continue;
				if(isPlayerInNeedOfHelp(ent))
				{
					double dist = ((ent.posX - x) * (ent.posX - x));
					dist += ((ent.posY - y) * (ent.posY - y));
					dist += ((ent.posZ - z) * (ent.posZ - z));
					if(dist <= Configs.nanogeneRange)
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
		if(((tt % Configs.nanogeneTimer) == 0) && ServerHelper.isServer())
		{
			ArrayList<EntityLivingBase> ents = getNearbyPlayers();
			for(EntityLivingBase ent : ents)
			{
				IArtronEnergyProvider core = getArtronEnergyProvider();
				if((core != null) && core.takeArtronEnergy(Configs.nanogeneCost, false))
				{
					Helper.spawnParticle(ParticleType.NANOGENE, WorldHelper.getWorldID(parentObj), ent.posX , ent.posY+1, ent.posZ,20,true);
					if(ent.isBurning())
						ent.extinguish();
					ent.heal(Configs.nanogeneHealAmount);
					if(Configs.nanogeneFeed && (ent instanceof EntityPlayerMP))
					{
						FoodStats fs = ((EntityPlayerMP)ent).getFoodStats();
						fs.addStats(1, 1);
					}
				}
			}
		}
	}

	private static String getModeString(int state)
	{
		switch(state)
		{
			case 0: return "Heal all";
			case 1: return "Heal players/animals";
			case 2: return "Heal only players";
			default: return "Nope";
		}
	}

	@Override
	public boolean screw(ScrewdriverHelper helper, ScrewdriverMode mode, EntityPlayer player)
	{
		TardisDataStore ds = getDatastore();
		if((mode == ScrewdriverMode.Reconfigure) && ((ds == null) || ds.hasPermission(player, TardisPermission.ROUNDEL)))
		{
			state = (state + 1) % 3;
			if(ServerHelper.isServer())
				ServerHelper.sendString(player, "New mode: " + getModeString(state));
			return true;
		}
		return false;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("state", state);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		state = nbt.getInteger("state");
	}
}
