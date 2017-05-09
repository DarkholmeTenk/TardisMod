package tardis.core.console.control;

import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;

import io.darkcraft.darkcore.mod.handlers.containers.PlayerContainer;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.RenderHelper;
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
	private final Supplier<Optional<Double>> method;

	private final Random r = new Random();

	private double current;
	private double last;

	public ControlGauge(ControlBuilder<?> builder, ControlHolder holder, double min, double max, Supplier<Optional<Double>> method)
	{
		super(builder, 0.32, 0.26, 0, holder);
		this.min = min;
		this.max = max;
		dist = (max - min);
		this.method = method;
	}

	@Override
	protected boolean activateControl(TardisInfo info, PlayerContainer player, boolean sneaking)
	{
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
			current = MathHelper.clamp(method.get().orElse(min), min, max) + randomness();
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
		private final Supplier<Optional<Double>> method;
		public ControlGaugeBuilder(double min, double max, Supplier<Optional<Double>> method)
		{
			this.min = min;
			this.max = max;
			this.method = method;
		}

		@Override
		public ControlGauge build(ControlHolder holder)
		{
			return new ControlGauge(this, holder, min, max, method);
		}

	}
}
