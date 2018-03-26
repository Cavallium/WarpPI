package org.warp.picalculator.math.functions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;
import org.nevec.rjm.BigDecimalMath;
import org.warp.picalculator.Error;
import org.warp.picalculator.Utils;
import org.warp.picalculator.gui.expression.blocks.Block;
import org.warp.picalculator.gui.expression.blocks.BlockChar;
import org.warp.picalculator.gui.expression.blocks.BlockContainer;
import org.warp.picalculator.gui.expression.blocks.BlockExponentialNotation;
import org.warp.picalculator.gui.expression.blocks.BlockPower;
import org.warp.picalculator.math.Function;
import org.warp.picalculator.math.MathContext;
import org.warp.picalculator.math.rules.Rule;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class Number implements Function {

	private final MathContext root;
	protected BigDecimal term;

	public Number(MathContext root, BigInteger val) {
		this.root = root;
		term = new BigDecimal(val).setScale(Utils.scale, Utils.scaleMode2);
	}

	public Number(MathContext root, BigDecimal val) {
		this.root = root;
		term = val.setScale(Utils.scale, Utils.scaleMode2);
	}

	public Number(MathContext root, String s) throws Error {
		this(root, new BigDecimal(s).setScale(Utils.scale, Utils.scaleMode2));
	}

	public Number(MathContext root, int s) {
		this(root, BigDecimal.valueOf(s).setScale(Utils.scale, Utils.scaleMode2));
	}

	public Number(MathContext root, float s) {
		this(root, BigDecimal.valueOf(s).setScale(Utils.scale, Utils.scaleMode2));
	}

	public Number(MathContext root, double s) {
		this(root, BigDecimal.valueOf(s).setScale(Utils.scale, Utils.scaleMode2));
	}

	public BigDecimal getTerm() {
		return term;
	}

	public void setTerm(BigDecimal val) {
		term = val.setScale(Utils.scale, Utils.scaleMode2);
	}

	public Number add(Number f) {
		final Number ret = new Number(root, getTerm().add(f.getTerm()));
		return ret;
	}

	public Number multiply(Number f) {
		final Number ret = new Number(root, getTerm().multiply(f.getTerm()));
		return ret;
	}

	public Number divide(Number f) throws Error {
		final Number ret = new Number(root, BigDecimalMath.divideRound(getTerm(), f.getTerm()));
		return ret;
	}

	public Number pow(Number f) throws Error, InterruptedException {
		Number ret = new Number(root, BigDecimal.ONE);
		if (Utils.isIntegerValue(f.term)) {
			final BigInteger bi = f.term.toBigInteger().abs();
			for (BigInteger i = BigInteger.ZERO; i.compareTo(bi) < 0; i = i.add(BigInteger.ONE)) {
				if (Thread.interrupted()) throw new InterruptedException();
				ret = ret.multiply(new Number(root, getTerm()));
			}
			if (f.term.signum() == -1) {
				ret = new Number(root, 1).divide(ret);
			}
		} else {
			ret.term = BigDecimalMath.pow(term, f.term);
		}
		return ret;
	}

	@Override
	public String toString() {
		final String sWith0 = getTerm().setScale(Utils.displayScale, Utils.scaleMode2).toPlainString();
		final String sExtendedWith0 = getTerm().toPlainString();
		//Remove trailing zeroes. Thanks to Kent, http://stackoverflow.com/questions/14984664/remove-trailing-zero-in-java
		String s = sWith0.indexOf(".") < 0 ? sWith0 : sWith0.replaceAll("0*$", "").replaceAll("\\.$", "");
		final String sExtended = sExtendedWith0.indexOf(".") < 0 ? sExtendedWith0 : sExtendedWith0.replaceAll("0*$", "").replaceAll("\\.$", "");

		if (sExtended.length() > s.length()) {
			s = s + "…";
		}

		if (root.exactMode == false) {
			final String cuttedNumber = s.split("\\.")[0];
			if (cuttedNumber.length() > 8) {
				return cuttedNumber.substring(0, 1) + "," + cuttedNumber.substring(1, 8) + "ℯ℮" + (cuttedNumber.length() - 1);
			}
		}
		return s;
	}

	@Override
	public Number clone() {
		return new Number(root, term);
	}

	@Override
	public ObjectArrayList<Function> simplify(Rule rule) throws Error, InterruptedException {
		return rule.execute(this);
	}

	public int getNumberOfDecimalPlaces() {
		return Math.max(0, term.stripTrailingZeros().scale());
	}

	public boolean isInteger() {
		return getNumberOfDecimalPlaces() <= 0;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o != null & term != null) {
			if (o instanceof Number) {
				final BigDecimal nav = ((Number) o).getTerm();
				final boolean na1 = term.compareTo(BigDecimal.ZERO) == 0;
				final boolean na2 = nav.compareTo(BigDecimal.ZERO) == 0;
				if (na1 == na2) {
					if (na1 == true) {
						return true;
					}
				} else {
					return false;
				}
				return nav.compareTo(term) == 0;
			}
		}
		return false;
	}

	@Override
	public MathContext getMathContext() {
		return root;
	}

	/*
	 * @Override
	 * public void draw(int x, int y, Graphics g) {
	 * }
	 * 
	 * @Override
	 * public int getHeight() {
	 * return Utils.getFontHeight();
	 * }
	 * 
	 * @Override
	 * public int getWidth() {
	 * return 6*toString().length()-1;
	 * }
	 */

	public boolean canBeFactorized() {
		if (Utils.isIntegerValue(getTerm())) {
			return getTerm().toBigIntegerExact().compareTo(BigInteger.valueOf(1)) > 1;
		}
		return false;
	}

	/**
	 * @author programmingpraxis
	 * @return
	 */
	public LinkedList<BigInteger> getFactors() {
		BigInteger n = getTerm().toBigIntegerExact();
		final BigInteger two = BigInteger.valueOf(2);
		final BigInteger zero = BigInteger.ZERO;
		final LinkedList<BigInteger> fs = new LinkedList<>();

		final int comparedToZero = n.compareTo(zero);
		final int comparedToTwo = n.compareTo(two);
		if (comparedToZero == 0) {
			return fs;
		}
		if (comparedToTwo < 0) {
			if (comparedToZero > 0) {
				return fs;
			} else {
				fs.add(BigInteger.valueOf(-1));
				n = n.multiply(BigInteger.valueOf(-1));
			}
		}

		if (n.compareTo(two) < 0) {
			throw new IllegalArgumentException("must be greater than one");
		}

		while (n.mod(two).equals(BigInteger.ZERO)) {
			fs.add(two);
			n = n.divide(two);
		}

		if (n.compareTo(BigInteger.ONE) > 0) {
			BigInteger f = BigInteger.valueOf(3);
			while (f.compareTo(Utils.maxFactor) <= 0 && f.multiply(f).compareTo(n) <= 0) {
				if (n.mod(f).equals(BigInteger.ZERO)) {
					fs.add(f);
					n = n.divide(f);
				} else {
					f = f.add(two);
				}
			}
			fs.add(n);
		}

		return fs;
	}

	@Override
	public ObjectArrayList<Block> toBlock(MathContext context) {
		ObjectArrayList<Block> result = new ObjectArrayList<>();
		String numberString = this.toString();
		if (numberString.contains("ℯ℮")) {
			String[] numberParts = numberString.split("ℯ℮", 2);
			BlockPower bp = new BlockExponentialNotation();
			BlockContainer bpec = bp.getExponentContainer();
			for (char c : numberParts[0].toCharArray()) {
				result.add(new BlockChar(c));
			}
			for (char c : numberParts[1].toCharArray()) {
				bpec.appendBlockUnsafe(new BlockChar(c));
			} ;
			bpec.recomputeDimensions();
			bp.recomputeDimensions();
			result.add(bp);
			return result;
		} else {
			for (char c : numberString.toCharArray()) {
				result.add(new BlockChar(c));
			}
		}
		return result;
	}

	@Override
	public Function setParameter(int index, Function var) throws IndexOutOfBoundsException {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public Function getParameter(int index) throws IndexOutOfBoundsException {
		throw new IndexOutOfBoundsException();
	}
}
