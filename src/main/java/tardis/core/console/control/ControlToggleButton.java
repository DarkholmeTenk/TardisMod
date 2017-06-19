package tardis.core.console.control;

import io.darkcraft.darkcore.mod.handlers.containers.PlayerContainer;
import io.darkcraft.darkcore.mod.nbt.NBTProperty;
import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;

import tardis.core.TardisInfo;
import tardis.core.console.control.models.ModelButton;

@NBTSerialisable
public class ControlToggleButton extends AbstractStatefulControl
{
	private float state;
	@NBTProperty
	private boolean pressed;

	public ControlToggleButton(ControlToggleButtonBuilder builder, ControlHolder holder)
	{
		super(builder, holder);
		pressed = builder.defaultPressed;
	}

	@Override
	protected boolean activateControl(TardisInfo info, PlayerContainer player, boolean sneaking)
	{
		pressed = !pressed;
		return true;
	}

	@Override
	protected void tickControlClient()
	{
		state = getState(1);
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
	public float getState(float ptt)
	{
		if(pressed && (state < 1))
			return state + (Math.min(0.2f, 1 - state) * ptt);
		else if(!pressed && (state > 0))
			return state - (Math.min(0.2f, state) * ptt);
		return pressed ? 1 : 0;
	}

	public static class ControlToggleButtonBuilder extends StatefulControlBuilder<ControlToggleButton>
	{
		private boolean defaultPressed;

		public ControlToggleButtonBuilder(boolean defaultPressed)
		{
			this.defaultPressed = defaultPressed;
			withModel(ModelButton.i);
		}

		@Override
		public ControlToggleButton build(ControlHolder holder)
		{
			return new ControlToggleButton(this, holder);
		}
	}
}
