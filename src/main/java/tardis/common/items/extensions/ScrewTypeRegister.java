package tardis.common.items.extensions;

import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.nbt.NBTTagCompound;
import tardis.TardisMod;
import tardis.common.items.extensions.screwtypes.AbstractScrewdriverType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ScrewTypeRegister
{
	private static ArrayList<AbstractScrewdriverType> types = new ArrayList();

	public static AbstractScrewdriverType get(NBTTagCompound nbt)
	{
		if(nbt.hasKey("tname"))
			return get(nbt.getString("tname"));
		return TardisMod.defaultType;
	}

	private static AbstractScrewdriverType get(String string)
	{
		for(AbstractScrewdriverType type : types)
			if(type.getName().equals(string))
				return type;
		return TardisMod.defaultType;
	}

	public static void register(AbstractScrewdriverType type)
	{
		types.add(type);
		Collections.sort(types);
	}

	public static AbstractScrewdriverType get(int index)
	{
		if((index >= 0) && (index < types.size()))
			return types.get(index);
		return TardisMod.defaultType;
	}

	public static int getIndex(AbstractScrewdriverType type)
	{
		return types.indexOf(type);
	}

	public static int size()
	{
		return types.size();
	}

	@SideOnly(Side.CLIENT)
	public static void registerClientResources()
	{
		for(AbstractScrewdriverType type : types)
			type.registerClientResources();
	}
}
