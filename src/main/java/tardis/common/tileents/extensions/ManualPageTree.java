package tardis.common.tileents.extensions;

import java.util.ArrayList;
import java.util.List;

public class ManualPageTree
{
	public final ManualPage page;
	public final ManualPageTree[] children;
	public static ManualPageTree topTree = null;

	static
	{
		ManualPageTree[] nullT	= null;
		ManualPageTree conF		= new ManualPageTree(ManualPage.CONSOLEF, nullT);
		ManualPageTree conR		= new ManualPageTree(ManualPage.CONSOLER, nullT);
		ManualPageTree conL		= new ManualPageTree(ManualPage.CONSOLEL, nullT);
		ManualPageTree conB		= new ManualPageTree(ManualPage.CONSOLEB, nullT);
		ManualPageTree con		= new ManualPageTree(ManualPage.CONSOLE, conF,conR,conL,conB);
		ManualPageTree uncoord	= new ManualPageTree(ManualPage.UNCOORDINATED, nullT);
		ManualPageTree coords	= new ManualPageTree(ManualPage.COORDS, nullT);
		ManualPageTree takeoff	= new ManualPageTree(ManualPage.TAKEOFF, nullT);
		ManualPageTree damage	= new ManualPageTree(ManualPage.DAMAGE, nullT);
		ManualPageTree flight	= new ManualPageTree(ManualPage.FLIGHT, con, coords, takeoff, uncoord, damage);
		ManualPageTree recChr	= new ManualPageTree(ManualPage.LRCHRONO, nullT);
		ManualPageTree recDal	= new ManualPageTree(ManualPage.LRDALEK, nullT);
		ManualPageTree recKon	= new ManualPageTree(ManualPage.LRKONTRON, nullT);
		ManualPageTree recDir	= new ManualPageTree(ManualPage.LRTEMPDIRT, nullT);
		ManualPageTree lab		= new ManualPageTree(ManualPage.LAB, recChr, recDal, recKon, recDir);
		ManualPageTree craft	= new ManualPageTree(ManualPage.CRAFTING, lab);
		ManualPageTree grav		= new ManualPageTree(ManualPage.GRAVLIFT, nullT);
		ManualPageTree batt		= new ManualPageTree(ManualPage.BATTERY, nullT);
		ManualPageTree roundels	= new ManualPageTree(ManualPage.ROUNDELS, nullT);
		ManualPageTree landpad	= new ManualPageTree(ManualPage.LANDPAD, nullT);
		ManualPageTree engine	= new ManualPageTree(ManualPage.ENGINE, nullT);
		ManualPageTree sonic	= new ManualPageTree(ManualPage.SONIC, nullT);
		ManualPageTree tools	= new ManualPageTree(ManualPage.TOOLS, sonic, grav, batt, roundels, landpad, engine);
		ManualPageTree creds	= new ManualPageTree(ManualPage.CREDITS, nullT);
		topTree = new ManualPageTree(ManualPage.MAIN, flight, craft, tools, creds);
	}

	public ManualPageTree(ManualPage _page, ManualPageTree... _children)
	{
		page = _page;
		children = _children;
	}

	private String spaces(int spaces)
	{
		if(spaces == 0)
			return "";
		StringBuilder sb = new StringBuilder(spaces);
		for(int i = 0; i < spaces; i++)
			sb.append("  ");
		return sb.toString();
	}

	public List<String> getString(ManualPage currentlySelected, int depth)
	{
		ArrayList<String> listSoFar = new ArrayList<String>();
		String spaces = spaces(depth);
		if(children != null)
		{
			boolean doesContainSelected = page == currentlySelected;
			for(ManualPageTree child : children)
			{
				List<String> childList = child.getString(currentlySelected, depth+1);
				if((child.page == currentlySelected) || (childList.size() > 1))
					doesContainSelected = true;
			}
			listSoFar.add(spaces + (doesContainSelected? "-":"+") + page.title);
			if(!doesContainSelected)
				return listSoFar;
			for(ManualPageTree child : children)
			{
				List<String> childList = child.getString(currentlySelected, depth+1);
				listSoFar.addAll(childList);
			}
		}
		else
			listSoFar.add(spaces + page.title);

		return listSoFar;
	}
}
