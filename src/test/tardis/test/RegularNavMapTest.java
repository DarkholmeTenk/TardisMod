package tardis.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import tardis.core.console.panel.types.normal.NormalPanelX;

public class RegularNavMapTest
{
	private NormalPanelX xPanel;

	@Before
	public void newPanel()
	{
		xPanel = new NormalPanelX();
	}

	@Test
	public void testGet()
	{
		assertEquals(0, xPanel.getCurrentX());
	}

	@Test
	public void testSetThousand()
	{
		for(int i = -1000; i <= 1000; i++)
		{
			assertTrue("Can't set " + i, xPanel.setCurrentX(i, 0));
			assertEquals(i, xPanel.getCurrentX());
		}
	}

	@Test
	public void testDistance()
	{
		int i = 1;
		boolean canGoFurther = true;
		while(canGoFurther)
		{
			xPanel.setCurrentX(i, 0);
			canGoFurther = (i == xPanel.getCurrentX());
			i++;
		}
		System.out.println("Failed at " + i);
	}
}
