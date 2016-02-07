package tardis.client.renderer.portal;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import org.lwjgl.opengl.GL11;

public class PortalWorld extends World
{
	private final World				subWorld;
	private final SimpleCoordStore	door;
	private final int				facing;

	public PortalWorld(World w, SimpleCoordStore d)
	{
		super(w.getSaveHandler(), w.getWorldInfo().getWorldName(), w.provider, new WorldSettings(w.getWorldInfo()), w.theProfiler);
		door = d;
		facing = door.getMetadata() % 4;
		subWorld = w;
	}

	public boolean isValid(int x, int y, int z)
	{
		return true;
	}

	public List getTEs()
	{
		return subWorld.loadedTileEntityList;
	}

	public List getEnts()
	{
		return subWorld.loadedEntityList;
	}

	@Override
	public Block getBlock(int x, int y, int z)
    {
		if(!isValid(x,y,z)) return Blocks.air;
		return subWorld.getBlock(x, y, z);
    }

	@Override
	public int getBlockMetadata(int x, int y, int z)
    {
		if(!isValid(x,y,z)) return 0;
		return subWorld.getBlockMetadata(x, y, z);
    }

	@Override
	public TileEntity getTileEntity(int x, int y, int z)
    {
		if(!isValid(x,y,z)) return null;
		return subWorld.getTileEntity(x, y, z);
    }

	@Override
	protected boolean chunkExists(int x, int z)
    {
		return subWorld.getChunkProvider().chunkExists(x, z);
    }

	@Override
	public Chunk getChunkFromChunkCoords(int x, int z)
    {
        return subWorld.getChunkFromChunkCoords(x, z);
    }

	@Override
	protected IChunkProvider createChunkProvider()
	{
		return null;
	}

	@Override
	protected int func_152379_p()
	{
		return 0;
	}

	@Override
	public Entity getEntityByID(int p_73045_1_)
	{
		return null;
	}

	public void render()
	{
		IBlockAccess iba = RenderBlocks.getInstance().blockAccess;
		RenderBlocks.getInstance().blockAccess = this;
		GL11.glPushMatrix();
		GL11.glTranslated(0, 0.5, 0);
		GL11.glScaled(10, 10, 10);
		double o = 0.1;
		for(int x = -16; x <= 16; x++)
		{
			for(int y = -16; y <= 16; y++)
			{
				for(int z = -1; z <= 16; z++)
				{
					Block b = getBlock(door.x+x,door.y+y,door.z+z);
					if(b == Blocks.air) continue;
					GL11.glPushMatrix();
					GL11.glTranslated(o*x, o*y, o*z);
					RenderBlocks.getInstance().renderBlockAllFaces(b, door.x+x, door.y+y, door.z+z);
					GL11.glPopMatrix();
				}
			}
		}
		GL11.glPopMatrix();
		RenderBlocks.getInstance().blockAccess = iba;
	}
}
