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
		return "tardisDefault";
	}

	@Override
	public void render(TardisTileEntity te)
	{
		bindTexture(tex);
		tardis.renderAll();
	}

	@Override
	public void registerClientResources()
	{
		tardis = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/tardis/oldTardis.obj"));
		tex = new ResourceLocation("tardismod","textures/models/tardis/oldTardis.png");
	}

}
