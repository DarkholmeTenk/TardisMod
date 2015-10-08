package tardis.common.tileents;

import io.darkcraft.darkcore.mod.abstracts.AbstractTileEntity;
import io.darkcraft.darkcore.mod.datastore.Pair;
import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.interfaces.IActivatable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import tardis.TardisMod;
import tardis.api.IScrewable;
import tardis.api.ScrewdriverMode;
import tardis.api.TardisPermission;
import tardis.common.core.helpers.Helper;
import tardis.common.core.helpers.ScrewdriverHelper;
import tardis.common.dimension.TardisDataStore;

public class MagicDoorTileEntity extends AbstractTileEntity implements IScrewable, IActivatable
{
	private SimpleCoordStore otherDoor;
	private Set<SimpleCoordStore> otherDoorSet;

	@Override
	public void init()
	{
		super.init();
		if((otherDoor == null) && ServerHelper.isServer())
			setOtherDoor(TardisMod.internalDoorBlock.linkMap.remove(coords));
		recheckDoors(false);
	}

	public void clear()
	{
		TileEntity te = otherDoor.getTileEntity();
		if(te instanceof MagicDoorTileEntity)
			((MagicDoorTileEntity)te).otherDoor = null;
		otherDoor = null;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(((tt % 20) == 0) && (otherDoor == null))
			recheckDoors(true);
		if(isValidLink())
			checkEntities();
	}

	private AxisAlignedBB aabb;
	private AxisAlignedBB getAABB()
	{
		if(aabb != null)
			return aabb;
		int facing = getBlockMetadata();
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		switch(facing)
		{
			case 0: return aabb = AxisAlignedBB.getBoundingBox(x, y - 1, z - 1, x +0.3, y + 2, z + 2);
			case 2: return aabb = AxisAlignedBB.getBoundingBox(x + 0.7, y - 1, z - 1, x + 1, y + 2, z + 2);
			case 1:return aabb = AxisAlignedBB.getBoundingBox(x - 1, y - 1, z, x + 2, y + 2, z + 0.3);
			case 3:return aabb = AxisAlignedBB.getBoundingBox(x - 1, y - 1, z + 0.7, x + 2, y + 2, z + 1);
			default: return aabb = AxisAlignedBB.getBoundingBox(x - 1, y - 1, z +0.5, x + 2, y + 2, z + 1);
		}
	}

	private void checkEntities()
	{
		AxisAlignedBB bb = getAABB();
		List ents = worldObj.getEntitiesWithinAABBExcludingEntity(null, bb);
		for(Object o : ents)
		{
			if(!(o instanceof Entity)) continue;
			Entity ent = (Entity) o;
			if((ent.posX < bb.minX) || (ent.posY < bb.minY) || (ent.posZ < bb.minZ)) continue;
			if((ent.posX > bb.maxX) || (ent.posY > bb.maxY) || (ent.posZ > bb.maxZ)) continue;
			transportEntity(ent);
		}
	}

	private Pair<Double,Double> transform(int of, int nf, double dx, double dz)
	{
		double nx = 0;
		double nz = 0;
		double sideDistance = 0;
		double inDistance = 0;
		switch(of)
		{
			case 0:		inDistance = -dx; sideDistance = -dz; break;
			case 1:		inDistance = -dz; sideDistance = +dx; break;
			case 2:		inDistance = +dx; sideDistance = +dz; break;
			case 3:		inDistance = +dz; sideDistance = -dx; break;
		}
		switch(nf)
		{
			case 0:		nx += inDistance; nz += sideDistance; break;
			case 1:		nz += inDistance; nx -= sideDistance; break;
			case 2:		nx -= inDistance; nz -= sideDistance; break;
			case 3:		nz -= inDistance; nx += sideDistance; break;
		}
		return new Pair<Double,Double>(nx,nz);
	}

	private void transportEntity(Entity ent)
	{
		if((ent == null)) return;
		//System.out.println("AHHHHH!"+ ent.toString());
		if(ent instanceof EntityPlayer)
		{
			EntityPlayer pl = (EntityPlayer) ent;
			if(pl.capabilities.isCreativeMode && pl.capabilities.isFlying) return;
		}
		double dx = ent.posX - (xCoord + 0.5);
		double dy = ent.posY - yCoord;
		double dz = ent.posZ - (zCoord + 0.5);
		double nx = otherDoor.x + 0.5;
		double ny = otherDoor.y + dy;
		double nz = otherDoor.z + 0.5;
		int nf = otherDoor.getMetadata();
		int of = getBlockMetadata() % 4;
		Pair<Double,Double> pos = transform(of,nf,dx,dz);
		Pair<Double,Double> mot = transform(of,nf,ent.motionX,ent.motionZ);
		nx += pos.a;
		nz += pos.b;
		int facingDiff = (6 + (otherDoor.getMetadata() - of))%4;
		float nr = ent.rotationYaw + (90 * facingDiff);
		ent.setPositionAndRotation(nx, ny, nz, nr, ent.rotationPitch);
		ent.setLocationAndAngles(nx, ny, nz, nr, ent.rotationPitch);
		ent.posX = nx;
		ent.posY = ny;
		ent.posZ = nz;
		ent.rotationYaw = nr;
		ent.motionX = mot.a;
		ent.motionZ = mot.b;
		ent.velocityChanged = true;
	}

	private void setOtherDoor(SimpleCoordStore other)
	{
		if(other == null) return;
		otherDoor = other;
		otherDoorSet = new HashSet<SimpleCoordStore>();
		otherDoorSet.add(otherDoor);
		sendUpdate();
	}

	private void recheckDoors(boolean rem)
	{
		CoreTileEntity core = Helper.getTardisCore(this);
		if(core != null)
			core.refreshDoors(rem);
	}

	public boolean isValidLink()
	{
		if(otherDoor == null) return false;
		TileEntity te = otherDoor.getTileEntity();
		if(te instanceof MagicDoorTileEntity)
		{
			MagicDoorTileEntity mdte = (MagicDoorTileEntity) te;
			if(coords().equals(mdte.otherDoor))
				return true;
		}
		return false;
	}

	@Override
	public boolean activate(EntityPlayer ent, int side)
	{
		System.out.println(coords().getMetadata()+":"+otherDoor);
		sendUpdate();
		return false;
	}

	@Override
	public void writeTransmittable(NBTTagCompound nbt)
	{
		if(otherDoor != null)
			otherDoor.writeToNBT(nbt, "od");
	}

	@Override
	public void readTransmittable(NBTTagCompound nbt)
	{
		if(otherDoor != null) return;
		setOtherDoor(SimpleCoordStore.readFromNBT(nbt, "od"));
	}

	@Override
	public boolean screw(ScrewdriverHelper helper, ScrewdriverMode mode, EntityPlayer player)
	{
		if(mode == ScrewdriverMode.Dismantle)
		{
			TardisDataStore ds = Helper.getDataStore(this);
			if((ds == null) || ds.hasPermission(player, TardisPermission.ROOMS))
			{
				otherDoor = null;
				recheckDoors(true);
				return true;
			}
		}
		return false;
	}
}
