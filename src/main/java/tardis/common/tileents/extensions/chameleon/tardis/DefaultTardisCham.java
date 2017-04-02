package tardis.common.tileents.extensions.chameleon.tardis;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import tardis.common.tileents.TardisTileEntity;

public class DefaultTardisCham extends AbstractTardisChameleon
{
	private IModelCustom tardis;
	private ResourceLocation tex;

	@Override
	public String getName()
	{
		return "TardisCham.Default.name";
	}

	@Override
	public void render(TardisTileEntity te)
	{
		bindSkin(te);
		tardis.renderAll();
	}

	@Override
	public void registerClientResources()
	{
		tardis = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/tardis/oldTardis.obj"));
		tex = new ResourceLocation("tardismod","textures/models/tardis/TardisOld.png");
	}

	@Override
	public ResourceLocation defaultTex()
	{
		return tex;
	}

}
