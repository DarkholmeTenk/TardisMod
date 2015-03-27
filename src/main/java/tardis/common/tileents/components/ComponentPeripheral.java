package tardis.common.tileents.components;

import tardis.common.tileents.ComponentTileEntity;

public class ComponentPeripheral extends AbstractComponent
{
	
	protected ComponentPeripheral()
	{
		
	}
	
	public ComponentPeripheral(ComponentTileEntity parent)
	{
		parentObj = parent;
	}

	@Override
	public ITardisComponent create(ComponentTileEntity parent)
	{
		return new ComponentPeripheral();
	}

}
