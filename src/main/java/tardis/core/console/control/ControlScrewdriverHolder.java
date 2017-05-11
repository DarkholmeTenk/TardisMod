package tardis.core.console.control;

import net.minecraft.util.ResourceLocation;

import io.darkcraft.darkcore.mod.handlers.containers.PlayerContainer;
import io.darkcraft.darkcore.mod.helpers.RenderHelper;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import io.darkcraft.darkcore.mod.nbt.NBTProperty;
import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;

import tardis.client.renderer.model.console.SonicScrewdriverHolderModel;
import tardis.common.core.helpers.ScrewdriverHelper;
import tardis.common.core.helpers.ScrewdriverHelperFactory;
import tardis.core.TardisInfo;

@NBTSerialisable
public class ControlScrewdriverHolder extends AbstractControl
{
	private static final SonicScrewdriverHolderModel holder = new SonicScrewdriverHolderModel();

	@NBTProperty
	private ScrewdriverHelper helper;

	public ControlScrewdriverHolder(ControlScrewdriverHolderBuilder builder, ControlHolder holder)
	{
		super(builder, 0.25, 0.25, 0, holder);
	}

	@Override
	protected boolean activateControl(TardisInfo info, PlayerContainer player, boolean sneaking)
	{
		if(ServerHelper.isClient(player.getEntity()))
			return false;
		if(helper == null)
		{
			ScrewdriverHelper pls = ScrewdriverHelperFactory.get(player.getEntity().getHeldItem());
			if(pls != null)
			{
				player.getEntity().inventory.setInventorySlotContents(player.getEntity().inventory.currentItem, null);
				helper = pls;
			}
			else
				return false;
		}
		else
		{
			if(player.getEntity().getHeldItem() == null)
			{
				WorldHelper.giveItemStack(player.getEntity(), helper.getItemStack());
				helper = null;
			}
			else
				return false;
		}
		markDirty();
		return true;
	}

	public void generateScrewdriver()
	{
		helper = ScrewdriverHelperFactory.getNew();
		markDirty();
	}

	@Override
	public void render(float ptt)
	{
		RenderHelper.bindTexture(new ResourceLocation("tardismod", "textures/models/SonicScrewdriverHolder.png"));
		holder.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		if(helper != null)
			helper.render();
	}

	public static class ControlScrewdriverHolderBuilder extends ControlBuilder<ControlScrewdriverHolder>
	{

		public ControlScrewdriverHolderBuilder(boolean screwdriverByDefault)
		{
		}

		@Override
		public ControlScrewdriverHolder build(ControlHolder holder)
		{
			return new ControlScrewdriverHolder(this, holder);
		}
	}
}
