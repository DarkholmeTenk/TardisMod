package tardis.common.tileents;

import io.darkcraft.darkcore.mod.abstracts.AbstractTileEntity;
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
import tardis.common.core.flight.ShieldModifier;
import tardis.common.core.helpers.Helper;
import tardis.common.core.helpers.ScrewdriverHelper;

public class ShieldTileEntity extends AbstractTileEntity implements IScrewable, IActivatable
{
	private boolean active = true;

	public boolean isActive()
	{
		if(!active) return false;
		IArtronEnergyProvider iaep = Helper.getArtronProvider(this, true);
		if(iaep == null) return false;
		return iaep.getArtronEnergy() > 10;
	}

	public void blockAttempt(CoreTileEntity core)
	{
		IArtronEnergyProvider iaep = Helper.getArtronProvider(this, true);
		if(iaep != null)
			iaep.takeArtronEnergy(10, false);
	}

	@Override
	public void init()
	{
		super.init();
		ShieldModifier.registerShieldTileEntity(this);
	}

	@Override
	public boolean activate(EntityPlayer ent, int side)
	{
		if(ServerHelper.isServer());
			ServerHelper.sendString(ent, "TARDIS Prevention Matrix " + (active ? "active" : "inactive"));
		return true;
	}

	@Override
	public boolean screw(ScrewdriverHelper helper, ScrewdriverMode mode, EntityPlayer player)
	{
		if(ServerHelper.isClient()) return true;
		if(mode == ScrewdriverMode.Dismantle)
		{
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			WorldHelper.dropItemStack(new ItemStack(TardisMod.shieldBlock,1), coords().getCenter());
			return true;
		}
		else if(mode == ScrewdriverMode.Reconfigure)
		{
			active = !active;
			if(active)
				ServerHelper.sendString(player, "TARDIS Prevention Matrix activated");
			else
				ServerHelper.sendString(player, "TARDIS Prevention Matrix disabled");
		}
		return false;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setBoolean("active", active);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		active = nbt.getBoolean("active");
	}
}
