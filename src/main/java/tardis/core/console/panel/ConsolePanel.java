package tardis.core.console.panel;

import java.util.LinkedHashSet;
import java.util.Set;

import io.darkcraft.darkcore.mod.datastore.PropertyMap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.common.core.HitPosition.HitRegion;
import tardis.core.console.control.AbstractControl;

public class ConsolePanel
{
	private final PropertyMap<HitRegion, AbstractControl> controlMap = new PropertyMap<>(c->c.getHitRegion());
	private Set<AbstractControl> unstableControls = new LinkedHashSet<>();

	public ConsolePanel()
	{

	}

	protected void addControl(AbstractControl control)
	{
		controlMap.add(control);
		if(control.canBeUnstable())
			unstableControls.add(control);
	}

	@SideOnly(Side.CLIENT)
	public void render()
	{
		for(AbstractControl control : controlMap.values())
			control.renderControl();
	}
}
