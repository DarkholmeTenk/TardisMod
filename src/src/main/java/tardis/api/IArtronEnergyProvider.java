package tardis.api;

public interface IArtronEnergyProvider
{
	public int getMaxArtronEnergy();
	
	public int getArtronEnergy();
	
	public boolean addArtronEnergy(int amount, boolean sim);
	
	public boolean takeArtronEnergy(int amount, boolean sim);
}
