package tardis.common.tileents;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import tardis.TardisMod;
import tardis.api.IActivatable;
import tardis.common.core.Helper;
import tardis.common.core.TardisConfigFile;
import tardis.common.core.TardisOutput;
import tardis.common.core.store.SimpleCoordStore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TardisCoreTileEntity extends TardisAbstractTileEntity implements IActivatable
{
	private static TardisConfigFile config;
	private int exteriorWorld;
	private int exteriorX;
	private int exteriorY;
	private int exteriorZ;
	
	private int tickCount = 0;
	
	private static int energyPerSecond = 0;
	
	private double speed = 4;
	private static double maxSpeed;
	
	private int energy;
	private static int maxEnergy;
	
	private boolean inFlight = false;
	private int flightTimer = 0;
	private int inFlightTimer = 0;
	private int totalFlightTimer = 0;
	private int buttonTime = 80;
	
	private int numRooms = 0;
	private int maxNumRooms = 30;
	private double	tardisXP = 0;
	private int		tardisLevel = 0;
	
	private int shields;
	private int maxShields;
	private int hull;
	private int maxHull;
	
	private boolean deletingRooms = true;
	private boolean explode = false;
	
	private int instability = 0;
	private int desDim = 0;
	private int desX = 0;
	private int desY = 0;
	private int desZ = 0;
	private String[] desStrs = null;
	
	private HashSet<SimpleCoordStore> roomSet = new HashSet<SimpleCoordStore>();
	
	private String ownerName;
	
	{
		config = TardisMod.configHandler.getConfigFile("tardisCore");
		maxSpeed = config.getDouble("maxSpeed", 8);
		maxEnergy = config.getInt("maxEnergy", 1000);
		maxNumRooms = config.getInt("maxRooms", 30);
		maxShields  = config.getInt("maxShields", 1000);
		maxHull		= config.getInt("maxHull", 1000);
		shields		= maxShields;
		hull		= maxHull;
		
		energy = 100;
		energyPerSecond = config.getInt("energy per second",1);
	}
	
	public boolean activate(EntityPlayer player, int side)
	{
		if(ownerName != null)
			leaveTardis(player);
		return true;
	}
	
	public void enterTardis(EntityPlayer player)
	{
		Helper.teleportEntity(player, worldObj.provider.dimensionId, 9, 29, 0);
	}
	
	public void leaveTardis(EntityPlayer player)
	{
		if(!inFlight)
		{
			World ext = Helper.getWorld(exteriorWorld);
			if(ext != null)
			{
				if(ext.isRemote)
					return;
				int facing = ext.getBlockMetadata(exteriorX, exteriorY, exteriorZ);
				int dx = 0;
				int dz = 0;
				switch(facing)
				{
					case 0:dz = -1; break;
					case 1:dx =  1; break;
					case 2:dz =  1; break;
					case 3:dx = -1; break;
				}
				
				if(ext.isAirBlock(exteriorX+dx, exteriorY, exteriorZ+dz) && ext.isAirBlock(exteriorX+dx, exteriorY, exteriorZ+dz))
				{
					Helper.teleportEntity(player, exteriorWorld, exteriorX+(dx*1.5), exteriorY+1, exteriorZ+(dz*1.5));
				}
				else
				{
					player.addChatMessage("The door is obstructed");
				}
			}
			else
				player.addChatMessage("The door refuses to open");
		}
		else
		{
			player.addChatMessage("The TARDIS is in flight");
		}
	}
	
	public boolean takeOff(EntityPlayer pl)
	{
		if(!inFlight)
		{
			TardisConsoleTileEntity con = getConsole();
			int dDim = con.getDimFromControls();
			int dX = con.getXFromControls();
			int dY = con.getYFromControls();
			int dZ = con.getZFromControls();
			
			int distance = Math.abs(dX - exteriorX) + Math.abs(dY - exteriorY) + Math.abs(dZ - exteriorZ) + (dDim != exteriorWorld ? 300 : 0);
			int enCost = (int) Helper.clamp(distance, 1, 500);
			if(takeEnergy(enCost,false))
			{
				instability = 0;
				inFlight = true;
				inFlightTimer = 0;
				flightTimer = 0;
				TardisTileEntity te = getExterior();
				if(te != null)
					te.takeoff();
				sendUpdate();
				return true;
			}
			else
				pl.addChatMessage("Not enough energy");
		}
		return false;
	}
	
	private boolean isValidPos(World w, int x, int y, int z)
	{
		return w.isAirBlock(x, y, z) && w.isAirBlock(x,y+1,z) && y > 0 && y < 254;
	}
	
	private void placeBox()
	{
		if(worldObj.isRemote)
			return;
		
		TardisConsoleTileEntity con = getConsole();
		int dX = con.getXFromControls();
		int dY = con.getYFromControls();
		int dZ = con.getZFromControls();
		int facing = con.getFacingFromControls();
		World w = Helper.getWorld(con.getDimFromControls());
		if(!(isValidPos(w,dX,dY,dZ)))
		{
			boolean f = false;
			int[] check = {0,1,-1,2,-2};
			for(int i=0;i<5&&!f;i++)
			{
				int xO = check[i];
				for(int j=0;j<5&&!f;j++)
				{
					int zO = check[j];
					for(int k=0;k<5&&!f;k++)
					{
						int yO = check[k];
						if(isValidPos(w,dX+xO,dY+yO,dZ+zO))
						{
							dX += xO;
							dY += yO;
							dZ += zO;
							f = true;
						}
					}
				}
			}
		}
		boolean landOnGround = con.getLandFromControls();
		if(landOnGround)
		{
			int offset = 1;
			while(dY - offset > 0 && w.isAirBlock(dX, dY-offset, dZ))
				offset++;
			dY = dY + 1 - offset;
		}
		w.setBlock(dX, dY, dZ, TardisMod.tardisBlock.blockID, facing, 3);
		w.setBlock(dX, dY+1, dZ, TardisMod.tardisTopBlock.blockID, facing, 3);
		
		setExterior(w,dX,dY,dZ);
		TileEntity te = w.getBlockTileEntity(dX,dY,dZ);
		if(te != null && te instanceof TardisTileEntity)
		{
			TardisTileEntity tardis = (TardisTileEntity) te;
			tardis.linkedCore = this;
			tardis.linkedDimension = worldObj.provider.dimensionId;
			tardis.land();
		}
	}
	
	public void land()
	{
		if(inFlight)
		{
			inFlight = false;
			sendUpdate();
			worldObj.playSound(xCoord, yCoord, zCoord, "tardismod:engineDrum", 0.75F, 1, true);
			TardisTileEntity ext = getExterior();
			if(ext != null)
				ext.forceLand();
			TardisConsoleTileEntity con = getConsole();
			if(con != null)
				con.land();
		}
	}
	
	private void safetyTick()
	{
		List<Object> players = worldObj.playerEntities;
		for(Object o : players)
		{
			if(o instanceof EntityPlayer)
			{
				EntityPlayer pl = (EntityPlayer)o;
				if(pl.posY < -5 && !pl.capabilities.isFlying)
					Helper.teleportEntityToSafety(pl);
			}
		}
	}
	
	private void flightTick()
	{
		if(inFlightTimer == 0)
			worldObj.playSound(xCoord, yCoord, zCoord, "tardismod:takeoff", 0.75F, 1, true);
		totalFlightTimer++;
		inFlightTimer++;
		int timeTillTakenOff = (20 * 11);
		int timeTillLanding = timeTillTakenOff +  (int) (((2+(2*getMaxSpeed())) - (2*getSpeed(true))) * 69);
		int timeTillLandingInt = timeTillLanding + (20 * 6);
		int timeTillLanded  = timeTillLanding + (20 * 11);
		int numButtons = (timeTillLanding - timeTillTakenOff) / buttonTime;
		if(inFlightTimer >= timeTillTakenOff)//Taken off
		{
			int takenTime = inFlightTimer - timeTillTakenOff;
			if(!worldObj.isRemote && takenTime % buttonTime == 0 && (buttonTime * numButtons) >= takenTime)
			{
				TardisConsoleTileEntity con = getConsole();
				if(takenTime > 0)
				{
					if(con.unstableControlPressed())
					{
						instability -= 0.5;
						addXP(1);
					}
					else
					{
						instability++;
						explode = true;
						worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
					}
					instability = Helper.clamp(instability, 0, 10);
				}
				if(con != null && con.unstableFlight() && (buttonTime * numButtons) >= takenTime)
					con.randomUnstableControl();
				else if((buttonTime * numButtons) == takenTime)
					con.clearUnstableControl();
			}
			if(takenTime == 0)// remove old tardis
			{
				World w = Helper.getWorld(exteriorWorld);
				if(w != null)
				{
					if(w.getBlockId(exteriorX,exteriorY,exteriorZ) == TardisMod.tardisBlock.blockID)
					{
						w.setBlockToAir(exteriorX, exteriorY, exteriorZ);
						w.setBlockToAir(exteriorX, exteriorY+1, exteriorZ);
						TardisOutput.print("TCTE", "Blanking exterior");
						exteriorWorld = 10000;
						exteriorX = 0;
						exteriorY = 0;
						exteriorZ = 0;
					} 	
				}
			}
			if(inFlightTimer < timeTillLanding)
			{
				if(flightTimer % 69 == 0 && inFlight)
					worldObj.playSound(xCoord, yCoord, zCoord, "tardismod:engines", 0.75F, 1, true);
				flightTimer++;
			}
			else
			{
				if(flightTimer % 69 == 0 && inFlightTimer < timeTillLandingInt)
					worldObj.playSound(xCoord, yCoord, zCoord, "tardismod:engines", 0.75F, 1, true);
				flightTimer++;
				
				if(inFlightTimer == timeTillLanding)
					placeBox();
				
				if(inFlightTimer == timeTillLandingInt)
					worldObj.playSound(xCoord, yCoord, zCoord, "tardismod:landingInt", 0.75F, 1, true);
				
				if(inFlightTimer >= timeTillLanded)
					land();
			}
		}
	}
	
	@Override
	public void updateEntity()
	{
		tickCount++;
		
		if(explode)
		{
			double xO = (rand.nextDouble()*3) - 1.5;
			double zO = (rand.nextDouble()*3) - 1.5;
			worldObj.playSound(xCoord, yCoord, zCoord, "random.explosion", 0.5F, 1, true);
			worldObj.createExplosion(null, xCoord+0.5+xO, yCoord-0.5, zCoord+0.5+zO, 1F, true);
			explode = false;
		}
		
		if(tickCount % 20 == 0)
		{
			addEnergy(energyPerSecond,false);
			safetyTick();
		}
		
		if(inFlight)
			flightTick();
		
		if(!worldObj.isRemote)
		{
			if(deletingRooms)
			{
				Iterator<SimpleCoordStore> i = roomSet.iterator();
				if(i.hasNext())
				{
					SimpleCoordStore coord = i.next();
					TileEntity te = worldObj.getBlockTileEntity(coord.x, coord.y, coord.z);
					if(te != null && te instanceof TardisSchemaCoreTileEntity)
					{
						TardisOutput.print("TCTE", "Removing room @ " + coord);
						TardisSchemaCoreTileEntity schemaCore = (TardisSchemaCoreTileEntity)te;
						schemaCore.remove();
					}
					i.remove();
				}
				else
				{
					deletingRooms = false;
					numRooms = 0;
				}
			}
			if(tickCount % 80 == 0)
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}
	
	private void sendUpdate()
	{
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	////////////////////////////////////////////////
	//////////////DATA STUFF////////////////////////
	////////////////////////////////////////////////
	
	public boolean inCoordinatedFlight()
	{
		return inFlight;
	}
	
	public boolean inFlight()
	{
		return inFlight;
	}
	
	public float getProximity()
	{
		if(inFlight)
		{
			int rate = 40;
			double val = Math.abs((tickCount % rate) - (rate / 2));
			double max = 0.4;
			return (float) (max * 2 * (val / rate));
		}
		else
		{
			return 0;
		}
	}
	
	public void setOwner(String name)
	{
		TardisOutput.print("TCTE", "Setting owner to " + name+"#"+worldObj.isRemote, TardisOutput.Priority.DEBUG);
		ownerName = name;
	}
	
	public void setExterior(World w, int x, int y, int z)
	{
		exteriorWorld = w.provider.dimensionId;
		exteriorX = x;
		exteriorY = y;
		exteriorZ = z;
		TardisOutput.print("TCTE", "Exterior placed @ " + x + ","+ y +","+z+","+exteriorWorld +","+worldObj.isRemote);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	public TardisTileEntity getExterior()
	{
		World w = Helper.getWorld(exteriorWorld);
		if(w != null)
		{
			TileEntity te = w.getBlockTileEntity(exteriorX,exteriorY,exteriorZ);
			if(te != null && te instanceof TardisTileEntity)
				return (TardisTileEntity) te;
		}
		return null;
	}
	
	public boolean canModify(EntityPlayer player)
	{
		TardisOutput.print("TCTE","OwnerCheck:"+player.username+":"+ ownerName+"#"+worldObj.isRemote,TardisOutput.Priority.DEBUG);
		return player.username.equals(ownerName);
	}
	
	public String getOwner()
	{
		return ownerName;
	}
	
	public TardisConsoleTileEntity getConsole()
	{
		TileEntity te = worldObj.getBlockTileEntity(xCoord, yCoord - 2, zCoord);
		if(te != null)
		{
			if(te instanceof TardisConsoleTileEntity)
				return (TardisConsoleTileEntity)te;
		}
		return null;
	}
	
	public double getMaxSpeed()
	{
		return maxSpeed;
	}
	
	public double getSpeed(boolean modified)
	{
		if(!modified)
			return speed;
		double mod = ((double)getNumRooms()) / getMaxNumRooms();
		return speed * (mod / 2.0);
	}
	
	public double addSpeed(double a)
	{
		if(!inFlight)
			speed = speed + a;
		speed = Helper.clamp(speed, 0, getMaxSpeed());
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		return speed;
	}
	
	public int getLevel()
	{
		return tardisLevel;
	}
	
	public double getXP()
	{
		return tardisXP;
	}
	
	public double addXP(double a)
	{
		return (tardisXP += Math.abs(a));
	}
	
	public int getNumRooms()
	{
		return numRooms;
	}
	
	public int getMaxNumRooms()
	{
		return maxNumRooms;
	}
	
	public boolean addRoom(boolean sub, TardisSchemaCoreTileEntity te)
	{
		if(sub && numRooms > 0)
		{
			if(Helper.isServer() && te != null)
				roomSet.remove(new SimpleCoordStore(te));
			numRooms --;
			return true;
		}
		
		if(!sub && numRooms < maxNumRooms)
		{
			if(Helper.isServer() && te != null)
				roomSet.add(new SimpleCoordStore(te));
			numRooms++;
			return true;
		}
		
		return false;
	}
	
	public boolean addRoom(TardisSchemaCoreTileEntity te)
	{
		if(Helper.isServer() && te != null)
			return roomSet.add(new SimpleCoordStore(te));
		return false;
	}
	
	public void removeAllRooms()
	{
		/*for(SimpleCoordStore coord : roomSet)
		{
			TileEntity te = worldObj.getBlockTileEntity(coord.x, coord.y, coord.z);
			if(te != null && te instanceof TardisSchemaCoreTileEntity)
			{
				TardisOutput.print("TCTE", "Removing room @ " + coord);
				TardisSchemaCoreTileEntity schemaCore = (TardisSchemaCoreTileEntity)te;
				schemaCore.remove();
			}
		}
		roomSet.clear();
		numRooms = 0;*/
		deletingRooms = true;
	}
	
	public int getMaxEnergy()
	{
		return maxEnergy;
	}
	
	public int getEnergy()
	{
		return energy;
	}
	
	public boolean addEnergy(int amount, boolean sim)
	{
		if(!sim)
			energy += amount;
		energy = Helper.clamp(energy,0,getMaxEnergy());
		return true;
	}
	
	public boolean takeEnergy(int amount, boolean sim)
	{
		if(energy >= amount)
		{
			if(!sim)
				energy -= amount;
			return true;
		}
		energy = Helper.clamp(energy,0,getMaxEnergy());
		return false;
	}
	
	public int getShields()
	{
		return shields;
	}
	
	public int getMaxShields()
	{
		return maxShields;
	}
	
	public int getHull()
	{
		return hull;
	}
	
	public int getMaxHull()
	{
		return maxHull;
	}
	
	public void sendDestinationStrings(EntityPlayer pl)
	{
		TardisConsoleTileEntity console = getConsole();
		if(console != null)
		{
			int dD = console.getDimFromControls();
			int dX = console.getXFromControls();
			int dY = console.getYFromControls();
			int dZ = console.getZFromControls();
			TardisOutput.print("TCTE","Dest:" + dD +","+dX+","+dY+","+dZ);
			if(dD==desDim&&dX==desX&&dY==desY&&dZ==desZ&&desStrs!=null)
				for(String s:desStrs)
					pl.addChatMessage(s);
			else
			{
				int instability = 20;
				desDim = dD;
				String[] send = new String[4];
				if(desStrs!= null && desStrs.length == 4)
					send = desStrs;
				
				send[0] = "The TARDIS will materialize in dimension " + dD + " near:";
				if(dX != desX || send[1] == null)
					send[1] = "x = " + (dX + (rand.nextInt(2 * instability) - instability));
				if(dY != desY || send[2] == null)
					send[2] = "y = " + (dY + (rand.nextInt(2 * instability) - instability)); 
				if(dZ != desZ || send[3] == null)
				send[3] = "z = " + (dZ + (rand.nextInt(2 * instability) - instability)); 
				desX = dX;
				desY = dY;
				desZ = dZ;
				desStrs = send;
				for(String s:desStrs)
					pl.addChatMessage(s);
			}
		}
	}
	
	//////////////////////////////
	//////NBT DATA////////////////
	//////////////////////////////
	
	public void repair(String newO, int numRoom, int en)
	{
		energy = en;
		numRooms = numRoom;
		maxNumRooms = config.getInt("maxRooms", 30);
		setOwner(newO);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		tardisXP  = nbt.getDouble("tardisXP");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setDouble("tardisXP", tardisXP);
	}

	@Override
	public void writeTransmittable(NBTTagCompound nbt)
	{
		if(ownerName != null)
		{
			nbt.setBoolean("explode",explode);
			nbt.setInteger("tardisLevel", tardisLevel);
			nbt.setString("ownerName", ownerName);
			nbt.setInteger("extWorld", exteriorWorld);
			nbt.setInteger("extX", exteriorX);
			nbt.setInteger("extY", exteriorY);
			nbt.setInteger("extZ", exteriorZ);
			
			nbt.setInteger("energy",energy);
			
			nbt.setBoolean("inFlight", inFlight);
			nbt.setInteger("flightTimer", flightTimer);
			nbt.setInteger("totalFlightTimer", totalFlightTimer);
			nbt.setInteger("inFlightTimer", inFlightTimer);
			nbt.setInteger("numRooms", numRooms);
			nbt.setDouble("speed", speed);
			
			nbt.setInteger("shields",shields);
			nbt.setInteger("hull",hull);
		}
	}

	@Override
	public void readTransmittable(NBTTagCompound nbt)
	{
		if(nbt.hasKey("ownerName"))
		{
			explode = nbt.getBoolean("explode");
			tardisLevel = nbt.getInteger("tardisLevel");
			ownerName = nbt.getString("ownerName");
			exteriorWorld = nbt.getInteger("extWorld");
			exteriorX = nbt.getInteger("extX");
			exteriorY = nbt.getInteger("extY");
			exteriorZ = nbt.getInteger("extZ");
			
			energy = nbt.getInteger("energy");
			
			flightTimer = nbt.getInteger("flightTimer");
			inFlightTimer = nbt.getInteger("inFlightTimer");
			totalFlightTimer = nbt.getInteger("totalFlightTimer");
			inFlight = nbt.getBoolean("inFlight");
			numRooms = nbt.getInteger("numRooms");
			speed = nbt.getDouble("speed");
			
			shields  = nbt.getInteger("shields");
			hull     = nbt.getInteger("hull");
		}
	}

}
