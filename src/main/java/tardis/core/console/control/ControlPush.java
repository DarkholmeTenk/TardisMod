package tardis.core.console.control;

import static io.darkcraft.darkcore.mod.nbt.NBTProperty.SerialisableType.TRANSMIT;

import io.darkcraft.darkcore.mod.handlers.containers.PlayerContainer;
import io.darkcraft.darkcore.mod.nbt.NBTProperty;
import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;

import tardis.core.TardisInfo;

@NBTSerialisable
public abstract class ControlPush extends AbstractControl
{
	private final Runnable function;

	private int pressedTT;
	private boolean wasPressed = false;
	private double state = 0;
	@NBTProperty(TRANSMIT)
	private boolean pressed;

	public ControlPush(ControlPushBuilder builder, ControlHolder holder)
	{
		super(builder, 0.25, 0.25, 0, holder);
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

	protected double getState(float ptt)
	{
		if(pressed && (state < 1))
			return (state + (Math.min(1-state, 0.25) * ptt));
		else if(!pressed && (state > 0))
			return (state - (Math.min(state, 0.25)*ptt));
		return pressed ? 1 : 0;
	}

	public abstract static class ControlPushBuilder<T extends ControlPush> extends ControlBuilder<T>
	{
		private final Runnable function;

		public ControlPushBuilder(Runnable function)
		{
			this.function = function;
		}
	}
}
