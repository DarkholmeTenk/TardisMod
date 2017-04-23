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

	private ControlWheel(ControlWheelBuilder builder)
	{
		super(builder, regularXSize, regularYSize, 45);
	}

	@Override
	public void setValue(int value)
	{
		this.value = MathHelper.cycle(value, min, max);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render()
	{
		GL11.glPushMatrix();
		GL11.glRotated(((value - min)/((double)max-min))*360, 0, 1, 0);
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
		public ControlWheel build()
		{
			return new ControlWheel(this);
		}
	}
}
