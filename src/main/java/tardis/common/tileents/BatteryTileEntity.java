package tardis.common.tileents;

import io.darkcraft.darkcore.mod.abstracts.AbstractTileEntity;
import io.darkcraft.darkcore.mod.config.ConfigFile;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import io.darkcraft.darkcore.mod.interfaces.IActivatable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tardis.TardisMod;
import tardis.api.IArtronEnergyProvider;
import tardis.api.IScrewable;
import tardis.api.ScrewdriverMode;
import tardis.common.core.Helper;
import tardis.common.tileents.extensions.LabFlag;

public class BatteryTileEntity extends AbstractTileEntity implements IArtronEnergyProvider, IScrewable, IActivatable
{
	private static ConfigFile	config				= null;
	private static int			maxEnergyPerLevel	= 100;
	private static int			energyPerLevel		= 1;
	private static int			ticksPerEnergy		= 20;
	private static boolean		needsJumpStart		= true;

	private int					level				= -1;
	private int					artronEnergy		= 0;
	private int					mode				= 0;		// 0 landed, 1 uncoordinated, 2 coordinated

	private static double		rotSpeed			= 4;
	private double				angle				= 0;		// max = 359.99
	private boolean				changed				= false;

	static
	{
		if (config == null) refreshConfigs();
	}

	public static void refreshConfigs()
	{
		if (config == null) config = TardisMod.configHandler.registerConfigNeeder("ArtronBattery");
		maxEnergyPerLevel = config.getInt("max energy per level", 100, "The amount of max energy that is gained per level");
		energyPerLevel = config.getInt("energy per level", 1, "The amount of energy per pulse that is gained per level");
		ticksPerEnergy = config.getInt("ticks per energy", 20, "The number of ticks between each energy pulse");
		needsJumpStart = config.getBoolean("needs jump start", true, "True if the battery needs to be jumpstarted from inside a TARDIS");
	}

	public BatteryTileEntity(int _level)
	{
		level = _level;
	}

	public BatteryTileEntity()
	{
	}

	@Override
	public void init()
	{
		if (level == -1) level = worldObj.getBlockMetadata(xCoord, yCoord, zCoord) + 1;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if (((tt % ticksPerEnergy) == 0) && ((artronEnergy > 0) || Helper.isTardisWorld(worldObj) || !needsJumpStart)) addArtronEnergy(level * energyPerLevel, false);
		if ((tt % 1200) == 0) sendUpdate();
		if (((tt % 85) == 0) && changed)
		{
			changed = false;
			sendUpdate();
		}

		if (ServerHelper.isClient())
		{
			angle += ((rotSpeed * getArtronEnergy()) / getMaxArtronEnergy());
			while (angle >= 360)
				angle -= 360;
		}
	}

	@Override
	public int getMaxArtronEnergy()
	{
		return level * maxEnergyPerLevel;
	}

	@Override
	public int getArtronEnergy()
	{
		return artronEnergy;
	}

	@Override
	public boolean addArtronEnergy(int amount, boolean sim)
	{
		if (artronEnergy < getMaxArtronEnergy())
		{
			if (!sim) artronEnergy = Math.min(getMaxArtronEnergy(), artronEnergy + amount);
			return true;
		}
		return false;
	}

	@Override
	public boolean takeArtronEnergy(int amount, boolean sim)
	{
		if (artronEnergy >= amount)
		{
			if (!sim)
			{
				artronEnergy -= amount;
				changed = true;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean doesSatisfyFlag(LabFlag flag)
	{
		switch (flag)
		{
			case NOTINFLIGHT:
				return mode == 0;
			case INFLIGHT:
				return mode != 0;
			case INUNCOORDINATEDFLIGHT:
				return mode == 1;
			case INCOORDINATEDFLIGHT:
				return mode == 2;
			default:
				return false;
		}
	}

	@Override
	public boolean screw(ScrewdriverMode mode, EntityPlayer player)
	{
		if (mode.equals(ScrewdriverMode.Dismantle) && ServerHelper.isServer())
		{
			int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			ItemStack is = new ItemStack(TardisMod.battery, 1, meta);
			NBTTagCompound nbt = new NBTTagCompound();
			writeToNBT(nbt);
			is.stackTagCompound = nbt;
			WorldHelper.giveItemStack(player, is);
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			return true;
		}
		return false;
	}

	@Override
	public void writeTransmittable(NBTTagCompound nbt)
	{
		nbt.setInteger("ae", artronEnergy);
		nbt.setInteger("l", level);
		nbt.setInteger("m", mode);
	}

	@Override
	public void readTransmittable(NBTTagCompound nbt)
	{
		if (nbt.hasKey("l"))
		{
			artronEnergy = nbt.getInteger("ae");
			level = nbt.getInteger("l");
			mode = nbt.getInteger("m");
		}
	}

	@Override
	public boolean activate(EntityPlayer pl, int side)
	{
		mode = (mode + 1) % 3;
		sendUpdate();
		return true;
	}

	public double getAngle()
	{
		return angle;
	}

	public int getMode()
	{
		return mode;
	}

	public int getLevel()
	{
		return level;
	}

}
