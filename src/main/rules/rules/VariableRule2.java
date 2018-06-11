package rules;
/*
SETTINGS: (please don't move this part)
 PATH=VariableRule2
*/

import org.warp.picalculator.math.Function;
import org.warp.picalculator.math.FunctionOperator;
import org.warp.picalculator.math.FunctionDynamic;
import org.warp.picalculator.math.FunctionSingle;
import org.warp.picalculator.math.MathContext;
//Imports

import org.warp.picalculator.Error;
import org.warp.picalculator.math.Function;
import org.warp.picalculator.math.FunctionOperator;
import org.warp.picalculator.math.MathContext;
import org.warp.picalculator.math.functions.Multiplication;
import org.warp.picalculator.math.functions.Number;
import org.warp.picalculator.math.functions.Subtraction;
import org.warp.picalculator.math.functions.Sum;
import org.warp.picalculator.math.rules.Rule;
import org.warp.picalculator.math.rules.RuleType;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * Variable rule
 * ax+x=(a+1)*x (a,b NUMBER; x VARIABLES)
 * 
 * @author Andrea Cavalli
 *
 */
public class VariableRule2 implements Rule {
	// Rule name
	@Override
	public String getRuleName() {
		return "VariableRule2";
	}

	// Rule type
	@Override
	public RuleType getRuleType() {
		return RuleType.REDUCTION;
	}

	/* Rule function
	   Returns:
	     - null if it's not executable on the function "f"
	     - An ObjectArrayList<Function> if it did something
	*/

	@Override
	public ObjectArrayList<Function> execute(Function f) {
		boolean isExecutable = false;
		if (f instanceof Sum || f instanceof Subtraction) {
			FunctionOperator fnc = (FunctionOperator) f;
			if (fnc.getParameter1() instanceof Multiplication) {
				FunctionOperator m1 = (FunctionOperator) fnc.getParameter1();
				if (m1.getParameter2().equals(fnc.getParameter2())) {
					isExecutable = true;
				}
			}
		}

		if (isExecutable) {
			FunctionOperator fnc = (FunctionOperator) f;
			MathContext root = fnc.getMathContext();
			ObjectArrayList<Function> result = new ObjectArrayList<>();
			FunctionOperator m1 = (FunctionOperator) fnc.getParameter1();
			Function a = m1.getParameter1();
			Function x = fnc.getParameter2();

			Function rets;
			if (fnc instanceof Sum) {
				rets = new Sum(root, a, new Number(root, 1));
			} else {
				rets = new Subtraction(root, a, new Number(root, 1));
			}
			Function retm = new Multiplication(root, rets, x);
			result.add(retm);
			return result;
		} else {
			return null;
		}
	}
}
