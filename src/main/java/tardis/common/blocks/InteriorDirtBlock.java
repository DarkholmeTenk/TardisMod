package tardis.common.blocks;

import java.util.Random;

import net.minecraft.block.Block;
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
import tardis.common.core.Helper;
import tardis.common.tileents.CoreTileEntity;
import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.config.ConfigFile;
import io.darkcraft.darkcore.mod.helpers.MathHelper;

public class InteriorDirtBlock extends AbstractBlock
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
	}

	@Override
	public void initRecipes()
	{
		// TODO Auto-generated method stub

	}

	public boolean isFertile(World world, int x, int y, int z)
	{
		return true;
	}

	public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction, IPlantable plantable)
	{
		if(Helper.isTardisWorld(world))
			return true;
		return false;
	}
	
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
			if(core != null && core.tt % getNewTickRate(b.tickRate(w)) == 0)
			{
				if(w instanceof WorldServer)
				{
					FakePlayer pl = FakePlayerFactory.getMinecraft((WorldServer)w);
					ItemStack is = new ItemStack(Items.dye,1,15);
					if(rand.nextDouble() <= boneChance)
						ItemDye.applyBonemeal(is, w, x, y+1, z, pl);
					int i;
					for(i=1;w.getBlock(x, y+i, z)==b;i++);
					b.updateTick(w, x, y+i-1, z, rand);
				}
			}
		}

	}

}
