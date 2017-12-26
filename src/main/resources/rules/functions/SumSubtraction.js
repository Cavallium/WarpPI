// Imports
var ObjectArrayList = Java.type("it.unimi.dsi.fastutil.objects.ObjectArrayList");
var ScriptUtils = org.warp.picalculator.ScriptUtils;
var Rule = org.warp.picalculator.math.rules.Rule;
var RuleType = org.warp.picalculator.math.rules.RuleType;
var RulesManager = org.warp.picalculator.math.rules.RulesManager;
var Multiplication = org.warp.picalculator.math.functions.Multiplication;
var Sum = org.warp.picalculator.math.functions.Sum;
var Subtraction = org.warp.picalculator.math.functions.Subtraction;
var SumSubtraction = org.warp.picalculator.math.functions.SumSubtraction;
var Number = org.warp.picalculator.math.functions.Number;
var Division = org.warp.picalculator.math.functions.Division;

/**
 * SumSumbraction
 * a±b = c, d
 * 
 * @author Andrea Cavalli
 *
 */
var rule = {
	// Rule name
	getRuleName: function() {
		return "SumSubtraction";
	},
	// Rule type
	getRuleType: function() {
		return RuleType.CALCULATION;
	},
	/* Rule function
	   Returns:
	     - null if it's not executable on the function "f"
	     - An ObjectArrayList<Function> if it did something
	*/
	execute: function(f) {
		if (ScriptUtils.instanceOf(f, SumSubtraction.class)) {
			var result = new ObjectArrayList();
			var variable1 = f.getParameter1();
			var variable2 = f.getParameter2();
			var mathContext = f.getMathContext();
			if (ScriptUtils.instanceOf(variable1, Number.class) && ScriptUtils.instanceOf(variable2, Number.class)) {
				//a±b = c, d
				result.add(variable1.add(variable2));
				result.add(variable1.add(variable2.multiply(new Number(mathContext, -1))));
				return result;
			}
		}
		return null;
	}
}

//Add this rule to the list of rules
RulesManager.addRule(engine.getInterface(rule, Rule.class));