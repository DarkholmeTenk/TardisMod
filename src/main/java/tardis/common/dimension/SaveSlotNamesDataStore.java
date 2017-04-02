package tardis.common.dimension;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;

import io.darkcraft.darkcore.mod.abstracts.AbstractWorldDataStore;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;

public class SaveSlotNamesDataStore extends AbstractWorldDataStore
{
	private HashMap<Integer,String> nameMap = new HashMap<Integer,String>();

	public SaveSlotNamesDataStore(String n)
	{
		super(n);
	}

	public SaveSlotNamesDataStore(int dim)
	{
		super("tardisSSN", dim);
	}

	public boolean setName(String name, int slot)
	{
		if((slot < 0) || (slot >= 20)) return false;
		nameMap.put(slot, name);
		markDirty();
		save();
		sendUpdate();
		return true;
	}

	public String getName(int slot)
	{
		if(nameMap.containsKey(slot))
			return nameMap.get(slot);
		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		System.out.println("Reading"+ ServerHelper.isServer());
		for(int i = 0; i < 20; i++)
			if(nbt.hasKey("name"+i))
				nameMap.put(i,nbt.getString("name"+i));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		System.out.println("Writing"+ ServerHelper.isServer());
		for(Integer i : nameMap.keySet())
			nbt.setString("name"+i, nameMap.get(i));
	}

}
