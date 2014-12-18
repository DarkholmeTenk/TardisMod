package tardis.common.tileents.extensions;

import net.minecraft.item.ItemStack;
import tardis.TardisMod;

public enum CraftingComponentType {
	CHRONOSTEEL("IngotChronosteel"),
	DALEKANIUM("IngotDalek");
	
	public final String name;
	CraftingComponentType(String passedName)
	{
		name=passedName;
	}
	
	public ItemStack getIS(int amount)
	{
		return new ItemStack(TardisMod.craftingComponentItem,1,ordinal());
	}
}