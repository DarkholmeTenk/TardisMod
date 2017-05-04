package tardis.common.tileents;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import io.darkcraft.darkcore.mod.abstracts.AbstractTileEntitySer;
import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.handlers.containers.EntityContainerHandler;
import io.darkcraft.darkcore.mod.handlers.containers.PlayerContainer;
import io.darkcraft.darkcore.mod.helpers.DCReflectionHelper;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.interfaces.IExplodable;
import io.darkcraft.darkcore.mod.nbt.NBTProperty;
import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.api.IControlMatrix;
import tardis.common.core.HitPosition;
import tardis.common.core.helpers.Helper;
import tardis.common.dimension.TardisDataStore;
import tardis.common.dimension.damage.ExplosionDamageHelper;
import tardis.common.network.packet.ControlPacketHandler;
import tardis.core.console.control.AbstractControl;
import tardis.core.console.panel.ConsolePanel;
import tardis.core.console.panel.group.AbstractPanelGroup;
import tardis.core.console.panel.group.NavGroup;
import tardis.core.console.panel.interfaces.OptionPanels.OptPanelRelativeCoords;
import tardis.core.console.panel.types.normal.NormalPanelX;
import tardis.core.console.panel.types.normal.NormalPanelY;

@NBTSerialisable
public class ConsoleTileEntity extends AbstractTileEntitySer implements IControlMatrix, IExplodable
{
	public ConsoleTileEntity(){}

	public ConsoleTileEntity(World w)
	{
		worldObj = w;
	}

	@Override
	public void tick()
	{
		for(ConsolePanel panel : getPanels())
			if(panel != null)
				panel.tick();
	}

	private HitPosition activateSide(EntityPlayer pl, int blockX, int blockY, int blockZ, float i, float j, float k, int side)
	{
		float distanceAway = ((side == 0) || (side == 2)) ? (float) (Math.abs(pl.posX - 0.5) - 0.5) : (float) (Math
				.abs(pl.posZ - 0.5) - 0.5);
		float distanceSide = ((side == 0) || (side == 2)) ? (float) (pl.posZ + 1) : (float) (pl.posX + 1);
		float hitAway;
		if ((blockX != 0) || (blockZ != 0))
		{
			if ((side == 0) && (blockX < 1))
				return null;
			if ((side == 1) && (blockZ < 1))
				return null;
			if ((side == 2) && (blockX > -1))
				return null;
			if ((side == 3) && (blockZ > -1))
				return null;
			hitAway = (side == 0 ? i : (side == 2 ? 1 - i : (side == 1 ? k : 1 - k)));
		}
		else
		{
			if ((side == 0) && (i < 0.9))
				return null;
			if ((side == 2) && (i > 0.1))
				return null;
			if ((side == 3) && (k > 0.1))
				return null;
			if ((side == 1) && (k < 0.9))
				return null;
			j = j + 1;
			hitAway = (side == 0 ? i : (side == 2 ? 1 - i : (side == 1 ? k : 1 - k))) - 1;
		}
		float hitSide;
		if ((side == 0) || (side == 2))
			hitSide = blockZ + 1 + k;
		else
			hitSide = blockX + 1 + i;

		float delta = activatedDelta(hitAway, j, distanceAway, (float) ((pl.posY + pl.eyeHeight) - yCoord));
		float hitX = activatedX(hitAway, distanceAway, delta);
		float hitZ = activatedZ(hitSide, distanceSide, delta);
		if((side == 3) || (side == 0))
			hitZ = 3 - hitZ;
		if (((hitZ < 1) && ((1 - hitX) >= hitZ)) || ((hitZ > 2) && ((1 - hitX) > (3 - hitZ))))
			return null;
		return new HitPosition(hitX, hitZ, side);
	}

	private float activatedDelta(float xH, float yH, float xP, float yP)
	{
		float delta = (float) ((1.5 - xP - yP) / ((-xP + yH + xH) - yP));
		return delta;
	}

	private float activatedX(float xH, float xP, float delta)
	{
		return xP - (delta * (xP - xH));
	}

	private float activatedZ(float zH, float zP, float delta)
	{
		return zP - (delta * (zP - zH));
	}

	public int getControlFromHit(HitPosition hit)
	{
		return -1;
	}

	public AbstractControl getControlFromHit(int blockX, int blockY, int blockZ, Vec3 hit, EntityPlayer pl)
	{
		float i = (float) (hit.xCoord - blockX - xCoord);
		float j = (float) (hit.yCoord - blockY);
		float k = (float) (hit.zCoord - blockZ - zCoord);
		// TardisOutput.print("TConTE", String.format("x: %d, y %d, z %d : %f, %f, %f",blockX,blockY,blockZ,i,j,k));
		HitPosition hitPos = getHitPosition(pl, blockX, blockY, blockZ, i,j,k, 0);
		if (hitPos != null)
			return getControl(EntityContainerHandler.getPlayerContainer(pl), hitPos);
		return null;
	}

	public HitPosition getHP(int blockX, int blockY, int blockZ, Vec3 hit, EntityPlayer pl)
	{
		float i = (float) (hit.xCoord - blockX - xCoord);
		float j = (float) (hit.yCoord - blockY);
		float k = (float) (hit.zCoord - blockZ - zCoord);
		// TardisOutput.print("TConTE", String.format("x: %d, y %d, z %d : %f, %f, %f",blockX,blockY,blockZ,i,j,k));
		HitPosition hitPos = null;
		for (int cnt = 0; (cnt < 4) && (hitPos == null); cnt++)
			hitPos = activateSide(pl, blockX, blockY, blockZ, i, j, k, cnt);
		return hitPos;
	}

	public boolean activate(EntityPlayer pl, int blockX, int blockY, int blockZ, float i, float j, float k, int side)
	{
		if (ServerHelper.isServer())
			return true;
		HitPosition hit = getHitPosition(pl, blockX, blockY, blockZ, i, j, k, side);
		ControlPacketHandler.sendPacket(hit, this, EntityContainerHandler.getPlayerContainer(pl));
		return hit != null;
	}

	@NBTProperty
	private ConsolePanel[] panels = new ConsolePanel[]{new NormalPanelX(), new NormalPanelY(), null,null};
	public ConsolePanel[] getPanels()
	{
		return panels;
	}

	private final Map<Class<? extends AbstractPanelGroup>, Optional<AbstractPanelGroup>> groups = new HashMap<>();
	public void panelsChanged()
	{
		groups.clear();
	}

	public <T> Optional<T> getPanel(Class<T> clazz)
	{
		for(ConsolePanel panel : getPanels())
			if((panel != null) && clazz.isInstance(panel))
				return Optional.of((T) panel);
		return Optional.empty();
	}

	public <T extends AbstractPanelGroup> Optional<T> getPanelGroup(Class<T> clazz)
	{
		groups.computeIfAbsent(clazz, c->{
			T t = DCReflectionHelper.newInstance(clazz);
			if((t != null) && t.fillIn(this))
				return Optional.of(t);
			return Optional.empty();
		});
		return (Optional<T>) groups.get(clazz);
	}

	public boolean hasPanels(Class<?>... classes)
	{
		for(Class<?> clazz : classes)
		{
			if(!getPanel(clazz).isPresent())
				return false;
		}
		return true;
	}

	public boolean hasAllFlightPanels()
	{
		return getPanelGroup(NavGroup.class).isPresent();
	}

	@SideOnly(Side.CLIENT)
	public String[] getExtraInfo(int controlID)
	{
		CoreTileEntity core = Helper.getTardisCore(this);
		TardisDataStore ds = Helper.getDataStore(worldObj);

		return null;
	}

	public boolean getRelativeCoords()
	{
		return getPanel(OptPanelRelativeCoords.class).map(p->p.areCoordinatesRelative()).orElse(false);
	}

	@Override
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
		return AxisAlignedBB.getBoundingBox(xCoord-1, yCoord, zCoord-1, xCoord+2, yCoord+2, zCoord+2);
    }

	@Override
	public void explode(SimpleCoordStore pos, Explosion explosion)
	{
		TardisDataStore ds = Helper.getDataStore(this);
		if(ds != null)
			ExplosionDamageHelper.damage(ds.damage, pos, explosion, 0.6);
	}

	@Override
	public AbstractControl getControl(PlayerContainer player, HitPosition position)
	{
		int side = position.side;
		ConsolePanel[] panels = getPanels();
		if(panels[side] != null)
		{
			ConsolePanel panel = panels[side];
			return panel.getControl(player, position);
		}
		return null;
	}

	@Override
	public HitPosition getHitPosition(EntityPlayer pl, int blockX, int blockY, int blockZ, float i, float j, float k, int side)
	{
		HitPosition hit = null;
		for (int cnt = 0; (cnt < 4) && (hit == null); cnt++)
			hit = activateSide(pl, blockX, blockY, blockZ, i, j, k, cnt);
		return hit;
	}
}
