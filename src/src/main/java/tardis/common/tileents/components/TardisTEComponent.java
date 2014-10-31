package tardis.common.tileents.components;

public enum TardisTEComponent
{
		TRANSMAT	("Transmat"	,new TardisComponentTransmat()),
		GRID		("Mass-Energy",new TardisComponentGrid()),
		ENERGY		("RF",new TardisComponentEnergy()),
		INVENTORY	("Inv",new TardisComponentInventory()),
		FLUID		("Fluid",new TardisComponentFluid());
		
		private static String[] stringArray = null;
		public String componentName;
		
		public ITardisComponent baseObj;
		TardisTEComponent(String name, ITardisComponent comp)
		{
			baseObj = comp;
			componentName = name;
		}
		
		public static String[] getStrings()
		{
			if(stringArray == null)
			{
				TardisTEComponent[] vals = values();
				stringArray = new String[vals.length];
				for(int i = 0; i < vals.length;i++)
					stringArray[i] = vals[i].componentName;
			}
			return stringArray;
		}
}
