package tardis.core.console.control;

import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;

import tardis.core.console.control.models.ModelWheel;

@NBTSerialisable
public class ControlWheel extends AbstractControlInt
{
	private ControlWheel(ControlWheelBuilder builder, ControlHolder holder)
	{
		super(builder, holder);
	}

	@Override
	public void setValue(int value)
	{
		this.value = MathHelper.cycle(value, min, max);
	}

	private static final float updateDist = 0.01f;
	@Override
	public float getState(float ptt)
	{
		float state = (value - min) / (float)((max - min)+1);
		if(state == lastValue)
			return state;
		float diff = state - lastValue;
		if(((diff > 0) && (diff < 0.5)) || (diff < -0.5))
			return Math.min(lastValue + (updateDist * ptt), state < lastValue ? state + 1 : state) % 1;
		else
			return (1+Math.max(lastValue - (updateDist * ptt), state > lastValue ? state - 1 : state)) % 1;
	}

	public static class ControlWheelBuilder extends ControlIntBuilder<ControlWheel>
	{
		public ControlWheelBuilder(int min, int max, int defaultVal)
		{
			super(min, max, defaultVal);
			withModel(ModelWheel.i);
		}

		@Override
		public ControlWheel build(ControlHolder holder)
		{
			return new ControlWheel(this, holder);
		}
	}
}
