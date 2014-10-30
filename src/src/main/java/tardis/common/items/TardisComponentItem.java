package tardis.common.items;

import tardis.common.tileents.components.TardisTEComponent;

public class TardisComponentItem extends TardisAbstractItem
{

	public TardisComponentItem(int par1)
	{
		super(par1);
		setUnlocalizedName("Component");
		setSubNames(TardisTEComponent.getStrings());
		setMaxStackSize(1);
	}

	@Override
	public void initRecipes()
	{

	}

}
