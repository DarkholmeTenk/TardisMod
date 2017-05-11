package tardis.core.console.control;

import java.util.Random;
import java.util.function.Supplier;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;

import io.darkcraft.darkcore.mod.handlers.containers.PlayerContainer;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.RenderHelper;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;

import tardis.client.renderer.model.console.GaugeDisplayModel;
import tardis.client.renderer.model.console.GaugeNeedleModel;
import tardis.core.TardisInfo;

@NBTSerialisable
public class ControlGauge extends AbstractControl
{
	private final static GaugeDisplayModel gaugeDisplay = new GaugeDisplayModel();
	private final static ResourceLocation gaugeDisplayTex = new ResourceLocation("tardismod","textures/models/TardisConsoleGaugeDisplay.png");
	private final static GaugeNeedleModel gaugeNeedle = new GaugeNeedleModel();
	private final static ResourceLocation gaugeNeedleTex = new ResourceLocation("tardismod","textures/models/TardisConsoleGaugeNeedle.png");

	private final double dist;
	private final double min;
	private final double max;
	private final Supplier<Double> method;
	private final String formatString;

	private final Random r = new Random();

	private double actual;
	private double current;
	private double last;

	public ControlGauge(ControlGaugeBuilder builder, ControlHolder holder)
	{
		super(builder, 0.32, 0.26, 0, holder);
		min = builder.min;
		max = builder.max;
		dist = (max - min);
		method = builder.method;
		formatString = builder.formatString;
	}

	@Override
	protected boolean activateControl(TardisInfo info, PlayerContainer player, boolean sneaking)
	{
		if(formatString != null)
			ServerHelper.sendString(player.getEntity(), String.format(formatString, actual, min, max));
		return false;
	}

	private double randomness()
	{
		double t = dist / 10;
		return (r.nextDouble() * t) - (t/2);
	}

	@Override
	protected void tickControl()
	{
		if((tt % 10) == 0)
		{
			Double valObj = method.get();
			double val = valObj == null ? min : valObj;
			actual = MathHelper.clamp(val, min, max);
			current = actual + randomness();
		}
		last = MathHelper.clamp(getState(1), 0, 1);
	}

	private final static double speed = 0.02;
	private double getState(float ptt)
	{
		double val = (current - min) / dist;
		double dist = Math.abs(last-val);
		double s = MathHelper.clamp(dist, 0, speed);

		if(val > last)
			return last + (s * ptt);
		else if(val < last)
			return last - (s * ptt);
		return val;
	}

	@Override
	public void render(float ptt)
	{
		GL11.glRotated(90, 1, 0, 0);
		GL11.glRotated(90, 0, 0, -1);
		GL11.glTranslated(-0.155, -0.125, -0.05);
		GL11.glPushMatrix();
		GL11.glPushMatrix();
		RenderHelper.bindTexture(gaugeDisplayTex);
		gaugeDisplay.render(null, 0F, 0F, 0F, 0F, 0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		RenderHelper.bindTexture(gaugeNeedleTex);
		GL11.glTranslated(0.15, 0.175, 0.5/8);
		GL11.glRotated(-90-(getState(ptt) * 180), 0, 0, 1);
		GL11.glScaled(0.25, 0.40, 0.25);
		gaugeNeedle.render(null, 0F, 0F, 0F, 0F, 0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	public static class ControlGaugeBuilder extends ControlBuilder<ControlGauge>
	{
		private final double min;
		private final double max;
		private final Supplier<Double> method;
		private String formatString = null;

		public ControlGaugeBuilder(double min, double max, Supplier<Double> method)
		{
			this.min = min;
			this.max = max;
			this.method = method;
		}

		/**
		 * Use a format string. Arguments are passed in as <code>value, min, max</code> use $ specifiers to select
		 * individual arguments.<p/>
		 * All values are doubles.
		 */
		public ControlGaugeBuilder withFormatString(String string)
		{
			formatString = string;
			return this;
		}

		@Override
		public ControlGauge build(ControlHolder holder)
		{
			return new ControlGauge(this, holder);
		}

	}
}
