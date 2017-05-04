package tardis.api;

import net.minecraft.entity.player.EntityPlayer;

import io.darkcraft.darkcore.mod.handlers.containers.PlayerContainer;

import tardis.common.core.HitPosition;
import tardis.core.console.control.AbstractControl;

public interface IControlMatrix
{
	public AbstractControl getControl(PlayerContainer player, HitPosition position);

	public HitPosition getHitPosition(EntityPlayer pl, int blockX, int blockY, int blockZ,
														float i, float j, float k, int side);
}
