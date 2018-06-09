package org.warp.picalculator;

import java.io.PrintStream;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.warp.picalculator.math.MathematicalSymbols;

public class ConsoleUtils {

	public static final class AdvancedOutputStream extends StringWriter {
	
		private void print(PrintStream stream, String str) {
			stream.print(fixString(str));
		}
	
		private void println(PrintStream stream, String str) {
			stream.println(fixString(str));
		}
	
		private void println(PrintStream stream) {
			stream.println();
		}
	
		private String fixString(String str) {
	
			return str.replace("" + MathematicalSymbols.NTH_ROOT, "root").replace("" + MathematicalSymbols.SQUARE_ROOT, "sqrt").replace("" + MathematicalSymbols.POWER, "powerOf").replace("" + MathematicalSymbols.POWER_OF_TWO, "powerOfTwo").replace("" + MathematicalSymbols.SINE, "sine").replace("" + MathematicalSymbols.COSINE, "cosine").replace("" + MathematicalSymbols.TANGENT, "tangent").replace("" + MathematicalSymbols.ARC_SINE, "asin").replace("" + MathematicalSymbols.ARC_COSINE, "acos").replace("" + MathematicalSymbols.ARC_TANGENT, "atan").replace("" + MathematicalSymbols.UNDEFINED, "undefined").replace("" + MathematicalSymbols.PI, "PI").replace("" + MathematicalSymbols.EULER_NUMBER, "EULER_NUMBER").replace("" + MathematicalSymbols.X, "X").replace("" + MathematicalSymbols.Y, "Y");
		}
	
		public void println(String str) {
			println(0, str);
		}
	
		public void println(int level) {
			if (StaticVars.outputLevel >= level) {
				final String time = getTimeString();
				if (StaticVars.outputLevel == 0) {
					println(System.out);
				} else {
					println(System.out);
				}
			}
		}
	
		public void println(int level, String str) {
			if (StaticVars.outputLevel >= level) {
				final String time = getTimeString();
				if (StaticVars.outputLevel == 0) {
					println(System.out, "[" + time + "]" + str);
				} else {
					println(System.out, "[" + time + "]" + str);
				}
			}
		}
	
		public void print(int level, String str) {
			if (StaticVars.outputLevel >= level) {
				if (StaticVars.outputLevel == 0) {
					print(System.out, str);
				} else {
					print(System.out, str);
				}
			}
		}
	
		public void println(int level, String prefix, String str) {
			if (StaticVars.outputLevel >= level) {
				final String time = getTimeString();
				if (StaticVars.outputLevel == 0) {
					println(System.out, "[" + time + "][" + prefix + "]" + str);
				} else {
					println(System.out, "[" + time + "][" + prefix + "]" + str);
				}
			}
		}
	
		public void println(int level, String... parts) {
			if (StaticVars.outputLevel >= level) {
				final String time = getTimeString();
				String output = "";
				for (int i = 0; i < parts.length; i++) {
					if (i + 1 == parts.length) {
						output += parts[i];
					} else {
						output += "[" + parts[i] + "]";
					}
				}
				if (StaticVars.outputLevel == 0) {
					println(System.out, "[" + time + "]" + output);
				} else {
					println(System.out, "[" + time + "]" + output);
				}
			}
		}
	
		private String getTimeString() {
			return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
		}
	
		int before = 0;
		boolean due = false;
	
	}
	public static ConsoleUtils.AdvancedOutputStream out = new ConsoleUtils.AdvancedOutputStream();
	public static final int OUTPUTLEVEL_NODEBUG = 0;
	public static final int OUTPUTLEVEL_DEBUG_MIN = 1;
	public static final int OUTPUTLEVEL_DEBUG_VERBOSE = 4;

}
