package tardis.common.tileents.extensions.chameleon.tardis;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import tardis.common.tileents.TardisTileEntity;

public class NewTardisCham extends AbstractTardisChameleon
{
	private IModelCustom tardis;
	private ResourceLocation tex;

	@Override
	public String getName()
	{
		return "TardisCham.New.name";
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
		tardis = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/tardis/newTardis.obj"));
		tex = new ResourceLocation("tardismod","textures/models/tardis/Tardis.png");
	}

	@Override
	public ResourceLocation defaultTex()
	{
		return tex;
	}

}
