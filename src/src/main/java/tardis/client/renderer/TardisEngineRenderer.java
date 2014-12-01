package tardis.client.renderer;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import tardis.TardisMod;
import tardis.common.blocks.TardisAbstractBlock;
import tardis.common.core.Helper;
import tardis.common.tileents.TardisCoreTileEntity;
import tardis.common.tileents.TardisEngineTileEntity;

public class TardisEngineRenderer extends TardisAbstractBlockRenderer
{
	TardisControlRenderer comps = null;
	
	@Override
	public TardisAbstractBlock getBlock()
	{
		return TardisMod.tardisEngineBlock;
	}
	
	private void renderRight(Tessellator tess, TardisEngineTileEntity eng, TardisCoreTileEntity core)
	{
		if(core != null)
		{
			comps.renderTextScreen(tess, eng, core.getOwner(), 0,  0.8, 2.4, -0.02,  0, 0, 180,  0.3, 0.3, 0.3);
		}
		comps.renderTextScreen(tess, eng, eng.currentPerson, 3,  0.65, 0.6, -0.02, 0, 0, 180,  0.3, 0.3, 0.3);
		comps.renderButton(tess,eng,4, 0.72,0.625,-0.0125, 90,0,0, 0.3,0.3,0.3);
		comps.renderButton(tess,eng,5, 0.72,0.535,-0.0125, 90,0,0, 0.3,0.3,0.3);
		comps.renderLight( tess,eng,6, 0.82,0.6155,-0.0125, 90,0,0, 0.3,0.3,0.3);
		comps.renderButton(tess,eng,7, 0.81,0.535,-0.0125, 90,0,0, 0.3,0.3,0.3);
	}
	
	private void renderFront(Tessellator tess, TardisEngineTileEntity eng, TardisCoreTileEntity core)
	{
		double base  = 0.10625;
		double delta = 0.2;
		comps.renderGauge(tess,eng,30,  1.0125,0.875,base+(3*delta), 180,-90,0, 0.6,0.6,0.6);
		comps.renderGauge(tess,eng,23,	1.0125,0.7,base, 180,-90,0, 0.6,0.6,0.6);
		comps.renderGauge(tess,eng,22,	1.0125,0.7,base+delta, 180,-90,0, 0.6,0.6,0.6);
		comps.renderGauge(tess,eng,21,	1.0125,0.7,base+(2*delta), 180,-90,0, 0.6,0.6,0.6);
		comps.renderGauge(tess,eng,20,	1.0125,0.7,base+(3*delta), 180,-90,0, 0.6,0.6,0.6);
		
		base += 0.07375;
		comps.renderButton(tess,eng,13,	1.04,0.45,base,			0,0,90, 0.6,0.6,0.6);
		comps.renderButton(tess,eng,12,	1.04,0.45,base+delta,		0,0,90, 0.6,0.6,0.6);
		comps.renderButton(tess,eng,11,	1.04,0.45,base+(2*delta),	0,0,90, 0.6,0.6,0.6);
		comps.renderButton(tess,eng,10,	1.04,0.45,base+(3*delta),	0,0,90, 0.6,0.6,0.6);
	}
	
	private void renderLeft(Tessellator tess, TardisEngineTileEntity eng, TardisCoreTileEntity core)
	{
		comps.renderScrewdriverHolder(tess, eng, 0.6, 0.5, 1, 90, 0, 0, 0.5, 0.5, 0.5);
		comps.renderScrewdriver(tess, eng, 0, 0.6, 0.5, 1.1, -90, 0, 0, 0.5, 0.5, 0.5);
		comps.renderButton(tess,eng, 41, 0.3, 0.388, 1.02, -90, 0, 0, 0.3, 0.3, 0.3);
		comps.renderButton(tess,eng, 44, 0.3, 0.488, 1.02, -90, 0, 0, 0.3, 0.3, 0.3);
		comps.renderButton(tess,eng, 45, 0.3, 0.588, 1.02, -90, 0, 0, 0.3, 0.3, 0.3);
		comps.renderLight(tess, eng, 51, 0.4, 0.4, 1.02, -90, 0, 0, 0.3, 0.3, 0.3);
		comps.renderLight(tess, eng, 54, 0.4, 0.5, 1.02, -90, 0, 0, 0.3, 0.3, 0.3);
		comps.renderLight(tess, eng, 55, 0.4, 0.6, 1.02, -90, 0, 0, 0.3, 0.3, 0.3);
	}
	
	private void renderBack(Tessellator tess, TardisEngineTileEntity eng, TardisCoreTileEntity core)
	{
		
	}

	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z)
	{
		if(comps == null)
			comps = new TardisControlRenderer(func_147498_b(),field_147501_a.field_147553_e);
		if(te instanceof TardisEngineTileEntity)
		{
			TardisCoreTileEntity core = Helper.getTardisCore(te.getWorldObj());
			TardisEngineTileEntity eng = (TardisEngineTileEntity)te;
			renderRight(tess,eng,core);
			renderFront(tess,eng,core);
			renderLeft (tess,eng,core);
			renderBack (tess,eng,core);
		}
	}

}
