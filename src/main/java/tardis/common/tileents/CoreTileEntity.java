package tardis.common.tileents;

import io.darkcraft.darkcore.mod.abstracts.AbstractTileEntity;
import io.darkcraft.darkcore.mod.config.ConfigFile;
import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.SoundHelper;
import io.darkcraft.darkcore.mod.helpers.TeleportHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import io.darkcraft.darkcore.mod.interfaces.IActivatable;
import io.darkcraft.darkcore.mod.interfaces.IChunkLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import tardis.TardisMod;
import tardis.api.IArtronEnergyProvider;
import tardis.api.TardisFunction;
import tardis.api.TardisPermission;
import tardis.api.TardisUpgradeMode;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.dimension.TardisDataStore;
import tardis.common.dimension.TardisDimensionHandler;
import tardis.common.items.KeyItem;
import tardis.common.tileents.components.TardisTEComponent;
import tardis.common.tileents.extensions.CoreGrid;
import tardis.common.tileents.extensions.LabFlag;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CoreTileEntity extends AbstractTileEntity implements IActivatable, IChunkLoader, IGridHost, IArtronEnergyProvider
{
	private static ConfigFile				config					= null;
	public static final ChatComponentText	cannotModifyFly			= new ChatComponentText("[TARDIS] You do not have permission to fly this TARDIS");
	public static final ChatComponentText	cannotModifyRecall		= new ChatComponentText("[TARDIS] You do not have permission to recall this TARDIS");
	public static final ChatComponentText	cannotModifyRecolour	= new ChatComponentText("[TARDIS] You do not have permission to recolour this block");
	public static final ChatComponentText	cannotModifyRoundel		= new ChatComponentText("[TARDIS] You do not have permission to modify this roundel");
	public static final ChatComponentText	cannotModifyMessage		= new ChatComponentText("[TARDIS] You do not have permission to modify this TARDIS");
	public static final ChatComponentText	cannotModifyPermissions	= new ChatComponentText("[TARDIS] You do not have permission to modify this TARDIS's permissions");
	public static final ChatComponentText	cannotUpgradeMessage	= new ChatComponentText("[TARDIS] You do not have enough upgrade points");
	private int								oldExteriorWorld		= 0;

	private float							lastProximity			= 0;
	private double							lastSpin				= 0;

	private double							speed					= 4;
	private static double					maxSpeed;

	private int								energy;
	private static int						buttonTime				= 80;

	private FlightState						flightState				= FlightState.LANDED;
	// Timer to make sure buttons occur at a regular interval
	private int								flightButtonTimer		= 0;
	// Timer just used to make sure engine sounds don't overlap
	private int								flightSoundTimer		= 0;
	// Timer tracking how long the TARDIS has been in its current flight state
	private int								flightTimer				= 0;
	private int								totalFlightTimer		= 0;

	private int								numRooms				= 0;

	private SimpleCoordStore				transmatPoint			= null;
	private int								shields;
	private int								hull;
	private boolean							deletingRooms			= false;
	private static double					explodeChance			= 0.25;
	private boolean							explode					= false;

	private TardisDataStore					ds						= null;
	private int								instability				= 0;
	private int								desDim					= 0;
	private int								desX					= 0;
	private int								desY					= 0;
	private int								desZ					= 0;
	private Integer[]						desLocs					= null;
	private String							desDimName				= "Overworld";
	private int								screenAngle				= 0;

	private enum LockState
	{
		Open, OwnerOnly, KeyOnly, Locked
	};

	private LockState					lockState			= LockState.Open;
	private int							lastLockSound		= Integer.MIN_VALUE;
	private static int					lockSoundDelay		= 80;

	private HashSet<SimpleCoordStore>	roomSet				= new HashSet<SimpleCoordStore>();
	private String						ownerName;
	private ArrayList<Integer>			modders				= new ArrayList<Integer>();

	private static ChunkCoordIntPair[]	loadable			= null;
	private boolean						forcedFlight		= false;

	private boolean						fast				= false;

	private ArrayList<SimpleCoordStore>	gridLinks			= new ArrayList<SimpleCoordStore>();

	private static final int			takeOffTicks		= 20 * 11;
	private static final int			landSlowTicks		= 20 * 22;
	private static final int			landFastTicks		= 20 * 5;
	private SimpleCoordStore			sourceLocation		= null;
	private SimpleCoordStore			destLocation		= null;
	private double						distanceToTravel	= 0;
	private double						distanceTravelled	= 0;
	private int							currentBlockSpeed	= 0;
	private int							maxBlockSpeed		= 0;

	private static int					maxNumRooms			= 30;
	private static int					maxNumRoomsInc		= 10;
	private static int					maxShields			= 1000;
	private static int					maxShieldsInc		= 500;
	private static int					maxHull;
	private static int					maxEnergy			= 1000;
	private static int					maxEnergyInc		= 1000;
	private static int					energyPerSecond		= 1;
	private static int					energyPerSecondInc	= 1;
	public static int					energyCostDimChange	= 2000;
	private static int					energyCostFlightMax	= 3000;
	private static int					maxMoveForFast		= 3;
	private static int					energyPerSpeed		= 200;
	private IGridNode					node				= null;

	static
	{
		if (config == null) refreshConfigs();
		loadable = new ChunkCoordIntPair[4];
		loadable[0] = new ChunkCoordIntPair(0, 0);
		loadable[1] = new ChunkCoordIntPair(-1, 0);
		loadable[2] = new ChunkCoordIntPair(0, -1);
		loadable[3] = new ChunkCoordIntPair(-1, -1);
	}

	public CoreTileEntity(World w)
	{
		// this();
		worldObj = w;
		ds = Helper.getDataStore(WorldHelper.getWorldID(w));
		shields = maxShields;
		hull = maxHull;

		energy = 100;
	}

	public CoreTileEntity()
	{
		shields = maxShields;
		hull = maxHull;
		energy = 100;
	}

	private TardisDataStore gDS()
	{
		if (ds == null) if (worldObj != null) ds = Helper.getDataStore(worldObj);
		return ds;
	}

	public static void refreshConfigs()
	{
		if (config == null) config = TardisMod.configHandler.registerConfigNeeder("TARDIS Core");
		explodeChance = config.getDouble("Explosion chance", 0.6, "The chance of an explosion being caused if an active control is not pressed");
		maxSpeed = config.getDouble("max speed", 8, "The maximum speed setting that can be reached");
		maxEnergy = config.getInt("Max energy", 1000, "The base maximum energy");
		maxNumRooms = config.getInt("Max rooms", 30, "The base maximum number of rooms");
		maxShields = config.getInt("Max shields", 1000, "The base maximum amount of shielding");
		maxHull = config.getInt("Max hull", 1000, "The maximum hull strength");
		maxNumRoomsInc = config.getInt("Max rooms increase", 10, "How much a level of max rooms increases the maximum number of rooms");
		maxShieldsInc = config.getInt("Max shields increase", 500, "How much a level of max shields increases the amount of shielding");
		maxEnergyInc = config.getInt("Max energy increase", 1000, "How much a level of energy increases the max amount of energy");
		energyPerSecondInc = config.getInt("Energy Rate increase", 1, "How much a level of energy rate increases the amount of energy per second");
		energyCostDimChange = config.getInt("Dimension jump cost", 2000, "How much energy it costs to jump between dimensions");
		energyCostFlightMax = config.getInt("Max flight cost", 3000, "The maximum amount that a flight can cost");
		maxMoveForFast = config.getInt("Short hop distance", 3, "The maximum distance for which a jump can be considered a short hop which takes less time");
		energyPerSecond = config.getInt("Energy rate", 1, "The base amount of energy the TARDIS generates per second");
		energyPerSpeed = config.getInt("Energy per speed", 50, "Energy per unit of block speed", "The tardis moves at a max speed of (max flight cost / energy per speed) blocks per tick");
		lockSoundDelay = config.getInt("Lock sound delay", 80, "Amount of ticks between lock sounds being allowed to play");
	}

	private void calculateFlightDistances()
	{
		SimpleCoordStore newStart = null;
		TardisTileEntity ext = gDS().getExterior();
		if (ext != null)
			newStart = new SimpleCoordStore(gDS().getExterior());
		else if ((sourceLocation != null) && (destLocation != null)) newStart = sourceLocation.travelTo(destLocation, distanceTravelled / distanceToTravel, true).floor();
		if (newStart != null)
		{
			ConsoleTileEntity con = getConsole();
			if (con != null)
			{
				fast = (con.shouldLand() && isFastLanding());
				sourceLocation = newStart;
				destLocation = con.getCoordsFromControls(newStart);
				distanceToTravel = sourceLocation.distance(destLocation);
				distanceTravelled = 0;
			}
		}
		else
			fast = true;
	}

	private void nextFlightState()
	{
		ConsoleTileEntity con = getConsole();
		if (con == null) return;
		if (flightState == FlightState.TAKINGOFF)
		{
			removeOldBox();
			if ((fast && con.shouldLand()) || forcedFlight)
			{
				placeBox();
				flightState = FlightState.LANDING;
			}
			else
			{
				if (con.shouldLand())
					flightState = FlightState.FLIGHT;
				else
					flightState = FlightState.DRIFT;
			}
		}
		else if (flightState == FlightState.DRIFT)
		{
			calculateFlightDistances();
			if (con.shouldLand() && !fast)
				flightState = FlightState.FLIGHT;
			else
			{
				placeBox();
				flightState = FlightState.LANDING;
			}
		}
		else if (flightState == FlightState.FLIGHT)
		{
			if (con.shouldLand())
			{
				placeBox();
				flightState = FlightState.LANDING;
			}
			else
				flightState = FlightState.DRIFT;
		}
		else if (flightState == FlightState.LANDING) land();
		flightTimer = 0;
	}

	private void handleSound()
	{
		if (flightTimer == 0)
		{
			if (flightState == FlightState.TAKINGOFF) SoundHelper.playSound(this, "tardismod:takeoff", 0.75F);
			if ((flightState == FlightState.LANDING) && fast) SoundHelper.playSound(this, "tardismod:landingInt", 0.75F);
		}
		if ((flightState == FlightState.FLIGHT) || (flightState == FlightState.DRIFT))
		{
			if ((flightSoundTimer++ % 69) == 0) SoundHelper.playSound(this, "tardismod:engines", 0.75F);
		}
		else if ((flightState == FlightState.LANDING) && !fast && (flightTimer < (landSlowTicks - landFastTicks)))
		{
			if ((flightSoundTimer++ % 69) == 0) SoundHelper.playSound(this, "tardismod:engines", 0.75F);
		}
		else if ((flightState == FlightState.LANDING) && !fast && (flightTimer == (landSlowTicks - landFastTicks))) SoundHelper.playSound(this, "tardismod:landingInt", 0.75F);
	}

	private void flightTick()
	{

		if ((currentBlockSpeed == 0) || (maxBlockSpeed == 0))
		{
			currentBlockSpeed = 1;
			updateMaxBlockSpeed();
		}
		ConsoleTileEntity con = getConsole();
		if (con == null) return;
		totalFlightTimer++;
		if(ServerHelper.isServer())
			handleSound();
		flightTimer++;
		if ((flightState == FlightState.TAKINGOFF) && (flightTimer >= takeOffTicks)) nextFlightState();
		if ((flightState == FlightState.LANDING) && (flightTimer >= (fast ? landFastTicks : landSlowTicks))) nextFlightState();
		if (((flightState == FlightState.DRIFT) && con.shouldLand()) || ((flightState == FlightState.FLIGHT) && !con.shouldLand())) nextFlightState();
		if ((flightState == FlightState.FLIGHT) && (distanceTravelled >= distanceToTravel)) nextFlightState();

		if (flightState == FlightState.FLIGHT)
		{
			if (((flightTimer % 20) == 0) && (currentBlockSpeed < maxBlockSpeed) && takeArtronEnergy(energyPerSpeed, false))
			{
				currentBlockSpeed++;
				sendUpdate();
			}
			distanceTravelled += MathHelper.clamp(currentBlockSpeed, 1, maxBlockSpeed);
		}
		if (inAbortableFlight() && con.unstableFlight() && !forcedFlight)
		{
			int buttonTime = getButtonTime();
			if ((flightButtonTimer++ % buttonTime) == 0)
			{
				if (con.unstableControlPressed() && (flightButtonTimer > 0))
				{
					instability = MathHelper.clamp(MathHelper.floor(instability - (0.5 * getSpeed(false))), 0, 100);
					gDS().addXP(getSpeed(false) + 4);
				}
				else if (flightButtonTimer > 0)
				{
					instability = MathHelper.clamp(MathHelper.floor(instability + getSpeed(false)), 0, 100);
					if (shouldExplode()) explode = true;
				}
				con.randomUnstableControl();
			}
		}
		else if (flightButtonTimer != 0)
		{
			con.clearUnstableControl();
			flightButtonTimer = 0;
		}
	}

	private void safetyTick()
	{
		List<Object> players = worldObj.playerEntities;
		for (Object o : players)
		{
			if (o instanceof EntityPlayer)
			{
				EntityPlayer pl = (EntityPlayer) o;
				if ((pl.posY < -1) && !pl.capabilities.isFlying) Helper.teleportEntityToSafety(pl);
			}
		}
	}

	@Override
	public void sendUpdate()
	{
		getDestinationLocations();
		super.sendUpdate();
		gDS().markMaybeDirty();
	}

	@Override
	public void init()
	{
		if(TardisMod.plReg == null) return;
		if (ownerName == null) setOwner(TardisMod.plReg.getPlayerName(WorldHelper.getWorldID(this)));
		if (ds == null) ds = Helper.getDataStore(WorldHelper.getWorldID(this));
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (explode)
		{
			double xO = (rand.nextDouble() * 3) - 1.5;
			double zO = (rand.nextDouble() * 3) - 1.5;
			SoundHelper.playSound(this, "minecraft:random.explosion", 0.5F);
			worldObj.createExplosion(null, xCoord + 0.5 + xO, yCoord - 0.5, zCoord + 0.5 + zO, 1F, true);
			explode = false;
		}

		if ((tt == 2) && ServerHelper.isServer()) refreshDoors(false);

		if ((tt % 20) == 0)
		{
			addArtronEnergy(getEnergyPerSecond(gDS().getLevel(TardisUpgradeMode.REGEN)), false);
			safetyTick();
		}

		if (inFlight()) flightTick();

		if (!worldObj.isRemote)
		{
			if (((tt % 100) == 0) && (TardisMod.plReg != null))
			{
				if (!TardisMod.plReg.hasTardis(ownerName)) TardisMod.plReg.addPlayer(ownerName, worldObj.provider.dimensionId);
			}

			if (deletingRooms)
			{
				Iterator<SimpleCoordStore> i = roomSet.iterator();
				if (i.hasNext())
				{
					SimpleCoordStore coord = i.next();
					TileEntity te = worldObj.getTileEntity(coord.x, coord.y, coord.z);
					if ((te != null) && (te instanceof SchemaCoreTileEntity))
					{
						TardisOutput.print("TCTE", "Removing room @ " + coord);
						SchemaCoreTileEntity schemaCore = (SchemaCoreTileEntity) te;
						schemaCore.remove();
					}
					i.remove();
				}
				else
				{
					deletingRooms = false;
					refreshDoors(true);
					numRooms = 0;
				}
			}
			if ((tt % 1200) == 0)
			{
				sendUpdate();
			}
		}
	}

	@Override
	public boolean activate(EntityPlayer player, int side)
	{
		if (ServerHelper.isClient()) return true;
		double mx = xCoord + 0.5;
		double mz = zCoord + 0.5;
		if(mz > player.posZ)
			screenAngle = 90 + MathHelper.round((180 / Math.PI) * Math.atan((mx-player.posX)/(mz-player.posZ)));
		else
			screenAngle = -90 + MathHelper.round((180 / Math.PI) * Math.atan((mx-player.posX)/(mz-player.posZ)));
		sendUpdate();
		return true;
	}

	public boolean hasKey(EntityPlayer player, boolean inHand)
	{
		if (inHand)
		{
			ItemStack held = player.getHeldItem();
			String on = KeyItem.getOwnerName(held);
			if (on != null)
				TardisOutput.print("TCTE", "Key owner = " + on);
			else
				TardisOutput.print("TCTE", "Key owner = null");
			if ((on != null) && on.equals(ownerName)) return true;
		}
		else
		{
			InventoryPlayer inv = player.inventory;
			if (inv == null) return false;
			for (ItemStack is : inv.mainInventory)
			{
				String on = KeyItem.getOwnerName(is);
				if ((on != null) && on.equals(ownerName)) return true;
			}
		}
		return false;
	}

	public boolean canOpenLock(EntityPlayer player, boolean isInside)
	{
		if (isInside && !lockState.equals(LockState.Locked))
			return true;
		else if (isInside) return false;

		if (player == null) return false;

		if (lockState.equals(LockState.Open))
			return true;
		else if (lockState.equals(LockState.Locked))
			return false;
		else if (lockState.equals(LockState.OwnerOnly))
			return isOwner(ServerHelper.getUsername(player));
		else if (lockState.equals(LockState.KeyOnly)) return isOwner(ServerHelper.getUsername(player)) || hasKey(player, false);
		return false;
	}

	private void playLockSound()
	{
		if(tt < (lastLockSound + lockSoundDelay)) return;
		lastLockSound = tt;
		SoundHelper.playSound(worldObj, xCoord+13, yCoord-1, zCoord, "tardismod:locked", 0.5f);
		TardisDataStore ds = gDS();
		if((ds != null) && (ds.getExterior() != null))
			SoundHelper.playSound(ds.getExterior(), "tardismod:locked", 0.5f);
	}

	private void enterTardis(EntityLivingBase ent)
	{
		TeleportHelper.teleportEntity(ent, worldObj.provider.dimensionId, xCoord + 13.5, yCoord - 1, zCoord + 0.5, 90);
	}

	public void enterTardis(EntityPlayer player, boolean ignoreLock)
	{
		if (ServerHelper.isClient()) return;
		if (ignoreLock || canOpenLock(player, false))
			enterTardis(player);
		else
		{
			playLockSound();
			player.addChatMessage(new ChatComponentText("[TARDIS]The door is locked"));
		}
	}

	public void leaveTardis(EntityPlayer player, boolean ignoreLock)
	{
		if (ServerHelper.isClient()) return;
		if (!inFlight() && gDS().hasValidExterior())
		{
			if (ignoreLock || canOpenLock(player, true))
			{
				World ext = WorldHelper.getWorld(gDS().exteriorWorld);
				if (ext != null)
				{
					int facing = ext.getBlockMetadata(gDS().exteriorX, gDS().exteriorY, gDS().exteriorZ);
					int dx = 0;
					int dz = 0;
					double rot = 0;
					switch (facing)
					{
						case 0:
							dz = -1;
							rot = 180;
							break;
						case 1:
							dx = 1;
							rot = -90;
							break;
						case 2:
							dz = 1;
							rot = 0;
							break;
						case 3:
							dx = -1;
							rot = 90;
							break;
					}

					if (softBlock(ext, gDS().exteriorX + dx, gDS().exteriorY, gDS().exteriorZ + dz) && softBlock(ext, gDS().exteriorX + dx, gDS().exteriorY, gDS().exteriorZ + dz))
					{
						TeleportHelper.teleportEntity(player, gDS().exteriorWorld, gDS().exteriorX + 0.5 + (dx), gDS().exteriorY, gDS().exteriorZ + 0.5 + (dz), rot);
					}
					else
						ServerHelper.sendString(player, "TARDIS", "The door is obstructed");
				}
				else
					ServerHelper.sendString(player, "TARDIS", "The door refuses to open");
			}
			else
			{
				playLockSound();
				ServerHelper.sendString(player, "TARDIS", "The door is locked");
			}
		}
		else if (inFlight())
			ServerHelper.sendString(player, "TARDIS", "The door won't open in flight");
		else
			ServerHelper.sendString(player, "TARDIS", "The door refuses to open for some reason");
	}

	public boolean changeLock(EntityPlayer pl, boolean inside)
	{
		if (ServerHelper.isClient()) return false;

		TardisOutput.print("TCTE", "Changing lock");
		if (!hasKey(pl, true) && !inside) return false;

		if (lockState.equals(LockState.Locked) && !inside) // If you're outside,
															// you can't unlock
															// a "Locked" door
			return false;
		// If you're not the owner/not inside, you can't unlock an owner only door
		if (lockState.equals(LockState.OwnerOnly) && !(inside || isOwner(pl))) return false;

		if (!pl.isSneaking()) return false;

		int num = LockState.values().length;
		LockState[] states = LockState.values();
		lockState = states[((lockState.ordinal() + 1) % num)];
		// If you're not inside, you can't put the door in locked mode
		if (lockState.equals(LockState.Locked) && !inside) lockState = states[((lockState.ordinal() + 1) % num)];
		// If you're not the owner, you can't put the door in owner only mode
		if (lockState.equals(LockState.OwnerOnly) && !isOwner(pl)) lockState = states[((lockState.ordinal() + 1) % num)];

		TardisOutput.print("TTE", "Lockstate:" + lockState.toString());
		if (lockState.equals(LockState.KeyOnly))
			pl.addChatMessage(new ChatComponentText("[TARDIS]The door will open for its owner and people with keys"));
		else if (lockState.equals(LockState.Locked))
			pl.addChatMessage(new ChatComponentText("[TARDIS]The door will not open"));
		else if (lockState.equals(LockState.Open))
			pl.addChatMessage(new ChatComponentText("[TARDIS]The door will open for all"));
		else if (lockState.equals(LockState.OwnerOnly)) pl.addChatMessage(new ChatComponentText("[TARDIS]The door will only open for its owner"));
		return true;
	}

	private int[] scanForValidPos(World w, int[] current, int mh)
	{
		int[] check = { 0, 1, -1, 2, -2, 3, -3, 4, -4, 5, -5, 6, -6, 7, -7, 8, -8, 9, -9 };
		for (int i = 0; i < check.length; i++)
		{
			int yO = check[i];
			for (int j = 0; j < check.length; j++)
			{
				int xO = check[j];
				for (int k = 0; k < check.length; k++)
				{
					int zO = check[k];
					if (isValidPos(w, current[0] + xO, current[1] + yO, current[2] + zO, mh)) { return new int[] { current[0] + xO, current[1] + yO, current[2] + zO }; }
				}
			}
		}
		return current;
	}

	private int[] scanForLandingPad(World w, int[] current)
	{
		int[] check = { 0, 1, -1, 2, -2, 3, -3, 4, -4, 5, -5, -6, 6 };
		for (int i = 0; i < check.length; i++)
		{
			int xO = check[i];
			for (int j = 0; j < check.length; j++)
			{
				int zO = check[j];
				for (int k = 0; k < check.length; k++)
				{
					int yO = check[k];
					TileEntity te = w.getTileEntity(current[0] + xO, current[1] + yO, current[2] + zO);
					if (te instanceof LandingPadTileEntity)
					{
						if (((LandingPadTileEntity) te).isClearBottom())
						{
							return new int[] { current[0] + xO, current[1] + yO + 1, current[2] + zO };
						}
						else if (((LandingPadTileEntity) te).isClearTop()) { return new int[] { current[0] + xO, current[1] + yO + 2, current[2] + zO }; }
					}
				}
			}
		}
		return current;
	}

	public int[] findGround(World w, int[] curr)
	{
		if (softBlock(w, curr[0], curr[1] - 1, curr[2]))
		{
			int newY = curr[1] - 2;
			while ((newY > 0) && softBlock(w, curr[0], newY, curr[2]))
				newY--;
			return new int[] { curr[0], newY + 1, curr[2] };
		}
		return curr;
	}

	private int[] getModifiedControls(ConsoleTileEntity con, int[] posArr)
	{
		if (con == null) return posArr;
		World w = WorldHelper.getWorld(con.getDimFromControls());
		if (w == null) return posArr;
		int mh = TardisDimensionHandler.getMaxHeight(con.getDimFromControls());
		if (posArr[1] >= mh) posArr[1] = mh - 1;
		if (!(isValidPos(w, posArr[0], posArr[1], posArr[2], mh)))
		{
			if ((posArr[0] != gDS().exteriorX) || (posArr[1] != gDS().exteriorY) || (posArr[2] != gDS().exteriorZ)) posArr = scanForValidPos(w, posArr, mh);
		}
		if (con.getLandOnGroundFromControls()) posArr = findGround(w, posArr);
		if (con.getLandOnPadFromControls()) posArr = scanForLandingPad(w, posArr);
		if ((posArr[1] == 0) && (w.getBlock(posArr[0], posArr[1], posArr[2]) == Blocks.bedrock)) posArr[1]++;
		return posArr;
	}

	public boolean isMoving()
	{
		ConsoleTileEntity con = getConsole();
		if (con == null) return false;
		int dim = con.getDimFromControls();
		if (gDS().exteriorWorld == 10000)
		{
			if (oldExteriorWorld != dim) return true;
		}
		else if (dim != gDS().exteriorWorld) return true;
		int[] posArr = new int[] { con.getXFromControls(gDS().exteriorX), con.getYFromControls(gDS().exteriorY), con.getZFromControls(gDS().exteriorZ) };
		posArr = getModifiedControls(con, posArr);
		TardisOutput.print("TCTE", "Moving to :" + Arrays.toString(posArr) + " from " + gDS().exteriorX + "," + gDS().exteriorY + "," + gDS().exteriorZ);
		if (Math.abs(posArr[0] - gDS().exteriorX) > maxMoveForFast) return true;
		if (Math.abs(posArr[1] - gDS().exteriorY) > maxMoveForFast) return true;
		if (Math.abs(posArr[2] - gDS().exteriorZ) > maxMoveForFast) return true;
		return false;
	}

	public boolean takeOffEnergy(EntityPlayer pl)
	{
		ConsoleTileEntity con = getConsole();
		if (con != null)
		{
			int dDim = con.getDimFromControls();

			int extW = inFlight() ? oldExteriorWorld : gDS().exteriorWorld;
			int enCost = (dDim != extW ? Math.max(TardisDimensionHandler.getEnergyCost(dDim), TardisDimensionHandler.getEnergyCost(extW)) : 0);
			return takeArtronEnergy(enCost, false);
		}
		return false;
	}

	public boolean takeOff(boolean forced, EntityPlayer pl)
	{
		forcedFlight = forced;
		if (!inFlight())
		{
			ConsoleTileEntity con = getConsole();
			if (con == null) return false;
			if ((!con.shouldLand()) || takeOffEnergy(pl))
			{
				instability = 0;
				flightState = FlightState.TAKINGOFF;
				flightTimer = 0;
				flightSoundTimer = 0;
				flightButtonTimer = 0;
				currentBlockSpeed = 1;
				updateMaxBlockSpeed();
				TardisTileEntity te = gDS().getExterior();
				fast = (te == null) || (con.shouldLand() && isFastLanding());
				oldExteriorWorld = gDS().exteriorWorld;
				if (te != null)
				{
					te.takeoff();
					calculateFlightDistances();
				}
				sendUpdate();
				return true;
			}
			else
				ServerHelper.sendString(pl, "TARDIS", "Not enough energy to take off");
		}
		return false;
	}

	public boolean takeOff(EntityPlayer pl)
	{
		return takeOff(false, pl);
	}

	private boolean isValidPos(World w, int x, int y, int z, int mh)
	{
		return (y > 0) && (y < (mh - 1)) && softBlock(w, x, y, z) && softBlock(w, x, y + 1, z);
	}

	private int getUnstableOffset()
	{
		if (forcedFlight) return 0;
		if (instability == 0) return 0;
		return rand.nextInt(Math.max(0, 2 * instability)) - instability;
	}

	private void removeOldBox()
	{
		if(ServerHelper.isClient())return;
		World w = WorldHelper.getWorld(gDS().exteriorWorld);
		if (w != null)
		{
			if (w.getBlock(gDS().exteriorX, gDS().exteriorY, gDS().exteriorZ) == TardisMod.tardisBlock)
			{
				w.setBlockToAir(gDS().exteriorX, gDS().exteriorY, gDS().exteriorZ);
				w.setBlockToAir(gDS().exteriorX, gDS().exteriorY + 1, gDS().exteriorZ);
				TardisOutput.print("TCTE", "Blanking exterior");
				gDS().exteriorWorld = 10000;
			}
		}
	}

	private void placeBox()
	{
		if (ServerHelper.isClient() || gDS().hasValidExterior()) return;
		ConsoleTileEntity con = getConsole();
		if (con == null)
		{
			flightTimer--;
			return;
		}
		int[] posArr;
		if ((distanceToTravel == 0) || (distanceTravelled >= distanceToTravel) || (sourceLocation == null) || (destLocation == null) || forcedFlight || fast)
			posArr = new int[] { con.getXFromControls(gDS().exteriorX) + getUnstableOffset(), con.getYFromControls(gDS().exteriorY), con.getZFromControls(gDS().exteriorZ) + getUnstableOffset() };
		else
		{
			SimpleCoordStore newPos = sourceLocation.travelTo(destLocation, distanceTravelled / distanceToTravel, true).round();
			posArr = new int[] { newPos.x + getUnstableOffset(), newPos.y + getUnstableOffset(), newPos.z + getUnstableOffset() };
		}
		int facing = con.getFacingFromControls();
		posArr = getModifiedControls(con, posArr);
		World w = WorldHelper.getWorld(con.getDimFromControls());
		w.setBlock(posArr[0], posArr[1], posArr[2], TardisMod.tardisBlock, facing, 3);
		w.setBlock(posArr[0], posArr[1] + 1, posArr[2], TardisMod.tardisTopBlock, facing, 3);

		gDS().setExterior(w, posArr[0], posArr[1], posArr[2]);
		oldExteriorWorld = 0;
		TardisTileEntity tardis = gDS().getExterior();
		if (tardis != null)
		{
			tardis.linkedDimension = WorldHelper.getWorldID(this);
			tardis.land(fast);
		}
	}

	public boolean attemptToLand()
	{
		if (inAbortableFlight())
		{
			if (distanceTravelled <= maxMoveForFast) fast = true;
			nextFlightState();
			if (flightState == FlightState.LANDING) return true;
		}
		return false;
	}

	private void land()
	{
		if (inFlight())
		{
			ConsoleTileEntity con = getConsole();
			forcedFlight = false;
			currentBlockSpeed = 1;
			if(!forcedFlight)
				gDS().addXP((con != null) && !fast && con.isStable() ? 15 : (45 - instability));
			fast = false;
			flightState = FlightState.LANDED;
			SoundHelper.playSound(this, "tardismod:engineDrum", 0.75F);
			TardisTileEntity ext = gDS().getExterior();
			if (ext != null)
			{
				ext.forceLand();
				List<Entity> inside = ext.getEntitiesInside();
				for (Entity e : inside)
					if (e instanceof EntityLivingBase) enterTardis((EntityLivingBase) e);
			}
			if (con != null) con.land();
			sendUpdate();
		}
	}

	private int getButtonTime()
	{
		double mod = 1;
		if (getSpeed(false) != -1)
			mod = getMaxSpeed() / ((getSpeed(false) + 1) * 2);
		else
			mod = 0;
		mod = MathHelper.clamp(mod, 0.5, 4);
		int buttonTimeMod = MathHelper.clamp(MathHelper.round(buttonTime * mod), 30, buttonTime * 4);
		return buttonTimeMod;
	}

	private boolean shouldExplode()
	{
		double eC = explodeChance * (((getSpeed(false) + 1) * 3) / getMaxSpeed());
		eC *= MathHelper.clamp(3.0 / ((gDS().getLevel() + 1) / 2), 0.2, 1);
		return rand.nextDouble() < eC;
	}

	// //////////////////////////////////////////////
	// ////////////DATA STUFF////////////////////////
	// //////////////////////////////////////////////

	public boolean inCoordinatedFlight()
	{
		return inFlight() && (flightState != FlightState.DRIFT);
	}

	public boolean inFlight()
	{
		return flightState != FlightState.LANDED;
	}

	public boolean inAbortableFlight()
	{
		return (flightState == FlightState.DRIFT) || (flightState == FlightState.FLIGHT);
	}

	public float getProximity()
	{
		if (inFlight())
		{
			int rate = 40;
			double val = Math.abs((tt % rate) - (rate / 2));
			double max = 0.4;
			lastProximity = (float) (max * 2 * (val / rate));
			return lastProximity;
		}
		else if (lastProximity > 0)
			return (lastProximity = lastProximity - (1 / 20.0F));
		else
		{
			return 0;
		}
	}

	@SideOnly(Side.CLIENT)
	public double getSpin()
	{
		double slowness = 3;
		if (inFlight()) lastSpin = ((lastSpin + 1) % (360 * slowness));
		return lastSpin / slowness;
	}

	public ConsoleTileEntity getConsole()
	{
		TileEntity te = worldObj.getTileEntity(xCoord, yCoord - 2, zCoord);
		if (te instanceof ConsoleTileEntity) return (ConsoleTileEntity) te;
		return null;
	}

	public EngineTileEntity getEngine()
	{
		TileEntity te = worldObj.getTileEntity(xCoord, yCoord - 5, zCoord);
		if (te instanceof EngineTileEntity) return (EngineTileEntity) te;
		return null;
	}

	public SchemaCoreTileEntity getSchemaCore()
	{
		TileEntity te = worldObj.getTileEntity(xCoord, yCoord - 10, zCoord);
		if (te instanceof SchemaCoreTileEntity) return (SchemaCoreTileEntity) te;
		return null;
	}

	public void loadConsoleRoom(String sub)
	{
		String fullName = "tardisConsole" + sub;
		SchemaCoreTileEntity schemaCore = getSchemaCore();
		if (schemaCore != null)
			Helper.loadSchemaDiff(schemaCore.getName(), fullName, worldObj, xCoord, yCoord - 10, zCoord, 0);
		else
			Helper.loadSchema(fullName, worldObj, xCoord, yCoord - 10, zCoord, 0);
	}

	public boolean canModify(String playerName)
	{
		if (playerName == null) return false;
		return isOwner(playerName) || ((modders != null) && modders.contains(playerName.hashCode()));
	}

	public void toggleModifier(EntityPlayer modder, String name)
	{
		if (isOwner(modder.getCommandSenderName()))
		{
			if (!modder.getCommandSenderName().equals(name))
			{
				if (modders.contains(name.hashCode()))
					modders.remove(name.hashCode());
				else
					modders.add(name.hashCode());
			}
		}
		else
			modder.addChatMessage(cannotModifyMessage);
	}

	private boolean isOwner(EntityPlayer pl)
	{
		if (pl == null) return false;
		return isOwner(ServerHelper.getUsername(pl));
	}

	public boolean isOwner(String name)
	{
		if (ownerName != null) return ownerName.equals(name);
		return false;
	}

	public String getOwner()
	{
		return ownerName;
	}

	public void setOwner(String name)
	{
		if (name == null) return;
		TardisOutput.print("TCTE", "Setting owner to " + name + "#" + ServerHelper.isClient(), TardisOutput.Priority.DEBUG);
		ownerName = name;
		if (ServerHelper.isServer() && (TardisMod.plReg != null) && !TardisMod.plReg.hasTardis(ownerName)) TardisMod.plReg.addPlayer(ownerName, worldObj.provider.dimensionId);
		TardisTileEntity ext = gDS().getExterior();
		if (ext != null) ext.owner = name;
	}

	public boolean hasFunction(TardisFunction fun)
	{
		return gDS().hasFunction(fun);
	}

	public double getMaxSpeed()
	{
		return maxSpeed;
	}

	public double getSpeed(boolean modified)
	{
		if (forcedFlight) return getMaxSpeed() + 1;
		if (!modified) return speed;
		double mod = ((double) getNumRooms()) / getMaxNumRooms();
		return speed * (1 - (mod / 2.0));
	}

	public boolean isFastLanding()
	{
		if (isMoving())
		{
			TardisOutput.print("TCTE", "Definitely moving?");
			return getSpeed(true) > getMaxSpeed();
		}
		return true;
	}

	public double addSpeed(double a)
	{
		speed = speed + a;
		speed = MathHelper.clamp(speed, 0, getMaxSpeed());
		System.out.println("HI!" + speed);
		updateMaxBlockSpeed();
		sendUpdate();
		return speed;
	}

	private void updateMaxBlockSpeed()
	{
		maxBlockSpeed = MathHelper.floor((energyCostFlightMax * (getSpeed(true) / getMaxSpeed())) / energyPerSpeed);
		if (maxBlockSpeed < 3) maxBlockSpeed = 3;
	}

	public int getNumRooms()
	{
		if (ServerHelper.isServer()) return roomSet.size();
		return numRooms;
	}

	public int getMaxNumRooms(int level)
	{
		return maxNumRooms + (maxNumRoomsInc * level);
	}

	public int getMaxNumRooms()
	{
		return getMaxNumRooms(gDS().getLevel(TardisUpgradeMode.ROOMS));
	}

	private void refreshRoomCount()
	{
		Iterator<SimpleCoordStore> iter = roomSet.iterator();
		while (iter.hasNext())
		{
			SimpleCoordStore scs = iter.next();
			TileEntity te = scs.getTileEntity();
			if (!(te instanceof SchemaCoreTileEntity)) iter.remove();
		}
	}

	private void refreshDoors(boolean isRoomBeingRemoved)
	{
		if (tt <= 2) return;
		synchronized (roomSet)
		{
			System.out.println("Rechecking doors");
			for (SimpleCoordStore room : roomSet)
			{
				TileEntity te = room.getTileEntity();
				if (te instanceof SchemaCoreTileEntity) ((SchemaCoreTileEntity) te).recheckDoors(isRoomBeingRemoved);
			}
			SchemaCoreTileEntity te = getSchemaCore();
			if (te != null) te.recheckDoors(isRoomBeingRemoved);
		}
	}

	public boolean addRoom(boolean sub, SchemaCoreTileEntity te)
	{
		boolean ret = false;
		synchronized (roomSet)
		{
			if (sub)
			{
				if (ServerHelper.isServer() && (te != null)) roomSet.remove(new SimpleCoordStore(te));
				ret = true;
				if (!deletingRooms) refreshDoors(true);
			}

			if (!sub && (getNumRooms() < getMaxNumRooms()))
			{
				if (ServerHelper.isServer() && (te != null)) roomSet.add(new SimpleCoordStore(te));
				ret = true;
				refreshDoors(false);
			}
		}
		updateMaxBlockSpeed();
		return ret;
	}

	public boolean addRoom(SchemaCoreTileEntity te)
	{
		if (ServerHelper.isServer() && (te != null)) return addRoom(false, te);
		return false;
	}

	public Set<SimpleCoordStore> getRooms()
	{
		return roomSet;
	}

	public void removeAllRooms(boolean force)
	{
		if (!force)
			removeAllRooms();
		else
		{
			for (SimpleCoordStore coord : roomSet)
			{
				TileEntity te = worldObj.getTileEntity(coord.x, coord.y, coord.z);
				if ((te != null) && (te instanceof SchemaCoreTileEntity))
				{
					TardisOutput.print("TCTE", "Removing room @ " + coord);
					SchemaCoreTileEntity schemaCore = (SchemaCoreTileEntity) te;
					schemaCore.remove();
				}
			}
			roomSet.clear();
			numRooms = 0;
		}
	}

	public void removeAllRooms()
	{
		deletingRooms = true;
	}

	public int getEnergyPerSecond()
	{
		TardisDataStore mds = gDS();
		if (mds == null) return getEnergyPerSecond(0);
		return getEnergyPerSecond(mds.getLevel(TardisUpgradeMode.REGEN));
	}

	public int getEnergyPerSecond(int level)
	{
		return energyPerSecond + (energyPerSecondInc * level);
	}

	public int getMaxArtronEnergy(int level)
	{
		return maxEnergy + (maxEnergyInc * level);
	}

	@Override
	public int getMaxArtronEnergy()
	{
		TardisDataStore mds = gDS();
		if (mds == null) return getMaxArtronEnergy(0);
		return getMaxArtronEnergy(mds.getLevel(TardisUpgradeMode.ENERGY));
	}

	@Override
	public int getArtronEnergy()
	{
		return energy;
	}

	@Override
	public boolean addArtronEnergy(int amount, boolean sim)
	{
		if (amount == 0) return true;
		if (!sim) energy += amount;
		energy = MathHelper.clamp(energy, 0, getMaxArtronEnergy(gDS().getLevel(TardisUpgradeMode.ENERGY)));
		return true;
	}

	@Override
	public boolean takeArtronEnergy(int amount, boolean sim)
	{
		if (amount == 0) return true;
		if (energy >= amount)
		{
			if (!sim) energy -= amount;
			return true;
		}
		energy = MathHelper.clamp(energy, 0, getMaxArtronEnergy(gDS().getLevel(TardisUpgradeMode.ENERGY)));
		return false;
	}

	@Override
	public boolean doesSatisfyFlag(LabFlag flag)
	{
		switch (flag)
		{
			case NOTINFLIGHT:
				return !inFlight();
			case INFLIGHT:
				return inFlight();
			case INCOORDINATEDFLIGHT:
				return inCoordinatedFlight();
			case INUNCOORDINATEDFLIGHT:
				return inFlight() && !inCoordinatedFlight();
			default:
				return false;
		}
	}

	public int getShields()
	{
		return shields;
	}

	public int getMaxShields(int level)
	{
		return maxShields + (level * maxShieldsInc);
	}

	public int getHull()
	{
		return hull;
	}

	public int getMaxHull(int level)
	{
		return maxHull;
	}

	public void addGridLink(SimpleCoordStore pos)
	{
		TardisOutput.print("TCTE", "Adding coord:" + pos.toString());
		if (pos != null) gridLinks.add(pos);
	}

	public DimensionalCoord[] getGridLinks(SimpleCoordStore asker)
	{
		if (gridLinks.size() == 0) return null;
		ArrayList<DimensionalCoord> coords = new ArrayList<DimensionalCoord>(gridLinks.size() - 1);
		Iterator<SimpleCoordStore> iter = gridLinks.iterator();
		while (iter.hasNext())
		{
			SimpleCoordStore s = iter.next();
			if (s.equals(asker)) continue;

			TileEntity te = worldObj.getTileEntity(s.x, s.y, s.z);
			if (te instanceof ComponentTileEntity)
			{
				if (((ComponentTileEntity) te).hasComponent(TardisTEComponent.GRID))
					coords.add(new DimensionalCoord(worldObj, s.x, s.y, s.z));
				else
					iter.remove();
			}
			else
				iter.remove();
		}
		DimensionalCoord[] retVal = new DimensionalCoord[coords.size()];
		coords.toArray(retVal);
		return retVal;
	}

	private int getMaxTransmatDistance()
	{
		return 250;
	}

	public boolean canTransmatEntity(Entity ent)
	{
		if (!hasFunction(TardisFunction.TRANSMAT)) return false;
		TardisDataStore ds = gDS();
		if ((ds != null) && (ent instanceof EntityPlayer)) if (!ds.hasPermission((EntityPlayer) ent, TardisPermission.TRANSMAT)) return false;
		int entWorld = WorldHelper.getWorldID(ent.worldObj);
		boolean trans = false;
		if (entWorld == WorldHelper.getWorldID(worldObj)) // if ent is in the tardis
			trans = true;
		else if ((entWorld == gDS().exteriorWorld) && (flightState.equals(FlightState.LANDING) || flightState.equals(FlightState.LANDED)))
		{
			double distance = Math.pow(((gDS().exteriorX + 0.5) - ent.posX), 2);
			distance += Math.pow(((gDS().exteriorY + 0.5) - ent.posY), 2);
			distance += Math.pow(((gDS().exteriorZ + 0.5) - ent.posZ), 2);
			distance = Math.pow(distance, 0.5);
			if (distance <= getMaxTransmatDistance()) trans = true;
		}
		return trans;
	}

	public boolean transmatEntity(Entity ent)
	{
		if (!hasFunction(TardisFunction.TRANSMAT)) return false;
		SimpleCoordStore to = getTransmatPoint();
		if (canTransmatEntity(ent))
		{
			SoundHelper.playSound(ent, "tardismod:transmat", 0.6F, 1);
			TeleportHelper.teleportEntity(ent, WorldHelper.getWorldID(worldObj), to.x + 0.5, to.y + 1, to.z + 0.5, 90);
			SoundHelper.playSound(worldObj, to.x, to.y + 1, to.z, "tardismod:transmat", 0.6F);
			return true;
		}
		else
		{
			SoundHelper.playSound(ent, "tardismod:transmatFail", 0.6F, 1);
			return false;
		}
	}

	public SimpleCoordStore getTransmatPoint()
	{
		if (isTransmatPointValid()) return transmatPoint;
		return new SimpleCoordStore(worldObj, xCoord + 13, yCoord - 2, zCoord);
	}

	public void setTransmatPoint(SimpleCoordStore s)
	{
		transmatPoint = s;
	}

	/**
	 * @return true if the other simple coord store is the same transmat point as this ones
	 */
	public boolean isTransmatPoint(SimpleCoordStore other)
	{
		if (transmatPoint != null) return transmatPoint.equals(other);
		return false;
	}

	public boolean isTransmatPointValid()
	{
		if (transmatPoint == null) return false;
		World w = transmatPoint.getWorldObj();
		for (int i = 0; i < 5; i++)
			if (!w.isAirBlock(transmatPoint.x, transmatPoint.y - i, transmatPoint.z)) return true;
		return false;
	}

	public void rescuePlayer(EntityPlayerMP pl)
	{
		if ((pl == null) || (pl.worldObj == null) || (pl.worldObj.provider == null)) return;
		int dim = pl.worldObj.provider.getRespawnDimension(pl);
		pl.getBedLocation(dim);
	}

	private static final Integer[] nullArray = new Integer[]{0,0,0,0};
	private Integer[] getDestinationLocations()
	{
		if(ServerHelper.isServer())
		{
			ConsoleTileEntity console = getConsole();
			if(console == null) return nullArray;
			int dD = console.getDimFromControls();
			int dX = console.getXFromControls(gDS().exteriorX);
			int dY = console.getYFromControls(gDS().exteriorY);
			int dZ = console.getZFromControls(gDS().exteriorZ);
			if ((dD == desDim) && (dX == desX) && (dY == desY) && (dZ == desZ) && (desLocs != null))
				return desLocs;
			int instability = MathHelper.clamp(20 - (2 * gDS().getLevel()), 3, 20);
			if(desLocs == null) desLocs = new Integer[]{null,null,null,null};
			desLocs[0] = dD;
			if(!hasFunction(TardisFunction.CLARITY))
			{
				if ((dX != desX) || (desLocs[1] == null)) desLocs[1] = (dX + (rand.nextInt(2 * instability) - instability));
				if ((dY != desY) || (desLocs[2] == null)) desLocs[2] = (dY + (rand.nextInt(2 * instability) - instability));
				if ((dZ != desZ) || (desLocs[3] == null)) desLocs[3] = (dZ + (rand.nextInt(2 * instability) - instability));
			}
			else
			{
				if ((dX != desX) || (desLocs[1] == null)) desLocs[1] = dX;
				if ((dY != desY) || (desLocs[2] == null)) desLocs[2] = dY;
				if ((dZ != desZ) || (desLocs[3] == null)) desLocs[3] = dZ;
			}
			desX = dX;
			desY = dY;
			desZ = dZ;
			return desLocs;
		}
		else if(desLocs != null)
			return desLocs;
		return nullArray;
	}

	private String[] getDestinationStrings(Integer[] locs)
	{
		String[] toRet = new String[4];
		if(ServerHelper.isServer())
			toRet[0] = "Dim: " + getDimensionName(locs[0]) + "[" + locs[0] + "]";
		else
		{
			toRet[0] = "Dim: " + desDimName + "[" + locs[0] + "]";
			if(toRet[0].length() > 24)
				toRet[0] = "Dim: ["+locs[0]+"]";
			else if(toRet[0].length() > 18)
				toRet[0] = "D:" + desDimName;
			else if(toRet[0].length() > 15)
				toRet[0] = "D:" + desDimName + "[" + locs[0] + "]";
		}
		toRet[1] = "X: " + locs[1];
		toRet[2] = "Y: " + locs[2];
		toRet[3] = "Z: " + locs[3];
		return toRet;
	}

	public void sendDestinationStrings(EntityPlayer pl)
	{
		for (String s : getDestinationStrings(getDestinationLocations()))
			pl.addChatMessage(new ChatComponentText(s));
	}

	/**
	 * @param w
	 *            A world object to check
	 * @param x
	 *            The xCoord to check
	 * @param y
	 *            The yCoord of the bottom block of the TARDIS
	 * @param z
	 *            The zCoord to check
	 * @return A boolean array of length 6. Element 0 = If door is not obstructed by a solid block (i.e. the door is openable) Element 1 = If door is obstructed by a dangerous block (e.g. lava/fire) Element 2 = There is a drop of 2 or more blocks Element 3 = The drop will result in water Element 4 =
	 *         The drop will result in lava/fire Element 5 = The outside is wet
	 */
	private boolean[] getObstructData(World w, int x, int y, int z)
	{
		boolean[] data = new boolean[6];
		data[0] = data[1] = data[2] = data[3] = data[4] = data[5] = false;
		TardisOutput.print("TCTE", "Checking for air @ " + x + "," + y + "," + z, TardisOutput.Priority.DEBUG);
		if (softBlock(w, x, y, z) && softBlock(w, x, y + 1, z))
		{
			data[0] = true;
			Block bottom = w.getBlock(x, y, z);
			Block top = w.getBlock(x, y + 1, z);
			if ((bottom == Blocks.fire) || (bottom == Blocks.lava) || (bottom == Blocks.flowing_lava) || (top == Blocks.fire) || (top == Blocks.lava) || (top == Blocks.flowing_lava))
				data[1] = true;
			else
			{
				if ((bottom == Blocks.water) || (bottom == Blocks.flowing_water) || (top == Blocks.water) || (top == Blocks.flowing_water)) data[5] = true;
				for (int i = 0; i <= 2; i++)
				{
					if ((y - i) < 1)
					{
						data[2] = true;
						break;
					}
					if (!softBlock(w, x, y - i, z)) break;
					if (i == 2) data[2] = true;
				}
				if (data[2])
				{
					boolean g = true;
					for (int i = y - 2; (i > 0) && g; i--)
					{
						if (!softBlock(w, x, i, z)) g = true;
						Block b = w.getBlock(x, i, z);
						if ((b == Blocks.water) || (b == Blocks.flowing_water))
							data[3] = true;
						else if ((b == Blocks.lava) || (b == Blocks.flowing_lava))
							data[4] = true;
						else
							continue;
						break;
					}
				}
			}
		}
		return data;
	}

	public void sendScannerStrings(EntityPlayer pl)
	{
		if (inFlight())
		{
			pl.addChatMessage(new ChatComponentText("Cannot use exterior scanners while in flight"));
			return;
		}
		List<String> string = new ArrayList<String>();
		TardisTileEntity ext = gDS().getExterior();
		if (ext == null) return;
		World w = ext.getWorldObj();
		int dx = 0;
		int dz = 0;
		string.add("Current position: Dimension " + getDimensionName(gDS().exteriorWorld) + "[" + gDS().exteriorWorld + "] : " + gDS().exteriorX + "," + gDS().exteriorY + "," + gDS().exteriorZ);
		int facing = w.getBlockMetadata(gDS().exteriorX, gDS().exteriorY, gDS().exteriorZ);
		for (int i = 0; i < 4; i++)
		{
			switch (i)
			{
				case 0:
					dz = -1;
					dx = 0;
					break;
				case 1:
					dx = 1;
					dz = 0;
					break;
				case 2:
					dz = 1;
					dx = 0;
					break;
				case 3:
					dx = -1;
					dz = 0;
					break;
			}
			String s = (i == facing ? "Current facing " : "Facing ");
			boolean[] data = getObstructData(w, gDS().exteriorX + dx, gDS().exteriorY, gDS().exteriorZ + dz);
			if (data[5])
				s += "wet";
			else if (!data[0])
				s += "obstructed";
			else if (data[1])
				s += "unsafe exit";
			else if (data[2])
			{
				s += "unsafe drop";
				if (data[3]) s += " into water";
				if (data[4]) s += " into lava";
			}
			else
				s += " safe";
			string.add(s);
		}

		for (String s : string)
			pl.addChatMessage(new ChatComponentText(s));
	}

	private String getDimensionName(int worldID)
	{
		if (worldID != 10000) return WorldHelper.getDimensionName(worldID);
		return "The Time Vortex";
	}

	// ////////////////////////////
	// ////NBT DATA////////////////
	// ////////////////////////////

	public void commandRepair(String newO, int numRoom, int en)
	{
		energy = en;
		numRooms = numRoom;
		maxNumRooms = config.getInt("maxRooms", 30);
		setOwner(newO);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		if (fast) nbt.setBoolean("fast", fast);
		nbt.setInteger("oExW", oldExteriorWorld);
		nbt.setBoolean("fF", forcedFlight);
		nbt.setInteger("lS", lockState.ordinal());
		if (hasFunction(TardisFunction.TRANSMAT) && isTransmatPointValid()) nbt.setTag("tP", transmatPoint.writeToNBT());
		if ((modders != null) && (modders.size() > 0))
		{
			int[] mods = new int[modders.size()];
			for (int i = 0; i < modders.size(); i++)
				mods[i] = modders.get(i);
			nbt.setIntArray("mods", mods);
		}
	}

	@Override
	public void writeTransmittable(NBTTagCompound nbt)
	{
		if (ownerName != null)
		{
			nbt.setBoolean("explode", explode);
			nbt.setString("ownerName", ownerName);

			nbt.setInteger("energy", energy);

			nbt.setInteger("fT", flightTimer);
			nbt.setInteger("tFT", totalFlightTimer);
			nbt.setDouble("sped", speed);
			nbt.setInteger("fS", flightState.ordinal());

			nbt.setInteger("shld", shields);
			nbt.setInteger("hull", hull);
			nbt.setInteger("scrAng", screenAngle);
			if ((sourceLocation != null) && (destLocation != null))
			{
				sourceLocation.writeToNBT(nbt, "srcLoc");
				destLocation.writeToNBT(nbt, "dstLoc");
				nbt.setDouble("dT", distanceTravelled);
				nbt.setDouble("dtT", distanceToTravel);
				nbt.setInteger("cbs", currentBlockSpeed);
			}
		}
	}

	@Override
	public void writeTransmittableOnly(NBTTagCompound nbt)
	{
		if (ServerHelper.isServer() && (gDS() != null))
		{
			NBTTagCompound dsTC = new NBTTagCompound();
			gDS().writeToNBT(dsTC);
			nbt.setTag("ds", dsTC);
			nbt.setInteger("numR", getNumRooms());
			getDestinationLocations();
			nbt.setInteger("dld", desLocs[0]);
			nbt.setString("dldn", WorldHelper.getDimensionName(desLocs[0]));
			nbt.setInteger("dlx", desLocs[1]);
			nbt.setInteger("dly", desLocs[2]);
			nbt.setInteger("dlz", desLocs[3]);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		fast = nbt.hasKey("fast") && nbt.getBoolean("fast");
		oldExteriorWorld = nbt.getInteger("oExW");
		forcedFlight = nbt.getBoolean("fF");
		lockState = LockState.values()[nbt.getInteger("lS")];
		if (nbt.hasKey("tP")) transmatPoint = SimpleCoordStore.readFromNBT(nbt.getCompoundTag("tP"));
		if (nbt.hasKey("dT"))
		{
			sourceLocation = SimpleCoordStore.readFromNBT(nbt, "srcLoc");
			destLocation = SimpleCoordStore.readFromNBT(nbt, "dstLoc");
		}
		if (nbt.hasKey("mods"))
		{
			int[] mods = nbt.getIntArray("mods");
			if ((mods != null) && (mods.length > 0))
			{
				for (int i : mods)
					modders.add(i);
			}
		}
	}

	@Override
	public void readTransmittable(NBTTagCompound nbt)
	{
		super.readTransmittable(nbt);
		if (nbt.hasKey("ownerName"))
		{
			if(nbt.hasKey("dT"))
			{
				distanceTravelled = nbt.getDouble("dT");
				distanceToTravel = nbt.getDouble("dtT");
				currentBlockSpeed = nbt.getInteger("cbs");
			}
			explode = nbt.getBoolean("explode");
			ownerName = nbt.getString("ownerName");

			energy = nbt.getInteger("energy");

			flightTimer = nbt.getInteger("fT");
			totalFlightTimer = nbt.getInteger("tFT");

			speed = nbt.getDouble("sped");
			flightState = FlightState.get(nbt.getInteger("fS"));

			shields = nbt.getInteger("shld");
			hull = nbt.getInteger("hull");
			screenAngle = nbt.getInteger("scrAng");
		}
	}

	@Override
	public void readTransmittableOnly(NBTTagCompound nbt)
	{
		if (nbt.hasKey("ds") && ServerHelper.isClient())
		{
			TardisDataStore tds = gDS();
			if (tds != null) tds.readFromNBT(nbt.getCompoundTag("ds"));
		}
		if (nbt.hasKey("dld"))
		{
			if(desLocs == null) desLocs = new Integer[4];
			desLocs[0] = nbt.getInteger("dld");
			desLocs[1] = nbt.getInteger("dlx");
			desLocs[2] = nbt.getInteger("dly");
			desLocs[3] = nbt.getInteger("dlz");
			desDimName = nbt.getString("dldn");
		}
		else
			desLocs = nullArray.clone();
		numRooms = nbt.getInteger("numR");
	}

	@Override
	public boolean shouldChunkload()
	{
		return true;
	}

	@Override
	public SimpleCoordStore coords()
	{
		return coords;
	}

	@Override
	public ChunkCoordIntPair[] loadable()
	{
		return loadable;
	}

	public boolean canBeAccessedExternally()
	{
		EngineTileEntity engine = getEngine();
		if (engine == null) return false;
		return !engine.getInternalOnly();
	}

	public IGridNode getNode()
	{
		if (TardisMod.aeAPI == null) return null;
		if (node == null) node = TardisMod.aeAPI.createGridNode(new CoreGrid(this));
		return node;
	}

	@Override
	public IGridNode getGridNode(ForgeDirection dir)
	{
		if (dir != ForgeDirection.UNKNOWN) return null;
		return getNode();
	}

	@Override
	public AECableType getCableConnectionType(ForgeDirection dir)
	{
		// TODO Auto-generated method stub
		return AECableType.NONE;
	}

	@Override
	public void securityBreak()
	{
	}

	@Override
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
		return AxisAlignedBB.getBoundingBox(xCoord-2, yCoord-1, zCoord-2, xCoord+3, yCoord+4, zCoord+3);
    }

	private enum FlightState
	{
		LANDED, TAKINGOFF, DRIFT, FLIGHT, LANDING;
		public static FlightState get(int i)
		{
			FlightState[] vals = values();
			i = MathHelper.clamp(i, 0, vals.length - 1);
			return vals[i];
		}
	}

	public int getScreenAngle()
	{
		return screenAngle;
	}

	private static final String[] empty = new String[]{"TARDIS"," Data","  Interface"};
	private static final String[] takeoff = new String[]{"Taking off"};
	private static final String[] landing = new String[]{"Landing"};
	public String[] getScreenText()
	{
		TardisDataStore ds = gDS();
		String[] locs = getDestinationStrings(getDestinationLocations());
		if((screenAngle > 45) && (screenAngle < 135))
			return new String[]{"","",locs[1]};
		if((screenAngle >-135) && (screenAngle < -45))
			return new String[]{"","",locs[3]};
		if((screenAngle <= -135) || (screenAngle >= 135))
			return new String[]{"","",locs[0],locs[2]};
		if(flightState == FlightState.TAKINGOFF)
			return takeoff;
		if(flightState == FlightState.FLIGHT)
		{
			if((ds == null) || !ds.hasFunction(TardisFunction.CLARITY))
				return new String[]{"Speed: "+currentBlockSpeed+"b/t",
						String.format("Travel: %04.1f%%", (100*distanceTravelled)/distanceToTravel)};
			else
				return new String[]{"Speed: "+currentBlockSpeed+"b/t",
					String.format("Travel: %04.1f%%", (100*distanceTravelled)/distanceToTravel),
					String.format("ETA: %ds", MathHelper.ceil((distanceToTravel-distanceTravelled)/(currentBlockSpeed*20)))};
		}
		if(flightState == FlightState.LANDING)
			return landing;
		return empty;
	}

}
