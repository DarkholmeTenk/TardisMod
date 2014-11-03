package tardis.common.tileents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tardis.TardisMod;
import tardis.api.IControlMatrix;
import tardis.api.TardisScrewdriverMode;
import tardis.api.TardisUpgradeMode;
import tardis.common.core.Helper;
import tardis.common.core.HitPosition;
import tardis.common.core.TardisOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatMessageComponent;

public class TardisEngineTileEntity extends TardisAbstractTileEntity implements IControlMatrix
{
	private String[] currentUsers;
	private int currentUserID;
	public String currentPerson;
	
	public int lastButton = -1;
	public int lastButtonTT = -1;
	private TardisUpgradeMode preparingToUpgrade = null;
	private int preparingToUpgradeTT = -1;
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(tt % 40 == 1)
		{
			verifyEngineBlocks();
			getUsernames();
		}
		
		if(lastButtonTT != -1 && tt > (lastButtonTT + 15))
		{
			lastButton = -1;
			lastButtonTT = -1;
		}
		
		if(preparingToUpgrade != null && tt > (preparingToUpgradeTT + 80))
		{
			preparingToUpgrade = null;
			preparingToUpgradeTT = -1;
		}
	}
	
	private void getUsernames()
	{
		currentUsers = new String[0];
		ArrayList<String> users = new ArrayList<String>();
		List plList = worldObj.playerEntities;
		if(plList != null && plList.size() > 0)
		{
			for(Object o : plList)
			{
				if(o instanceof EntityPlayer)
					users.add(((EntityPlayer)o).username);
			}
			Collections.sort(users, String.CASE_INSENSITIVE_ORDER);
			currentUsers = users.toArray(currentUsers);
			currentUserID = Helper.clamp(currentUserID, 0, currentUsers.length);
		}
		setUsername();
	}
	
	private void setUsername()
	{
		if(currentUsers != null && currentUsers.length > 0)
		{
			currentUserID = Helper.cycle(currentUserID, 0, currentUsers.length-1);
			currentPerson = currentUsers[currentUserID];
		}
		else
			currentPerson = "";
	}
	
	private void verifyEngineBlocks()
	{
		if(worldObj.getBlockId(xCoord, yCoord-1, zCoord) == 0)
			worldObj.setBlock(xCoord, yCoord-1, zCoord, TardisMod.schemaComponentBlock.blockID, 7, 3);
		if(worldObj.getBlockId(xCoord, yCoord+1, zCoord) == 0)
			worldObj.setBlock(xCoord, yCoord+1, zCoord, TardisMod.schemaComponentBlock.blockID, 7, 3);
		if(worldObj.getBlockId(xCoord, yCoord+2, zCoord) == 0)
			worldObj.setBlock(xCoord, yCoord+2, zCoord, TardisMod.schemaComponentBlock.blockID, 7, 3);
	}

	public int getControlFromHit(HitPosition hit)
	{
		if(hit.within(2, 0.558, 0.690, 0.660, 0.768))
			return 4;
		else if(hit.within(2, 0.488, 0.690, 0.564, 0.768))
			return 5;
		else if(hit.within(2, 0.558, 0.780, 0.660, 0.859))
			return 6;
		else if(hit.within(2, 0.488, 0.780, 0.565, 0.859))
			return 7;
		else if(hit.within(5, 0.393, 0.725, 0.545, 0.876))
			return 10;
		else if(hit.within(5, 0.393, 0.523, 0.545, 0.679))
			return 11;
		else if(hit.within(5, 0.393, 0.326, 0.545, 0.477))
			return 12;
		else if(hit.within(5, 0.393, 0.122, 0.545, 0.276))
			return 13;
		else if(hit.within(5, 0.551, 0.711, 0.704, 0.898))
			return 20;
		else if(hit.within(5, 0.551, 0.510, 0.704, 0.696))
			return 21;
		else if(hit.within(5, 0.551, 0.311, 0.704, 0.492))
			return 22;
		else if(hit.within(5, 0.551, 0.108, 0.704, 0.294))
			return 23;
		else if(hit.within(5, 0.729, 0.711, 0.888, 0.898))
			return 30;
		else
			TardisOutput.print("TETE", hit.toString());
		return -1;
	}
	
	public boolean activate(EntityPlayer pl, int side, int blockY, float x, float y, float z)
	{
		if(!Helper.isServer())
			return true;
		float relativeY = (blockY - yCoord) + y;
		float relativeX = (side >= 4) ? z : x;
		HitPosition hit = new HitPosition(relativeX, relativeY, side);
		int control = getControlFromHit(hit);
		if(control != -1)
		{
			activateControl(pl,control);
		}
		return true;
	}
	
	public void activateControl(EntityPlayer pl, int control)
	{
		lastButton = control;
		lastButtonTT = tt;
		TardisOutput.print("TETE","Control activated:" + control);
		TardisCoreTileEntity core = Helper.getTardisCore(worldObj);
		if(core != null)
		{
			if(control == 4 || control == 5)
			{
				currentUserID += control == 4 ? 1 : -1;
				setUsername();
			}
			else if(control == 6)
				pl.sendChatToPlayer(new ChatMessageComponent().addText("[TARDIS] " + currentPerson + " does " + (core.canModify(currentPerson)?"":"not ") + " have permission to modify this TARDIS"));
			else if(control == 7)
				core.toggleModifier(pl, currentPerson);
			else if(control >= 10 && control < 20)
			{
				if(core.unspentLevelPoints() > 0)
				{
					if(core.canModify(pl))
					{
						TardisUpgradeMode mode = TardisUpgradeMode.getUpgradeMode(control - 10);
						if(mode != null)
						{
							if(preparingToUpgrade == mode && pl.isSneaking())
							{
								preparingToUpgrade = null;
								preparingToUpgradeTT = -1;
								core.upgradeLevel(mode, 1);
								core.sendUpdate();
							}
							else if(preparingToUpgrade == null)
							{
								preparingToUpgrade = mode;
								preparingToUpgradeTT = tt;
								pl.addChatMessage("[ENGINE] Sneak and activate the button to upgrade " + mode.name);
							}
						}
					}
					else
					{
						pl.addChatMessage(TardisCoreTileEntity.cannotModifyMessage);
					}
				}
			}
			else if(control >= 20 && control < 30)
			{
				TardisUpgradeMode mode = TardisUpgradeMode.getUpgradeMode(control - 20);
				if(mode != null)
				{
					int level = core.getLevel(mode);
					pl.addChatMessage("[ENGINE] " + mode.name + " lvl: " + level + "/" + core.maxUnspentLevelPoints());
				}
			}
			else if(control == 30)
				pl.addChatMessage("Unspent level points: " + core.unspentLevelPoints() +"/"+core.maxUnspentLevelPoints());
		}
		sendUpdate();
	}

	@Override
	public double getControlState(int controlID, boolean wobble)
	{
		double maxWobble = 0.025;
		double count = 20;
		int maxRand = 10;
		double wobbleAmount = 0;
		if(wobble)
		{
			wobbleAmount = (((tt + rand.nextInt(maxRand)) % count) / count);
			wobbleAmount = Math.abs(wobbleAmount - 0.5) * maxWobble * 2;
		}
		return getControlState(controlID) + wobbleAmount;
	}

	@Override
	public double getControlState(int cID)
	{
		TardisCoreTileEntity core = Helper.getTardisCore(worldObj);
		if(core != null)
		{
			if(cID == 4 || cID == 5 || cID == 7 || cID >= 10 && cID < 20)
				return (lastButton == cID) ? 1.0 : 0;
			if(cID == 6)
				return core.canModify(currentPerson) ? 1 : 0.2;
			if(cID >= 20 && cID < 30)
			{
				TardisUpgradeMode mode = TardisUpgradeMode.getUpgradeMode(cID - 20);
				if(mode != null && core.maxUnspentLevelPoints() > 0)
					return ((double)core.getLevel(mode)) / ((double)core.maxUnspentLevelPoints());
				return 0;
			}
			if(cID == 30)
			{
				if(core.maxUnspentLevelPoints() > 0)
					return ((double)core.unspentLevelPoints()) / ((double)core.maxUnspentLevelPoints());
				return 0;
			}
		}
		return (float)(((tt+cID) % 40) / 39.0);
	}

	@Override
	public double[] getColorRatio(int controlID)
	{
		double[] retVal = { 0, 0, 0 };
		if(controlID == 6)
			retVal = new double[]{ 0.2, 0.3, 0.9 };
		return retVal;
	}

	@Override
	public double getControlHighlight(int controlID)
	{
		if(controlID >= 10 && controlID < 20)
		{
			TardisUpgradeMode mode = TardisUpgradeMode.getUpgradeMode(controlID - 10);
			if(mode != null)
				return preparingToUpgrade == mode ? 1 : -1;
		}
		return -1;
	}

	@Override
	public boolean hasScrewdriver(int slot)
	{
		return false;
	}

	@Override
	public TardisScrewdriverMode getScrewMode(int slot)
	{
		return TardisScrewdriverMode.Dismantle;
	}

	@Override
	public void writeTransmittable(NBTTagCompound nbt)
	{
		if(preparingToUpgrade != null)
			nbt.setInteger("preparingToUpgrade", preparingToUpgrade.ordinal());
		nbt.setInteger("lastButton"			, lastButton);
	}

	@Override
	public void readTransmittable(NBTTagCompound nbt)
	{
		lastButton		= nbt.getInteger("lastButton");
		lastButtonTT	= tt;
		preparingToUpgradeTT = tt;
		if(nbt.hasKey("preparingToUpgrade"))
			preparingToUpgrade = TardisUpgradeMode.getUpgradeMode(nbt.getInteger("preparingToUpgrade"));
	}

}
