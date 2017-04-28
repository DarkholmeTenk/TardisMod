package tardis.common.tileents;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import io.darkcraft.darkcore.mod.abstracts.AbstractTileEntity;
import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.DCReflectionHelper;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.MessageHelper;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import io.darkcraft.darkcore.mod.interfaces.IExplodable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.Configs;
import tardis.TardisMod;
import tardis.api.IControlMatrix;
import tardis.api.TardisFunction;
import tardis.api.TardisPermission;
import tardis.common.TMRegistry;
import tardis.common.core.HitPosition;
import tardis.common.core.TardisOutput;
import tardis.common.core.flight.FlightConfiguration;
import tardis.common.core.helpers.Helper;
import tardis.common.core.helpers.ScrewdriverHelper;
import tardis.common.core.helpers.ScrewdriverHelperFactory;
import tardis.common.core.store.ControlStateStore;
import tardis.common.dimension.SaveSlotNamesDataStore;
import tardis.common.dimension.TardisDataStore;
import tardis.common.dimension.damage.ExplosionDamageHelper;
import tardis.common.items.NameTagItem;
import tardis.core.console.panel.ConsolePanel;
import tardis.core.console.panel.group.AbstractPanelGroup;
import tardis.core.console.panel.group.NavGroup;
import tardis.core.console.panel.interfaces.NavPanels;
import tardis.core.console.panel.interfaces.OptionPanels.OptPanelRelativeCoords;
import tardis.core.console.panel.types.normal.NormalPanelX;
import tardis.core.console.panel.types.normal.NormalPanelY;

public class ConsoleTileEntity extends AbstractTileEntity implements IControlMatrix, IExplodable
{
	public static final float					cycleLength				= 80;
	private int									tickTimer;

	private int									facing					= 0;
	private Integer								dc;
	private int									dimControl				= 0;
	private int[]								xControls				= new int[7];
	private int[]								zControls				= new int[7];
	private int[]								yControls				= new int[4];
	private boolean								landGroundControl		= true;
	private boolean								relativeCoords			= false;
	private boolean								uncoordinated			= false;
	private boolean								stable					= false;
	private boolean								landOnPad				= true;
	private boolean								attemptToLand			= false;

	private boolean								saveCoords				= false;
	private HashMap<Integer, ControlStateStore>	states					= new HashMap<>();
	private ControlStateStore					currentLanding;
	private ControlStateStore					lastLanding	;

	private int									rdpCounter				= 0;
	private boolean								roomDeletePrepare		= false;
	private boolean								primed					= false;
	private boolean								regulated				= false;

	private int									lastButton				= -1;
	private int									lastButtonTT			= -1;

	private String[]							schemaList;
	private static String[]						categoryList;

	private int									schemaNum				= 0;
	private int									lastCategoryNum			= -1;
	private int									categoryNum				= 0;
	private ScrewdriverHelper					frontScrewHelper		= ScrewdriverHelperFactory.getNew();
	private ScrewdriverHelper					backScrewHelper;

	public int									unstableControl			= -1;
	private boolean								unstablePressed			= false;

	public String								schemaChooserString		= "";
	public String								schemaCategoryString	= "";
	private float								dimControlState			= 0;
	private LinkedList<Integer>					unstableQueue			= new LinkedList<>();

	private SaveSlotNamesDataStore	ssnds;

	{
		for (int i = 0; i < 7; i++)
		{
			xControls[i] = 0;
			zControls[i] = 0;
			if (i < 4)
				yControls[i] = 0;
		}
		clampControls();
		if(ServerHelper.isServer())
			refillUnstableQueue();
	}

	public ConsoleTileEntity(){}

	public ConsoleTileEntity(World w)
	{
		worldObj = w;
	}

	private boolean importantButton(int button)
	{
		switch(button)
		{
			case 901: return true;
			default: return false;
		}
	}

	@Override
	public void init()
	{
		ssnds = Helper.getSSNDataStore(WorldHelper.getWorldID(this));
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if (((++tickTimer % cycleLength) == 0) && (tickTimer != 0))
			tickTimer = 0;

		if((dc != null) && (Helper.getDataStore(this) != null) && ServerHelper.isServer())
		{
			dimControl = getControlFromDim(dc);
			dc = null;
		}

		if ((lastButtonTT != -1) && (tt > (lastButtonTT + (importantButton(lastButton) ? FlightConfiguration.shiftPressTime : 20))))
		{
			lastButton = -1;
			lastButtonTT = -1;
			sendUpdate();
		}

		if (ServerHelper.isServer())
		{
			if (rdpCounter > 0)
				rdpCounter--;

			if (roomDeletePrepare && (rdpCounter <= 0))
				roomDeletePrepare = false;

			if (schemaList == null)
			{
				refreshSchemas();
			}
			if ((tt % 1200) == 0)
				sendUpdate();
		}
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

	public int getControlFromHit(int blockX, int blockY, int blockZ, Vec3 hit, EntityPlayer pl)
	{
		float i = (float) (hit.xCoord - blockX - xCoord);
		float j = (float) (hit.yCoord - blockY);
		float k = (float) (hit.zCoord - blockZ - zCoord);
		// TardisOutput.print("TConTE", String.format("x: %d, y %d, z %d : %f, %f, %f",blockX,blockY,blockZ,i,j,k));
		HitPosition hitPos = null;
		for (int cnt = 0; (cnt < 4) && (hitPos == null); cnt++)
			hitPos = activateSide(pl, blockX, blockY, blockZ, i, j, k, cnt);
		if (hitPos != null)
		{
			// TardisOutput.print("TConTE", "H:" + hit.toString(),TardisOutput.Priority.DEBUG);
			int controlHit = getControlFromHit(hitPos);
			if (controlHit >= 0)
				return controlHit;
		}
		return -1;
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

	public boolean randomiseControls(CoreTileEntity core)
	{
		for(ConsolePanel panel : getPanels())
			if(panel instanceof NavPanels)
				((NavPanels)panel).randomizeDestination();
		return true;
	}

	public boolean activate(EntityPlayer pl, int blockX, int blockY, int blockZ, float i, float j, float k)
	{
		if (ServerHelper.isServer())
			return true;
		HitPosition hit = null;
		for (int cnt = 0; (cnt < 4) && (hit == null); cnt++)
			hit = activateSide(pl, blockX, blockY, blockZ, i, j, k, cnt);
		if (hit != null)
		{
			// int controlHit = -1;
			int controlHit = getControlFromHit(hit);
			if (controlHit >= 0)
				Helper.activateControl(this, pl, controlHit);
			else
				TardisOutput.print("TConTE", "H:" + hit.toString(), TardisOutput.Priority.INFO);
		}
		else
			TardisOutput.print("TConTE", "No hit");
		return true;
	}

	public boolean isMovementControl(int controlID)
	{
		if ((controlID >= 10) && (controlID < 40))
			return true;
		switch(controlID)
		{
			case 3:
			case 53:
			case 55:
			case 43:
			case 60: return true;
			default: return false;
		}
	}

	private boolean requiresFlightPermission(int controlID)
	{
		if(controlID == unstableControl) return false;
		if((controlID >= 1000) && (controlID < 1020)) return true;
		switch(controlID)
		{
			case 904:
			case 42 :
			case 55 :
			case 53 :
			case 56 :
			case 900:
			case 902:
			case 903:
			case 4  :
			case 40 :
			case 41 :
			case 34 : return true;
			default: return false;
		}
	}

	@Override
	public void activateControl(EntityPlayer pl, int controlID)
	{
		CoreTileEntity core = Helper.getTardisCore(worldObj);
		TardisDataStore ds = Helper.getDataStore(worldObj);
		if ((core == null) || (ds == null))
			return;
		if((isMovementControl(controlID) || requiresFlightPermission(controlID)) && !ds.hasPermission(pl, TardisPermission.FLY))
		{
			pl.addChatMessage(CoreTileEntity.cannotModifyFly);
			return;
		}
		TardisOutput.print("TConTE", "Control:" + controlID, TardisOutput.Priority.DEBUG);
		if (controlID == 0)
			pl.addChatMessage(new ChatComponentText("Energy: " + core.getArtronEnergy() + "/" + core.getMaxArtronEnergy()));
		else if (controlID == 1)
			pl.addChatMessage(new ChatComponentText("Rooms: " + core.getNumRooms() + "/" + core.getMaxNumRooms()));
		else if (controlID == 2)
			pl.addChatMessage(new ChatComponentText(String.format("Speed: %.1f/%.1f", core.getSpeed(true), core.getMaxSpeed())));
		else if (controlID == 8)
		{
			pl.addChatMessage(new ChatComponentText("XP: " + ds.getXP() + "/" + ds.getXPNeeded()));
			pl.addChatMessage(new ChatComponentText("Level:" + ds.getLevel()));
		}
		else if (controlID == 9)
			pl.addChatMessage(new ChatComponentText("Shields: " + ds.damage.getShields() + "/" + ds.damage.getMaxShields()));
		else if (controlID == 4) // Speed lever
		{
			int d = 1;
			if (pl.isSneaking())
				d = -1;
			core.addSpeed(d);
		}
		else if (controlID == 100) // Scanner
			core.sendDestinationStrings(pl);
		else if (controlID == 904) // Land on pad
			landOnPad = !landOnPad;
		else if (core.inAbortableFlight() && (controlID == 42))
			attemptToLand = core.attemptToLand();
		else if (controlID == 55)
			uncoordinated = !uncoordinated;
		else if (!core.inCoordinatedFlight())
		{
			if (isMovementControl(controlID) && (!core.inFlight() || !core.inCoordinatedFlight()))
			{
				if (!core.inFlight())
				{
					primed = false;
					regulated = false;
				}
				if (controlID == 3)
					facing = MathHelper.cycle(facing + (pl.isSneaking() ? -1 : 1), 0, 3);
				else if (((controlID >= 10) && (controlID < 14)) || (controlID == 16))
				{
					if (pl.isSneaking())
						xControls[controlID - 10]--;
					else
						xControls[controlID - 10]++;
					clampControls(xControls);
				}
				else if ((controlID >= 14) && (controlID < 16))
				{
					if (pl.isSneaking())
						xControls[controlID - 10] = MathHelper.cycle(xControls[controlID - 10] - 1, 0, 7);
					else
						xControls[controlID - 10] = MathHelper.cycle(xControls[controlID - 10] + 1, 0, 7);
					clampControls(xControls);
				}
				else if (((controlID >= 20) && (controlID < 24)) || (controlID == 26))
				{
					if (pl.isSneaking())
						zControls[controlID - 20]--;
					else
						zControls[controlID - 20]++;
					clampControls(zControls);
				}
				else if ((controlID >= 24) && (controlID < 26))
				{
					if (pl.isSneaking())
						zControls[controlID - 20] = MathHelper.cycle(zControls[controlID - 20] - 1, 0, 7);
					else
						zControls[controlID - 20] = MathHelper.cycle(zControls[controlID - 20] + 1, 0, 7);
					clampControls(zControls);
				}
				else if ((controlID >= 30) && (controlID < 34))
				{
					if (pl.isSneaking())
						yControls[controlID - 30]--;
					else
						yControls[controlID - 30]++;
					clampControls(yControls);
				}
				else if (controlID == 34)
					landGroundControl = !landGroundControl;
				else if (controlID == 53)
					relativeCoords = !relativeCoords;
				else if ((controlID == 60) && !core.inFlight())
				{
					int newDimControl = dimControl + (pl.isSneaking() ? -1 : 1);
					newDimControl = MathHelper.clamp(newDimControl, 0, getNumDims() - 1);
					dimControl = newDimControl;
				}
				else if (controlID ==43)
					randomiseControls(core);
			}
			else if (!core.inFlight())
			{
				if (controlID == 40)
					primed = true;
				else if ((controlID == 41) && primed)
					regulated = true;
				else if ((controlID == 42) && primed && regulated)
					core.takeOff(pl);
			}
		}

		if (controlID == 5)
		{
			if (ds.hasPermission(pl, TardisPermission.PERMISSIONS))
			{
				if ((frontScrewHelper == null) && core.takeArtronEnergy(500, false))
				{
					core.sendUpdate();
					frontScrewHelper = ScrewdriverHelperFactory.getNew();
				}
				else if ((frontScrewHelper != null) && core.addArtronEnergy(400, false))
				{
					core.sendUpdate();
					ScrewdriverHelperFactory.destroy(frontScrewHelper);
					frontScrewHelper = null;
				}
			}
			else
				ServerHelper.sendString(pl, CoreTileEntity.cannotModifyMessage);
		}
		else if ((controlID == 6) || (controlID == 7)) // Screwdriver slot 0/1
		{
			int slot = controlID == 6 ? 0 : 1;
			ScrewdriverHelper helper = getScrewHelper(slot);
			if (helper != null)
			{
				if(slot == 0) frontScrewHelper = null;
				if(slot == 1) backScrewHelper = null;
				helper.setOwner(core.getOwner());
				helper.setSchema(schemaCategoryString, schemaChooserString);
				ItemStack toGive = helper.getItemStack();
				TMRegistry.screwItem.notifyMode(helper, pl, false);
				WorldHelper.giveItemStack(pl, toGive);
			}
			else
			{
				ScrewdriverHelper newHelper = ScrewdriverHelperFactory.get(pl.getHeldItem());
				if (newHelper != null)
				{
					if(slot == 0) frontScrewHelper = newHelper;
					if(slot == 1) backScrewHelper = newHelper;
					newHelper.clear();
					InventoryPlayer inv = pl.inventory;
					inv.mainInventory[inv.currentItem] = null;
				}
			}
		}
		else if ((controlID == 50) || (controlID == 51)) // Schema change
		{
			TardisOutput.print("TConTE", "Cycling schema");
			schemaNum += (controlID == 50 ? -1 : 1);
			refreshSchemas();
		}
		else if ((controlID == 57) || (controlID == 58))
		{
			TardisOutput.print("TConTE", "Cycling schema");
			categoryNum += (controlID == 57 ? -1 : 1);
			refreshSchemas();
		}
		else if (controlID == 52)
			ds.setDaytimeSetting(((ds.getDaytimeSetting())+1) % 3);
		else if ((controlID == 54) && core.hasFunction(TardisFunction.SENSORS))
			core.sendScannerStrings(pl);
		else if ((controlID == 56) && core.hasFunction(TardisFunction.STABILISE))
		{
			stable = !stable;
			if (stable)
				clearUnstableControl();
		}
		else if (controlID == 900)
			saveCoords = !saveCoords;
		else if ((controlID == 902) || (controlID == 903))
		{
			if (!core.inFlight())
			{
				ControlStateStore toLoad = controlID == 902 ? lastLanding : currentLanding;
				loadControls(toLoad);
				primed = false;
				regulated = false;
			}
		}
		else if (controlID >= 1000) // Flight instabilitiers
		{
			if ((controlID >= 1000) && (controlID <= 1032))
			{
				lastButton = controlID;
				lastButtonTT = tickTimer;
			}
			boolean run = true;
			if((controlID >= 1000) && (controlID < 1020))
			{
				ItemStack is = pl.getHeldItem();
				if((is != null) && ((is.getItem() instanceof ItemNameTag) || (is.getItem() instanceof NameTagItem)) && (ssnds != null))
				{
					if(is.hasDisplayName())
					{
						String n = is.getDisplayName();
						if(ssnds.setName(n, controlID-1000))
						{
							MessageHelper.sendMessage(pl, "Bookmark slot " + (controlID -1000) + " renamed to " + n);
							run = false;
							if(!pl.capabilities.isCreativeMode)
								is.stackSize--;
						}
					}
				}
				if(run)
					ssnds.sendUpdate();
				if ((run == true) && !core.inFlight())
				{
					int num = controlID - 1000;
					if (saveCoords)
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
			}
			if (run && core.inFlight())
			{
				if (unstableControl == controlID)
					pressedUnstable();
				else
					TardisOutput.print("TConTE", "Stable button pressed:" + controlID + ":" + unstableControl);
			}
		}

		if ((controlID == 901) && ((Configs.deleteAllOwnerOnly && core.isOwner(pl)) || ds.hasPermission(pl, TardisPermission.ROOMS)))
		{
			if (!roomDeletePrepare)
			{
				if (!pl.isSneaking())
				{
					ChatComponentText c = new ChatComponentText("");
					c.getChatStyle().setColor(EnumChatFormatting.DARK_RED);
					c.appendText("[TARDIS]Sneak-right click this button again to activate deletion protocol");
					pl.addChatMessage(c);
					rdpCounter = 40;
					roomDeletePrepare = true;
				}
			}
			else
			{
				if (pl.isSneaking())
					core.removeAllRooms();
				roomDeletePrepare = false;
			}
		}
		else
		{
			if ((controlID == 901) && !ds.hasPermission(pl, TardisPermission.ROOMS))
				pl.addChatMessage(CoreTileEntity.cannotModifyMessage);
			if (roomDeletePrepare)
				roomDeletePrepare = false;
		}
		lastButton = controlID;
		lastButtonTT = tt;
		markDirty();
		core.sendUpdate();
	}

	public boolean setControls(int dim, int exX, int exZ, int x, int y, int z, boolean allowNearest)
	{
		int dCont = getControlFromDim(dim);
		int[] xCont = getControlsFromDest(x - exX);
		int[] yCont = getYControls(y);
		int[] zCont = getControlsFromDest(z - exZ);
		if ((allowNearest || ((getFromControls(xCont) == (x-exX)) && (getFromControls(zCont) == (z-exZ))))
				&& (getDimFromControl(dCont) == dim))
		{
			relativeCoords = true;
			dimControl = dCont;
			xControls = xCont;
			yControls = yCont;
			zControls = zCont;
			sendUpdate();
			return true;
		}
		return false;
	}

	public boolean setControls(int dim, int x, int y, int z, boolean allowNearest)
	{
		TardisDataStore ds = Helper.getDataStore(this);
		if(ds == null)
			return false;
		int dCont = getControlFromDim(dim);
		int[] xCont = getControlsFromDest(x);
		int[] yCont = getYControls(y);
		int[] zCont = getControlsFromDest(z);
		boolean set = false;
		if ((allowNearest || ((getFromControls(xCont) == x) && (getFromControls(zCont) == z)))
				&& (getDimFromControl(dCont) == dim))
		{
			relativeCoords = false;
			dimControl = dCont;
			xControls = xCont;
			yControls = yCont;
			zControls = zCont;
			sendUpdate();
			set = true;
		}
		if (!set)
		{
			TardisTileEntity e = ds.getExterior();
			if (e != null)
			{
				return setControls(dim, e.xCoord, e.zCoord, x, y, z, allowNearest);
			}
		}
		return set;
	}

	public boolean setControls(TardisTileEntity ext, boolean allowNearest)
	{
		int dim = WorldHelper.getWorldID(ext.getWorldObj());
		int xC = ext.xCoord;
		int yC = ext.yCoord;
		int zC = ext.zCoord;
		TardisOutput.print("TConTE", "Attempting to set controls to :" + dim + "," + xC + "," + yC + "," + zC,
				TardisOutput.Priority.DEBUG);
		return setControls(dim, xC, yC, zC, allowNearest);
	}

	public void setDesiredDim(int exteriorWorld)
	{
		dimControl = getControlFromDim(exteriorWorld);
	}

	private static int[] getYControls(int height)
	{
		int[] temp = new int[4];
		temp[3] = height & 3;
		temp[2] = (height & 12) >> 2;
		temp[1] = (height & 48) >> 4;
		temp[0] = (height & 192) >> 6;
		return temp;
	}

	private static int[] getControlsFromDest(int dest)
	{
		int[] temp = new int[7];
		int c = 0;
		for (int i = -6; i <= 6; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				int val = i * (int) Math.pow(4, j + 1);
				if (Math.abs(dest - val) < Math.abs(dest - c))
				{
					temp[0] = i;
					temp[4] = j;
					c = val;
				}
			}
		}

		int d = c;
		for (int i = -6; i <= 6; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				int val = d + (i * (int) Math.pow(2, j + 1));
				if (Math.abs(dest - val) < Math.abs(dest - c))
				{
					temp[1] = i;
					temp[5] = j;
					c = val;
				}
			}
		}
		temp[6] = MathHelper.clamp((dest - c) / 71, -6, 6);
		c += (71 * temp[6]);
		temp[2] = MathHelper.clamp((dest - c) / 6, -6, 6);
		c += (6 * temp[2]);
		temp[3] = MathHelper.clamp(dest - c, -6, 6);
		c += temp[3];
		if (c == dest)
			TardisOutput
					.print("TConTE", "Found coords for " + dest + ": " + Arrays.toString(temp), TardisOutput.Priority.DEBUG);
		else
			TardisOutput.print("TConTE", "Found nearest coords for " + dest + ": " + c + ": " + Arrays.toString(temp),
					TardisOutput.Priority.DEBUG);
		return temp;
	}

	private static int getFromControls(int[] control)
	{
		int controlOne = control[0] * (int) Math.pow(4, control[4] + 1);
		int controlTwo = control[1] * (int) Math.pow(2, control[5] + 1);
		int controlThree = (71 * control[6]) + (control[2] * 6) + control[3];
		return controlOne + controlTwo + controlThree;
	}

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

	public int getDimFromControls()
	{
		return getDimFromControl(dimControl);
	}

	private int getDimFromControl(int dCont)
	{
		TardisDataStore ds = Helper.getDataStore(this);
		if((ds != null) && (TardisMod.otherDims != null))
		{
			Integer i = TardisMod.otherDims.getDimFromControl(dCont,ds.getLevel(),ds);
			if(i != null)
				return i;
		}
		return 0;
	}

	public int getControlFromDim(int dim)
	{
		TardisDataStore ds = Helper.getDataStore(this);
		if(ds != null)
			return TardisMod.otherDims.getControlFromDim(dim, ds.getLevel(),ds);
		return 0;
	}

	public int getNumDims()
	{
		TardisDataStore ds = Helper.getDataStore(this);
		if(ds == null)
			return TardisMod.otherDims.numDims();
		else
			return TardisMod.otherDims.numDims(ds.getLevel(),ds);

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
		if (cont.length != 4)
			return 0;
		int count = cont[0];
		count = (count << 2) + cont[1];
		count = (count << 2) + cont[2];
		count = (count << 2) + cont[3];
		TardisOutput.print("TConTE", "YCont:" + cont[0] + "," + cont[1] + "," + cont[2] + "," + cont[3] + "=" + count,
				TardisOutput.Priority.OLDDEBUG);
		return count;
	}

	public int getYFromControls(int extY)
	{
		return getYFromControls(yControls);
	}

	public SimpleCoordStore getCoordsFromControls(TardisTileEntity ext)
	{
		int w = getDimFromControls();
		int x = getXFromControls(ext.xCoord);
		int y = getYFromControls(ext.yCoord);
		int z = getZFromControls(ext.zCoord);
		return new SimpleCoordStore(w, x, y, z);
	}

	public SimpleCoordStore getCoordsFromControls(SimpleCoordStore ext)
	{
		int w = getDimFromControls();
		int x = getXFromControls(ext.x);
		int y = getYFromControls(ext.y);
		int z = getZFromControls(ext.z);
		return new SimpleCoordStore(w, x, y, z);
	}

	public boolean getLandOnGroundFromControls()
	{
		return landGroundControl;
	}

	public boolean getLandOnPadFromControls()
	{
		return landOnPad;
	}

	public int getFacingFromControls()
	{
		return facing;
	}

	private void loadControls(int num)
	{
		TardisOutput.print("TConTE", "Attempting to load state " + num);
		if (states.containsKey(num))
			loadControls(states.get(num));
	}

	public void loadControls(ControlStateStore state)
	{
		if (state == null)
			return;
		if (state.isValid())
		{
			TardisOutput.print("TConTE", "Loading state");
			facing = state.facing;
			dimControl = getControlFromDim(state.dimControl);
			xControls = state.xControls.clone();
			yControls = state.yControls.clone();
			zControls = state.zControls.clone();
			landGroundControl = state.landGroundControl;
			relativeCoords = state.relative;
			clampControls();
			sendUpdate();
		}
	}

	private ControlStateStore getCurrentControlState()
	{
		return new ControlStateStore(facing, getDimFromControls(), xControls, yControls, zControls, landGroundControl,
				relativeCoords);
	}

	public void saveControls(int stateNum)
	{
		TardisOutput.print("TConTE", "Saving state to num:" + stateNum);
		ControlStateStore s = getCurrentControlState();
		states.put(stateNum, s);
	}

	private void clampControls()
	{
		if ((xControls == null) || (xControls.length != 7))
			xControls = ControlStateStore.fixControls(xControls);
		if ((yControls == null) || (yControls.length != 4))
			yControls = new int[4];
		if ((zControls == null) || (zControls.length != 7))
			zControls = ControlStateStore.fixControls(zControls);
		clampControls(xControls);
		clampControls(yControls);
		clampControls(zControls);
	}

	private void clampControls(int[] controls)
	{
		if (controls.length == 7)
		{
			controls[0] = MathHelper.clamp(controls[0], -6, 6);
			controls[1] = MathHelper.clamp(controls[1], -6, 6);
			controls[2] = MathHelper.clamp(controls[2], -6, 6);
			controls[3] = MathHelper.clamp(controls[3], -6, 6);
			controls[4] = MathHelper.clamp(controls[4], 0, 7);
			controls[5] = MathHelper.clamp(controls[5], 0, 7);
			controls[6] = MathHelper.clamp(controls[6], -6, 6);
		}
		else if (controls.length == 4)
		{
			controls[0] = MathHelper.clamp(controls[0], 0, 3);
			controls[1] = MathHelper.clamp(controls[1], 0, 3);
			controls[2] = MathHelper.clamp(controls[2], 0, 3);
			controls[3] = MathHelper.clamp(controls[3], 0, 3);
		}
	}

	@Override
	public double getControlState(int controlID, boolean wobble)
	{
		double maxWobble = 0.025;
		double count = 20;
		int maxRand = 10;
		double wobbleAmount = 0;
		if (wobble)
		{
			wobbleAmount = (((tickTimer + rand.nextInt(maxRand)) % count) / count);
			wobbleAmount = Math.abs(wobbleAmount - 0.5) * maxWobble * 2;
		}
		return getControlState(controlID) + wobbleAmount;
	}

	@Override
	public double getControlState(int controlID)
	{
		CoreTileEntity core = Helper.getTardisCore(worldObj);
		TardisDataStore ds = Helper.getDataStore(worldObj);
		if ((core != null) && (ds != null))
		{
			//System.out.println("LB:"+lastButton + ":" + controlID);
			if (controlID == 0) // Artron energy
				return ((double) core.getArtronEnergy() / core.getMaxArtronEnergy());
			if (controlID == 1) // Rooms gauge
				return ((double) core.getNumRooms() / core.getMaxNumRooms());
			if ((controlID == 2) || (controlID == 4)) // Speed
				return MathHelper.clamp(core.getSpeed(controlID == 2) / core.getMaxSpeed(), 0, 1);
			if (controlID == 3) // Facing
				return facing / 4.0;
			if (controlID == 8) // XP
				return (ds.getXP() / ds.getXPNeeded());
			if (controlID == 9) // Shields
				return (((double) ds.damage.getShields()) / ds.damage.getMaxShields());
			if (((controlID >= 10) && (controlID < 14)) || (controlID == 16))
				return ((double) (xControls[controlID - 10] + 6) / 12);
			if ((controlID >= 14) && (controlID < 16))
				return xControls[controlID - 10] / 8.0;
			if (((controlID >= 20) && (controlID < 24)) || (controlID == 26))
				return ((double) (zControls[controlID - 20] + 6) / 12);
			if ((controlID >= 24) && (controlID < 26))
				return zControls[controlID - 20] / 8.0;
			if ((controlID >= 30) && (controlID < 34))
				return yControls[controlID - 30] / 3.0;
			if (controlID == 34)
				return (landGroundControl ? 1 : 0);
			if (controlID == 40)
				return (primed ? 1 : 0);
			if (controlID == 41)
				return (regulated ? 1 : 0);
			if (controlID == 42)
				return ((!attemptToLand) && core.inFlight()) ? 1 : 0;
			if ((controlID == 50) || (controlID == 51) || (controlID == 5) || (controlID == 902) || (controlID == 903) || (controlID == 57)
					|| (controlID == 58) || (controlID == 43))
				return lastButton == controlID ? 1 : 0;
			if (controlID == 52)
				return ds.getDaytimeSetting() / 2.0;
			if (controlID == 53)
				return relativeCoords ? 1 : 0;
			if (controlID == 55)
				return uncoordinated ? 1 : 0;
			if (controlID == 56)
				return stable ? 1 : 0;
			if (controlID == 60)
				return dimControlState;
			if (controlID == 900)
				return saveCoords ? 1 : 0;
			if (controlID == 901)
				return roomDeletePrepare ? 1 : 0;
			if (controlID == 904)
				return landOnPad ? 1 : 0;
			if ((controlID >= 1000) && (controlID < 1033))
				return lastButton == controlID ? 1 : 0;
			return (((tickTimer + (controlID * 20)) % cycleLength) / cycleLength);
		}
		return 0;
	}

	@SideOnly(Side.CLIENT)
	public String[] getExtraInfo(int controlID)
	{
		CoreTileEntity core = Helper.getTardisCore(this);
		TardisDataStore ds = Helper.getDataStore(worldObj);
		if ((core != null) && (ds != null))
		{
			if (controlID == 0)
				return new String[] { "Energy: " + core.getArtronEnergy() + "/" + core.getMaxArtronEnergy() };
			else if (controlID == 1)
				return new String[] { "Rooms: " + core.getNumRooms() + "/" + core.getMaxNumRooms() };
			else if (controlID == 2)
				return new String[] { String.format("Speed: %.1f/%.1f", core.getSpeed(true), core.getMaxSpeed()) };
			else if (controlID == 8)
			{
				return new String[] { "XP:     " + ds.getXP() + "/" + ds.getXPNeeded(), "Level: " + ds.getLevel() };
			}
			else if (controlID == 9)
				return new String[] { "Shields: " + ds.damage.getShields() + "/" + ds.damage.getMaxShields() };
			if ((controlID >= 10) && (controlID < 17))
				return new String[] { "Set to " + xControls[controlID - 10] };
			if ((controlID >= 20) && (controlID < 27))
				return new String[] { "Set to " + zControls[controlID - 20] };
			if ((controlID >= 30) && (controlID < 34))
				return new String[] { "Set to " + yControls[controlID - 30] };

			if (controlID == 900)
				return new String[] { "Current: " + (saveCoords ? "Save" : "Load") };
			if (controlID == 904)
				return new String[] { "Current: " + (landOnPad ? "Use landing pads" : "Ignore landing pads") };
			if (controlID == 34)
				return new String[] { "Current: " + (landGroundControl ? "Land on ground" : "Land in midair") };
			if (controlID == 56)
				return new String[] { "Current: " + (stable ? "Stable flight" : "Unstable flight")};
			if (controlID == 52)
			{
				switch(ds.getDaytimeSetting()){
				case 0:
					return new String[] { "Current: Nighttime"};

				case 1:
					return new String[] { "Currently: Simulating Overworld"};

				case 2:
					return new String[] { "Current: Daytime"};
				}
			}
			if (controlID == 55)
				return new String[] { "Current: "
						+ (uncoordinated ? "Uncoordinated flight (Drifting)" : "Coordinated flight") };
			if (controlID == 53)
				return new String[] { "Current: " + (relativeCoords ? "Relative coordinates" : "Absolute coordinates") };
			if ((controlID >= 1000) && (controlID < 1020) && (ssnds != null))
			{
				int slot = controlID - 1000;
				String name = ssnds.getName(slot);
				if(name != null)
					return new String[] {"Name: " + name};
			}
		}
		return null;
	}

	private static double[]	defaultColors	= { 1, 1, 1 };
	private static double[]	flightColors	= { 0.8, 0.9, 1 };

	@Override
	public double[] getColorRatio(int controlID)
	{
		if (controlID >= 1020)
			return flightColors;
		if ((controlID >= 1000) && ((controlID % 2) == 0))
			return flightColors;
		return defaultColors;
	}

	@Override
	public double getControlHighlight(int controlID)
	{
		CoreTileEntity core = Helper.getTardisCore(worldObj);
		double highlightAmount = (Math.abs((tickTimer % 40) - 20) / 40.0) + 0.5;
		if ((controlID == unstableControl) && !unstablePressed && (core != null) && core.inFlight())
			return highlightAmount;

		if ((controlID == 901) && roomDeletePrepare)
			return highlightAmount;
		return -1;
	}

	private void pressedUnstable()
	{
		unstableControl = -1;
		unstablePressed = true;
	}

	public boolean unstableFlight()
	{
		return !stable;
	}

	public boolean isStable()
	{
		return stable;
	}

	private static final int minUnstable = 1010;
	private static final int maxUnstable = 1032;
	private void refillUnstableQueue()
	{
		int size = unstableQueue.size();
		while(size < 8)
		{
			int ran = 0;
			if (minUnstable != maxUnstable)
				ran = rand.nextInt((1 + maxUnstable) - minUnstable);
			if (ran < 10)
				ran = (ran * 2) - 20;
			int newControl = minUnstable + ran;
			if((size == 0) || (unstableQueue.getLast() != newControl))
			{
				unstableQueue.add(newControl);
				size++;
			}
		}
	}

	public void getNextUnstableControl()
	{
		if(ServerHelper.isServer())
			if(unstableQueue.size() < 3) refillUnstableQueue();
		if(ServerHelper.isClient())
			unstableControl = unstableQueue.size() > 0 ? unstableQueue.remove() : minUnstable;
		else
			unstableControl = unstableQueue.remove();
		unstablePressed = false;
		sendUpdate();
	}

	public void clearUnstableControl()
	{
		unstableControl = -1;
	}

	public boolean unstableControlPressed()
	{
		return unstablePressed || (unstableControl == -1);
	}

	public void land()
	{
		if(ServerHelper.isClient())return;
		attemptToLand = false;
		lastLanding = currentLanding;
		currentLanding = getCurrentControlState();
		primed = false;
		regulated = false;
		sendUpdate();
	}

	public void refreshSchemas()
	{
		if (categoryNum != lastCategoryNum)
		{
			if ((categoryList == null) || (categoryList.length == 0))
				refreshCategories();
			categoryNum = MathHelper.cycle(categoryNum, 0, categoryList.length - 1);
			lastCategoryNum = categoryNum;
			String category = categoryList[categoryNum];
			schemaCategoryString = categoryList[categoryNum];
			schemaList = TardisMod.schemaHandler.getSchemas(category);
		}
		schemaNum = MathHelper.cycle(schemaNum, 0, schemaList.length - 1);
		schemaChooserString = schemaList[schemaNum];
	}

	public static void refreshCategories()
	{
		categoryList = TardisMod.schemaHandler.getSchemaCategories();
	}

	public boolean getRelativeCoords()
	{
		return getPanel(OptPanelRelativeCoords.class).map(p->p.areCoordinatesRelative()).orElse(false);
	}

	@Override
	public ScrewdriverHelper getScrewHelper(int slot)
	{
		switch(slot)
		{
			case 0: return frontScrewHelper;
			case 1: return backScrewHelper;
			default: return null;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		schemaNum = nbt.getInteger("schemaNum");
		dc = nbt.getInteger("dC");
		for (int i = 0; i < 20; i++)
		{
			if (nbt.hasKey("css" + i))
				states.put(i, ControlStateStore.readFromNBT(nbt.getCompoundTag("css" + i)));
		}
		if (nbt.hasKey("lastLandingCSS"))
			lastLanding = ControlStateStore.readFromNBT(nbt.getCompoundTag("lastLandingCSS"));
		if (nbt.hasKey("currentLandingCSS"))
			currentLanding = ControlStateStore.readFromNBT(nbt.getCompoundTag("currentLandingCSS"));
		clampControls();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setInteger("schemaNum", schemaNum);
		int dimID = getDimFromControls();
		nbt.setInteger("dC", dimID);

		for (int i = 0; i < 20; i++)
		{
			if (states.containsKey(i))
			{
				NBTTagCompound state = new NBTTagCompound();
				states.get(i).writeToNBT(state);
				nbt.setTag("css" + i, state);
			}
		}
		if (lastLanding != null)
		{
			NBTTagCompound lT = new NBTTagCompound();
			lastLanding.writeToNBT(lT);
			nbt.setTag("lastLandingCSS", lT);
		}
		if (currentLanding != null)
		{
			NBTTagCompound lT = new NBTTagCompound();
			currentLanding.writeToNBT(lT);
			nbt.setTag("currentLandingCSS", lT);
		}
	}

	@Override
	public void readTransmittable(NBTTagCompound nbt)
	{
		stable = nbt.getBoolean("stable");
		uncoordinated = nbt.getBoolean("uncoordinated");
		relativeCoords = nbt.getBoolean("relativeCoords");
		roomDeletePrepare = nbt.getBoolean("rdp");
		tickTimer = nbt.getInteger("tickTimer");
		frontScrewHelper = ScrewdriverHelperFactory.get(nbt, "scNBT");
		backScrewHelper = ScrewdriverHelperFactory.get(nbt, "bscNBT");
		facing = nbt.getInteger("facing");
		xControls = nbt.getIntArray("xControls");
		zControls = nbt.getIntArray("zControls");
		yControls = nbt.getIntArray("yControls");
		primed = nbt.getBoolean("primed");
		regulated = nbt.getBoolean("regulated");
		saveCoords = nbt.getBoolean("saveCoords");
		landGroundControl = nbt.getBoolean("landGroundControl");
		{
			int newUnstable = nbt.getInteger("unstableControl");
			if(newUnstable != unstableControl)
			{
				if((unstableQueue.size() > 0) && (newUnstable == unstableQueue.peek()))
					unstableQueue.remove();
				else
					System.out.println("NewUnstable:"+ newUnstable + " vs OldUnstable:"+unstableControl + " mismatch");
			}
			unstableControl = newUnstable;
		}
		landOnPad = nbt.getBoolean("lOP");
		clampControls();
	}

	@Override
	public void writeTransmittable(NBTTagCompound nbt)
	{
		nbt.setBoolean("stable", stable);
		nbt.setBoolean("uncoordinated", uncoordinated);
		nbt.setBoolean("relativeCoords", relativeCoords);
		if(frontScrewHelper != null) frontScrewHelper.writeToNBT(nbt, "scNBT");
		if(backScrewHelper != null) backScrewHelper.writeToNBT(nbt, "bscNBT");
		nbt.setBoolean("rdp", roomDeletePrepare);
		nbt.setInteger("tickTimer", tickTimer);
		nbt.setBoolean("primed", primed);
		nbt.setBoolean("regulated", regulated);
		nbt.setInteger("facing", facing);
		nbt.setIntArray("xControls", xControls);
		nbt.setIntArray("zControls", zControls);
		nbt.setIntArray("yControls", yControls);
		nbt.setBoolean("saveCoords", saveCoords);
		nbt.setBoolean("landGroundControl", landGroundControl);
		nbt.setInteger("unstableControl", unstableControl);
		nbt.setBoolean("lOP", landOnPad);
	}

	@Override
	public void writeTransmittableOnly(NBTTagCompound nbt)
	{
		nbt.setString("schemaCategoryString", schemaCategoryString);
		nbt.setString("schemaChooserString", schemaChooserString);
		nbt.setFloat("dCS", (dimControl) / (getNumDims() - 1f));
		nbt.setInteger("lastButton", lastButton);
		nbt.setInteger("lastButtonTT", lastButtonTT);
		nbt.setBoolean("attemptToLand", attemptToLand);
		if(ServerHelper.isServer())
		{
			nbt.setInteger("unstableQueue0", unstableControl);
			int i = unstableControl == -1 ? 0 : 1;
			for(Integer unst : unstableQueue)
				nbt.setInteger("unstableQueue"+(i++), unst);
		}
	}

	@Override
	public void readTransmittableOnly(NBTTagCompound nbt)
	{
		schemaCategoryString = nbt.getString("schemaCategoryString");
		schemaChooserString = nbt.getString("schemaChooserString");
		dimControlState = nbt.getFloat("dCS");
		lastButton = nbt.getInteger("lastButton");
		lastButtonTT = nbt.getInteger("lastButtonTT");
		if(lastButton == unstableControl)
			pressedUnstable();
		attemptToLand = nbt.getBoolean("attemptToLand");
		if(nbt.hasKey("unstableQueue0"))
			unstableQueue.clear();
		for(int i = 0; nbt.hasKey("unstableQueue"+i); i++)
			unstableQueue.add(nbt.getInteger("unstableQueue"+i));
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

	public boolean shouldLand()
	{
		return true; //TODO: Remove
	}
}
