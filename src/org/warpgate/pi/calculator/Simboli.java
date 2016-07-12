package org.warpgate.pi.calculator;

import static org.warpgate.pi.calculator.Utils.concat;

public class Simboli {
	public static final String SUM = "+";
	public static final String SUBTRACTION = "-";
	public static final String MULTIPLICATION = "*";
	public static final String DIVISION = "/";
	public static final String NTH_ROOT = "√";
	public static final String SQUARE_ROOT = "Ⓐ";
	public static final String PARENTHESIS_OPEN = "(";
	public static final String PARENTHESIS_CLOSE = ")";
	public static final String POTENZA = "Ⓑ";
	public static final String EQUATION = "=";
	public static final String SYSTEM = "{";
	
	public static final String[] funzioni() {
		return concat(funzioniNSN(), funzioniSN());
	}
	public static final String[] funzioniNSN() {
		return new String[]{NTH_ROOT, POTENZA};
	}
	public static final String[] funzioniSN() {
		return new String[]{SQUARE_ROOT};
	}
	public static final String[] segni(boolean withMultiplication) {
		String[] ret = new String[]{SUM, DIVISION};
		if (withMultiplication) {
			ret = Utils.add(ret, MULTIPLICATION);
		}
		return ret;
	}
	public static final String[] parentesi() {
		return new String[]{PARENTHESIS_OPEN, PARENTHESIS_CLOSE};
	}
	public static String[] incognite() {
		return new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	}
	public static String[] sintassiGenerale() {
		return new String[]{SYSTEM, EQUATION};
	}
}
