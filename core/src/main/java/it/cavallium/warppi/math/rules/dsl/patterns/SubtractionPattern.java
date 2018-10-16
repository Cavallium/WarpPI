package it.cavallium.warppi.math.rules.dsl.patterns;

import it.cavallium.warppi.math.Function;
import it.cavallium.warppi.math.MathContext;
import it.cavallium.warppi.math.functions.Subtraction;
import it.cavallium.warppi.math.rules.dsl.Pattern;
import it.cavallium.warppi.math.rules.dsl.PatternUtils;
import it.cavallium.warppi.math.rules.dsl.VisitorPattern;

import java.util.Map;
import java.util.Optional;

/**
 * Matches and generates a subtraction of two other patterns.
 */
public class SubtractionPattern extends VisitorPattern {
    private final Pattern left;
    private final Pattern right;

    public SubtractionPattern(final Pattern left, final Pattern right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Optional<Map<String, Function>> visit(final Subtraction subtraction) {
        return PatternUtils.matchFunctionOperatorParameters(subtraction, left, right);
    }

    @Override
    public Function replace(final MathContext mathContext, final Map<String, Function> subFunctions) {
        return new Subtraction(
                mathContext,
                left.replace(mathContext, subFunctions),
                right.replace(mathContext, subFunctions)
        );
    }
}