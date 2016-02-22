package tardis.common.core.helpers;

import io.darkcraft.darkcore.mod.DarkcoreMod;
import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.TeleportHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import io.darkcraft.darkcore.mod.network.DataPacket;

import java.io.File;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import tardis.Configs;
import tardis.TardisMod;
import tardis.api.IArtronEnergyProvider;
import tardis.common.core.TardisDimensionRegistry;
import tardis.common.core.TardisOutput;
import tardis.common.core.events.TardisLandingEvent;
import tardis.common.core.exception.schema.UnmatchingSchemaException;
import tardis.common.core.schema.PartBlueprint;
import tardis.common.dimension.SaveSlotNamesDataStore;
import tardis.common.dimension.TardisDataStore;
import tardis.common.dimension.TardisWorldProvider;
import tardis.common.entities.particles.ParticleType;
import tardis.common.network.TardisPacketHandler;
import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.CoreTileEntity;
import tardis.common.tileents.EngineTileEntity;
import tardis.common.tileents.SchemaCoreTileEntity;
import tardis.common.tileents.TardisTileEntity;

public class Helper
{
	public static final Random								rand			= new Random();
	public static final int									tardisCoreX		= 0;
	public static final int									tardisCoreY		= 70;
	public static final int									tardisCoreZ		= 0;
	public static HashMap<Integer, TardisDataStore>			datastoreMap	= new HashMap();
	public static HashMap<Integer, SaveSlotNamesDataStore>	ssnDatastoreMap	= new HashMap();

	// /////////////////////////////////////////////////
	// /////////////TELEPORT STUFF//////////////////////
	// /////////////////////////////////////////////////

	public static void teleportEntityToSafety(Entity ent)
	{
		if ((ent.worldObj.provider) instanceof TardisWorldProvider)
		{
			if (ent instanceof EntityPlayer)
			{
				CoreTileEntity core = getTardisCore(ent.worldObj);
				if (core != null)
					core.enterTardis((EntityPlayer) ent, true);
				else
					TeleportHelper.teleportEntity(ent, 0, tardisCoreX - 2, tardisCoreY, tardisCoreZ);
			}
			else
			{
				TeleportHelper.teleportEntity(ent, 0, tardisCoreX - 2, tardisCoreY, tardisCoreZ);
			}
		}
	}

	public static void generateTardisInterior(int dimID, String ownerName, TardisTileEntity exterior)
	{
		World tardisWorld = WorldHelper.getWorldServer(dimID);
		try
		{
			loadSchema("tardisConsoleMain", tardisWorld, tardisCoreX, tardisCoreY - 10, tardisCoreZ, 0);
		}
		catch (Exception e)
		{
			TardisOutput.print("TH", "Generating tardis error: " + e.getMessage());
			e.printStackTrace();
		}
		if (tardisWorld.getBlock(tardisCoreX, tardisCoreY, tardisCoreZ) != TardisMod.tardisCoreBlock)
		{
			tardisWorld.setBlock(tardisCoreX, tardisCoreY, tardisCoreZ, TardisMod.tardisCoreBlock);
			tardisWorld.setBlock(tardisCoreX, tardisCoreY - 2, tardisCoreZ, TardisMod.tardisConsoleBlock);
			tardisWorld.setBlock(tardisCoreX, tardisCoreY - 5, tardisCoreZ, TardisMod.tardisEngineBlock);
		}
		CoreTileEntity te = getTardisCore(dimID);
		TardisDataStore ds = getDataStore(dimID);
		if ((te != null) && (ds != null))
		{
			te.setOwner(ownerName);
			if (exterior != null)
			{
				ds.linkToExterior(exterior);
				ConsoleTileEntity con = te.getConsole();
				if(con != null)
					con.setControls(exterior, true);
			}
		}
	}


	private static boolean isFree(int dim)
	{
		try
		{
			World w = WorldHelper.getWorld(dim);
			if(w != null)
				return false;
		}
		catch(Exception e){}
		return !DimensionManager.isDimensionRegistered(dim);
	}

	private static int getNextFreeDimID()
	{
		int startPoint = DimensionManager.getNextFreeDimId();
		while(true)
		{
			if(isFree(startPoint))
				return startPoint;
			startPoint++;
		}
	}

	public static int generateTardisInterior(String ownerName, TardisTileEntity exterior)
	{
		if (ServerHelper.isClient()) return 0;
		int dimID = getNextFreeDimID();
		DimensionManager.registerDimension(dimID, Configs.providerID);
		TardisMod.dimReg.addDimension(dimID);
		TardisDimensionRegistry.saveAll();
		TardisMod.dimReg.sendPacket();
		generateTardisInterior(dimID, ownerName, exterior);
		return dimID;
	}

	public static int generateTardisInterior(EntityPlayer player, TardisTileEntity exterior)
	{
		int dimID = generateTardisInterior(player.getCommandSenderName(), exterior);
		CoreTileEntity te = getTardisCore(dimID);
		if (te != null) te.enterTardis(player, true);
		return dimID;
	}

	public static void summonNewTardis(EntityPlayer player)
	{
		if (TardisMod.plReg.hasTardis(player.getCommandSenderName())) return;

		TardisTileEntity te = summonTardisExterior(player);
		if (te != null)
		{
			int dimID = generateTardisInterior(player.getCommandSenderName(), te);
			te.linkToDimension(dimID);
			te.land(true);
			ConsoleTileEntity con = getTardisConsole(dimID);
			if (con != null) con.setControls(te, true);
		}
	}

	public static boolean summonOldTardis(int dim, EntityPlayer player)
	{
		CoreTileEntity oldCore = getTardisCore(dim);
		if(oldCore == null)
			return false;
		oldCore.removeOldBox();
		TardisTileEntity te = summonTardisExterior(player);
		if (te != null)
		{
			te.land(true);
			te.linkToDimension(dim);
			ConsoleTileEntity con = getTardisConsole(dim);
			MinecraftForge.EVENT_BUS.post(new TardisLandingEvent(oldCore, new SimpleCoordStore(te)));
			oldCore.attemptToLand();
			if (con != null) con.setControls(te, true);
			return true;
		}
		else
			TardisOutput.print("Helper", "No exterior :(");
		return false;
	}

	public static boolean summonOldTardis(EntityPlayer player)
	{
		Integer dim = TardisMod.plReg.getDimension(player);
		if (dim == null) return false;
		return summonOldTardis(dim, player);
	}

	private static TardisTileEntity summonTardisExterior(EntityPlayer player)
	{
		World w = player.worldObj;
		if (ServerHelper.isClient()) return null;

		int x = (int) Math.floor(player.posX);
		int y = (int) Math.floor(player.posY);
		int z = (int) Math.floor(player.posZ);
		int[] validSpotRanges = { 0, -1, 1, -2, 2, -3, 3 };

		SimpleCoordStore place = null;
		for (int yOf = 0; yOf < 7; yOf++)
		{
			if (place != null) break;

			int yO = yOf > 3 ? 3 - yOf : yOf;
			for (int xO : validSpotRanges)
			{
				if (place != null) break;

				for (int zO : validSpotRanges)
				{
					if ((xO == 0) && (yO == 0) && (zO == 0)) continue;
					if ((y > 1) && (y < 253))
					{
						if (w.isAirBlock(x + xO, y + yO, z + zO) && w.isAirBlock(x + xO, y + yO + 1, z + zO))
						{
							place = new SimpleCoordStore(w, x + xO, y + yO, z + zO);
							break;
						}
					}
				}
			}
		}

		if (place != null)
		{
			w.setBlock(place.x, place.y, place.z, TardisMod.tardisBlock, 0, 3);
			w.setBlock(place.x, place.y + 1, place.z, TardisMod.tardisTopBlock, 0, 3);
			TileEntity te = w.getTileEntity(place.x, place.y, place.z);
			if (te instanceof TardisTileEntity) return (TardisTileEntity) te;
		}
		return null;
	}

	public static void repairConsole(World w)
	{
		if (!Helper.isTardisWorld(w)) return;
		int y = tardisCoreY - 2;
		for (int x = -1; x <= 1; x++)
		{
			for (int z = -1; z <= 1; z++)
			{
				if ((x == 0) && (z == 0)) continue;
				if (w.getBlock(x, y, z) != TardisMod.schemaComponentBlock) w.setBlock(x, y, z, TardisMod.schemaComponentBlock, 3, 3);
			}
		}
		if (w.getBlock(0, y + 1, 0) != TardisMod.schemaComponentBlock) w.setBlock(0, y + 1, 0, TardisMod.schemaComponentBlock, 6, 3);
		if (w.getBlock(0, tardisCoreY + 1, 0) != TardisMod.schemaComponentBlock) w.setBlock(0, tardisCoreY + 1, 0, TardisMod.schemaComponentBlock, 8, 3);
		if (w.getBlock(0, tardisCoreY - 5, 0) != TardisMod.tardisEngineBlock) w.setBlock(0, tardisCoreY - 5, 0, TardisMod.tardisEngineBlock);
	}

	public static boolean isBlockRemovable(Block blockID)
	{
		if (blockID == TardisMod.tardisCoreBlock)
			return false;
		else if (blockID == TardisMod.tardisConsoleBlock)
			return false;
		else if (blockID == TardisMod.tardisEngineBlock)
		;
		return true;
	}

	public static PartBlueprint loadSchema(String name)
	{
		return TardisMod.schemaHandler.getSchema(name);
	}

	public static void loadSchema(String name, File schemaFile, World w, int x, int y, int z, int facing)
	{
		PartBlueprint pb = new PartBlueprint(name, schemaFile);
		pb.reconstitute(w, x, y, z, facing);
	}

	public static void loadSchema(String name, World w, int x, int y, int z, int facing)
	{
		File schemaFile = TardisMod.schemaHandler.getSchemaFile(name);
		loadSchema(name, schemaFile, w, x, y, z, facing);
	}

	public static void loadSchemaDiff(String fromName, String toName, World worldObj, int xCoord, int yCoord, int zCoord, int facing)
	{
		if (fromName.equals(toName)) return;

		long mstime = System.currentTimeMillis();
		File schemaDiff = TardisMod.schemaHandler.getSchemaFile(toName + "." + fromName + ".diff");
		PartBlueprint diff = null;
		if (schemaDiff.exists())
		{
			TardisOutput.print("TH", "Loading diff from file");
			diff = TardisMod.schemaHandler.getSchema(toName + "." + fromName + ".diff");
		}
		else
		{
			PartBlueprint fromPB = TardisMod.schemaHandler.getSchema(fromName);
			PartBlueprint toPB = TardisMod.schemaHandler.getSchema(toName);
			try
			{
				diff = new PartBlueprint(toPB, fromPB);
				diff.saveTo(schemaDiff);
			}
			catch (UnmatchingSchemaException e)
			{
				TardisOutput.print("TH", e.getMessage());
				e.printStackTrace();
			}
		}

		if (diff != null)
		{
			diff.reconstitute(worldObj, xCoord, yCoord, zCoord, facing);
		}
		TardisOutput.print("TH", "TimeTaken (ms):" + (System.currentTimeMillis() - mstime));
	}

	public static void spawnParticle(ParticleType type, int dim, double x, double y, double z)
	{
		spawnParticle(type, dim, x, y, z, 1, false);
	}

	public static void spawnParticle(ParticleType type, int dim, double x, double y, double z, int c)
	{
		spawnParticle(type, dim, x, y, z, c, false);
	}

	public static void spawnParticle(ParticleType type, int dim, double x, double y, double z, boolean b)
	{
		spawnParticle(type, dim, x, y, z, 1, b);
	}

	public static void spawnParticle(ParticleType type, int dim, double x, double y, double z, int count, boolean rand)
	{
		if (ServerHelper.isClient()) return;
		NBTTagCompound data = new NBTTagCompound();
		data.setInteger("dim", dim);
		data.setDouble("x", x);
		data.setDouble("y", y);
		data.setDouble("z", z);
		data.setInteger("c", count);
		data.setBoolean("r", rand);
		data.setInteger("type", type.ordinal());
		DataPacket packet = new DataPacket(data, TardisPacketHandler.particleFlag);
		DarkcoreMod.networkChannel.sendToDimension(packet, dim);
	}

	public static void activateControl(TileEntity te, EntityPlayer player, int control)
	{
		if (ServerHelper.isServer()) return;
		NBTTagCompound data = new NBTTagCompound();
		data.setInteger("cID", control);
		data.setString("pl", ServerHelper.getUsername(player));
		data.setInteger("dim", WorldHelper.getWorldID(te));
		data.setInteger("x", te.xCoord);
		data.setInteger("y", te.yCoord);
		data.setInteger("z", te.zCoord);
		DataPacket p = new DataPacket(data, TardisPacketHandler.controlFlag);
		DarkcoreMod.networkChannel.sendToServer(p);
	}

	public static CoreTileEntity getTardisCore(int dimensionID)
	{
		if (dimensionID == 0) return null;
		World tardisWorld = WorldHelper.getWorld(dimensionID);
		if (tardisWorld != null)
			return getTardisCore(tardisWorld);
		else
			TardisOutput.print("TH", "No world passed", TardisOutput.Priority.DEBUG);
		return null;
	}

	public static CoreTileEntity getTardisCore(IBlockAccess tardisWorld)
	{
		if (tardisWorld != null)
		{
			TileEntity te = tardisWorld.getTileEntity(tardisCoreX, tardisCoreY, tardisCoreZ);
			if (te instanceof CoreTileEntity) { return (CoreTileEntity) te; }
		}
		else
		{
			TardisOutput.print("TH", "No world passed", TardisOutput.Priority.DEBUG);
		}
		return null;
	}

	public static CoreTileEntity getTardisCore(TileEntity te)
	{
		if (te != null)
		{
			World w = te.getWorldObj();
			if (w != null) return getTardisCore(w);
		}
		return null;
	}

	public static EngineTileEntity getTardisEngine(int dim)
	{
		return getTardisEngine(WorldHelper.getWorld(dim));
	}

	public static EngineTileEntity getTardisEngine(IBlockAccess w)
	{
		CoreTileEntity core = getTardisCore(w);
		if (core != null) return core.getEngine();
		return null;
	}

	public static ConsoleTileEntity getTardisConsole(int dimID)
	{
		World w = WorldHelper.getWorldServer(dimID);
		if (w != null) return getTardisConsole(w);
		return null;
	}

	public static ConsoleTileEntity getTardisConsole(IBlockAccess tardisWorld)
	{
		if(!Helper.isTardisWorld(tardisWorld)) return null;
		TileEntity te = tardisWorld.getTileEntity(tardisCoreX, tardisCoreY - 2, tardisCoreZ);
		if(te instanceof ConsoleTileEntity)
			return (ConsoleTileEntity) te;
		if(tardisWorld instanceof World)
		{
			World w = (World) tardisWorld;
			w.setBlock(tardisCoreX, tardisCoreY - 2, tardisCoreZ, TardisMod.tardisConsoleBlock);
		}
		te = tardisWorld.getTileEntity(tardisCoreX, tardisCoreY - 2, tardisCoreZ);
		if(te instanceof ConsoleTileEntity)
			return (ConsoleTileEntity) te;
		return null;
	}

	public static IArtronEnergyProvider getArtronProvider(TileEntity source, boolean search)
	{
		World w = source.getWorldObj();
		if (w == null) return null;
		int x = source.xCoord;
		int y = source.yCoord;
		int z = source.zCoord;
		IArtronEnergyProvider iaep = null;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			TileEntity te = w.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
			if (te instanceof IArtronEnergyProvider)
				if((iaep == null) || (((IArtronEnergyProvider)te).getArtronEnergy() > iaep.getArtronEnergy()))
					iaep = (IArtronEnergyProvider) te;
		}
		if(iaep == null)
			return getTardisCore(w);
		return iaep;
	}

	public static TardisDataStore getDataStore(World w)
	{
		if (Helper.isTardisWorld(w))
			return getDataStore(WorldHelper.getWorldID(w));
		else
			return null;
	}

	public static TardisDataStore getDataStore(int dimID)
	{
		if (dimID == 0) return null;
		if (datastoreMap.containsKey(dimID)) return datastoreMap.get(dimID);
		TardisDataStore store = new TardisDataStore(dimID);
		store.load();
		store.save();
		datastoreMap.put(dimID, store);
		return store;
	}

	public static TardisDataStore getDataStore(TileEntity te)
	{
		if (te != null) return getDataStore(te.getWorldObj());
		return null;
	}

	public static SaveSlotNamesDataStore getSSNDataStore(int dimID)
	{
		if (dimID == 0) return null;
		if (ssnDatastoreMap.containsKey(dimID)) return ssnDatastoreMap.get(dimID);
		SaveSlotNamesDataStore store = new SaveSlotNamesDataStore(dimID);
		store.load();
		store.save();
		ssnDatastoreMap.put(dimID, store);
		return store;
	}

	public static boolean isTardisWorld(IBlockAccess world)
	{
		if (world instanceof World) return ((World) world).provider instanceof TardisWorldProvider;
		return false;
	}

	public static boolean isTardisWorld(int dim)
	{
		World w = WorldHelper.getWorld(dim);
		return isTardisWorld(w);
	}

	/**
	 * @param w
	 *            the world of the expected door position
	 * @param x
	 *            the location of the expected door
	 * @param y
	 *            the location of the expected door
	 * @param z
	 *            the location of the expected door
	 * @return the simple coord store of the schema core which has the door specified or null if none found
	 */
	public static SimpleCoordStore getExistingDoor(World w, int x, int y, int z)
	{
		if (isTardisWorld(w))
		{
			CoreTileEntity core = getTardisCore(w);
			if (core != null)
			{
				SimpleCoordStore pos = new SimpleCoordStore(w, x, y, z);
				Set<SimpleCoordStore> rooms = core.getRooms();
				for (SimpleCoordStore scs : rooms)
				{
					TileEntity te = scs.getTileEntity();
					if (te instanceof SchemaCoreTileEntity) if (((SchemaCoreTileEntity) te).isDoor(pos)) return scs;
				}
				SchemaCoreTileEntity te = core.getSchemaCore();
				if (te != null) if (te.isDoor(pos)) return new SimpleCoordStore(te);
			}
		}
		return null;
	}

	public static Integer getTardisDim(String dimDescriptor)
	{
		Integer w = 0;
		if(dimDescriptor.startsWith("#"))
			return TardisMod.plReg.getDimension(dimDescriptor.replaceFirst("#", ""));
		else
			return Integer.parseInt(dimDescriptor);
	}
}
