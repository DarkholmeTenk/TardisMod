package tardis.common.tileents;

import io.darkcraft.darkcore.mod.abstracts.AbstractTileEntity;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import tardis.TardisMod;
import tardis.api.IArtronEnergyProvider;
import tardis.api.IScrewable;
import tardis.api.ScrewdriverMode;
import tardis.common.core.flight.ShieldModifier;
import tardis.common.core.helpers.Helper;
import tardis.common.core.helpers.ScrewdriverHelper;

public class ShieldTileEntity extends AbstractTileEntity implements IScrewable
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
	public boolean screw(ScrewdriverHelper helper, ScrewdriverMode mode, EntityPlayer player)
	{
		if(mode == ScrewdriverMode.Dismantle)
		{
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			WorldHelper.dropItemStack(new ItemStack(TardisMod.shieldBlock,1), coords().getCenter());
			return true;
		}
		return false;
	}
}
