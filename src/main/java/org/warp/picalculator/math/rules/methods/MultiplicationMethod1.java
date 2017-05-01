package org.warp.picalculator.math.rules.methods;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.warp.picalculator.Error;
import org.warp.picalculator.math.MathContext;
import org.warp.picalculator.math.Function;
import org.warp.picalculator.math.functions.Multiplication;
import org.warp.picalculator.math.functions.Number;

/**
 * Multiplication method<br>
 * <b>Example: X*3*X*2 = 6*X^2</b>
 * 
 * @author Andrea Cavalli
 *
 */
public class MultiplicationMethod1 {

	public static boolean compare(Function f) {
		return ((Multiplication) f).getParameter1().isSimplified() && ((Multiplication) f).getParameter2().isSimplified() && !(((Multiplication) f).getParameter1() instanceof Number && ((Multiplication) f).getParameter2() instanceof Number) && getFirstWorkingMultiplicationCouple(getMultiplicationElements(f)) != null;
	}

	public static ObjectArrayList<Function> execute(Function f) throws Error {
		Function result;
		final MathContext root = f.getMathContext();
		final ObjectArrayList<Function> elements = getMultiplicationElements(f);
		final int[] workingElementCouple = getFirstWorkingMultiplicationCouple(elements);
		final Function elem1 = elements.get(workingElementCouple[0]);
		final Function elem2 = elements.get(workingElementCouple[1]);

		final int size = elements.size();
		Function prec = new Multiplication(root, elem1, elem2);
		for (int i = size - 1; i >= 0; i--) {
			if (i != workingElementCouple[0] & i != workingElementCouple[1]) {
				final Function a = prec;
				final Function b = elements.get(i);
				prec = new Multiplication(root, a, b);
			}
		}

		result = prec;

		final ObjectArrayList<Function> results = new ObjectArrayList<>();
		results.add(result);
		return results;
	}

	private static ObjectArrayList<Function> getMultiplicationElements(Function mult) {
		final ObjectArrayList<Function> elements = new ObjectArrayList<>();
		while (mult instanceof Multiplication) {
			elements.add(((Multiplication) mult).getParameter1());
			mult = ((Multiplication) mult).getParameter2();
		}
		elements.add(mult);
		return elements;
	}

	private static int[] getFirstWorkingMultiplicationCouple(ObjectArrayList<Function> elements) {
		final int size = elements.size();
		Function a;
		Function b;
		if (elements.size() == 0) {
			return null;
		}
		if (elements.size() == 2) {
			return null;
		}
		final MathContext root = elements.get(0).getMathContext();
		for (int i = 0; i < size; i++) {
			a = elements.get(i);
			for (int j = 0; j < size; j++) {
				b = elements.get(j);
				if (i != j) {
					Function testFunc;
					testFunc = new Multiplication(root, a, b);
					if (!testFunc.isSimplified()) {
						return new int[] { i, j };
					}
				}
			}
		}
		return null;
	}

}
