package tardis.common.blocks;

import java.util.EnumSet;
import java.util.Random;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlockContainer;
import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.datastore.SimpleDoubleCoordStore;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
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
import tardis.common.tileents.TemporalAcceleratorTileEntity;
import tardis.common.tileents.extensions.CraftingComponentType;
import tardis.common.tileents.extensions.LabFlag;
import tardis.common.tileents.extensions.LabRecipe;

public class TemporalAcceleratorBlock extends AbstractBlockContainer implements IScrewablePrecise
{
	
	public IIcon[] icons = new IIcon[6];
	
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
							new ItemStack(TardisMod.interiorDirtBlock,Configs.numDirtRecipe),
							new ItemStack(TardisMod.compressedBlock,2,0),
							new ItemStack(TardisMod.compressedBlock,2,1),
							CraftingComponentType.KONTRON.getIS(1)},
					new ItemStack[] { getIS(1, 0) },
					EnumSet.of(LabFlag.INFLIGHT),
					500
					));
	}
	
	@Override
	public int tickRate(World w)
    {
		if(Helper.isTardisWorld(w))
			return 1;
		return 100;
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
	
	@Override
	public void registerBlockIcons(IIconRegister reg) {
		this.icons[0] = reg.registerIcon(TardisMod.modName+":TemporalAccelerator");
		this.icons[1] = reg.registerIcon(TardisMod.modName+":TemporalAcceleratorTop");
		for (int i = 2; i < 6; i ++) {
			this.icons[i] = reg.registerIcon(TardisMod.modName+":TemporalAccelerator");
	    }
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
	    return this.icons[side];
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TemporalAcceleratorTileEntity();
	}

	@Override
	public Class<? extends TileEntity> getTEClass() {
		return TemporalAcceleratorTileEntity.class;
	}

}
