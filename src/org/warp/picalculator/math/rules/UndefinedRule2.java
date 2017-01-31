package org.warp.picalculator.math.rules;

import java.util.ArrayList;

import org.warp.picalculator.Error;
import org.warp.picalculator.math.Calculator;
import org.warp.picalculator.math.functions.Division;
import org.warp.picalculator.math.functions.Function;
import org.warp.picalculator.math.functions.Number;
import org.warp.picalculator.math.functions.Undefined;

/**
 * Undefined rule<br>
 * <b>a / 0 = undefined</b>
 * 
 * @author Andrea Cavalli
 *
 */
public class UndefinedRule2 {

	public static boolean compare(Function f) {
		final Calculator root = f.getRoot();
		final Division fnc = (Division) f;
		if (fnc.getVariable2() instanceof Number) {
			final Number numb = (Number) fnc.getVariable2();
			if (numb.equals(new Number(root, 0))) {
				return true;
			}
		}
		return false;
	}

	public static ArrayList<Function> execute(Function f) throws Error {
		final Calculator root = f.getRoot();
		final ArrayList<Function> result = new ArrayList<>();
		result.add(new Undefined(root));
		return result;
	}

}
