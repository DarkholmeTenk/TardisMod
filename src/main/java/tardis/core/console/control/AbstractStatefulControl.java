package tardis.core.console.control;

import tardis.core.console.control.models.IControlModel;
import tardis.core.console.control.models.ModelLever;

public abstract class AbstractStatefulControl extends AbstractControl
{
	private final IControlModel model;

	public AbstractStatefulControl(StatefulControlBuilder<? extends AbstractStatefulControl> builder, ControlHolder holder)
	{
		super(builder, builder.model.regularX(), builder.model.regularY(), builder.model.xAngle(), holder);
		model = builder.model;
	}

	public abstract float getState(float ptt);

	@Override
	public final void render(float ptt)
	{
		model.render(getState(ptt));
	}

	public abstract static class StatefulControlBuilder<T extends AbstractStatefulControl> extends ControlBuilder<T>
	{
		private IControlModel model = ModelLever.i;

		public StatefulControlBuilder()
		{

		}

		public StatefulControlBuilder<T> withModel(IControlModel newModel)
		{
			this.model = newModel;
			return this;
		}
	}

}
