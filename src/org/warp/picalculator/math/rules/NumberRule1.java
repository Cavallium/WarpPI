package org.warp.picalculator.math.rules;

import java.util.ArrayList;

import org.warp.picalculator.Error;
import org.warp.picalculator.math.Calculator;
import org.warp.picalculator.math.functions.Function;
import org.warp.picalculator.math.functions.Multiplication;
import org.warp.picalculator.math.functions.Number;

/**
 * Number rule<br>
 * <b>a * 0 = 0</b>
 * 
 * @author Andrea Cavalli
 *
 */
public class NumberRule1 {

	public static boolean compare(Function f) {
		final Calculator root = f.getRoot();
		final Multiplication mult = (Multiplication) f;
		if (mult.getVariable1() instanceof Number) {
			final Number numb = (Number) mult.getVariable1();
			if (numb.equals(new Number(root, 0))) {
				return true;
			}
		}
		if (mult.getVariable2() instanceof Number) {
			final Number numb = (Number) mult.getVariable2();
			if (numb.equals(new Number(root, 0))) {
				return true;
			}
		}
		return false;
	}

	public static ArrayList<Function> execute(Function f) throws Error {
		final ArrayList<Function> result = new ArrayList<>();
		result.add(new Number(f.getRoot(), "0"));
		return result;
	}

}
