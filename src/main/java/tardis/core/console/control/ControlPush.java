package tardis.core.console.control;

import static io.darkcraft.darkcore.mod.nbt.NBTProperty.SerialisableType.TRANSMIT;

import io.darkcraft.darkcore.mod.handlers.containers.PlayerContainer;
import io.darkcraft.darkcore.mod.nbt.NBTProperty;
import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;

import tardis.core.TardisInfo;
import tardis.core.console.control.models.ModelButton;

@NBTSerialisable
public class ControlPush extends AbstractStatefulControl
{
	private final Runnable function;

	private int pressedTT;
	private boolean wasPressed = false;
	private float state = 0;
	@NBTProperty(TRANSMIT)
	private boolean pressed;

	public ControlPush(ControlPushBuilder builder, ControlHolder holder)
	{
		super(builder, holder);
		function = builder.function;
	}

	@Override
	protected boolean activateControl(TardisInfo info, PlayerContainer player, boolean sneaking)
	{
		function.run();
		pressed = true;
		markDirty();
		return true;
	}

	@Override
	protected void tickControl()
	{
		state = getState(1);
		if(pressed && wasPressed && (tt > (pressedTT + 8)))
			wasPressed = pressed = false;
		else if(pressed && !wasPressed)
		{
			wasPressed = true;
			pressedTT = tt;
		}
	}

	@Override
	public float getState(float ptt)
	{
		if(pressed && (state < 1))
			return (state + (Math.min(1-state, 0.25f) * ptt));
		else if(!pressed && (state > 0))
			return (state - (Math.min(state, 0.25f)*ptt));
		return pressed ? 1 : 0;
	}

	public static class ControlPushBuilder extends StatefulControlBuilder<ControlPush>
	{
		private final Runnable function;

		public ControlPushBuilder(Runnable function)
		{
			this.function = function;
			withModel(ModelButton.i);
		}

		@Override
		public ControlPush build(ControlHolder holder)
		{
			return new ControlPush(this, holder);
		}
	}
}
