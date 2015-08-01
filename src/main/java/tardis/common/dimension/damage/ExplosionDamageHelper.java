package tardis.common.dimension.damage;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.datastore.SimpleDoubleCoordStore;
import net.minecraft.world.Explosion;

public class ExplosionDamageHelper
{
	public static void damage(TardisDamageSystem ds, SimpleCoordStore pos, Explosion explosion, double mult)
	{
		int w = pos.world;
		SimpleDoubleCoordStore explosionPos = new SimpleDoubleCoordStore(w,explosion.explosionX, explosion.explosionY, explosion.explosionZ);
		double distance = explosionPos.distance(pos);
		distance = Math.max(1, distance);
		double damageAmount = mult * (explosion.explosionSize / distance);
		if(damageAmount > 0)
			ds.damage(TardisDamageType.EXPLOSION,damageAmount);
	}
}
