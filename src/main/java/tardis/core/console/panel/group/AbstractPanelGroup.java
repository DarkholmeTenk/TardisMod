package tardis.core.console.panel.group;

import java.lang.reflect.Field;
import java.util.Optional;

import tardis.common.tileents.ConsoleTileEntity;
import tardis.core.TardisInfo;

public abstract class AbstractPanelGroup
{
	protected TardisInfo info;

	public final boolean fillIn(ConsoleTileEntity console)
	{
		info = TardisInfo.get(console);
		try
		{
			for(Field f : this.getClass().getDeclaredFields())
			{
				Panel p = f.getDeclaredAnnotation(Panel.class);
				if(p == null)
					continue;
				Class<?> c = f.getType();
				Optional<?> value = console.getPanel(c);
				Object o = value.get();
				if((o == null) && p.required())
					return false;
				f.set(this, o);
			}
		}
		catch(SecurityException | IllegalArgumentException | IllegalAccessException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static @interface Panel
	{
		public boolean required() default true;
	}
}
