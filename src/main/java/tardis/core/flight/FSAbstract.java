package tardis.core.flight;

import io.darkcraft.darkcore.mod.helpers.SoundHelper;

import tardis.Configs;
import tardis.core.TardisInfo;

public abstract class FSAbstract
{
	private int tickLength;
	private String sound;
	protected TardisInfo info;
	protected int tt;

	public FSAbstract(int tickLength, String sound)
	{
		this.tickLength = tickLength;
		tt = -1;
	}

	protected void setSound(int length, String sound)
	{
		tickLength = length;
		this.sound = sound;
	}

	public final FSAbstract tickState()
	{
		if(info == null)
			return this;
		tt++;
		if((tt == 0) && (sound != null))
			playSound(sound);
		if(tt > tickLength)
			return getNextState();
		tick();
		return this;
	}

	public void setTardisInfo(TardisInfo info)
	{
		this.info = info;
	}

	protected final FSAbstract reset()
	{
		tt = -1;
		return this;
	}

	protected void playSound(String sound)
	{
		SoundHelper.playSound(info.getCore(), sound, (float) Configs.tardisVol);
	}

	protected abstract void tick();

	protected abstract FSAbstract getNextState();
}
