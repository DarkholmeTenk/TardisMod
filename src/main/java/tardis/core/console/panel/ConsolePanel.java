package tardis.core.console.panel;

import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import io.darkcraft.darkcore.mod.datastore.PropertyMap;
import io.darkcraft.darkcore.mod.handlers.containers.PlayerContainer;
import io.darkcraft.darkcore.mod.nbt.NBTProperty;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.common.core.HitPosition;
import tardis.common.core.HitPosition.HitRegion;
import tardis.core.TardisInfo;
import tardis.core.console.control.AbstractControl;

public class ConsolePanel
{
	@NBTProperty
	private final PropertyMap<HitRegion, AbstractControl> controlMap = new PropertyMap<>(c->c.getHitRegion());
	private Set<AbstractControl> unstableControls = new LinkedHashSet<>();

	private TardisInfo info;

	public ConsolePanel()
	{

	}

	protected void addControl(AbstractControl control)
	{
		controlMap.add(control);
		if(control.canBeUnstable())
			unstableControls.add(control);
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

	public void setTardisInfo(TardisInfo info)
	{
		this.info = info;
		for(AbstractControl control : controlMap.values())
			control.setInfo(info);
	}

	public TardisInfo getTardisInfo()
	{
		return info;
	}

	@SideOnly(Side.CLIENT)
	public void render()
	{
		for(AbstractControl control : controlMap.values())
			control.renderControl();
	}
}
