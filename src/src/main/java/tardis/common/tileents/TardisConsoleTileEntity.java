package tardis.common.tileents;

import java.util.Arrays;
import java.util.HashMap;

import tardis.TardisMod;
import tardis.api.IControlMatrix;
import tardis.api.TardisFunction;
import tardis.api.TardisScrewdriverMode;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.core.store.ControlStateStore;
import tardis.common.items.TardisSonicScrewdriverItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

public class TardisConsoleTileEntity extends TardisAbstractTileEntity implements IControlMatrix
{
	public static final float cycleLength = 80;
	private int tickTimer;
	
	private int hasScrewdriver = 1;
	
	private int   facing    = 0;
	private int   dimControl = 0;
	private int[] xControls = new int[6];
	private int[] zControls = new int[6];
	private int[] yControls = new int[4];
	private boolean landGroundControl = false;
	private boolean dayNightControl   = false;
	private boolean relativeCoords    = false;
	private boolean uncoordinated	  = false;
	private boolean stable			  = false;
	
	private boolean saveCoords = false;
	private HashMap<Integer,ControlStateStore> states = new HashMap<Integer,ControlStateStore>();
	
	private int rdpCounter = 0;
	private boolean roomDeletePrepare = false;
	private boolean primed = false;
	private boolean regulated = false;
	
	private int lastButton = -1;
	private int lastButtonTT = -1;

	private static String[] schemaList = null;
	
	private int schemaNum = 0;
	private int screwMode = 0;
	
	private int unstableControl = -1;
	private boolean unstablePressed = false;
	
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
		super.updateEntity();
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
			
		
		if(Helper.isServer() && !worldObj.isRemote)
		{
			if(rdpCounter > 0)
				rdpCounter--;
			
			if(roomDeletePrepare && rdpCounter <= 0)
				roomDeletePrepare = false;
			
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
	
/*	private float activatedY(float yH, float yP, float delta)
	{
		return (float) (yP - (delta * (yP - yH)));
	}*/
	
	private float activatedZ(float zH, float zP, float delta)
	{
		return (float) (zP - (delta * (zP - zH)));
	}
	
	public boolean activate(EntityPlayer pl, int blockX,int blockY,int blockZ, float i, float j, float k)
	{
		TardisCoreTileEntity core = Helper.getTardisCore(worldObj);
		HitPosition hit = null;
		for(int cnt=0;cnt<4&&hit==null;cnt++)
			hit = activateSide(pl,blockX, blockY, blockZ, i, j, k, cnt);
		if(hit != null && core != null)
		{
			boolean update = true;
			TardisOutput.print("TConTE", "H:" + hit.toString(),TardisOutput.Priority.DEBUG);
			if(hit.within(0, 0.985, 0.420, 1.124, 0.521)) // Screwdriver
				activateControl(pl,6);
			else if(hit.within(2, 1.214, 0.581, 1.338, 0.669))
				activateControl(pl,7);
			else if(hit.within(0, 0.779, 0.431, 0.901, 0.525))
				activateControl(pl,5);
			else if(hit.within(0, 1.651, 0.271, 1.883, 0.400))//Gauge1
				pl.addChatMessage("Energy: " + core.getEnergy() + "/" + core.getMaxEnergy());
			else if(hit.within(0, 1.375, 0.271, 1.615, 0.400))
				pl.addChatMessage("Rooms: " + core.getNumRooms() + "/" + core.getMaxNumRooms());
			else if(hit.within(0, 1.517, 0.138, 1.750, 0.265))
			{
				pl.addChatMessage("XP: " + core.getXP() + "/" + core.getXPNeeded());
				pl.addChatMessage("Level:" + core.getLevel());
			}
			else if(hit.within(0, 1.10 , 0.271, 1.335, 0.400))
				pl.addChatMessage(String.format("Speed: %.1f/%.1f", core.getSpeed(true),core.getMaxSpeed()));
				//pl.addChatMessage("Speed: " + core.getSpeed(true) + "/" + core.getMaxSpeed())
			else if(hit.within(0,0.865,0.55,1.327,0.868))
				activateControl(pl,3);
			else if(hit.within(0, 1.725, 0.585,  2.05,  0.846))
			{
				int d = 1;
				if(pl.isSneaking())
					d = -1;
				core.addSpeed(d);
			}
			else if(hit.within(3,2.156, 0.654,  2.540, 0.924))
				core.sendDestinationStrings(pl);
			else if(hit.within(3, 1.727, 0.626, 2.019, 0.826))
				activateControl(pl, 10);
			else if(hit.within(3, 1.361, 0.626, 1.634, 0.826))
				activateControl(pl, 11);
			else if(hit.within(3, 0.981, 0.626, 1.210, 0.826))
				activateControl(pl, 12);
			else if(hit.within(3, 1.361, 0.268, 1.634, 0.431))
				activateControl(pl, 13);
			else if(hit.within(3, 1.694, 0.227, 1.951, 0.408))
				activateControl(pl, 14);
			else if(hit.within(3, 1.039, 0.257, 1.238, 0.417))
				activateControl(pl, 15);
			else if(hit.within(1, 1.145, 0.207, 1.259, 0.443))
				activateControl(pl, 20);
			else if(hit.within(1, 1.333, 0.207, 1.465, 0.443))
				activateControl(pl, 21);
			else if(hit.within(1, 1.531, 0.207, 1.648, 0.443))
				activateControl(pl, 22);
			else if(hit.within(1, 1.730, 0.207, 1.856, 0.443))
				activateControl(pl, 23);
			else if(hit.within(1, 0.650, 0.664, 0.943, 0.856))	//Z Wheel 1
				activateControl(pl, 24);
			else if(hit.within(1, 1.264, 0.664, 1.545, 0.856))	//Z Wheel 2
				activateControl(pl, 25);
			else if(hit.within(2, 1.730, 0.147, 1.859, 0.470))
				activateControl(pl, 30);
			else if(hit.within(2, 1.581, 0.147, 1.710, 0.470))
				activateControl(pl, 31);
			else if(hit.within(2, 1.430, 0.147, 1.558, 0.470))
				activateControl(pl, 32);
			else if(hit.within(2, 1.284, 0.147, 1.415, 0.470))
				activateControl(pl, 33);
			else if(hit.within(2, 1.129, 0.283, 1.241, 0.384))
				activateControl(pl, 34);
//			else if(hit.within(2, 1.408, 0.677, 1.660, 0.855))
//				activateControl(pl,35);
			else if(hit.within(1, 0.985, 0.664, 1.230, 0.856))	//Flight Primer
				activateControl(pl, 40);
			else if(hit.within(2, 0.553, 0.629, 0.838, 0.924))	//Flight Regulator
				activateControl(pl, 41);
			else if(hit.within(0, 2.110, 0.562, 2.441, 0.872))	//Flight Takeoff
				activateControl(pl, 42);
			else if(hit.within(2, 2.251, 0.519, 2.371, 0.600))
				activateControl(pl, 50);
			else if(hit.within(2, 2.251, 0.646, 2.371, 0.730))
				activateControl(pl, 51);
			else if(hit.within(2, 0.290, 0.825, 0.441, 0.915))
				activateControl(pl, 52);
			else if(hit.within(0, 1.760, 0.172, 1.914, 0.265))
				activateControl(pl, 53);
			else if(hit.within(1, 1.013, 0.493, 1.194, 0.624))
				activateControl(pl, 54);
			else if(hit.within(3, 0.701, 0.703, 0.823, 0.792))
				activateControl(pl, 55);
			else if(hit.within(3, 0.701, 0.807, 0.823, 0.903))
				activateControl(pl, 56);
			else if(hit.within(2, 0.971, 0.598, 1.138, 0.941))
				activateControl(pl, 60);
			else if(hit.within(3, 2.557, 0.806, 2.683, 0.896))	//Delete rooms button
				activateControl(pl, 901);
			else if(hit.within(1, 2.369, 0.801, 2.491, 0.894))	//Load/Save Switch
				activateControl(pl, 900);
			else if(hit.within(1, 1.700, 0.513, 2.355, 0.898))
			{
				int jx = (int)(5*(hit.posZ - 1.700) / (2.355 - 1.700));
				int ix = (int)(4*(hit.posY - 0.513) / (0.898 - 0.513));
				int control = 1000 + (5 * ix) + jx;
				activateControl(pl, control);
			}
			else if(hit.within(2, 1.187, 0.768, 1.603, 0.947))
				activateControl(pl, 1020);
			else if(hit.within(2, 1.677, 0.768, 2.103, 0.947))
				activateControl(pl, 1021);
			else if(hit.within(2, 2.189, 0.768, 2.592, 0.947))
				activateControl(pl, 1022);
			else
				update = false;
			
			if(update)
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			//sendDataPacket();
		}
		return true;
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
//		float hitY = activatedY(j,pl.eyeHeight,delta);
		float hitZ = activatedZ(hitSide,distanceSide, delta);
		if((hitZ < 1 && (1-hitX) >= hitZ) || (hitZ > 2 && (1-hitX) > (3-hitZ)))
			return null;
		return new HitPosition(hitX,hitZ,side);
	}
	
	private void activateControl(EntityPlayer pl,int controlID)
	{
		TardisCoreTileEntity core = Helper.getTardisCore(worldObj);
		TardisOutput.print("TConTE","Control:"+controlID,TardisOutput.Priority.DEBUG);
		if(!core.inCoordinatedFlight())
		{
			if(isMovementControl(controlID) && (!core.inFlight() || !core.inCoordinatedFlight()))
			{
				if(!core.inFlight())
				{
					primed = false;
					regulated = false;
				}
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
				else if(controlID == 53)
					relativeCoords = !relativeCoords;
				else if(controlID == 55)
					uncoordinated  = !uncoordinated;
				else if(controlID == 60)
					dimControl = Helper.cycle(dimControl + (pl.isSneaking()?-1:1), -1, 1);
			}
			else if(!core.inFlight())
			{
				if(controlID == 40)
					primed = true;
				else if(controlID == 41 && primed)
					regulated = true;
				else if(controlID == 42 && primed && regulated)
					core.takeOff(pl);
			}
		}
		
		if(controlID == 5)
		{
			lastButton = 5;
			lastButtonTT = tickTimer;
			if(!hasScrewdriver(0) && core.takeEnergy(core.getMaxEnergy()/2,false))
				setScrewdriver(0,true);
			else if(hasScrewdriver(0) && core.addEnergy(2*core.getMaxEnergy()/5, false))
				setScrewdriver(0,false);
		}
		else if(controlID == 6 || controlID == 7) // Screwdriver slot 0/1
		{
			int slot = controlID == 6 ? 0 : 1;
			if(hasScrewdriver(slot) && pl instanceof EntityPlayerMP)
			{
				setScrewdriver(slot,false);
				ItemStack toGive = new ItemStack(TardisMod.screwItem,1,0);
				toGive.stackTagCompound = new NBTTagCompound();
				toGive.stackTagCompound.setString("schemaName", schemaChooserString);
				toGive.stackTagCompound.setInteger("screwdriverMode", screwMode);
				toGive.stackTagCompound.setInteger("linkedTardis", Helper.getWorldID(worldObj));
				TardisMod.screwItem.notifyMode(toGive,pl);
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
						screwMode = TardisSonicScrewdriverItem.getMode(held).ordinal();
						setScrewdriver(slot,true);
					}
				}
			}
		}
		else if(controlID == 50 || controlID == 51) // Schema change
		{
			TardisOutput.print("TConTE", "Cycling schema");
			lastButton = controlID;
			lastButtonTT = tickTimer;
			schemaNum += (controlID == 50 ? -1 : 1);
			schemaNum = Helper.cycle(schemaNum,0,schemaList.length-1);
			schemaChooserString = schemaList[schemaNum];
		}
		else if(controlID == 52)
			dayNightControl = !dayNightControl;
		else if(controlID == 54 && core.hasFunction(TardisFunction.SENSORS))
			core.sendScannerStrings(pl);
		else if(controlID == 56 && core.hasFunction(TardisFunction.STABILISE))
		{
			stable = !stable;
			if(stable)
				clearUnstableControl();
		}
		else if(controlID == 900)
			saveCoords = !saveCoords;
		else if(controlID >= 1000) //Flight instabilitiers
		{
			if(controlID >= 1000 && controlID < 1023)
			{
				lastButton = controlID;
				lastButtonTT = tickTimer;
			}
			if(!core.inFlight() && controlID >= 1000 && controlID < 1020)
			{
				int num = controlID - 1000;
				if(saveCoords)
				{
					saveControls(num);
				}
				else
				{
					loadControls(num);
				}
				primed = false;
				regulated = false;
			}
			if(core.inFlight())
			{
				if(unstableControl == controlID)
					pressedUnstable();
				else
					TardisOutput.print("TConTE","Stable button pressed:" +controlID + ":"+unstableControl);
			}
		}
		
		if(controlID == 901 && core.canModify(pl))
		{
			if(!roomDeletePrepare)
			{
				if(!pl.isSneaking())
				{
					ChatMessageComponent c = new ChatMessageComponent();
					c.setColor(EnumChatFormatting.DARK_RED);
					c.addText("[TARDIS]Sneak-right click this button again to activate deletion protocol");
					pl.sendChatToPlayer(c);
					rdpCounter = 40;
					roomDeletePrepare = true;
				}
			}
			else
			{
				if(pl.isSneaking())
					core.removeAllRooms();
				roomDeletePrepare = false;
			}
		}
		else
		{
			if(controlID == 901 && !core.canModify(pl))
				pl.addChatMessage("[TARDIS]You don't have permission to modify this TARDIS");
			if(roomDeletePrepare)
				roomDeletePrepare = false;
		}
	}

	public boolean isMovementControl(int controlID)
	{
		if(controlID >= 10 && controlID < 40)
			return true;
		if(controlID == 3)
			return true;
		if(controlID == 53)
			return true;
		if(controlID == 55)
			return true;
		if(controlID == 60)
			return true;
		return false;
	}
	
	public boolean setControls(int dim, int x, int y, int z, boolean allowNearest)
	{
		int dCont   = Helper.clamp(dim,-1,1);
		int[] xCont = getControlsFromDest(x);
		int[] yCont = getYControls(y);
		int[] zCont = getControlsFromDest(z);
		if((allowNearest || (getFromControls(xCont) == x && getFromControls(zCont) == z)) && (dCont == dim))
		{
			relativeCoords = false;
			dimControl = dCont;
			xControls = xCont;
			yControls = yCont;
			zControls = zCont;
			worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
			return allowNearest ? (getFromControls(xCont) == x && getFromControls(zCont) == z) : true;
		}
		return false;
	}
	
	public boolean setControls(TardisTileEntity ext, boolean allowNearest)
	{
		int dim = Helper.getWorldID(ext.worldObj);
		int xC = ext.xCoord;
		int yC = ext.yCoord;
		int zC = ext.zCoord;
		TardisOutput.print("TConTE", "Attempting to set controls to :" + dim + "," + xC + "," + yC + "," + zC,TardisOutput.Priority.DEBUG);
		return setControls(dim,xC,yC,zC,allowNearest);
	}
	
	private static int[] getYControls(int height)
	{
		int[] temp = new int[4];
		temp[3] = height & 4;
		temp[2] = (height & 12) >> 2;
		temp[1] = (height & 48) >> 4;
		temp[0] = (height & 192)>> 6;
		return temp;
	}
	
	private static int[] getControlsFromDest(int dest)
	{
		int[] temp = new int[6];
		int c = 0;
		for(int i=-5;i<=5;i++)
		{
			for(int j=0;j<8;j++)
			{
				int val = i * (int)Math.pow(4,j+1);
				if(Math.abs(dest-val) < Math.abs(dest-c))
				{
					temp[0] = i;
					temp[4] = j;
					c = val;
				}
			}
		}
		
		int d = c;
		for(int i=-5;i<=5;i++)
		{
			for(int j=0;j<8;j++)
			{
				int val = d + (i * (int)Math.pow(2,j+1));
				if(Math.abs(dest-val) < Math.abs(dest-c))
				{
					temp[1] = i;
					temp[5] = j;
					c = val;
				}
			}
		}
		temp[2] = Helper.clamp((dest-c)/6,-5,5);
		c += temp[2];
		temp[3] = Helper.clamp(dest-c    ,-5,5);
		TardisOutput.print("TConTE","Found coords:" + Arrays.toString(temp),TardisOutput.Priority.DEBUG);
		return temp;
	}
	
	private static int getFromControls(int[] control)
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
	
	public int getZFromControls(int extZ)
	{
		return getFromControls(zControls) + (relativeCoords ? extZ : 0);
	}
	
	public int getXFromControls(int extX)
	{
		return getFromControls(xControls) + (relativeCoords ? extX : 0);
	}
	
	public int getYFromControls(int[] cont)
	{
		if(cont.length != 4)
			return 0;
		int count = cont[0];
		count = (count << 2) + cont[1];
		count = (count << 2) + cont[2];
		count = (count << 2) + cont[3];
		TardisOutput.print("TConTE", "YCont:" + cont[0] + "," + cont[1] + "," + cont[2] + "," + cont[3]+"="+ count,TardisOutput.Priority.OLDDEBUG);
		return count;
	}
	
	public int getYFromControls(int extY)
	{
		return getYFromControls(yControls) + (relativeCoords ? extY : 0);
	}
	
	public boolean getLandFromControls()
	{
		return landGroundControl;
	}
	
	public int getFacingFromControls()
	{
		return facing;
	}
	
	private void loadControls(int num)
	{
		TardisOutput.print("TConTE","Attempting to load state " + num);
		if(states.containsKey(num))
			loadControls(states.get(num));
	}
	
	public void loadControls(ControlStateStore state)
	{
		if(state.isValid())
		{
			TardisOutput.print("TConTE", "Loading state");
			facing = state.facing;
			dimControl = state.dimControl;
			xControls = state.xControls.clone();
			yControls = state.yControls.clone();
			zControls = state.zControls.clone();
			landGroundControl = state.landGroundControl;
			relativeCoords = state.relative;
			clampControls();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}
	
	public void saveControls(int stateNum)
	{
		TardisOutput.print("TConTE", "Saving state to num:"+stateNum);
		ControlStateStore s = new ControlStateStore(facing,dimControl,xControls,yControls,zControls,landGroundControl,relativeCoords);
		states.put(stateNum, s);
	}
	
	private void clampControls()
	{
		if(xControls == null || xControls.length != 6)
			xControls = new int[6];
		if(yControls == null || yControls.length != 4)
			yControls = new int[4];
		if(zControls == null || zControls.length != 6)
			zControls = new int[6];
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
	
	@Override
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
	
	@Override
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
				return Helper.clamp(core.getSpeed(controlID == 2) / core.getMaxSpeed(),0,1);
			if(controlID == 3)
				return facing/4.0;
			if(controlID == 8)
				return (core.getXP() / core.getXPNeeded());
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
			if(controlID == 52)
				return dayNightControl ? 1 : 0;
			if(controlID == 53)
				return relativeCoords ? 1 : 0;
			if(controlID == 55)
				return uncoordinated ? 1 : 0;
			if(controlID == 56)
				return stable ? 1 : 0;
			if(controlID == 60)
				return (dimControl+1) / 2.0;
			if(controlID == 900)
				return saveCoords ? 1 : 0;
			if(controlID == 901)
				return roomDeletePrepare ? 1 : 0;
			if(controlID >= 1000 && controlID < 1023)
				return lastButton == controlID ? 1 : 0;
			return (((tickTimer + (controlID * 20)) % cycleLength) / cycleLength);
		}
		return 0;
	}
	
	@Override
	public double getControlHighlight(int controlID)
	{
		TardisCoreTileEntity core = Helper.getTardisCore(worldObj);
		double highlightAmount = Math.abs((tickTimer % 40) - 20) / 40.0 + 0.5;
		if(controlID == unstableControl && !unstablePressed && core != null && core.inFlight())
			return highlightAmount;
		
		if(controlID == 901 && roomDeletePrepare)
			return highlightAmount;
		return -1;
	}
	
	private void pressedUnstable()
	{
		TardisOutput.print("TConTE", "Unstable button pressed");
		unstableControl = -1;
		unstablePressed = true;
	}
	
	public boolean unstableFlight()
	{
		return true;
	}
	
	public void randomUnstableControl()
	{
		int min = 1000;
		int max = 1022;
		int ran = 0;
		if(min != max)
			ran = rand.nextInt(1 + max - min);
		unstableControl = min+ran;
		unstablePressed = false;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	public void clearUnstableControl()
	{
		unstableControl = -1;
	}
	
	public boolean unstableControlPressed()
	{
		return unstablePressed || unstableControl == -1;
	}
	
	public void setScrewdriver(int slot, boolean bool)
	{
		int bit = (int)Math.pow(2, slot);
		if((hasScrewdriver & bit) == 0 && bool)
			hasScrewdriver += bit;
		else if((hasScrewdriver & bit) == bit && !bool)
			hasScrewdriver -= bit;
	}
	
	public boolean hasScrewdriver(int slot)
	{
		int bit = (int)Math.pow(2, slot);
		return (hasScrewdriver & bit) == bit;
	}
	
	public void land()
	{
		primed = false;
		regulated = false;
	}
	
	public static void refreshSchemas()
	{
		schemaList = TardisMod.configHandler.getSchemas();
		Arrays.sort(schemaList);
	}
	
	public boolean getRelativeCoords()
	{
		return relativeCoords;
	}
	
	public boolean getDaytimeSetting()
	{
		return dayNightControl;
	}
	
	public TardisScrewdriverMode getScrewMode(int slot)
	{
		TardisScrewdriverMode[] vals = TardisScrewdriverMode.values();
		if(screwMode >= 0 && screwMode < vals.length)
			return vals[screwMode];
		return vals[0];
	}
	
	public boolean shouldLand()
	{
		return !uncoordinated;
	}
	
	public boolean isStable()
	{
		return stable;
	}
	
	public TardisCoreTileEntity getCore()
	{
		TileEntity core = worldObj.getBlockTileEntity(Helper.tardisCoreX, Helper.tardisCoreY, Helper.tardisCoreZ);
		if(core != null && core instanceof TardisCoreTileEntity)
			return (TardisCoreTileEntity)core;
		return null;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		schemaNum = nbt.getInteger("schemaNum");
		for(int i = 0;i<20;i++)
		{
			if(nbt.hasKey("css"+i))
				states.put(i, ControlStateStore.readFromNBT(nbt.getCompoundTag("css"+i)));
		}
		clampControls();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setInteger("schemaNum", schemaNum);
		for(int i = 0;i<20;i++)
		{
			if(states.containsKey(i))
			{
				NBTTagCompound state = new NBTTagCompound();
				states.get(i).writeToNBT(state);
				nbt.setCompoundTag("css"+i, state);
			}
		}
	}

	@Override
	public void readTransmittable(NBTTagCompound nbt)
	{
		stable			= nbt.getBoolean("stable");
		uncoordinated	= nbt.getBoolean("uncoordinated");
		relativeCoords  = nbt.getBoolean("relativeCoords");
		dayNightControl = nbt.getBoolean("dayNightControl");
		screwMode = nbt.getInteger("screwMode");
		roomDeletePrepare = nbt.getBoolean("rdp");
		tickTimer = nbt.getInteger("tickTimer");
		hasScrewdriver = nbt.getInteger("hasScrewdriver");
		facing    = nbt.getInteger("facing");
		dimControl = nbt.getInteger("dimControl");
		xControls = nbt.getIntArray("xControls");
		zControls = nbt.getIntArray("zControls");
		yControls = nbt.getIntArray("yControls");
		primed    = nbt.getBoolean("primed");
		regulated = nbt.getBoolean("regulated");
		saveCoords = nbt.getBoolean("saveCoords");
		landGroundControl = nbt.getBoolean("landGroundControl");
		unstableControl = nbt.getInteger("unstableControl");
		schemaChooserString = nbt.getString("schemaChooserString");
		lastButton = nbt.getInteger("lastButton");
		lastButtonTT = nbt.getInteger("lastButtonTT");
		clampControls();
	}
	
	@Override
	public void writeTransmittable(NBTTagCompound nbt)
	{
		nbt.setBoolean("stable",stable);
		nbt.setBoolean("uncoordinated",  uncoordinated);
		nbt.setBoolean("relativeCoords", relativeCoords);
		nbt.setBoolean("dayNightControl", dayNightControl);
		nbt.setInteger("screwMode", screwMode);
		nbt.setBoolean("rdp",roomDeletePrepare);
		nbt.setInteger("tickTimer", tickTimer);
		nbt.setInteger("hasScrewdriver", hasScrewdriver);
		nbt.setBoolean("primed", primed);
		nbt.setBoolean("regulated", regulated);
		nbt.setInteger("facing", facing);
		nbt.setInteger("dimControl",dimControl);
		nbt.setIntArray("xControls", xControls);
		nbt.setIntArray("zControls", zControls);
		nbt.setIntArray("yControls", yControls);
		nbt.setBoolean("saveCoords",saveCoords);
		nbt.setBoolean("landGroundControl", landGroundControl);
		nbt.setInteger("unstableControl",unstableControl);
		nbt.setString("schemaChooserString", schemaChooserString);
		nbt.setInteger("lastButton",lastButton);
		nbt.setInteger("lastButtonTT",lastButtonTT);
	}
}
