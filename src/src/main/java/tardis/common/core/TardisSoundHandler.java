package tardis.common.core;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.sound.SoundLoadEvent;

public class TardisSoundHandler
{
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onSoundLoad(SoundLoadEvent event)
	{
		try
		{
			/*event.manager.addSound("tardismod:takeoff.wav");
			event.manager.addSound("tardismod:landing.wav");
			event.manager.addSound("tardismod:landingInt.wav");
			event.manager.addSound("tardismod:engines.wav");
			event.manager.addSound("tardismod:engineDrum.wav");
			event.manager.addSound("tardismod:levelup.wav");
			event.manager.addSound("tardismod:transmat.wav");
			event.manager.addSound("tardismod:transmatFail.wav");
			event.manager.addSound("tardismod:sonic.wav");*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
