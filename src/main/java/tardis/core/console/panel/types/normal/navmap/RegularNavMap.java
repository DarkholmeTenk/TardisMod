package tardis.core.console.panel.types.normal.navmap;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.darkcraft.darkcore.mod.helpers.MathHelper;

import tardis.core.console.control.ControlHolder;
import tardis.core.console.control.ControlLever;
import tardis.core.console.control.ControlWheel;

public class RegularNavMap implements NavMap
{
	public static final RegularNavMap i = new RegularNavMap();
	private RegularNavMap(){}

	private final static Cache<Class<? extends ControlHolder>, NavMapCoefficients> coeffCache =
			CacheBuilder.newBuilder()
				.maximumSize(20)
				.build();

	@Override
	public int getVal(Class<? extends ControlHolder> clazz, ControlWheel[] wheels, ControlLever[] levers)
	{
		NavMapCoefficients coeffs = getCoefficients(clazz, wheels, levers);
		int sum = 0;
		for(int i = 0; i < wheels.length; i++)
			sum += (levers[i].getValue() * (int) Math.pow(coeffs.wheelBases[i], wheels[i].getValue() + 1));
		for(int i = 0; i < (levers.length - wheels.length); i++)
			sum += (levers[i+wheels.length].getValue() * coeffs.leverCoeffs[i]);
		return sum;
	}

	private int closestWheel(int delta, ControlWheel wheel, ControlLever lever, int base, boolean set)
	{
		int c = 0;
		int closestWheel = 0;
		int closestLever = 0;
		lLoop:
		for (int i = lever.min; i <= lever.max; i++)
		{
			if((delta < 0) != (i < 0))
				continue;
			for (int j = wheel.min; j <= wheel.max; j++)
			{
				int val = i * (int) Math.pow(base, j + 1);
				if (Math.abs(delta - val) < Math.abs(delta - c))
				{
					closestWheel = j;
					closestLever = i;
					c = val;
					if(val == delta)
						break lLoop;
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

	private int closestLevers(int delta, int[] coeffs, ControlLever[] levers, int s, boolean set)
	{
		int v = 0;
		for(int i = 0; i < (levers.length - s); i++)
		{
			ControlLever l = levers[i + s];
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
	public boolean setVal(int dest, int tolerance,
			Class<? extends ControlHolder> clazz, ControlWheel[] wheels, ControlLever[] levers)
	{
		NavMapCoefficients coeffs = getCoefficients(clazz, wheels, levers);
		int t = 0;
		for(int i = 0; i < wheels.length; i++)
			t += closestWheel(dest - t, wheels[i], levers[i], coeffs.wheelBases[i], false);
		t += closestLevers(dest - t, coeffs.leverCoeffs, levers, wheels.length, false);
		if(Math.abs(dest - t) <= tolerance)
		{
			t = 0;
			for(int i = 0; i < wheels.length; i++)
				t += closestWheel(dest - t, wheels[i], levers[i], coeffs.wheelBases[i], true);
			t += closestLevers(dest - t, coeffs.leverCoeffs, levers, wheels.length, true);
			return true;
		}
		return false;
	}

	public static NavMapCoefficients getCoefficients(Class<? extends ControlHolder> clazz,
			ControlWheel[] wheels, ControlLever[] levers)
	{
		return coeffCache.asMap().computeIfAbsent(clazz, a->{
			NavMapCoefficients coeffs = new NavMapCoefficients();
			coeffs.wheelBases = new int[wheels.length];
			int b = 1;
			for(int i = wheels.length - 1; i >= 0; i--)
				coeffs.wheelBases[i] = b+=2;
			coeffs.leverCoeffs = new int[levers.length - wheels.length];
			coeffs.leverCoeffs[coeffs.leverCoeffs.length-1] = 1;
			for(int i = coeffs.leverCoeffs.length-2; i >= 0; i--)
			{
				ControlLever prevLever = levers[i+wheels.length+1];
				coeffs.leverCoeffs[i] = coeffs.leverCoeffs[i+1] * ((1+prevLever.max) - prevLever.min);
			}
			return coeffs;
		});
	}

	private static class NavMapCoefficients
	{
		private int[] wheelBases;
		private int[] leverCoeffs;
	}
}
