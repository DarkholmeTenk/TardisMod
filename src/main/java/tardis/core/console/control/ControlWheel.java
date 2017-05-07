package tardis.core.console.control;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;

import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.RenderHelper;
import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.client.renderer.model.console.ValveWheelModel;

@NBTSerialisable
public class ControlWheel extends AbstractControlInt
{
	private static ValveWheelModel wheel = new ValveWheelModel();

	private static final double regularXSize = 1;
	private static final double regularYSize = 1;

	private ControlWheel(ControlWheelBuilder builder, ControlHolder holder)
	{
		super(builder, regularXSize, regularYSize, 0, holder);
	}

	@Override
	public void setValue(int value)
	{
		this.value = MathHelper.cycle(value, min, max);
	}

	private static final float updateDist = 0.3f;
	private final float edgeRounding = Math.max(1, (max - min) / 8f);
	@Override
	protected float getState(float ptt)
	{
		if(value == lastValue)
			return value;
		float last;
		if((value < (min + edgeRounding)) && (lastValue > (max - edgeRounding)))
			last = min- (max-lastValue) - 1;
		else if((value > (max  - edgeRounding)) && (lastValue < (min + edgeRounding)))
			last = max + (lastValue - min) + 1;
		else
			last = lastValue;
		if((ptt == 1) && (Math.abs(value-last) < updateDist))
			return value;
		float speed = Math.max(updateDist, Math.abs(value - lastValue) * 0.3f);
		if(value > last)
			return MathHelper.interpolate(Math.min(last + speed, value), lastValue, ptt);
		else if(value < last)
			return MathHelper.interpolate(Math.max(last - speed, value), lastValue, ptt);
		return value;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float ptt)
	{
		GL11.glPushMatrix();
		GL11.glRotated(((getState(ptt) - min)/((1.0+max)-min))*360, 0, 1, 0);
		GL11.glTranslated(-0.03125, 0, -0.03125);
		RenderHelper.bindTexture(new ResourceLocation("tardismod","textures/models/TardisValveWheel.png"));
		wheel.render(null, 0F, 0F, 0F, 0F, 0F, 0.0625F);
		GL11.glPopMatrix();
	}

	public static class ControlWheelBuilder extends ControlIntBuilder<ControlWheel>
	{
		public ControlWheelBuilder(int min, int max, int defaultVal)
		{
			super(min, max, defaultVal);
		}

		@Override
		public ControlWheel build(ControlHolder holder)
		{
			return new ControlWheel(this, holder);
		}
	}
}
