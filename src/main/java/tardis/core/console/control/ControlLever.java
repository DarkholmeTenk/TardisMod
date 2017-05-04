package tardis.core.console.control;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;

import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.RenderHelper;
import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.client.renderer.model.console.LeverBaseModel;
import tardis.client.renderer.model.console.LeverModel;

@NBTSerialisable
public class ControlLever extends AbstractControlInt
{
	private static LeverModel lever = new LeverModel();
	private static LeverBaseModel leverBase = new LeverBaseModel();

	private static final double regularXSize = 0.8;
	private static final double regularYSize = 0.45;

	private ControlLever(ControlLeverBuilder builder, ControlHolder holder)
	{
		super(builder, regularXSize, regularYSize, 0, holder);
	}

	@Override
	public void setValue(int value)
	{
		super.setValue(value);
		this.value = MathHelper.clamp(value, min, max);
	}

	private static final int ticksToUpdate = 10;
	private double getClientRendering(float ptt)
	{
		if((value == lastValue) || (valueChangeTT == 0))
			return value;
		if((tt - valueChangeTT) < ticksToUpdate)
		{
			float perc = ((tt+ptt) - valueChangeTT) / ticksToUpdate;
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
		GL11.glRotated((((getClientRendering(ptt) - min)/((double)max-min))*140) - 70, 1, 0, 0);
		RenderHelper.bindTexture(new ResourceLocation("tardismod","textures/models/TardisConsoleLever.png"));
		lever.render(null, 0F, 0F, 0F, 0F, 0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		RenderHelper.bindTexture(new ResourceLocation("tardismod","textures/models/TardisConsoleLeverBase.png"));
		leverBase.render(null,0F,0F,0F,0F,0F,0.0625F);
		GL11.glColor3d(1, 1, 1);
		GL11.glPopMatrix();
	}

	public static class ControlLeverBuilder extends ControlIntBuilder<ControlLever>
	{
		public ControlLeverBuilder(int min, int max, int defaultVal)
		{
			super(min, max, defaultVal);
		}

		@Override
		public ControlLever build(ControlHolder holder)
		{
			return new ControlLever(this, holder);
		}
	}
}
