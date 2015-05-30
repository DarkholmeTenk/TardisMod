package tardis.api;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;

public interface ILinkable
{
	public boolean link(EntityPlayer pl, SimpleCoordStore other);
	public boolean unlink(EntityPlayer pl, SimpleCoordStore other);
	public boolean unlink(EntityPlayer pl);
	public Set<SimpleCoordStore> getLinked();
}
