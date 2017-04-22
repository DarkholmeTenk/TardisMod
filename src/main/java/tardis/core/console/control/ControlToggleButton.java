package tardis.core.console.control;

import io.darkcraft.darkcore.mod.handlers.containers.PlayerContainer;
import io.darkcraft.darkcore.mod.nbt.NBTProperty;
import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;

import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.CoreTileEntity;

@NBTSerialisable
public class ControlToggleButton extends AbstractControl
{
	@NBTProperty
	private boolean pressed;

	public ControlToggleButton(ControlToggleButtonBuilder builder)
	{
		super(builder, 1, 1, 45);
		pressed = builder.defaultPressed;
	}

	@Override
	protected boolean activateControl(CoreTileEntity tardis, ConsoleTileEntity console, PlayerContainer player, boolean sneaking)
	{
		pressed = !pressed;
		return true;
	}

	public boolean getPressed()
	{
		return pressed;
	}

	public void setPressed(boolean pressed)
	{
		this.pressed = pressed;
	}

	@Override
	public void render()
	{
		// TODO Auto-generated method stub

	}

	public static class ControlToggleButtonBuilder extends ControlBuilder<ControlToggleButton>
	{
		private boolean defaultPressed;

		public ControlToggleButtonBuilder(boolean defaultPressed)
		{
			this.defaultPressed = defaultPressed;
		}

		@Override
		public ControlToggleButton build()
		{
			return new ControlToggleButton(this);
		}
	}
}
