package tardis.core.console.panel.types.normal.navmap;

import io.darkcraft.darkcore.mod.helpers.MathHelper;

import tardis.core.console.control.ControlLever;
import tardis.core.console.control.ControlWheel;

public class RegularNavMap implements NavMap
{
	public static final RegularNavMap i = new RegularNavMap();
	private RegularNavMap(){}

	private static final int BASE_1 = 2;
	private static final int BASE_2 = 4;
	private static final int COEFF_1 = 2197;
	private static final int COEFF_2 = 169;
	private static final int COEFF_3 = 13;
	private static final int COEFF_4 = 1;

	@Override
	public int getVal(ControlWheel[] wheels, ControlLever[] levers)
	{
		int controlOne = levers[0].getValue() * (int) Math.pow(BASE_1, wheels[0].getValue() + 1);
		int controlTwo = levers[1].getValue() * (int) Math.pow(BASE_2, wheels[1].getValue() + 1);
		int controlThree =    (COEFF_1 * levers[2].getValue())
							+ (COEFF_2 * levers[3].getValue())
							+ (COEFF_3 * levers[4].getValue())
							+ (COEFF_4 * levers[5].getValue());
		return controlOne + controlTwo + controlThree;
	}

	private int closestWheel(int delta, ControlWheel wheel, ControlLever lever, int base, boolean set)
	{
		int c = 0;
		int closestWheel = 0;
		int closestLever = 0;
		for (int i = lever.min; i <= lever.max; i++)
		{
			for (int j = wheel.min; j <= wheel.max; j++)
			{
				int val = i * (int) Math.pow(base, j + 1);
				if (Math.abs(delta - val) < Math.abs(delta - c))
				{
					closestWheel = j;
					closestLever = i;
					c = val;
				}
			}
		}
		if(set)
		{
			wheel.setValue(closestWheel);
			lever.setValue(closestLever);
		}
		return c;
	}

	private static final int[] coeffs = {COEFF_1, COEFF_2, COEFF_3, COEFF_4};
	private int closestLevers(int delta, ControlLever[] levers, boolean set)
	{
		int v = 0;
		for(int i = 0; i < 4; i++)
		{
			ControlLever l = levers[i + 2];
			int coeff = coeffs[i];
			int diff = delta - v;
			int newSetting = MathHelper.clamp(MathHelper.round(diff / (double) coeff), l.min, l.max);
			if(set)
				l.setValue(newSetting);
			v += newSetting * coeff;
		}
		return v;
	}

	@Override
	public boolean setVal(int dest, int tolerance, ControlWheel[] wheels, ControlLever[] levers)
	{
		int cA = closestWheel(dest, wheels[0], levers[0], BASE_1, false);
		int cB = closestWheel(dest - cA, wheels[0], levers[0], BASE_2, false);
		int cL = closestLevers(dest - cA - cB, levers, false);
		int nV = cA + cB + cL;
		if(Math.abs(dest - nV) <= tolerance)
		{
			closestWheel(dest, wheels[0], levers[0], BASE_1, true);
			closestWheel(dest - cA, wheels[1], levers[1], BASE_2, true);
			closestLevers(dest - cA - cB, levers, true);
			return true;
		}
		return false;
	}

}
