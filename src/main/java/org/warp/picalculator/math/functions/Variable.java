package org.warp.picalculator.math.functions;

import org.warp.picalculator.Error;
import org.warp.picalculator.gui.expression.blocks.Block;
import org.warp.picalculator.gui.expression.blocks.BlockChar;
import org.warp.picalculator.math.Function;
import org.warp.picalculator.math.MathContext;
import org.warp.picalculator.math.rules.Rule;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class Variable implements Function {

	protected char var;
	protected final MathContext root;
	protected V_TYPE type = V_TYPE.CONSTANT;

	public Variable(MathContext root, char val, V_TYPE type) {
		this.root = root;
		var = val;
		this.type = type;
	}

	public Variable(MathContext root, String s, V_TYPE type) throws Error {
		this(root, s.charAt(0), type);
	}

	public char getChar() {
		return var;
	}

	public Variable setChar(char val) {
		return new Variable(root, val, type);
	}

	public V_TYPE getType() {
		return type;
	}

	public Variable setType(V_TYPE typ) {
		return new Variable(root, var, typ);
	}

	@Override
	public String toString() {
		return "" + getChar();
	}

	public static class VariableValue {
		public final Variable v;
		public final Number n;

		public VariableValue(Variable v, Number n) {
			this.v = v;
			this.n = n;
		}
	}

	@Override
	public ObjectArrayList<Function> simplify(Rule rule) throws Error, InterruptedException {
		return rule.execute(this);
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Variable) {
			return ((Variable) o).getChar() == var && ((Variable) o).getType() == type;
		}
		return false;
	}

	@Override
	public MathContext getMathContext() {
		return root;
	}

	@Override
	public Variable clone() {
		return new Variable(root, var, type);
	}

	public static enum V_TYPE {
		CONSTANT, VARIABLE, SOLUTION
	}

	@Override
	public Function setParameter(int index, Function var) throws IndexOutOfBoundsException {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public Function getParameter(int index) throws IndexOutOfBoundsException {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public ObjectArrayList<Block> toBlock(MathContext context) {
		final ObjectArrayList<Block> result = new ObjectArrayList<>();
		//TODO: Temporary solution. In near future Variables will be distint objects and they will have a color. So they will be no longer a BlockChar/FeatureChar
		result.add(new BlockChar(getChar()));
		return result;
	}
}
