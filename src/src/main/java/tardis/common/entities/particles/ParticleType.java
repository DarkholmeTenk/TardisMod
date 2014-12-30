package tardis.common.entities.particles;

public enum ParticleType
{
	NANOGENE;
	
	public static ParticleType get(int type)
	{
		ParticleType[] types = values();
		if(type >= 0 && type < types.length)
			return types[type];
		return NANOGENE;
	}
}
