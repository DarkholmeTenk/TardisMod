package tardis.core.console.control;

import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;

import tardis.core.console.control.models.ModelLever;

@NBTSerialisable
public class ControlLever extends AbstractControlInt
{
	private ControlLever(ControlLeverBuilder builder, ControlHolder holder)
	{
		super(builder, holder);
	}

	@Override
	public void setValue(int value)
	{
		this.value = MathHelper.clamp(value, min, max);
	}

	private static final float updateDist = 0.01f;
	@Override
	public float getState(float ptt)
	{
		float value = (this.value - min) / (float)(max - min);
		if(value == lastValue)
			return value;
		if((ptt == 1) && (Math.abs(value-lastValue) < updateDist))
			return value;
		float speed = Math.max(updateDist, Math.abs(value - lastValue) * 0.3f);
		if(value > lastValue)
			return MathHelper.interpolate(Math.min(lastValue + speed, value), lastValue, ptt);
		else if(value < lastValue)
			return MathHelper.interpolate(Math.max(lastValue - speed, value), lastValue, ptt);
		return value;
	}

	public static class ControlLeverBuilder extends ControlIntBuilder<ControlLever>
	{
		public ControlLeverBuilder(int min, int max, int defaultVal)
		{
			super(min, max, defaultVal);
			withModel(ModelLever.i);
		}

		@Override
		public ControlLever build(ControlHolder holder)
		{
			return new ControlLever(this, holder);
		}
	}
}
