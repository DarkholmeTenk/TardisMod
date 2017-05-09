package tardis.core.console.panel;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;

import io.darkcraft.darkcore.mod.abstracts.AbstractTileEntity;
import io.darkcraft.darkcore.mod.datastore.PropertyMap;
import io.darkcraft.darkcore.mod.handlers.containers.PlayerContainer;
import io.darkcraft.darkcore.mod.nbt.NBTMethod;
import io.darkcraft.darkcore.mod.nbt.NBTMethod.Type;
import io.darkcraft.darkcore.mod.nbt.NBTProperty;
import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.api.IControlMatrix;
import tardis.common.core.HitPosition;
import tardis.common.core.HitPosition.HitRegion;
import tardis.core.TardisInfo;
import tardis.core.console.control.AbstractControl;
import tardis.core.console.control.AbstractControl.ControlBuilder;
import tardis.core.console.control.ControlHolder;

@NBTSerialisable
public class ConsolePanel implements ControlHolder
{
	@NBTProperty
	private final PropertyMap<HitRegion, AbstractControl> controlMap = new PropertyMap<>(c->c.getHitRegion());

	private final HashSet<AbstractControl> controls = new HashSet<>();
	private Set<AbstractControl> unstableControls = new LinkedHashSet<>();

	private TardisInfo info;
	private IControlMatrix matrix;
	private int side;

	public ConsolePanel()
	{

	}

	protected <T extends AbstractControl> T addControl(ControlBuilder<T> builder)
	{
		T control = builder.build(this);
		controlMap.add(control);
		if(control.canBeUnstable())
			unstableControls.add(control);
		controls.add(control);
		return control;
	}

	public void activate(PlayerContainer player, HitPosition position)
	{
		for(Entry<HitRegion, AbstractControl> entry : controlMap.entrySet())
		{
			if(entry.getKey().contains(position.side, position))
			{
				AbstractControl control = entry.getValue();
				control.activate(player, player.getEntity().isSneaking());
				if(unstableControls.contains(control))
					unstableControls.remove(control);
				return;
			}
		}
	}

	public void setTardisInfo(TardisInfo info, IControlMatrix matrix, int side)
	{
		this.info = info;
		this.matrix = matrix;
		this.side = side;
	}

	@Override
	public TardisInfo getTardisInfo()
	{
		return info;
	}

	@Override
	public int getSide()
	{
		return side;
	}

	@SideOnly(Side.CLIENT)
	public void render(float ptt)
	{
		for(AbstractControl control : controlMap.values())
			control.renderControl(ptt);
	}

	@Override
	public double yScale()
	{
		return 1.414;
	}

	@Override
	public double xAngle()
	{
		return 45;
	}

	public AbstractControl getControl(PlayerContainer player, HitPosition position)
	{
		for(Entry<HitRegion, AbstractControl> entry : controlMap.entrySet())
		{
			if(entry.getKey().contains(position.side, position))
			{
				return entry.getValue();
			}
		}
		return null;
	}

	public void tick()
	{
		for(AbstractControl control : controlMap.values())
			control.tick();
	}

	@NBTMethod(Type.READ)
	public void reset(NBTTagCompound nbt)
	{
		for(AbstractControl c : controls)
			if(!controlMap.containsKey(c.getHitRegion()))
				controlMap.add(c);
	}

	@Override
	public void markDirty()
	{
		if(matrix instanceof AbstractTileEntity)
			((AbstractTileEntity) matrix).queueUpdate();
	}
}
