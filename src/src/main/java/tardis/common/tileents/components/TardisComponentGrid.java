package tardis.common.tileents.components;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import appeng.api.events.LocatableEventAnnounce;
import appeng.api.features.ILocatable;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.parts.BusSupport;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartCollsionHelper;
import appeng.api.parts.IPartHost;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.parts.PartItemStack;
import appeng.api.util.AECableType;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.core.store.SimpleCoordStore;
import tardis.common.tileents.TardisComponentTileEntity;
import tardis.common.tileents.TardisCoreTileEntity;

public class TardisComponentGrid extends TardisAbstractComponent implements IPart, IGridHost, ILocatable
{
	private boolean inited = false;
	private SimpleCoordStore myCoords = null;
	
	protected TardisComponentGrid()	{	}
	public TardisComponentGrid(TardisComponentTileEntity parent)
	{
		//parentAdded(parent);
	}

	@Override
	public ITardisComponent create(TardisComponentTileEntity parent)
	{
		return new TardisComponentGrid(parent);
	}
	
	@Override
	public void updateTick()
	{
		if(!inited && parentObj != null)
		{
			inited = true;
			TardisCoreTileEntity core = Helper.getTardisCore(parentObj);
			if(core != null)
			{
				core.addGridLink(myCoords);
			}
		}
	}
	
	@Override
	public void parentAdded(TardisComponentTileEntity parent)
	{
		super.parentAdded(parent);
		myCoords = new SimpleCoordStore(parent);
	}
	
	@Override
	public void revive(TardisComponentTileEntity parent)
	{
		TardisOutput.print("TCG", "Reviving TCG");
		super.revive(parent);
		MinecraftForge.EVENT_BUS.post(new LocatableEventAnnounce(this,LocatableEventAnnounce.LocatableEvent.Register));
	}
	
	@Override
	public void die()
	{
		if(parentObj != null && parentObj.getWorldObj() != null)
			MinecraftForge.EVENT_BUS.post(new LocatableEventAnnounce(this,LocatableEventAnnounce.LocatableEvent.Unregister));
		super.die();
	}
	@Override
	public long getLocatableSerial()
	{
		if(myCoords == null)
			myCoords = new SimpleCoordStore(parentObj);
		return myCoords.hashCode();
	}
	@Override
	public IGridNode getGridNode(ForgeDirection dir)
	{
		TardisCoreTileEntity core = Helper.getTardisCore(parentObj);
		if(core != null)
		{
			//if(core.grid == null)
			//	core.grid = TardisMod.aeAPI.createGridNode(this);
		}
		return null;
	}
	@Override
	public AECableType getCableConnectionType(ForgeDirection dir)
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void securityBreak()
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void getBoxes(IPartCollsionHelper bch)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public ItemStack getItemStack(PartItemStack type)
	{
		return new ItemStack(TardisMod.tardisCoreBlock,1);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventory(IPartRenderHelper rh, RenderBlocks renderer)
	{
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void renderStatic(int x, int y, int z, IPartRenderHelper rh, RenderBlocks renderer)
	{
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void renderDynamic(double x, double y, double z, IPartRenderHelper rh, RenderBlocks renderer)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getBreakingTexture()
	{
		return null;
	}
	@Override
	public boolean requireDynamicRender()
	{
		return false;
	}
	@Override
	public boolean isSolid()
	{
		return false;
	}
	@Override
	public boolean canConnectRedstone()
	{
		return false;
	}
	@Override
	public int getLightLevel()
	{
		return 0;
	}
	@Override
	public boolean isLadder(EntityLivingBase entity)
	{
		return false;
	}
	@Override
	public void onNeighborChanged()
	{
	}
	@Override
	public int isProvidingStrongPower()
	{
		return 0;
	}
	@Override
	public int isProvidingWeakPower()
	{
		return 0;
	}
	@Override
	public void writeToStream(ByteBuf data) throws IOException
	{
	}
	@Override
	public boolean readFromStream(ByteBuf data) throws IOException
	{
		return false;
	}
	@Override
	public IGridNode getGridNode()
	{
		TardisCoreTileEntity core = Helper.getTardisCore(parentObj.getWorldObj());
		if(core != null)
		{
			if(core.grid == null)
				core.grid = TardisMod.aeAPI.createGridNode(core);
			return core.grid;
		}
		return null;
	}
	@Override
	public void onEntityCollision(Entity entity)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void removeFromWorld()
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void addToWorld()
	{
		
	}
	
	@Override
	public IGridNode getExternalFacingNode()
	{
		TardisCoreTileEntity core = Helper.getTardisCore(parentObj.getWorldObj());
		if(core != null)
			return core.grid;
		return null;
	}
	@Override
	public void setPartHostInfo(ForgeDirection side, IPartHost host, TileEntity tile)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onActivate(EntityPlayer player, Vec3 pos)
	{
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onShiftActivate(EntityPlayer player, Vec3 pos)
	{
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void getDrops(List<ItemStack> drops, boolean wrenched)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public int cableConnectionRenderTo()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random r)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPlacement(EntityPlayer player, ItemStack held, ForgeDirection side)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean canBePlacedOn(BusSupport what)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
