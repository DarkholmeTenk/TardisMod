package tardis.common.tileents.components;

public enum TardisTEComponent
{
		TRANSMAT	("Transmat","stickTrans",new TardisComponentTransmat()),
		GRID		("Mass-Energy","stickGrid",new TardisComponentGrid()),
		ENERGY		("RF","stickEnergy",new TardisComponentEnergy()),
		INVENTORY	("Inv","stickInv",new TardisComponentInventory()),
		FLUID		("Fluid","stickFlu",new TardisComponentFluid()),
		CHUNK		("ChunkLoader","stickChLo", new TardisComponentChunkLoader()),
		COMPUTER	("Peripheral","stickPer", new TardisComponentPeripheral());
		
		private static String[] stringArray = null;
		public final String componentName;
		public final String tex;
		
		public ITardisComponent baseObj;
		TardisTEComponent(String name, String _tex, ITardisComponent comp)
		{
			tex = _tex;
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
