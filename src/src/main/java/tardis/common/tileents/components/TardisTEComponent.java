package tardis.common.tileents.components;

public enum TardisTEComponent
{
		TRANSMAT	("Transmat","stickTrans",new TardisComponentTransmat(),ValPos.INSIDE),
		GRID		("Mass-Energy","stickGrid",new TardisComponentGrid(),ValPos.BOTH),
		ENERGY		("RF","stickEnergy",new TardisComponentEnergy(),ValPos.BOTH),
		INVENTORY	("Inv","stickInv",new TardisComponentInventory(),ValPos.BOTH),
		FLUID		("Fluid","stickFlu",new TardisComponentFluid(),ValPos.BOTH),
		CHUNK		("ChunkLoader","stickChLo", new TardisComponentChunkLoader(),ValPos.BOTH),
		COMPUTER	("Peripheral","stickPer", new TardisComponentPeripheral(),ValPos.BOTH),
		NANOGENE	("Nanogene","stickNano", new TardisComponentNanogene(),ValPos.BOTH);
		
		public enum ValPos
		{
			INSIDE, OUTSIDE, BOTH;
		}
		
		private static String[] stringArray = null;
		public final ValPos valPos;
		public final String componentName;
		public final String tex;
		
		public ITardisComponent baseObj;
		TardisTEComponent(String name, String _tex, ITardisComponent comp, ValPos validPosition)
		{
			tex = _tex;
			baseObj = comp;
			componentName = name;
			valPos = validPosition;
		}
		
		public boolean isValid(boolean inside)
		{
			if(inside)
				return valPos!=ValPos.OUTSIDE;
			return valPos!=ValPos.INSIDE;
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
