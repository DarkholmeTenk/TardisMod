package tardis.core.console.control;

import io.darkcraft.darkcore.mod.handlers.containers.PlayerContainer;

import tardis.core.TardisInfo;

public class ControlPushButton extends AbstractControl
{

	public ControlPushButton(ControlBuilder<?> builder, ControlHolder holder)
	{
		super(builder, 1, 1, 0, holder);
	}

	@Override
	protected boolean activateControl(TardisInfo info, PlayerContainer player, boolean sneaking)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void render(float ptt)
	{
		// TODO Auto-generated method stub

	}

	public static class ControlPushButtonBuilder extends ControlBuilder<ControlPushButton>
	{

		@Override
		public ControlPushButton build(ControlHolder holder)
		{
			// TODO Auto-generated method stub
			return null;
		}
	}
}
