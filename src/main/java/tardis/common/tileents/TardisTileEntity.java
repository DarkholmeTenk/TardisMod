package tardis.common.tileents;

import io.darkcraft.darkcore.mod.abstracts.AbstractTileEntity;
import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.SoundHelper;
import io.darkcraft.darkcore.mod.interfaces.IBlockUpdateDetector;
import io.darkcraft.darkcore.mod.interfaces.IChunkLoader;
import io.darkcraft.darkcore.mod.interfaces.IExplodable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import tardis.Configs;
import tardis.TardisMod;
import tardis.common.core.helpers.Helper;
import tardis.common.dimension.TardisDataStore;
import tardis.common.dimension.damage.ExplosionDamageHelper;
import tardis.common.tileents.extensions.chameleon.tardis.AbstractTardisChameleon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TardisTileEntity extends AbstractTileEntity implements IChunkLoader, IBlockUpdateDetector, IExplodable
{
	private int fadeTimer = 0;

	private boolean landed = false;
	private boolean landFast = true;
	private boolean takingOff = false;
	private boolean landing   = true;

	private boolean takingOffSoundPlayed = false;
	private boolean landingSoundPlayed = false;

	public String owner;
	public static String baseURL = null;
	public AbstractTardisChameleon chameleon;

	Integer linkedDimension = null;

	private boolean gened = false;
	private int cgR = 1;
	private int cgP = 0;
	private int cgT = 0;
	private int totalGen = 0;

	private Runnable chunkGenerated = new Runnable(){@Override
	public void run(){}};

	private void genChunks()
	{
		cgT = 0;
		IChunkProvider icp = worldObj.getChunkProvider();
		if(cgR == 0)
			cgR = 1;
		if(icp instanceof ChunkProviderServer)
		{
			ChunkProviderServer cps = (ChunkProviderServer) icp;
			boolean temp = cps.loadChunkOnProvideRequest;
			cps.loadChunkOnProvideRequest = false;
			while((cgT < Configs.exteriorGenChunksPT) && (cgR <= Configs.exteriorGenChunksRad))
			{
				int sidePos = cgP % (2 * cgR);
				int side = cgP / (2 * cgR);
				int x, z;
				switch(side)
				{
					case 0: x = (sidePos - cgR) + 1; z = -cgR; break;
					case 1: x = cgR; z = (sidePos - cgR) + 1; break;
					case 2: x = -((sidePos - cgR) + 1); z = cgR; break;
					case 3: x = -cgR; z = -((sidePos - cgR) + 1); break;
					default: cgP = 0; cgR++; continue;
				}
				cgP++;
				int chunkX = (xCoord >> 4) + x;
				int chunkZ = (zCoord >> 4) + z;
				Chunk c = cps.provideChunk(chunkX, chunkZ);
				if(c instanceof EmptyChunk)
				{
					cps.loadChunk(chunkX, chunkZ, chunkGenerated);
					cgT++;
					totalGen++;
				}
			}
			cps.loadChunkOnProvideRequest = temp;
			if(cgR > Configs.exteriorGenChunksRad)
			{
				System.out.println("Generated " + totalGen + " chunks");
				gened = true;
			}
		}
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(baseURL == null)
			baseURL = Configs.modConfig.getString("Skin URL", "http://skins.darkcraft.io/tardis/");

		if(ServerHelper.isServer() && (Configs.exteriorGenChunksRad > 0) && ((tt % Configs.exteriorGenChunksTR) == 0) && !gened)
			genChunks();

		if(ServerHelper.isServer())
		{
			CoreTileEntity linkedCore = getCore();
			if((linkedCore != null) && (owner == null))
			{
				owner = linkedCore.getOwner();
				sendUpdate();
			}
		}
		if((linkedDimension != null) && ((tt % 20) == 0))
		{
			TardisDataStore ds = Helper.getDataStore(linkedDimension);
			if(ds != null)
			{
				TardisTileEntity ext = ds.getExterior();
				if(ext != null)
				{
					if(!equals(ext))
					{
						worldObj.setBlockToAir(xCoord, yCoord+1, zCoord);
						worldObj.setBlockToAir(xCoord, yCoord, zCoord);
						return;
					}
				}
			}
		}
		if(inFlight())
		{
			if(isLanding() && !landingSoundPlayed)
				playLandSound();
			else if(isTakingOff() && !takingOffSoundPlayed)
				playTakeoffSound();
			//sendUpdate();
			if(++fadeTimer >( !landFast && isLanding() ? 21.5 * 20 : 3.5 * 20))
			{
				if(isLanding())
					landed = true;
				else if(isTakingOff())
				{
					worldObj.setBlockToAir(xCoord, yCoord+1, zCoord);
					worldObj.setBlockToAir(xCoord, yCoord, zCoord);
				}
				landing = takingOff = false;
			}
		}
		else
		{
			if(!landed)
				land();
		}
	}

	public boolean isTakingOff()
	{
		return takingOff;
	}

	public boolean isLanding()
	{
		return landing;
	}

	public boolean inFlight()
	{
		return isTakingOff() || isLanding();
	}

	private void playTakeoffSound()
	{
		if(ServerHelper.isClient())return;
		SoundHelper.playSound(this, "tardismod:takeoff", 1);
		takingOffSoundPlayed = true;
	}

	public void takeoff()
	{
		fadeTimer = 0;
		takingOff = true;
		playTakeoffSound();
		sendUpdate();
	}

	public void forceLand()
	{
		landing = false;
		landed = true;
	}

	private void playLandSound()
	{
		if(ServerHelper.isClient())return;

		if(!landFast)
			SoundHelper.playSound(this, "tardismod:landing", 1);
		else
			SoundHelper.playSound(this, "tardismod:landingInt", 1);
		landingSoundPlayed = true;
	}

	public synchronized void land(boolean fast)
	{
		fadeTimer = 0;
		if(!landing)
			playLandSound();
		landing = true;
		landFast = fast;
		//TardisOutput.print("TTE", "LANDING!!!! " + (fast ? "FAST " : "SLOW ") + (ServerHelper.isClient()?"REM":"SER") + ":" + (landed?"LAN":"UNL"));
		sendUpdate();
	}

	public void land()
	{
		land(false);
	}

	public void linkToDimension(int dimID)
	{
		linkedDimension = dimID;
		TardisDataStore ds = Helper.getDataStore(dimID);
		if(ds != null)
		{
 			ds.linkToExterior(this);
			chameleon = ds.getChameleon();
			sendUpdate();
		}
	}

	public AbstractTardisChameleon getChameleon()
	{
		if(chameleon == null)
			return TardisMod.tardisChameleonReg.getDefault();
		return chameleon;
	}

	public float getTransparency()
	{
		double multiplier = 1;
		if(!landed && !isLanding())
			return 0.0F;
		else if(isTakingOff() || isLanding())
		{
			if(isLanding())
				multiplier = (fadeTimer / (landFast ? 50 : 80 * 5.5));
			else if(isTakingOff())
				multiplier = 1 - (fadeTimer / (80 * 5.5));

			double remainder;
			double transVal;
			if(isLanding())
			{
				int value = landFast ? 30 : 80;
				remainder = ((fadeTimer - (value/2)) % value);
				transVal = multiplier * (Math.abs(1-((2*remainder)/value)));
			}
			else
			{
				remainder = (fadeTimer % 80);
				transVal = multiplier * (Math.abs(1-((2*remainder)/80)));
			}
			return (float) transVal;
		}
		else
		{
			return 1.0F;
		}
	}

	public void doorActivated(World world, int x, int y, int z, EntityPlayer player)
	{
		if(ServerHelper.isClient())
			return;
		if(!inFlight())
		{
			if(linkedDimension == null)
			{
				if(!TardisMod.plReg.hasTardis(player.getCommandSenderName()))
					linkedDimension = Helper.generateTardisInterior(player,this);
				else
					player.addChatMessage(new ChatComponentText("You already own a TARDIS"));
			}
			else
			{
				CoreTileEntity core = Helper.getTardisCore(linkedDimension);
				TardisDataStore ds = Helper.getDataStore(linkedDimension);
				if((core != null) && (ds != null))
				{
					ds.linkToExterior(this);
					if(!core.changeLock(player,false))
						core.enterTardis(player,false);
				}
			}
		}
	}

	public TardisDataStore getDataStore()
	{
		if((linkedDimension != null) && (linkedDimension != 0))
			return Helper.getDataStore(linkedDimension);
		return null;
	}

	public CoreTileEntity getCore()
	{
		if((linkedDimension != null) && (linkedDimension != 0))
			return Helper.getTardisCore(linkedDimension);
		return null;
	}

	@Override
	public void writeTransmittable(NBTTagCompound nbt)
	{
		nbt.setBoolean("takingOff",takingOff);
		nbt.setBoolean("landing",landing);
		nbt.setBoolean("landFast", landFast);
		nbt.setBoolean("landed", landed);
		nbt.setInteger("fadeTimer", fadeTimer);
		nbt.setBoolean("gened", gened);
		if(linkedDimension != null)
			nbt.setInteger("linkedDimension", linkedDimension);
		if(owner != null)
			nbt.setString("owner", owner);
		if((chameleon != TardisMod.tardisChameleonReg.getDefault()) && (chameleon != null))
			if(ServerHelper.isServer())
				chameleon.writeToNBT(nbt);
	}

	@Override
	public void readTransmittable(NBTTagCompound nbt)
	{
		takingOff = nbt.getBoolean("takingOff");
		landing = nbt.getBoolean("landing");
		landFast = nbt.getBoolean("landFast");
		landed = nbt.getBoolean("landed");
		fadeTimer = nbt.getInteger("fadeTimer");
		if(nbt.hasKey("linkedDimension"))
			linkedDimension = nbt.getInteger("linkedDimension");
		if(nbt.hasKey("owner"))
			owner = nbt.getString("owner");
		chameleon = TardisMod.tardisChameleonReg.get(nbt, AbstractTardisChameleon.nbtKey);
		gened = nbt.getBoolean("gened");
	}

	public List<Entity> getEntitiesInside()
	{
		ArrayList<Entity> list = new ArrayList<Entity>();
		List<Object> genList = worldObj.getEntitiesWithinAABBExcludingEntity(null, AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+1, yCoord+2, zCoord+1));
		if(genList != null)
		{
			for(Object o: genList)
			{
				if(o instanceof EntityLivingBase)
					list.add((EntityLivingBase) o);
			}
		}
		return list;
	}

	@Override
	public boolean shouldChunkload()
	{
		return linkedDimension != null;
	}

	@Override
	public SimpleCoordStore coords()
	{
		if(coords == null)
			coords = new SimpleCoordStore(this);
		return coords;
	}

	@Override
	public ChunkCoordIntPair[] loadable()
	{
		ChunkCoordIntPair[] loadable = new ChunkCoordIntPair[1];
		loadable[0] = coords().toChunkCoords();
		return loadable;
	}

	@Override
	public void blockUpdated(Block neighbourBlockID)
	{
		if((!takingOff) && (worldObj.getBlock(xCoord, yCoord+1, zCoord) != TardisMod.tardisTopBlock))
			worldObj.setBlock(xCoord, yCoord, zCoord, TardisMod.tardisTopBlock, worldObj.getBlockMetadata(xCoord, yCoord, zCoord),3);
	}

	@Override
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
		return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+1, yCoord+2, zCoord+1);
    }

	@Override
	public void explode(SimpleCoordStore pos, Explosion explosion)
	{
		TardisDataStore ds = getDataStore();
		if(ds != null)
			ExplosionDamageHelper.damage(ds.damage, pos, explosion, 1);
	}
}
