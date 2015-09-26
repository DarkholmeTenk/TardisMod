package tardis.common.tileents.extensions.chameleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import tardis.TardisMod;
import tardis.client.TardisClientProxy;

public class ChameleonRegistry<C extends IChameleon>
{
	private C		defaultC;
	private List<C> registry = new ArrayList<C>();
	private boolean postInit = false;

	public ChameleonRegistry(C defaultChameleon)
	{
		defaultC = defaultChameleon;
	}

	public void clear()
	{
		registry.clear();
	}

	public boolean register(C toRegister)
	{
		if(postInit) return false;
		registry.add(toRegister);
		Collections.sort(registry);
		return true;
	}

	public void postInit()
	{
		postInit = true;
		if(TardisMod.proxy instanceof TardisClientProxy)
			for(C c : registry)
				c.registerClientResources();
	}

	public int size()
	{
		return registry.size();
	}

	public int getIndex(C c)
	{
		return registry.indexOf(c);
	}

	public C get(int index)
	{
		if((index >= 0) && (index < size()))
			return registry.get(index);
		return defaultC;
	}

	public C get(String name)
	{
		for(C c : registry)
			if(c.getName().equals(name))
				return c;
		return defaultC;
	}

	public C get(NBTTagCompound nbt, String n)
	{
		if(nbt.hasKey(n))
			return get(nbt.getString(n));
		return defaultC;
	}
}
