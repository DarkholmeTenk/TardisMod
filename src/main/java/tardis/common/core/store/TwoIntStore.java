package tardis.common.core.store;

public class TwoIntStore
{

	private final int l;
	private final int u;
	private final boolean hasU;
	
	public TwoIntStore(int a)
	{
		l = a;
		u = -1;
		hasU = false;
	}
	
	public TwoIntStore(int a, int b)
	{
		l = a;
		u = b;
		hasU = true;
	}
	
	public boolean within(int x)
	{
		if(!hasU)
			return x == l;
		return (x >=l && x<=u);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (hasU ? 1231 : 1237);
		result = prime * result + l;
		result = prime * result + u;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		TwoIntStore other = (TwoIntStore) obj;
		if (hasU != other.hasU)
		{
			return false;
		}
		if (l != other.l)
		{
			return false;
		}
		if (u != other.u)
		{
			return false;
		}
		return true;
	}
}
