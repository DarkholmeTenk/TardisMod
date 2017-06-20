package tardis.core.console.control;

import java.lang.reflect.Array;
import java.util.EnumSet;

import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;

import tardis.core.console.control.models.ModelLever;

@NBTSerialisable
public class EnumControl<T extends Enum<T>> extends ControlLever
{
	private final T[] array;

	private EnumControl(EnumControlBuilder<T> builder, ControlHolder holder)
	{
		super(builder, holder);
		this.array = builder.array;
	}

	public T getEnumValue()
	{
		return array[getValue()];
	}

	public static class EnumControlBuilder<T extends Enum<T>> extends AbstractControlLeverBuilder<EnumControl>
	{
		private static <T extends Enum<T>> int indexOf(EnumSet<T> enums, T def)
		{
			int i = 0;
			for(T t : enums)
			{
				if(t == def)
					return i;
				i++;
			}
			return 0;
		}

		private final T[] array;

		public EnumControlBuilder(EnumSet<T> enums, T def)
		{
			super(0, enums.size(), indexOf(enums, def));
			withModel(ModelLever.i);
			array = enums.toArray((T[]) Array.newInstance(def.getClass(), enums.size()));
		}

		@Override
		public EnumControl build(ControlHolder holder)
		{
			return new EnumControl(this, holder);
		}
	}
}
