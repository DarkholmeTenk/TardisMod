package tardis.common.core.events;

import net.minecraft.entity.player.EntityPlayer;
import tardis.api.IControlMatrix;
import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class TardisControlEvent extends TardisModEvent
{
	public final IControlMatrix matrix;
	public final int control;
	public final EntityPlayer player;

	public TardisControlEvent(){this(null,-1,null);}
	public TardisControlEvent(IControlMatrix _matrix, int _control, EntityPlayer _player)
	{
		super();
		matrix = _matrix;
		control = _control;
		player = _player;
	}
}
