package tardis.api;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;

/**
 * SimpleCoordStore link is the position of the block which is being linked to other. For TileEntities, this is effectively this.
 * @author dark
 *
 */
public interface ILinkable
{
	public boolean link(EntityPlayer pl, SimpleCoordStore link, SimpleCoordStore other);
	public boolean unlink(EntityPlayer pl, SimpleCoordStore link, SimpleCoordStore other);
	public boolean unlink(EntityPlayer pl, SimpleCoordStore link);
	public Set<SimpleCoordStore> getLinked(SimpleCoordStore link);
	public boolean isLinkable();
}
