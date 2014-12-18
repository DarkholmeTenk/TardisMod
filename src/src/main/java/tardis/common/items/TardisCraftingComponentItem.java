package tardis.common.items;

import net.minecraft.item.ItemStack;

public class TardisCraftingComponentItem extends TardisAbstractItem
{
	enum ComponentType {
		CHRONOSTEEL("IngotChronosteel"),
		DALEKANIUM("IngotDalek");
		
		public final String name;
		ComponentType(String passedName)
		{
			name=passedName;
		}
	}
	
	private static String[] names;
	
	static
	{
		names = new String[ComponentType.values().length];
		ComponentType[] types = ComponentType.values();
		for(int i = 0;i<names.length;i++)
			names[i] = types[i].name;
	}
	
	public TardisCraftingComponentItem()
	{
		setUnlocalizedName("CraftingComponent");
		setSubNames(names);
	}
	
	public ItemStack getIS(ComponentType type, int amount)
	{
		return new ItemStack(this,amount,type.ordinal());
	}

	@Override
	public void initRecipes()
	{
		
	}

}
