package tardis.core.console.control;

import io.darkcraft.darkcore.mod.DarkcoreMod;
import io.darkcraft.darkcore.mod.handlers.containers.PlayerContainer;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.nbt.NBTProperty;

import tardis.core.TardisInfo;

public abstract class AbstractControlInt extends AbstractStatefulControl
{
	public final int min;
	public final int max;

	@NBTProperty
	protected int value;

	protected float lastValue;

	protected AbstractControlInt(ControlIntBuilder builder, ControlHolder holder)
	{
		super(builder, holder);
		min = builder.min;
		max = builder.max;
		value = builder.defaultVal;
	}

	public final int getValue()
	{
		return value;
	}

	public abstract void setValue(int value);

	public final void randomize()
	{
		setValue(DarkcoreMod.r.nextInt(max-min)+min);
	}

	@Override
	protected boolean activateControl(TardisInfo info, PlayerContainer player, boolean sneaking)
	{
		int ov = getValue();
		setValue(ov + (sneaking ? -1 : 1));
		return ov != getValue();
	}

	@Override
	protected void tickControl()
	{
		if(ServerHelper.isClient())
			lastValue = tt == 1 ? value : getState(1);
	}

	public abstract static class ControlIntBuilder<T extends AbstractControlInt> extends StatefulControlBuilder<T>
	{
		private int min;
		private int max;
		private int defaultVal;

		public ControlIntBuilder(int min, int max, int defaultVal)
		{
			this.min = min;
			this.max = max;
			this.defaultVal = defaultVal;
		}
	}
}
