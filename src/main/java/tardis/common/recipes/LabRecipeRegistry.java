package tardis.common.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import gnu.trove.map.hash.THashMap;
import io.darkcraft.darkcore.mod.nbt.NBTHelper;
import io.darkcraft.darkcore.mod.nbt.impl.PrimMapper;
import net.minecraft.nbt.NBTTagCompound;
import tardis.common.tileents.extensions.LabRecipe;

public class LabRecipeRegistry
{
	private static List<LabRecipe> recipes = new ArrayList<LabRecipe>();
	private static Map<String,LabRecipe> recipeMap = new THashMap();

	static
	{
		NBTHelper.register(LabRecipe.class, new PrimMapper<LabRecipe>(){
			@Override
			public boolean handleSubclasses(){ return true; }

			@Override
			public void writeToNBT(NBTTagCompound nbt, String id, Object t)
			{
				if(!(t instanceof LabRecipe))
					return;
				nbt.setString("id", ((LabRecipe)t).id);
			}

			@Override
			public LabRecipe readFromNBT(NBTTagCompound nbt, String id)
			{
				return getRecipe(nbt.getString("id"));
			}
		});
	}

	public static void addRecipe(LabRecipe toAdd)
	{
		if((toAdd != null) && toAdd.isValid())
		{
			if(recipeMap.containsKey(toAdd.id)) return;
			recipes.add(toAdd);
			recipeMap.put(toAdd.id,toAdd);
			Collections.sort(recipes, sorter);
		}
	}

	public static void removeRecipe(LabRecipe toRem)
	{
		recipes.remove(toRem);
		recipeMap.remove(toRem.id);
	}

	public static void removeRecipe(String id)
	{
		LabRecipe lr = recipeMap.get(id);
		if(lr != null)
		{
			recipeMap.remove(id);
			recipes.remove(lr);
		}
	}

	public static List<LabRecipe> getRecipes()
	{
		return recipes;
	}

	public static LabRecipe getRecipe(String id)
	{
		return recipeMap.get(id);
	}

	private static Comparator<LabRecipe> sorter = new Comparator<LabRecipe>(){

		@Override
		public int compare(LabRecipe a, LabRecipe b)
		{
			return a.id.compareTo(b.id);
		}
	};
}
