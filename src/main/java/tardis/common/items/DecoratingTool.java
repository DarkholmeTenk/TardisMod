package tardis.common.items;

import io.darkcraft.darkcore.mod.abstracts.AbstractItem;
import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.BlockIterator;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.SoundHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tardis.Configs;
import tardis.TardisMod;
import tardis.api.TardisPermission;
import tardis.common.core.helpers.Helper;
import tardis.common.dimension.TardisDataStore;
import tardis.common.items.extensions.DecoratorToolTypes;
import tardis.common.tileents.extensions.CraftingComponentType;
import cpw.mods.fml.common.registry.GameRegistry;

public class DecoratingTool extends AbstractItem
{
	private static HashMap<Integer,Long> lastChangeMap = new HashMap();

	public DecoratingTool()
	{
		super(TardisMod.modName);
		setUnlocalizedName("Decorator");
	}

	@Override
	public void initRecipes()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this,1), false, "iic", "i  ","ic ",
				'i', "ingotIron",
				'c', CraftingComponentType.CHRONOSTEEL.getIS(1)));
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player)
    {
        return true;
    }

	public SimpleCoordStore getPlayerLookingAt(EntityPlayer pl)
	{
		World w = pl.worldObj;
		MovingObjectPosition hitPos = getMovingObjectPositionFromPlayer(w, pl, true);
		if((hitPos != null) && (hitPos.typeOfHit == MovingObjectType.BLOCK))
			return new SimpleCoordStore(w, hitPos);
		return null;
	}

	private void playSound(EntityPlayer player)
	{
		if(ServerHelper.isServer())
			SoundHelper.playSound(player, "tardismod:decorate", 1, 1);
	}

	@Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack is)
    {
		if(!(entityLiving instanceof EntityPlayer)) return true;
		EntityPlayer player = (EntityPlayer) entityLiving;
		if(!hasPerm(player.worldObj, player)) return true;
		DecoratorToolTypes t = getType(is);
		SimpleCoordStore pos = getPlayerLookingAt(player);
		if(pos != null)
		{
			DecoratorToolTypes c = DecoratorToolTypes.getMatching(pos);
			if((c != null) && (t != c))
			{
				playSound(player);
				t.set(pos);
			}
		}
        return true;
    }

	private DecoratorToolTypes getType(ItemStack is)
	{
		return DecoratorToolTypes.get(is.getItemDamage());
	}

	private boolean updateTick(World w)
	{
		int dim = WorldHelper.getWorldID(w);
		long lastChangeTick = lastChangeMap.containsKey(dim) ? lastChangeMap.get(dim) : 0;
		if(lastChangeTick < (w.getTotalWorldTime() - 10))
		{
			lastChangeMap.put(dim, w.getTotalWorldTime());
			return true;
		}
		return false;
	}

	private boolean hasPerm(World w, EntityPlayer pl)
	{
		if(Helper.isTardisWorld(w))
		{
			TardisDataStore ds = Helper.getDataStore(w);
			if(ds == null) return true;
			if(ds.hasPermission(pl, TardisPermission.RECOLOUR)) return true;
			if(ServerHelper.isServer())
			{
				if(updateTick(w))
				{
					ServerHelper.sendString(pl, "You do not have permission to change this block");
				}
			}
			return false;
		}
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer pl)
    {
		if(pl.isSneaking())
		{
			int ord = is.getItemDamage();
			DecoratorToolTypes t = DecoratorToolTypes.get(ord);
			do
			{
				ord = (ord + 1) % DecoratorToolTypes.values().length;
				t = DecoratorToolTypes.get(ord);
			}
			while(!t.valid);
			is.setItemDamage(ord);
			if(ServerHelper.isServer())
				ServerHelper.sendString(pl, "Decorator", "New Block: " + t.getName());
			return is;
		}
		if(ServerHelper.isClient()) return is;
		if(!hasPerm(w,pl)) return is;
		DecoratorToolTypes t = getType(is);
		SimpleCoordStore pos = getPlayerLookingAt(pl);
		if(pos != null)
		{
			DecoratorToolTypes c = DecoratorToolTypes.getMatching(pos);
			if((c != null) && (c != t))
			{
				playSound(pl);
				BlockIterator iter = new BlockIterator(pos, c.getCondition(), false, Configs.decoratorRange);
				while(iter.hasNext())
				{
					SimpleCoordStore next = iter.next();
					t.set(next);
				}
			}
		}
		return is;
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInfo(ItemStack is, EntityPlayer player, List infoList)
	{
		infoList.add("A jacked up Sonic Blaster");
		DecoratorToolTypes t = DecoratorToolTypes.get(is.getItemDamage());
		infoList.add("New Block: " + t.getName());
	}

}
