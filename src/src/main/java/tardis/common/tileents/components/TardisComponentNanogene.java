package tardis.common.tileents.components;

import tardis.common.core.Helper;
import tardis.common.entities.particles.ParticleType;
import tardis.common.tileents.TardisComponentTileEntity;

public class TardisComponentNanogene extends TardisAbstractComponent
{

	public TardisComponentNanogene(TardisComponentTileEntity parent)
	{
		parentObj = parent;
	}
	
	protected TardisComponentNanogene(){}

	@Override
	public ITardisComponent create(TardisComponentTileEntity parent)
	{
		return new TardisComponentNanogene(parent);
	}
	
	@Override
	public void updateTick()
	{
		super.updateTick();
		if(parentObj == null || !Helper.isServer())
			return;
		if(tt % 20 == 0)
			Helper.spawnParticle(ParticleType.NANOGENE, Helper.getWorldID(parentObj), xCoord, yCoord+1, zCoord);
	}

}
