package tardis.common.tileents;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import io.darkcraft.darkcore.mod.interfaces.IExplodable;
import io.darkcraft.darkcore.mod.nbt.NBTMethod;
import io.darkcraft.darkcore.mod.nbt.NBTMethod.Type;
import io.darkcraft.darkcore.mod.nbt.NBTProperty;
import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.api.IControlMatrix;
import tardis.common.TMRegistry;
import tardis.common.core.HitPosition;
import tardis.common.core.helpers.Helper;
import tardis.common.dimension.TardisDataStore;
import tardis.common.dimension.damage.ExplosionDamageHelper;
import tardis.common.network.packet.ControlPacketHandler;
import tardis.core.TardisInfo;
import tardis.core.console.control.AbstractControl;
import tardis.core.console.panel.ConsolePanel;
import tardis.core.console.panel.group.AbstractPanelGroup;
import tardis.core.console.panel.group.NavGroup;
import tardis.core.console.panel.interfaces.OptionPanels.OptPanelRelativeCoords;
import tardis.core.console.panel.types.normal.NormalPanelX;
import tardis.core.console.panel.types.normal.NormalPanelY;
import tardis.core.console.panel.types.normal.NormalPanelZ;

@NBTSerialisable
public class ConsoleTileEntity extends AbstractTileEntitySer implements IControlMatrix, IExplodable
{
	public ConsoleTileEntity(){}

	public ConsoleTileEntity(World w)
	{
		worldObj = w;
	}

	@Override
	public void init()
	{
		restoreAfterLoad();
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
		boolean even = (side & 1) == 0;
		float plPosX = (float) pl.posX;
		float plPosY = (float) pl.posY;
		float plPosZ = (float) pl.posZ;
		float distanceAway = (Math.abs((even ? plPosX : plPosZ) - 0.5f) - 0.5f);
		float distanceSide = (even ? plPosZ : plPosX) +1;
		float hitAway;
		hitAway = (side == 0 ? (blockX + i) - 1 : (side == 2 ?-blockX-i : (side == 1 ?
				(blockZ + k) - 1 : -blockZ - k)));
		if(((side == 0) && (plPosX < 1))
				|| ((side == 2) && (plPosX > 0))
				|| (((side == 1) && (plPosZ < 1))
				|| ((side == 3) && (plPosZ > 0))))
			return null;
		if((blockX == 0) && (blockZ == 0))
		{
			hitAway = (side == 0 ? i : (side == 2 ?1-i : (side == 1 ? k : 1 - k)));
			if (((side == 0) && (i < 0.9))
					|| ((side == 2) && (i > 0.1))
					|| ((side == 3) && (k > 0.1))
					|| ((side == 1) && (k < 0.9)))
				return null;
			j = j + 1;
			hitAway --;
		}
		float hitSide = 1 + (even ? blockZ + k : blockX + i);

		float delta = activatedDelta(hitAway, j, distanceAway, (plPosY) - yCoord);
		float hitX = activatedX(hitAway, distanceAway, delta);
		float hitZ = activatedZ(hitSide, distanceSide, delta);
		if((side == 3) || (side == 0))
			hitZ = 3 - hitZ;
		HitPosition hp = new HitPosition(hitX, hitZ, side);
		if((hp.posZ < (1-hp.posY)) || ((hp.posZ-2) > (hp.posY)) || (hp.posY > 1) || (hp.posY < 0))
			return null;
		return hp;
	}

	private float activatedDelta(float xH, float yH, float xP, float yP)
	{
		float delta = (float) ((1.5 - xP - yP) / ((-xP + yH + xH) - yP));
//		if(Math.random() < 0.005)
//			System.out.format("xH: %1.2f yH: %1.2f xP: %1.2f yP: %1.2f d: %1.2f%n", xH, yH, xP, yP, delta);
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
		if (ServerHelper.isServer(this))
		{
			return true;
		}
		HitPosition hit = getHitPosition(pl, blockX, blockY, blockZ, i, j, k, side);
		ControlPacketHandler.sendPacket(hit, this, EntityContainerHandler.getPlayerContainer(pl));
		return hit != null;
	}

	@NBTProperty
	private ConsolePanel[] panels = new ConsolePanel[]{new NormalPanelX(), new NormalPanelY(), new NormalPanelZ(),null};
	public ConsolePanel[] getPanels()
	{
		if(ServerHelper.isServer(this) && (panels[2] == null))
			setPanel(2,new NormalPanelZ());
		return panels;
	}

	public void removePanel(ConsolePanel consolePanel)
	{
		int side = consolePanel.getSide();
		panels[side] = null;
		ItemStack is = TMRegistry.consolePanelItem.getPanelItem(consolePanel);
		WorldHelper.dropItemStack(is, coords().getCenter().translate(0, 1.5, 0));
		panelsChanged();
	}

	private final Map<Class<? extends AbstractPanelGroup>, Optional<AbstractPanelGroup>> groups = new HashMap<>();
	public void panelsChanged()
	{
		groups.clear();
		queueUpdate();
	}

	private void setPanel(int i, ConsolePanel panel)
	{
		panels[i] = panel;
		panel.setTardisInfo(TardisInfo.get(this), this, i);
		panelsChanged();
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

	@NBTMethod(Type.READ)
	public void restoreAfterLoad()
	{
		TardisInfo info = TardisInfo.get(this);
		ConsolePanel[] panels = getPanels();
		for(int i = 0; (i < 4) && (i < panels.length); i++)
			if(panels[i] != null)
				panels[i].setTardisInfo(info, this, i);
	}

	@Override
	public void activatedWithoutControl(PlayerContainer playerCont, HitPosition position)
	{
		int side = position.side;
		EntityPlayer player = playerCont.getEntity();
		if(panels[side] == null)
		{
			ConsolePanel newPanel = TMRegistry.consolePanelItem.getPanel(player.getHeldItem());
			if(newPanel != null)
			{
				setPanel(side, newPanel);
			}
		}
	}
}
