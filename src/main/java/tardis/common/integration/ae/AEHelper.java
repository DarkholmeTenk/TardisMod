package tardis.common.integration.ae;

import appeng.api.AEApi;
import appeng.api.IAppEngApi;

public class AEHelper
{
	public static IAppEngApi	aeAPI	= null;

	public static void init()
	{
		try
		{
			AEHelper.aeAPI = AEApi.instance();
		}
		catch (Exception e)
		{
			System.err.println("Error loading AE API");
		}
		;
	}

}
