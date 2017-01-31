package org.warp.picalculator.math.rules;

import java.util.ArrayList;

import org.warp.picalculator.Error;
import org.warp.picalculator.math.functions.Function;
import org.warp.picalculator.math.functions.Number;
import org.warp.picalculator.math.functions.Power;

/**
 * Exponent rule<br>
 * <b>a^0=1</b>
 * 
 * @author Andrea Cavalli
 *
 */
public class ExponentRule3 {

	public static boolean compare(Function f) {
		final Power fnc = (Power) f;
		if (fnc.getVariable2().equals(new Number(f.getRoot(), 0))) {
			return true;
		}
		return false;
	}

	public static ArrayList<Function> execute(Function f) throws Error {
		final ArrayList<Function> result = new ArrayList<>();
		result.add(new Number(f.getRoot(), 1));
		return result;
	}

}
