package tardis.common.tileents;

import io.darkcraft.darkcore.mod.abstracts.AbstractTileEntity;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import tardis.TardisMod;
import tardis.api.IControlMatrix;
import tardis.api.ScrewdriverMode;
import tardis.api.TardisPermission;
import tardis.api.TardisUpgradeMode;
import tardis.common.core.Helper;
import tardis.common.core.HitPosition;
import tardis.common.core.TardisOutput;
import tardis.common.dimension.TardisDataStore;
import tardis.common.items.SonicScrewdriverItem;

public class EngineTileEntity extends AbstractTileEntity implements IControlMatrix
{
	private String[]			currentUsers;
	private int					currentUserID;
	public String				currentPerson;

	public int					lastButton				= -1;
	public int					lastButtonTT			= -1;
	private TardisUpgradeMode	preparingToUpgrade		= null;
	private int					preparingToUpgradeTT	= -1;

	private boolean				internalOnly			= false;

	private boolean				hasScrew				= false;
	private NBTTagCompound		screwNBT				= null;
	private int					screwMode				= 0;

	private int					consoleSettingControl	= 0;
	private String				consoleSettingString	= "Main";	// The string displayed on the console room selection screen.
	private static String[]		availableConsoleRooms	= null;

	public static void updateConsoleRooms()
	{
		availableConsoleRooms = TardisMod.schemaHandler.getSchemas(true);
	}

	private boolean importantButton(int button)
	{
		switch(button)
		{
			case 10:
			case 11:
			case 12:
			case 13:
			case 73: return true;
			default: return false;
		}
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if (((tt % 40) == 1) && ServerHelper.isServer())
		{
			if (availableConsoleRooms == null)
				refreshAvailableConsoleRooms();
			verifyEngineBlocks();
			getUsernames();
		}

		if ((lastButtonTT != -1) && (tt > (lastButtonTT + (importantButton(lastButton) ? TardisMod.shiftPressTime : 20))))
		{
			lastButton = -1;
			lastButtonTT = -1;
		}

		if ((preparingToUpgrade != null) && (tt > (preparingToUpgradeTT + 80)))
		{
			preparingToUpgrade = null;
			preparingToUpgradeTT = -1;
			if (ServerHelper.isServer())
				sendUpdate();
		}
	}

	private void getUsernames()
	{
		currentUsers = new String[0];
		ArrayList<String> users = new ArrayList<String>();
		List plList = worldObj.playerEntities;
		if ((plList != null) && (plList.size() > 0))
		{
			for (Object o : plList)
			{
				if (o instanceof EntityPlayer)
					users.add(((EntityPlayer) o).getCommandSenderName());
			}
			Collections.sort(users, String.CASE_INSENSITIVE_ORDER);
			currentUsers = users.toArray(currentUsers);
		}
		setUsername();
	}

	private void setUsername()
	{
		if ((currentUsers != null) && (currentUsers.length > 0))
		{
			currentUserID = MathHelper.cycle(currentUserID, 0, currentUsers.length - 1);
			currentPerson = currentUsers[currentUserID];
		}
		else
			currentPerson = "";
	}

	private void verifyEngineBlocks()
	{
		if (worldObj.getBlock(xCoord, yCoord - 1, zCoord) == Blocks.air)
			worldObj.setBlock(xCoord, yCoord - 1, zCoord, TardisMod.schemaComponentBlock, 7, 3);
		if (worldObj.getBlock(xCoord, yCoord + 1, zCoord) == Blocks.air)
			worldObj.setBlock(xCoord, yCoord + 1, zCoord, TardisMod.schemaComponentBlock, 7, 3);
		if (worldObj.getBlock(xCoord, yCoord + 2, zCoord) == Blocks.air)
			worldObj.setBlock(xCoord, yCoord + 2, zCoord, TardisMod.schemaComponentBlock, 7, 3);
	}

	public int getControlFromHit(HitPosition hit)
	{
		if (hit.within(2, 2.318, 0.170, 2.432, 0.830))
			return 0;
		else if (hit.within(2, 0.558, 0.685, 0.660, 0.768))
			return 4;
		else if (hit.within(2, 0.488, 0.685, 0.564, 0.768))
			return 5;
		else if (hit.within(2, 0.558, 0.780, 0.660, 0.859))
			return 6;
		else if (hit.within(2, 0.488, 0.780, 0.565, 0.859))
			return 7;
		else if (hit.within(5, 0.393, 0.725, 0.545, 0.876))
			return 10;
		else if (hit.within(5, 0.393, 0.523, 0.545, 0.679))
			return 11;
		else if (hit.within(5, 0.393, 0.326, 0.545, 0.477))
			return 12;
		else if (hit.within(5, 0.393, 0.122, 0.545, 0.276))
			return 13;
		else if (hit.within(5, 0.551, 0.711, 0.704, 0.898))
			return 20;
		else if (hit.within(5, 0.551, 0.510, 0.704, 0.696))
			return 21;
		else if (hit.within(5, 0.551, 0.311, 0.704, 0.492))
			return 22;
		else if (hit.within(5, 0.551, 0.108, 0.704, 0.294))
			return 23;
		else if (hit.within(5, 0.729, 0.711, 0.888, 0.898))
			return 30;
		else if (hit.within(3, 0.42, 0.532, 0.59, 0.664))
			return 39;
		else if (hit.within(3, 0.360, 0.274, 0.440, 0.346))
			return 41;
		else if (hit.within(3, 0.460, 0.274, 0.540, 0.346))
			return 44;
		else if (hit.within(3, 0.560, 0.274, 0.640, 0.346))
			return 45;
		else if (hit.within(3, 0.360, 0.360, 0.440, 0.438))
			return 51;
		else if (hit.within(3, 0.460, 0.360, 0.540, 0.438))
			return 54;
		else if (hit.within(3, 0.560, 0.360, 0.640, 0.438))
			return 55;
		else if (hit.within(3, 0.743, 0.254, 0.876, 0.375))
			return 60;
		else if (hit.within(2, 0.716, 0.036, 0.832, 0.674))
			return 70;
		else if (hit.within(2, 0.783, 0.687, 0.859, 0.769))
			return 71;
		else if (hit.within(2, 0.691, 0.687, 0.768, 0.769))
			return 72;
		else if (hit.within(2, 0.735, 0.782, 0.813, 0.859))
			return 73;
		else if (hit.side == 2)
		{
			for(TardisPermission p : TardisPermission.values())
			{
				int o = p.ordinal();
				double d = 0.078;
				double x = 0.68 - (o * d);
				double X = 0.68 - ((o + 1) * d);
				if(hit.within(2, 0.33, X, 0.43, x))
					return 80 + o;
				if(hit.within(2, 0.43, X, 0.53, x))
					return 90 + o;
			}
		}
		return -1;
	}

	public int getControlFromHit(int blockX, int blockY, int blockZ, Vec3 hitPos, EntityPlayer pl)
	{
		int side = hitPos.xCoord == 0 ? 4 : (hitPos.xCoord == 1 ? 5 : (hitPos.zCoord == 0 ? 2 : 3));
		float relativeY = (float) (hitPos.yCoord - blockY);
		float relativeX = (float) ((side >= 4) ? hitPos.zCoord : hitPos.xCoord);
		HitPosition hit = new HitPosition(relativeX, relativeY, side);
		return getControlFromHit(hit);
	}

	public boolean activate(EntityPlayer pl, int side, int blockY, float x, float y, float z)
	{
		if (ServerHelper.isServer())
			return true;
		float relativeY = (blockY - yCoord) + y;
		float relativeX = (side >= 4) ? z : x;
		HitPosition hit = new HitPosition(relativeX, relativeY, side);
		int control = getControlFromHit(hit);
		if (control != -1)
			Helper.activateControl(this, pl, control);
		else
			TardisOutput.print("TETE", hit.toString());
		return true;
	}

	private static final String hasPerm = " has permission to ";
	private static final String hasNoPerm = " does not have permission to ";
	@Override
	public void activateControl(EntityPlayer pl, int control)
	{
		int prevLastButton = lastButton;
		lastButton = control;
		lastButtonTT = tt;
		TardisOutput.print("TETE", "Control activated:" + control);
		CoreTileEntity core = Helper.getTardisCore(worldObj);
		TardisDataStore ds = Helper.getDataStore(worldObj);
		if ((core != null) && (ds != null))
		{
			if ((control == 4) || (control == 5))
			{
				currentUserID += control == 4 ? 1 : -1;
				setUsername();
			}
			else if ((control >= 10) && (control < 20))
			{
				if (ds.unspentLevelPoints() > 0)
				{
					if (ds.hasPermission(pl, TardisPermission.POINTS))
					{
						TardisUpgradeMode mode = TardisUpgradeMode.getUpgradeMode(control - 10);
						TardisOutput.print("TETE", "Setting mode to " + mode.name);
						if (mode != null)
						{
							if ((preparingToUpgrade == mode) && pl.isSneaking())
							{
								preparingToUpgrade = null;
								preparingToUpgradeTT = -1;
								ds.upgradeLevel(mode, 1);
								core.sendUpdate();
							}
							else if (preparingToUpgrade == null)
							{
								preparingToUpgrade = mode;
								preparingToUpgradeTT = tt;
								pl.addChatMessage(new ChatComponentText(
										"[ENGINE] Sneak and activate the button again to upgrade " + mode.name));
							}
						}
					}
					else
					{
						pl.addChatMessage(CoreTileEntity.cannotModifyMessage);
					}
				}
			}
			else if ((control >= 20) && (control < 30))
			{
				TardisUpgradeMode mode = TardisUpgradeMode.getUpgradeMode(control - 20);
				if (mode != null)
				{
					int level = ds.getLevel(mode);
					pl.addChatMessage(new ChatComponentText("[ENGINE] " + mode.name + " lvl: " + level + "/"
							+ ds.maxUnspentLevelPoints()));
				}
			}
			else if (control == 30)
				pl.addChatMessage(new ChatComponentText("[ENGINE] Unspent level points: " + ds.unspentLevelPoints() + "/"
						+ ds.maxUnspentLevelPoints()));
			else if (control == 39)
			{
				if (hasScrewdriver(0) && (pl instanceof EntityPlayerMP))
				{
					ItemStack toGive = new ItemStack(TardisMod.screwItem, 1, 0);
					validateScrewNBT();
					toGive.stackTagCompound = screwNBT;
					hasScrew = false;
					screwNBT = null;
					TardisMod.screwItem.notifyMode(toGive, pl, false);
					WorldHelper.giveItemStack(pl, toGive);
				}
				else
				{
					ItemStack held = pl.getHeldItem();
					if (held != null)
					{
						Item item = held.getItem();
						if (item instanceof SonicScrewdriverItem)
						{
							InventoryPlayer inv = pl.inventory;
							screwNBT = held.stackTagCompound;
							hasScrew = true;
							validateScrewNBT();
							inv.mainInventory[inv.currentItem] = null;
						}
					}
				}
			}
			else if ((control >= 40) && (control < 50))
			{
				validateScrewNBT();
				if (hasScrew)
				{
					if (ds.hasPermission(pl, TardisPermission.PERMISSIONS))
						SonicScrewdriverItem.togglePermission(screwNBT, SonicScrewdriverItem.getMode(control - 40));
					else
						ServerHelper.sendString(pl, CoreTileEntity.cannotModifyMessage);
				}

			}
			else if ((control >= 50) && (control < 60))
			{
				validateScrewNBT();
				if (hasScrew && (screwNBT != null))
				{
					ScrewdriverMode m = SonicScrewdriverItem.getMode(control - 50);
					String modeString = m.name();
					String s = "Sonic screwdriver ";
					if ((m.requiredFunction == null) || core.hasFunction(m.requiredFunction))
					{
						s += SonicScrewdriverItem.hasPermission(screwNBT, m) ? "has" : "does not have";
						s += " " + modeString + " permission";
					}
					else
					{
						s += "does not have " + modeString + " functionality";
					}
					ServerHelper.sendString(pl, "ENGINE", s);
				}
			}
			else if (control == 60)
				internalOnly = !internalOnly;
			else if ((control == 71) || (control == 72))
			{
				if ((availableConsoleRooms == null) || (availableConsoleRooms.length == 1))
					updateConsoleRooms();
				consoleSettingControl = MathHelper.cycle(consoleSettingControl + (control == 71 ? -1 : 1), 0,
						availableConsoleRooms.length - 1);
				consoleSettingString = availableConsoleRooms[consoleSettingControl];
			}
			else if (control == 73)
			{
				if (ds.hasPermission(pl, TardisPermission.ROOMS))
				{
					if ((prevLastButton != 73) && pl.isSneaking())
						lastButton = -1;
					else if ((prevLastButton == 73) && !pl.isSneaking())
						lastButton = -1;
					else if (prevLastButton != 73)
					{
						ServerHelper
								.sendString(pl, "ENGINE",
										"Warning: Performing this function may cause loss of items. Please right click, then sneak-right click this button to proceed");
					}
					else
					{
						core.loadConsoleRoom(sanitiseConsole(consoleSettingString));
					}
				}
				else
				{
					lastButton = -1;
					ServerHelper.sendString(pl, "ENGINE", CoreTileEntity.cannotModifyMessage.getFormattedText());
				}
			}
			else if((control >= 80) && (control < 90))
			{
				TardisPermission p = TardisPermission.get(control - 80);
				if(!ds.togglePermission(ServerHelper.getUsername(pl), currentPerson, p))
					ServerHelper.sendString(pl, CoreTileEntity.cannotModifyPermissions);
			}
			else if((control >= 90) && (control < 100))
			{
				TardisPermission p = TardisPermission.get(control - 90);
				ServerHelper.sendString(pl, currentPerson + (ds.hasPermission(currentPerson, p) ? hasPerm : hasNoPerm) + p.name);
			}
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
			wobbleAmount = (((tt + rand.nextInt(maxRand)) % count) / count);
			wobbleAmount = Math.abs(wobbleAmount - 0.5) * maxWobble * 2;
		}
		return getControlState(controlID) + wobbleAmount;
	}

	@Override
	public double getControlState(int cID)
	{
		if (ServerHelper.isServer())
			return 0;
		CoreTileEntity core = Helper.getTardisCore(worldObj);
		TardisDataStore ds = Helper.getDataStore(worldObj);
		if ((core != null) && (ds != null))
		{
			if ((cID == 4) || (cID == 5) || ((cID >= 10) && (cID < 20)) || ((cID >= 71) && (cID <= 73)))
				return (lastButton == cID) ? 1.0 : 0;
			if ((cID >= 20) && (cID < 30))
			{
				TardisUpgradeMode mode = TardisUpgradeMode.getUpgradeMode(cID - 20);
				if ((mode != null) && (ds.maxUnspentLevelPoints() > 0))
					return ((double) ds.getLevel(mode)) / ((double) ds.maxUnspentLevelPoints());
				return 0;
			}
			if (cID == 30)
			{
				if (ds.maxUnspentLevelPoints() > 0)
					return ((double) ds.unspentLevelPoints()) / ((double) ds.maxUnspentLevelPoints());
				return 0;
			}
			if ((cID >= 40) && (cID < 60))
			{
				if (!hasScrew)
					return 0;
				int mID = cID >= 50 ? cID - 50 : cID - 40;
				if (hasScrew && (screwNBT == null))
					validateScrewNBT();
				ScrewdriverMode m = SonicScrewdriverItem.getMode(mID);
				if (cID < 50)
					return SonicScrewdriverItem.hasPermission(screwNBT, m) ? 1 : 0;
				else
				{
					if ((m.requiredFunction == null) || core.hasFunction(m.requiredFunction))
					{
						double v = SonicScrewdriverItem.hasPermission(screwNBT, m) ? 1 : 0.2;
						// TardisOutput.print("TETE", "V:" + v);
						return v;
					}
					return 0.2;
				}
			}
			if (cID == 60)
				return internalOnly ? 1 : 0;
			if ((cID >= 80) && (cID < 90))
			{
				TardisPermission p = TardisPermission.get(cID-80);
				return ds.hasPermission(currentPerson, p) ? 1 : 0;
			}
			if((cID >= 90) && (cID < 100))
			{
				TardisPermission p = TardisPermission.get(90-cID);
				return ds.hasPermission(currentPerson, p) ? 1 : 0;
			}
		}
		return (float) (((tt + cID) % 40) / 39.0);
	}

	public String getConsoleSetting()
	{
		return consoleSettingString;
	}

	public boolean getInternalOnly()
	{
		return internalOnly;
	}

	public static void refreshAvailableConsoleRooms()
	{
		String[] rooms = TardisMod.schemaHandler.getSchemas(true);
		ArrayList<String> validRooms = new ArrayList<String>(rooms.length);
		for (String room : rooms)
		{
			if (room.startsWith("tardisConsole"))
			{
				validRooms.add(sanitiseConsole(room));
			}
		}
		availableConsoleRooms = validRooms.toArray(new String[validRooms.size()]);
	}

	private static String sanitiseConsole(String c)
	{
		if ((c == null) || (c.length() < 13))
			return c;
		return c.replace("tardisConsole", "");
		// return c.substring(13);
	}

	@Override
	public double[] getColorRatio(int controlID)
	{
		double[] retVal = { 0, 0, 0 };
		if (controlID == 6)
			retVal = new double[] { 0.2, 0.3, 0.9 };
		if ((controlID >= 50) && (controlID < 60))
		{
			ScrewdriverMode m = SonicScrewdriverItem.getMode(controlID - 50);
			// TardisOutput.print("TETE","Colors: "+ m.c[0] + "," + m.c[1] + "," + m.c[2]);
			return m.c;
		}
		return retVal;
	}

	@Override
	public double getControlHighlight(int controlID)
	{
		if ((controlID >= 10) && (controlID < 20))
		{
			TardisUpgradeMode mode = TardisUpgradeMode.getUpgradeMode(controlID - 10);
			if (mode != null)
				return preparingToUpgrade == mode ? 1 : -1;
		}
		return -1;
	}

	private void validateScrewNBT()
	{
		if (hasScrew)
		{
			if (screwNBT == null)
			{
				TardisOutput.print("TETE", "New NBT For Screw");
				screwNBT = SonicScrewdriverItem.getNewNBT();
				screwNBT.setInteger("linkedTardis", WorldHelper.getWorldID(this));
			}
			else
			{
				int dim = SonicScrewdriverItem.getLinkedDim(screwNBT);
				if ((dim != 0) && (dim != WorldHelper.getWorldID(this)))
					screwNBT.setInteger("perm", SonicScrewdriverItem.minPerms);
			}
			CoreTileEntity core = Helper.getTardisCore(worldObj);
			if(core != null)
				screwNBT.setString("owner", core.getOwner());
			screwNBT.setInteger("linkedTardis", WorldHelper.getWorldID(this));
		}
		else
		{
			if (screwNBT != null)
				screwNBT = null;
		}
	}

	@Override
	public boolean hasScrewdriver(int slot)
	{
		return hasScrew;
	}

	@Override
	public ScrewdriverMode getScrewMode(int slot)
	{
		return SonicScrewdriverItem.getMode(screwMode);
	}

	@Override
	public void writeTransmittable(NBTTagCompound nbt)
	{
		super.writeTransmittable(nbt);
		if (screwNBT != null)
			nbt.setTag("sNBT", screwNBT);
		if (currentPerson != null)
			nbt.setString("cP", currentPerson);
		if (preparingToUpgrade != null)
			nbt.setInteger("ptU", preparingToUpgrade.ordinal());
		else
			nbt.setBoolean("ptUN", false);
		nbt.setBoolean("io", internalOnly);
		nbt.setBoolean("hS", hasScrew);
		nbt.setInteger("lB", lastButton);
	}

	@Override
	public void readTransmittable(NBTTagCompound nbt)
	{
		super.readTransmittable(nbt);
		if (nbt.hasKey("sNBT"))
			screwNBT = nbt.getCompoundTag("sNBT");
		hasScrew = nbt.getBoolean("hS");
		currentPerson = nbt.getString("cP");
		lastButton = nbt.getInteger("lB");
		lastButtonTT = tt;
		preparingToUpgradeTT = tt;
		internalOnly = nbt.getBoolean("io");
		if (nbt.hasKey("ptU"))
			preparingToUpgrade = TardisUpgradeMode.getUpgradeMode(nbt.getInteger("ptU"));
		else if (nbt.hasKey("ptUN"))
			preparingToUpgrade = null;
	}

	@Override
	public void readTransmittableOnly(NBTTagCompound nbt)
	{
		if (nbt.hasKey("css"))
			consoleSettingString = nbt.getString("css");
		else
			consoleSettingString = "Main";
		screwMode = nbt.getInteger("scMo");
	}

	@Override
	public void writeTransmittableOnly(NBTTagCompound nbt)
	{
		nbt.setString("css", sanitiseConsole(consoleSettingString));
		if (screwNBT != null)
			nbt.setInteger("scMo", screwNBT.getInteger("scMo"));
	}

}
