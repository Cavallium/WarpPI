/*
SETTINGS: (please don't move this part)
 PATH=FractionsRule2
*/

//Imports


import org.warp.picalculator.Error;
import org.warp.picalculator.math.Function;
import org.warp.picalculator.math.functions.Division;
import org.warp.picalculator.math.functions.Number;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * Fractions rule
 * a / 1 = a
 * 
 * @author Andrea Cavalli
 *
 */
public class FractionsRule2 implements Rule {
	// Rule name
	@Override
	public String getRuleName() {
		return "FractionsRule2";
	}

	// Rule type
	@Override
	public RuleType getRuleType() {
		return RuleType.CALCULATION;
	}

	/* Rule function
	   Returns:
	     - null if it's not executable on the function "f"
	     - An ObjectArrayList<Function> if it did something
	*/

	@Override
	public ObjectArrayList<Function> execute(Function f) {
		boolean isExecutable = false;
		if (f instanceof Division) {
			Division fnc = (Division) f;
			if (fnc.getParameter2() instanceof Number) {
				Number numb = (Number) fnc.getParameter2();
				if (numb.equals(new Number(f.getMathContext(), 1))) {
					isExecutable = true;
				}
			}
		}
	
		if (isExecutable) {
			ObjectArrayList<Function> result = new ObjectArrayList<>();
			Division fnc = (Division) f;
			result.add(fnc.getParameter1());
			return result;
		} else {
			return null;
		}
	}
}
