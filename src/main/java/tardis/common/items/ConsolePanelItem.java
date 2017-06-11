package tardis.common.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import io.darkcraft.darkcore.mod.abstracts.AbstractItem;
import io.darkcraft.darkcore.mod.nbt.Mapper;
import io.darkcraft.darkcore.mod.nbt.NBTHelper;
import io.darkcraft.darkcore.mod.nbt.NBTProperty.SerialisableType;

import tardis.TardisMod;
import tardis.core.console.panel.ConsolePanel;
import tardis.core.console.panel.types.normal.NormalPanelX;
import tardis.core.console.panel.types.normal.NormalPanelY;
import tardis.core.console.panel.types.normal.NormalPanelZ;

public class ConsolePanelItem extends AbstractItem
{
	private static final Mapper<ConsolePanel> mapper = NBTHelper.getMapper(ConsolePanel.class, SerialisableType.WORLD);

	public ConsolePanelItem()
	{
		super(TardisMod.modName);
		setUnlocalizedName("ConsolePanelItem");
	}

	@Override
	public void initRecipes()
	{
	}

	public ConsolePanel getPanel(ItemStack stack)
	{
		try
		{
			if((stack == null) || !(stack.getItem() instanceof ConsolePanelItem))
				return null;
			NBTTagCompound nbt = stack.stackTagCompound;
			return mapper.readFromNBT(nbt, "console");
		}
		catch(RuntimeException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public ItemStack getPanelItem(ConsolePanel panel)
	{
		ItemStack is = new ItemStack(this,1);
		is.stackTagCompound = new NBTTagCompound();
		mapper.writeToNBT(is.stackTagCompound, "console", panel);
		return is;
	}

	private static final ConsolePanel[] panels = { new NormalPanelX(), new NormalPanelY(), new NormalPanelZ() };

	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List list)
	{
		for(ConsolePanel panel : panels)
			list.add(getPanelItem(panel));
	}
}
