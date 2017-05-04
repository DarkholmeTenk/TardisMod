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
		super.setValue(value);
		this.value = MathHelper.cycle(value, min, max);
	}

	private static final int ticksToUpdate = 10;
	private double getClientRendering(float ptt)
	{
		if((value == lastValue) || (valueChangeTT == 0))
			return value;
		if((tt - valueChangeTT) < ticksToUpdate)
		{
			float perc = ((tt+ptt) - valueChangeTT) / ticksToUpdate;
			if((lastValue == max) && (value == min))
				return MathHelper.interpolate(min, min-1, perc);
			if((lastValue == min) && (value == max))
				return MathHelper.interpolate(max, max+1, perc);
			return MathHelper.interpolate(value, lastValue, perc);
		}
		else
		{
			valueChangeTT = 0;
			return value;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float ptt)
	{
		GL11.glPushMatrix();
		GL11.glRotated(((getClientRendering(ptt) - min)/((1.0+max)-min))*360, 0, 1, 0);
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
