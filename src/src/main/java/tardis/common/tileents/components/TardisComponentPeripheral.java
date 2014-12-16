package tardis.common.tileents.components;

import tardis.common.tileents.TardisComponentTileEntity;

public class TardisComponentPeripheral extends TardisAbstractComponent
{
	
	protected TardisComponentPeripheral()
	{
		
	}
	
	public TardisComponentPeripheral(TardisComponentTileEntity parent)
	{
		parentObj = parent;
	}

	@Override
	public ITardisComponent create(TardisComponentTileEntity parent)
	{
		return new TardisComponentPeripheral();
	}

}
