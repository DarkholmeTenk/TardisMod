package tardis.common.tileents;

import io.darkcraft.darkcore.mod.abstracts.AbstractTileEntity;
import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.interfaces.IActivatable;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import tardis.TardisMod;
import tardis.api.ILinkable;
import tardis.api.TardisPermission;
import tardis.common.core.helpers.Helper;
import tardis.common.dimension.TardisDataStore;

public class MagicDoorTileEntity extends AbstractTileEntity implements ILinkable, IActivatable
{
	private SimpleCoordStore otherDoor;
	private Set<SimpleCoordStore> otherDoorSet;

	@Override
	public void init()
	{
		super.init();
		if(otherDoor == null)
		{
			otherDoor = TardisMod.internalDoorBlock.linkMap.remove(coords);
			otherDoorSet = new HashSet<SimpleCoordStore>();
			otherDoorSet.add(otherDoor);
		}
	}

	@Override
	public boolean link(EntityPlayer pl, SimpleCoordStore link, SimpleCoordStore other)
	{
		return false;
	}

	@Override
	public boolean unlink(EntityPlayer pl, SimpleCoordStore link)
	{
		TardisDataStore ds = Helper.getDataStore(this);
		if((ds == null) || ds.hasPermission(pl, TardisPermission.PERMISSIONS))
		{
			if(otherDoor.getBlock() == TardisMod.magicDoorBlock)
				otherDoor.setBlock(TardisMod.internalDoorBlock, otherDoor.getMetadata(), 3);
			coords.setBlock(TardisMod.internalDoorBlock, getBlockMetadata(), 3);
			return true;
		}
		return false;
	}

	public boolean isValidLink()
	{
		if(otherDoor == null) return false;
		TileEntity te = otherDoor.getTileEntity();
		if(te instanceof MagicDoorTileEntity)
		{
			MagicDoorTileEntity mdte = (MagicDoorTileEntity) te;
			if(coords.equals(mdte.otherDoor))
				return true;
		}
		return false;
	}

	@Override
	public Set<SimpleCoordStore> getLinked(SimpleCoordStore link)
	{
		return otherDoorSet;
	}

	@Override
	public boolean isLinkable(SimpleCoordStore link)
	{
		return false;
	}

	@Override
	public boolean activate(EntityPlayer ent, int side)
	{
		System.out.println(otherDoor);
		return true;
	}
}
