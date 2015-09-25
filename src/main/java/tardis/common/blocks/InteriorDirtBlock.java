package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.config.ConfigFile;
import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.datastore.SimpleDoubleCoordStore;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import java.util.EnumSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.ForgeDirection;
import tardis.TardisMod;
import tardis.api.IScrewablePrecise;
import tardis.api.ScrewdriverMode;
import tardis.api.TardisPermission;
import tardis.common.core.helpers.Helper;
import tardis.common.dimension.TardisDataStore;
import tardis.common.tileents.CoreTileEntity;
import tardis.common.tileents.LabTileEntity;
import tardis.common.tileents.extensions.CraftingComponentType;
import tardis.common.tileents.extensions.LabFlag;
import tardis.common.tileents.extensions.LabRecipe;

public class InteriorDirtBlock extends AbstractBlock implements IScrewablePrecise
{
	private static double tickMult = 1;
	private static double boneChance = 0.3;
	private static ConfigFile config = null;

	public static void refreshConfigs()
	{
		if(config == null)
			config = TardisMod.configHandler.registerConfigNeeder("Misc");

		tickMult = config.getDouble("Dirt block tick mult", 0.5,
				"The number the tick rate of the plant is multipied by to work out how often the dirt block applies a dirt tick",
				"e.g. A mult of 0.5 means a plant which would normally get a tick every 10 ticks will get an extra growth tick every 5 ticks");
		boneChance = config.getDouble("Dirt block bonemeal chance", 0.25,
				"The chance for a TARDIS dirt block to apply a bonemeal affect to the plant (as well as a growth tick)");
	}

	public InteriorDirtBlock()
	{
		super(TardisMod.modName);
	}

	@Override
	public void initData()
	{
		setBlockName("TardisDirt");
		setTickRandomly(true);
		if(config == null)
			refreshConfigs();
		setLightLevel(TardisMod.lightBlocks ? 1 : 0);
	}

	@Override
	public void initRecipes()
	{
		LabTileEntity.addRecipe(new LabRecipe(
				new ItemStack[] { new ItemStack(Blocks.dirt,64),
						CraftingComponentType.KONTRON.getIS(1),
						CraftingComponentType.CHRONOSTEEL.getIS(1),
						new ItemStack(Items.dye,32,15)},
				new ItemStack[] { getIS(TardisMod.numDirtRecipe, 0) },
				EnumSet.of(LabFlag.INFLIGHT),
				100
				));
	}

	@Override
	public boolean isFertile(World world, int x, int y, int z)
	{
		return true;
	}

	@Override
	public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction, IPlantable plantable)
	{
		if(Helper.isTardisWorld(world))
			return true;
		return false;
	}

	@Override
	public int tickRate(World w)
    {
		if(Helper.isTardisWorld(w))
			return 1;
		return 100;
    }

	public int getNewTickRate(int old)
	{
		return MathHelper.ceil(old * tickMult);
	}

	@Override
	public void updateTick(World w, int x, int y, int z, Random rand)
	{
		if(!Helper.isTardisWorld(w))
			return;
		if(w.isAirBlock(x, y+1, z))
		{
			w.scheduleBlockUpdate(x, y, z, this, 40);
			return;
		}
		w.scheduleBlockUpdate(x, y, z, this, 1);
		Block b = w.getBlock(x, y+1, z);
		if(b != null)
		{
			CoreTileEntity core = Helper.getTardisCore(w);
			if((core != null) && ((core.tt % getNewTickRate(b.tickRate(w))) == 0))
			{
				if(w instanceof WorldServer)
				{
					FakePlayer pl = FakePlayerFactory.getMinecraft((WorldServer)w);
					ItemStack is = new ItemStack(Items.dye,1,15);
					if(rand.nextDouble() <= boneChance)
						ItemDye.applyBonemeal(is, w, x, y+1, z, pl);
					int i=1;
					if(w.getBlock(x, y+1, z) == b)
					{
						for(i=1;w.getBlock(x, y+i, z)==b;i++);
						b.updateTick(w, x, (y+i)-1, z, rand);
					}
				}
			}
		}

	}

	@Override
	public boolean screw(ScrewdriverMode mode, EntityPlayer player, SimpleCoordStore s)
	{
		if(mode == ScrewdriverMode.Dismantle)
		{
			World w = s.getWorldObj();
			TardisDataStore ds = Helper.getDataStore(w);
			if((ds == null) || (ds.hasPermission(player, TardisPermission.ROOMS)))
			{
				Block b = s.getBlock();
				if(b instanceof InteriorDirtBlock)
				{
					w.setBlockToAir(s.x, s.y, s.z);
					WorldHelper.dropItemStack(new ItemStack(this,1), new SimpleDoubleCoordStore(player));
				}
			}
			else
				ServerHelper.sendString(player, CoreTileEntity.cannotModifyMessage);
		}
		return false;
	}

}
