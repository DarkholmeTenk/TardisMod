package tardis.common.tileents.extensions;

import net.minecraft.item.ItemStack;
import tardis.TardisMod;
import tardis.common.core.TardisOutput;

public enum CraftingComponentType {
	CHRONOSTEEL("IngotChronosteel"),
	DALEKANIUM("IngotDalek"),
	KONTRON("CrystalKontron"),
	UPGRADE("Upgrade");

	public final String name;
	CraftingComponentType(String passedName)
	{
		name=passedName;
	}

	public ItemStack getIS(int amount)
	{
		TardisOutput.print("CCT", "Getting IS for :" + name + " returning " + ordinal(),TardisOutput.Priority.OLDDEBUG);
		return new ItemStack(TardisMod.craftingComponentItem,amount,ordinal());
	}
}