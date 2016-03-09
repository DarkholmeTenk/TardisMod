package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.ForgeDirection;
import tardis.Configs;
import tardis.TardisMod;
import tardis.api.IScrewablePrecise;
import tardis.api.ScrewdriverMode;
import tardis.api.TardisPermission;
import tardis.common.core.helpers.Helper;
import tardis.common.core.helpers.ScrewdriverHelper;
import tardis.common.dimension.TardisDataStore;
import tardis.common.tileents.CoreTileEntity;
import tardis.common.tileents.LabTileEntity;
import tardis.common.tileents.extensions.CraftingComponentType;
import tardis.common.tileents.extensions.LabFlag;
import tardis.common.tileents.extensions.LabRecipe;

public class TemporalAcceleratorBlock extends AbstractBlock implements IScrewablePrecise
{
	public TemporalAcceleratorBlock()
	{
		super(TardisMod.modName);
	}

	@Override
	public void initData()
	{
		setBlockName("TemporalAccelerator");
		setTickRandomly(true);
		setLightLevel(Configs.lightBlocks ? 1 : 0);
	}

	@Override
	public void initRecipes()
	{
		if(Configs.numDirtRecipe > 0)
			LabTileEntity.addRecipe(new LabRecipe(
					new ItemStack[] { 
							new ItemStack(Blocks.dirt,64),
							CraftingComponentType.KONTRON.getIS(1),
							CraftingComponentType.CHRONOSTEEL.getIS(1),
							new ItemStack(Items.dye,32,15)},
					new ItemStack[] { getIS(1, 0) },
					EnumSet.of(LabFlag.INFLIGHT),
					100
					));
	}
	
	@Override
	public int tickRate(World w)
    {
		if(Helper.isTardisWorld(w))
			return 1;
		return 100;
    }

	public double getNewTickRate(int old)
	{
		return MathHelper.ceil((old * Configs.tempAccTickMult));
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
			if((core != null))
			{
				if(w instanceof WorldServer)
				{
					if(b instanceof TemporalAcceleratorBlock)
						return;
					b.updateTick(w, x, y + 1, z, rand);
					
					if(!b.hasTileEntity(b.getDamageValue(w, x, y+1, z)))
						return;
					TileEntity te = w.getTileEntity(x, y+1, z);
//					for(int i = 0; i < (getNewTickRate(b.tickRate(w))); i++){
						te.updateEntity();
						System.out.println(ServerHelper.isClient() + " : " + 1 + " / " + b.tickRate(w) + te);
//					}
				}
			}
		}

	}

	@Override
	public boolean screw(ScrewdriverHelper helper, ScrewdriverMode mode, EntityPlayer player, SimpleCoordStore s)
	{
		if(mode == ScrewdriverMode.Dismantle)
		{
			World w = s.getWorldObj();
			TardisDataStore ds = Helper.getDataStore(w);
			if((ds == null) || (ds.hasPermission(player, TardisPermission.ROOMS)))
			{
				Block b = s.getBlock();
				if(b instanceof TemporalAcceleratorBlock)
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
