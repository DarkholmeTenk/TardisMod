package tardis.common.tileents;

import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.items.TardisSonicScrewdriverItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TardisConsoleTileEntity extends TardisAbstractTileEntity
{
	public static final float cycleLength = 80;
	private int tickTimer;
	
	private boolean hasScrewdriver = true;
	
	private int   facing    = 0;
	private int   dimControl = 0;
	private int[] xControls = new int[6];
	private int[] zControls = new int[6];
	private int[] yControls = new int[4];
	private boolean landGroundControl = false;
	
	private boolean primed = false;
	private boolean regulated = false;
	
	private int lastButton = -1;
	private int lastButtonTT = -1;

	private static String[] schemaList = null;
	
	private int schemaNum = 0;
	private int screwMode = 0;
	
	public String schemaChooserString = "";
	
	{
		for(int i = 0;i<6;i++)
		{
			xControls[i] = 0;
			zControls[i] = 0;
			if(i < 4)
				yControls[i] = 0;
		}
		clampControls();
	}
	
	private class HitPosition
	{
		public int side;
		public float posZ;
		public float posY;
		
		HitPosition(float height, float pos, int s)
		{
			posY = height;
			posZ = pos;
			side = s;
		}
		
		public boolean within(int sideIn, double zMin, double yMin,double zMax,double yMax)
		{
			if(side != sideIn)
				return false;
			if(posZ < zMin || posZ > zMax)
				return false;
			if(posY < yMin || posY > yMax)
				return false;
			return true;
		}
		
		public String toString()
		{
			return "[Hit s:" + side + " ["+posZ+","+posY+"]]";
		}
	}
	
	@Override
	public void updateEntity()
	{
		if(++tickTimer % cycleLength == 0 && tickTimer != 0)
			tickTimer = 0;
		
		if(lastButton != -1)
		{
			int buttonDownTime = 5;
			boolean shouldGoUp = false;
			if(lastButtonTT + buttonDownTime > cycleLength)
			{
				if(tickTimer < lastButtonTT && (tickTimer > ((lastButtonTT + buttonDownTime) % cycleLength)))
						shouldGoUp = true;
			}
			else if(tickTimer > (lastButtonTT + buttonDownTime))
				shouldGoUp = true;
			
			if(shouldGoUp)
			{
				lastButton = -1;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
			
		
		if(Helper.isServer())
		{
			if(schemaList == null)
			{
				TardisOutput.print("TConTE", "Getting schemas");
				schemaList = TardisMod.configHandler.getSchemas();
				if(schemaList!= null && schemaList.length >0)
				{
					schemaNum = 0;
					schemaChooserString = schemaList[schemaNum];
				}
				else
					schemaList = null;
			}
			if(tickTimer % 80 == 0)
			{
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
	}
	
	private float activatedDelta(float xH,float yH, float xP, float yP)
	{
		float delta = (float) ((1.5 - xP - yP)/(-xP+yH+xH-yP));
		return delta;
	}
	
	private float activatedX(float xH, float xP, float delta)
	{
		return (float) (xP - (delta * (xP - xH)));
	}
	
	private float activatedY(float yH, float yP, float delta)
	{
		return (float) (yP - (delta * (yP - yH)));
	}
	
	private float activatedZ(float zH, float zP, float delta)
	{
		return (float) (zP - (delta * (zP - zH)));
	}
	
	private HitPosition activateSide(EntityPlayer pl, int blockX,int blockY,int blockZ, float i, float j, float k, int side)
	{
		if(pl.worldObj.isRemote)
			return null;
		
		float distanceAway = (side == 0 || side == 2) ? (float) (Math.abs(pl.posX - 0.5) - 0.5) : (float) (Math.abs(pl.posZ - 0.5) - 0.5);
		float distanceSide = (side == 0 || side == 2) ? (float) (pl.posZ + 1) : (float) (pl.posX + 1);
		float hitAway;
		if(blockX != 0 || blockZ != 0)
		{
			if(side == 0 && blockX < 1)
				return null;
			if(side == 1 && blockZ < 1)
				return null;
			if(side == 2 && blockX > -1)
				return null;
			if(side == 3 && blockZ > -1)
				return null;
			hitAway = (side == 0 ? i : (side == 2 ? 1-i : (side == 1 ? k : 1-k)));
		}
		else
		{
			if(side == 0 && i < 0.9)
				return null;
			if(side == 2 && i > 0.1)
				return null;
			if(side == 3 && k > 0.1)
				return null;
			if(side == 1 && k < 0.9)
				return null;
			//TardisOutput.print("TConTE", "side:"+side+","+i+","+j+","+k);
			j = j + 1;
			hitAway = (side == 0 ? i : (side == 2 ? 1-i : (side == 1 ? k : 1-k))) - 1;
		}
		//TardisOutput.print("TConTE","j:"+j+",a:"+hitAway);
		float hitSide;
		if(side == 0 || side == 2)
			hitSide = blockZ + 1 + k;
		else
			hitSide = blockX + 1 + i;
		
		float delta = activatedDelta(hitAway,j,distanceAway,pl.eyeHeight);
		float hitX = activatedX(hitAway,distanceAway,delta);
		float hitY = activatedY(j,pl.eyeHeight,delta);
		float hitZ = activatedZ(hitSide,distanceSide, delta);
		if((hitZ < 1 && (1-hitX) >= hitZ) || (hitZ > 2 && (1-hitX) > (3-hitZ)))
			return null;
		return new HitPosition(hitX,hitZ,side);
	}
	
	public boolean activate(EntityPlayer pl, int blockX,int blockY,int blockZ, float i, float j, float k)
	{
		TardisCoreTileEntity core = Helper.getTardisCore(worldObj);
		HitPosition hit = null;
		for(int cnt=0;cnt<4&&hit==null;cnt++)
			hit = activateSide(pl,blockX, blockY, blockZ, i, j, k, cnt);
		if(hit != null && core != null)
		{
			TardisOutput.print("TConTE", "H:" + hit.toString(),TardisOutput.Priority.DEBUG);
			if(hit.within(0, 0.985, 0.420, 1.124, 0.521)) // Screwdriver
			{
				if(hasScrewdriver() && pl instanceof EntityPlayerMP)
				{
					hasScrewdriver = false;
					ItemStack toGive = new ItemStack(TardisMod.screwItem,1,0);
					toGive.stackTagCompound = new NBTTagCompound();
					toGive.stackTagCompound.setString("schemaName", schemaChooserString);
					toGive.stackTagCompound.setInteger("screwdriverMode", screwMode);
					Helper.giveItemStack((EntityPlayerMP) pl, toGive);
				}
				else
				{
					ItemStack held = pl.getHeldItem();
					if(held != null)
					{
						Item item = held.getItem();
						if(item instanceof TardisSonicScrewdriverItem)
						{
							InventoryPlayer inv = pl.inventory;
							inv.mainInventory[inv.currentItem] = null;
							screwMode = TardisMod.screwItem.getMode(held).ordinal();
							hasScrewdriver = true;
						}
					}
				}
			}
			else if(hit.within(0, 0.779, 0.431, 0.901, 0.525))
				activateControl(pl,5);
			else if(hit.within(0,1.645,0.238,1.88,0.38))//Gauge1
				pl.addChatMessage("Energy: " + core.getEnergy() + "/" + core.getMaxEnergy());
			else if(hit.within(0,1.375,0.238,1.615,0.38))
				pl.addChatMessage("Rooms: " + core.getNumRooms() + "/" + core.getMaxNumRooms());
			else if(hit.within(0,1.10,0.238,1.335,0.38))
				pl.addChatMessage("Speed: " + core.getSpeed() + "/" + core.getMaxSpeed());
			else if(hit.within(0,0.865,0.55,1.327,0.868))
				activateControl(pl,3);
			else if(hit.within(0, 1.725, 0.585,  2.05,  0.846))
			{
				int d = 1;
				if(pl.isSneaking())
					d = -1;
				core.addSpeed(d);
			}
			else if(hit.within(3,2.156, 0.654,  2.540,  0.924))
				core.sendDestinationStrings(pl);
			else if(hit.within(3,1.727,0.626,2.019,0.826))
				activateControl(pl,10);
			else if(hit.within(3,1.361,0.626,1.634,0.826))
				activateControl(pl,11);
			else if(hit.within(3,0.981,0.626,1.210,0.826))
				activateControl(pl,12);
			else if(hit.within(3,1.361,0.268,1.634,0.431))
				activateControl(pl,13);
			else if(hit.within(3,1.694,0.227,1.951,0.408))
				activateControl(pl,14);
			else if(hit.within(3,1.039,0.257,1.238,0.417))
				activateControl(pl,15);
			else if(hit.within(1,1.145,0.207,1.259,0.443))
				activateControl(pl,20);
			else if(hit.within(1,1.333,0.207,1.465,0.443))
				activateControl(pl,21);
			else if(hit.within(1,1.531,0.207,1.648,0.443))
				activateControl(pl,22);
			else if(hit.within(1,1.730,0.207,1.856,0.443))
				activateControl(pl,23);
			else if(hit.within(1,0.958,0.601,1.250,0.841))
				activateControl(pl,24);
			else if(hit.within(1,1.761,0.601,2.048,0.841))
				activateControl(pl,25);
			else if(hit.within(2,1.730,0.147,1.859,0.470))
				activateControl(pl,30);
			else if(hit.within(2,1.581,0.147,1.710,0.470))
				activateControl(pl,31);
			else if(hit.within(2,1.430,0.147,1.558,0.470))
				activateControl(pl,32);
			else if(hit.within(2,1.284,0.147,1.415,0.470))
				activateControl(pl,33);
			else if(hit.within(2, 1.129, 0.283, 1.241, 0.384))
				activateControl(pl,34);
			else if(hit.within(2, 1.408, 0.677, 1.660, 0.855))
				activateControl(pl,35);
			else if(hit.within(1, 1.394, 0.678, 1.658, 0.858))
				activateControl(pl, 40);
			else if(hit.within(2, 0.553, 0.629, 0.838, 0.924))
				activateControl(pl,41);
			else if(hit.within(0, 2.110, 0.562, 2.441, 0.872))
				activateControl(pl,42);
			else if(hit.within(2, 2.251, 0.519, 2.371, 0.600))
				activateControl(pl,50);
			else if(hit.within(2, 2.251, 0.646, 2.371, 0.730))
				activateControl(pl,51);
			else if(hit.within(2, 0.971, 0.598, 1.138, 0.941))
				activateControl(pl, 60);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			sendDataPacket();
		}
		return true;
	}
	
	private int getFromControls(int[] control)
	{
		int controlOne = control[0] * (int) Math.pow(4,control[4]+1);
		int controlTwo = control[1] * (int) Math.pow(2,control[5]+1);
		int controlThree = control[2] * 6 + control[3];
		return controlOne + controlTwo + controlThree;
	}
	
	public int getDimFromControls()
	{
		return dimControl;
	}
	
	public int getZFromControls()
	{
		return getFromControls(zControls);
	}
	
	public int getXFromControls()
	{
		return getFromControls(xControls);
	}
	
	public int getYFromControls()
	{
		int count = yControls[0];
		count = (count << 2) + yControls[1];
		count = (count << 2) + yControls[2];
		count = (count << 2) + yControls[3];
		TardisOutput.print("TConTE", "YCont:" + yControls[0] + "," + yControls[1] + "," + yControls[2] + "," + yControls[3]+"="+ count);
		return count;
	}
	
	public boolean getLandFromControls()
	{
		return landGroundControl;
	}
	
	public int getFacingFromControls()
	{
		return facing;
	}
	
	private void clampControls()
	{
		clampControls(xControls);
		clampControls(yControls);
		clampControls(zControls);
	}
	
	private void clampControls(int[] controls)
	{
		if(controls.length == 6)
		{
			controls[0] = Helper.clamp(controls[0], -5, 5);
			controls[1] = Helper.clamp(controls[1], -5, 5);
			controls[2] = Helper.clamp(controls[2], -5, 5);
			controls[3] = Helper.clamp(controls[3], -5, 5);
			controls[4] = Helper.clamp(controls[4], 0, 7);
			controls[5] = Helper.clamp(controls[5], 0, 7);
		}
		else if(controls.length == 4)
		{
			controls[0] = Helper.clamp(controls[0], 0, 3);
			controls[1] = Helper.clamp(controls[1], 0, 3);
			controls[2] = Helper.clamp(controls[2], 0, 3);
			controls[3] = Helper.clamp(controls[3], 0, 3);
		}
	}
	
	public double getControlState(int controlID,boolean wobble)
	{
		double maxWobble = 0.025;
		double count = 20;
		int maxRand = 10;
		double wobbleAmount = 0;
		if(wobble)
		{
			wobbleAmount = (((tickTimer + rand.nextInt(maxRand)) % count) / count);
			wobbleAmount = Math.abs(wobbleAmount - 0.5) * maxWobble * 2;
		}
		return getControlState(controlID) + wobbleAmount;
	}
	
	public double getControlState(int controlID)
	{
		TardisCoreTileEntity core = Helper.getTardisCore(worldObj);
		if(core != null)
		{
			if(controlID == 0)
				return ((double) core.getEnergy() / core.getMaxEnergy());
			if(controlID == 1)
				return ((double) core.getNumRooms() / core.getMaxNumRooms());
			if(controlID == 2 || controlID == 4)
				return (core.getSpeed() / core.getMaxSpeed());
			if(controlID == 3)
				return facing/4.0;
			if(controlID >= 10 && controlID < 14)
				return ((double) (xControls[controlID-10] + 5) / 10);
			if(controlID >= 14 && controlID < 16)
				return xControls[controlID - 10] / 8.0;
			if(controlID >= 20 && controlID < 24)
				return ((double) (zControls[controlID-20] + 5) / 10);
			if(controlID >= 24 && controlID < 26)
				return zControls[controlID - 20] / 8.0;
			if(controlID >= 30 && controlID < 34)
				return yControls[controlID - 30] / 3.0;
			if(controlID == 34)
				return (landGroundControl ? 1 : 0);
			if(controlID == 40)
				return (primed ? 1 : 0);
			if(controlID == 41)
				return (regulated ? 1 : 0);
			if(controlID == 42)
				return core.inFlight() ? 1 : 0;
			if(controlID == 50 || controlID == 51 || controlID == 5)
				return lastButton == controlID ? 1 : 0;
			if(controlID == 60)
				return (dimControl+1) / 2.0;
			return (((tickTimer + (controlID * 20)) % cycleLength) / cycleLength);
		}
		return 0;
	}
	
	public double getControlHighlight(int controlID)
	{
		double highlightAmount = Math.abs((tickTimer % 40) - 20) / 40.0 + 0.5;
		return -1;
	}
	
	private void activateControl(EntityPlayer pl,int controlID)
	{
		TardisCoreTileEntity core = Helper.getTardisCore(worldObj);
		TardisOutput.print("TConTE","Control:"+controlID,TardisOutput.Priority.DEBUG);
		if(!core.inCoordinatedFlight())
		{
			if(controlID == 5)
			{
				lastButton = 5;
				lastButtonTT = tickTimer;
				if(!hasScrewdriver && core.takeEnergy(core.getMaxEnergy()/2,false))
					hasScrewdriver = true;
				else if(hasScrewdriver && core.addEnergy(core.getMaxEnergy()/2, false))
					hasScrewdriver = false;
			}
			if((controlID >= 10 && controlID < 40) || controlID == 3 || controlID == 60)
			{
				primed = false;
				regulated = false;
				if(controlID == 3)
					facing = Helper.cycle(facing+(pl.isSneaking()?-1:1), 0, 3);
				else if(controlID >= 10 && controlID < 14)
				{
					if(pl.isSneaking())
						xControls[controlID - 10] --;
					else
						xControls[controlID - 10] ++;
					clampControls(xControls);
				}
				else if(controlID >= 14 && controlID < 16)
				{
					if(pl.isSneaking())
						xControls[controlID - 10] = Helper.cycle(xControls[controlID - 10] - 1,0,7);
					else
						xControls[controlID - 10] = Helper.cycle(xControls[controlID - 10] + 1,0,7);
					clampControls(xControls);
				}
				else if(controlID >= 20 && controlID < 24)
				{
					if(pl.isSneaking())
						zControls[controlID - 20] --;
					else
						zControls[controlID - 20] ++;
					clampControls(zControls);
				}
				else if(controlID >= 24 && controlID < 26)
				{
					if(pl.isSneaking())
						zControls[controlID - 20] = Helper.cycle(zControls[controlID - 20] - 1,0,7);
					else
						zControls[controlID - 20] = Helper.cycle(zControls[controlID - 20] + 1,0,7);
					clampControls(zControls);
				}
				else if(controlID >= 30 && controlID < 34)
				{
					if(pl.isSneaking())
						yControls[controlID - 30] --;
					else
						yControls[controlID - 30] ++;
					clampControls(yControls);
				}
				else if(controlID == 34)
					landGroundControl = !landGroundControl;
				else if(controlID == 60)
					dimControl = Helper.cycle(dimControl + (pl.isSneaking()?-1:1), -1, 1);
			}
			else if(controlID == 40)
				primed = true;
			else if(controlID == 41 && primed)
				regulated = true;
			else if(controlID == 42 && primed && regulated)
				core.takeOff();
		}
		
		if(controlID == 50 || controlID == 51) // Schema change
		{
			TardisOutput.print("TConTE", "Cycling schema");
			lastButton = controlID;
			lastButtonTT = tickTimer;
			schemaNum += (controlID == 50 ? 1 : -1);
			schemaNum = Helper.cycle(schemaNum,0,schemaList.length-1);
			schemaChooserString = schemaList[schemaNum];
		}
	}
	
	public boolean hasScrewdriver()
	{
		return hasScrewdriver;
	}
	
	public void land()
	{
		primed = false;
		regulated = false;
	}
	
	public static void refreshSchemas()
	{
		schemaList = TardisMod.configHandler.getSchemas();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		schemaNum = nbt.getInteger("schemaNum");
		screwMode = nbt.getInteger("screwMode");
		clampControls();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setInteger("schemaNum", schemaNum);
		nbt.setInteger("screwMode", screwMode);
	}

	@Override
	public void readTransmittable(NBTTagCompound nbt)
	{
		tickTimer = nbt.getInteger("tickTimer");
		hasScrewdriver = nbt.getBoolean("hasScrewdriver");
		facing    = nbt.getInteger("facing");
		dimControl = nbt.getInteger("dimControl");
		xControls = nbt.getIntArray("xControls");
		zControls = nbt.getIntArray("zControls");
		yControls = nbt.getIntArray("yControls");
		primed    = nbt.getBoolean("primed");
		regulated = nbt.getBoolean("regulated");
		landGroundControl = nbt.getBoolean("landGroundControl");
		schemaChooserString = nbt.getString("schemaChooserString");
		lastButton = nbt.getInteger("lastButton");
		lastButtonTT = nbt.getInteger("lastButtonTT");
	}
	
	@Override
	public void writeTransmittable(NBTTagCompound nbt)
	{
		nbt.setInteger("tickTimer", tickTimer);
		nbt.setBoolean("hasScrewdriver", hasScrewdriver);
		nbt.setBoolean("primed", primed);
		nbt.setBoolean("regulated", regulated);
		nbt.setInteger("facing", facing);
		nbt.setInteger("dimControl",dimControl);
		nbt.setIntArray("xControls", xControls);
		nbt.setIntArray("zControls", zControls);
		nbt.setIntArray("yControls", yControls);
		nbt.setBoolean("landGroundControl", landGroundControl);
		nbt.setString("schemaChooserString", schemaChooserString);
		nbt.setInteger("lastButton",lastButton);
		nbt.setInteger("lastButtonTT",lastButtonTT);
	}
}
