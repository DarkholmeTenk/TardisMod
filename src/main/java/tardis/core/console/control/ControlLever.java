package tardis.core.console.control;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;

import io.darkcraft.darkcore.mod.handlers.containers.PlayerContainer;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.RenderHelper;
import io.darkcraft.darkcore.mod.nbt.NBTProperty;
import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.client.renderer.model.console.LeverBaseModel;
import tardis.client.renderer.model.console.LeverModel;
import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.CoreTileEntity;

@NBTSerialisable
public class ControlLever extends AbstractControl
{
	private static LeverModel lever = new LeverModel();
	private static LeverBaseModel leverBase = new LeverBaseModel();

	private static final double regularXSize = 1;
	private static final double regularYSize = 1;

	private final int min;
	private final int max;

	@NBTProperty
	private int value;

	private ControlLever(ControlLeverBuilder builder)
	{
		super(builder, regularXSize, regularYSize, 45);
		min = builder.min;
		max = builder.max;
		value = builder.defaultVal;
	}

	@Override
	protected boolean activateControl(CoreTileEntity tardis, ConsoleTileEntity console, PlayerContainer player, boolean sneaking)
	{
		setValue(getValue() + (sneaking ? -1 : 1));
		return true;
	}

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		this.value = MathHelper.clamp(value, min, max);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render()
	{
		GL11.glPushMatrix();
		GL11.glRotated((((value - min)/((double)max-min))*140) - 70, 1, 0, 0);
		RenderHelper.bindTexture(new ResourceLocation("tardismod","textures/models/TardisConsoleLever.png"));
		lever.render(null, 0F, 0F, 0F, 0F, 0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		RenderHelper.bindTexture(new ResourceLocation("tardismod","textures/models/TardisConsoleLeverBase.png"));
		leverBase.render(null,0F,0F,0F,0F,0F,0.0625F);
		GL11.glColor3d(1, 1, 1);
		GL11.glPopMatrix();
	}

	public static class ControlLeverBuilder extends ControlBuilder<ControlLever>
	{
		private int min;
		private int max;
		private int defaultVal;

		public ControlLeverBuilder(int min, int max, int defaultVal)
		{
			this.min = min;
			this.max = max;
			this.defaultVal = defaultVal;
		}

		@Override
		public ControlLever build()
		{
			return new ControlLever(this);
		}
	}
}
