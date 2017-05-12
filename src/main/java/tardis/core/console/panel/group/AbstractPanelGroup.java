package tardis.core.console.panel.group;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
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
				Panel p = f.getAnnotation(Panel.class);
				if(p == null)
					continue;
				Class<?> c = f.getType();
				Optional<?> value = console.getPanel(c);
				if(!value.isPresent())
				{
					if(p.required())
						return false;
					continue;
				}
				Object o = value.get();
				f.setAccessible(true);
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

	@Retention(RUNTIME)
	public static @interface Panel
	{
		public boolean required() default true;
	}
}
