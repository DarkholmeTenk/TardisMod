package tardis.core.console.control;

import java.util.EnumSet;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Strings;

import net.minecraft.util.StatCollector;

import io.darkcraft.darkcore.mod.handlers.containers.PlayerContainer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.Configs;
import tardis.client.renderer.tileents.ConsoleRenderer;
import tardis.common.core.HitPosition.HitRegion;
import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.CoreTileEntity;
import tardis.core.console.enums.ConsolePermissions;

public abstract class AbstractControl
{
	private final EnumSet<ConsolePermissions> requiredPermission;

	private final boolean canBeUnstable;

	private final boolean isFlightControl;

	private boolean isCurrentlyUnstable;

	public final double x,y,xSize,ySize,xScale,yScale,zScale,xAngle,angle;

	private final HitRegion hitRegion;

	private final String manualText;
	private final boolean manualIncludeValue;

	private AbstractControl(EnumSet<ConsolePermissions> requiredPermission, boolean canBeUnstable, boolean isFlightControl,
			double xPos, double yPos, double xSize, double ySize, double xScale, double yScale, double zScale, double xAngle, double angle,
			String manualText, boolean manualIncludeValue)
	{
		this.requiredPermission = requiredPermission;
		this.canBeUnstable = canBeUnstable;
		this.isFlightControl = isFlightControl;
		x = xPos;
		y = yPos;
		this.xSize = xSize;
		this.ySize = ySize;
		this.xScale = xScale;
		this.yScale = yScale;
		this.zScale = zScale;
		this.xAngle = xAngle;
		this.angle = angle;
		hitRegion = new HitRegion(xPos-(xSize/2), yPos-(ySize/2), xPos+(xSize/2), yPos+(ySize/2));
		this.manualText = manualText;
		this.manualIncludeValue = manualIncludeValue;
		if(canBeUnstable && isFlightControl)
			throw new RuntimeException("Control: " + this + " cannot be both unstable and flight control!");
	}

	public AbstractControl(ControlBuilder<?> builder, double regularX, double regularY, double xAngle)
	{
		this(builder.requiredPermissions, builder.canBeUnstable, builder.isFlightControl, builder.x, builder.y,
				regularX*builder.xScale, (regularY*builder.yScale) / 1.414,
				builder.xScale, builder.yScale, builder.zScale, xAngle, builder.angle,
				builder.manualText, builder.manualIncludeValue);
	}

	public final EnumSet<ConsolePermissions> getRequiredPermissions()
	{
		return requiredPermission;
	}

	public final HitRegion getHitRegion()
	{
		return hitRegion;
	}

	public boolean canBeUnstable()
	{
		return canBeUnstable;
	}

	public final boolean activate(PlayerContainer player, boolean sneaking)
	{
		if(isCurrentlyUnstable)
		{
			isCurrentlyUnstable = false;
			return true;
		}
		else
		{
			return activateControl(null, null, player, sneaking);
		}
	}

	protected abstract boolean activateControl(CoreTileEntity tardis, ConsoleTileEntity console, PlayerContainer player, boolean sneaking);

	@SideOnly(Side.CLIENT)
	public final void renderControl()
	{
		if(Configs.consoleDebug)
		{
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glPushMatrix();
			if(getHitRegion().contains(0, ConsoleRenderer.hp))
				GL11.glColor3f(1, 0, 0);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBegin(GL11.GL_LINE_LOOP);
			GL11.glVertex3d(hitRegion.yMin+0.5, hitRegion.yMin-1, hitRegion.zMin-1.5);
			GL11.glVertex3d(hitRegion.yMax+0.5, hitRegion.yMax-1, hitRegion.zMin-1.5);
			GL11.glVertex3d(hitRegion.yMax+0.5, hitRegion.yMax-1, hitRegion.zMax-1.5);
			GL11.glVertex3d(hitRegion.yMin+0.5, hitRegion.yMin-1, hitRegion.zMax-1.5);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}
		GL11.glPushMatrix();
		GL11.glTranslated(y+0.5, y-1, x-1.5);
		GL11.glRotated(xAngle, 0, 0, 1);
		GL11.glRotated(angle, 0, 1, 0);
		GL11.glScaled(xScale, yScale, zScale);
		render();
		GL11.glPopMatrix();
	}

	public final void addManualText(List<String> currentText)
	{
		if(!Strings.isNullOrEmpty(manualText))
			currentText.add(StatCollector.translateToLocal(manualText));
		if(manualIncludeValue)
			addValueToManual(currentText);
	}

	protected void addValueToManual(List<String> currentText) {};

	@SideOnly(Side.CLIENT)
	public abstract void render();

	public abstract static class ControlBuilder<T extends AbstractControl>
	{
		private EnumSet<ConsolePermissions> requiredPermissions = EnumSet.noneOf(ConsolePermissions.class);
		private boolean canBeUnstable = false;
		private boolean isFlightControl = false;
		private double x, y;
		private double xScale = 1;
		private double yScale = 1;
		private double zScale = 1;
		private double angle = 0;
		private String manualText = "";
		private boolean manualIncludeValue = true;

		protected ControlBuilder(){}

		public ControlBuilder<T> atPosition(double x, double y)
		{
			this.x = x;
			this.y = y;
			return this;
		}

		public ControlBuilder<T> requiresPermission(ConsolePermissions... permissions)
		{
			for(ConsolePermissions permission : permissions)
				this.requiredPermissions.add(permission);
			return this;
		}

		public ControlBuilder<T> isFlightControl()
		{
			this.isFlightControl = true;
			return this;
		}

		public ControlBuilder<T> canBeUnstable()
		{
			this.canBeUnstable = true;
			return this;
		}

		public ControlBuilder<T> manualIgnoreValue()
		{
			this.manualIncludeValue = false;
			return this;
		}

		public ControlBuilder<T> withManualText(String unlocalized)
		{
			this.manualText = unlocalized;
			return this;
		}

		public ControlBuilder<T> withScale(double xScale, double yScale, double zScale)
		{
			this.xScale = xScale;
			this.yScale = yScale;
			this.zScale = zScale;
			return this;
		}

		public ControlBuilder<T> withAngle(double angle)
		{
			this.angle = angle;
			return this;
		}

		public abstract T build();
	}
}
