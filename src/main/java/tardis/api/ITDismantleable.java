package tardis.api;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface ITDismantleable
{
	public boolean canDismantle(SimpleCoordStore scs, EntityPlayer pl);
	public List<ItemStack> dismantle(SimpleCoordStore scs, EntityPlayer pl);
}
