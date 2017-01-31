package org.warp.picalculator.math.functions.equations;

import java.util.ArrayList;
import java.util.List;

import org.warp.picalculator.Error;
import org.warp.picalculator.gui.DisplayManager;
import org.warp.picalculator.math.Calculator;
import org.warp.picalculator.math.functions.Expression;
import org.warp.picalculator.math.functions.Function;
import org.warp.picalculator.math.functions.FunctionMultipleValues;
import org.warp.picalculator.math.functions.Number;

public class EquationsSystem extends FunctionMultipleValues {
	static final int spacing = 2;

	public EquationsSystem(Calculator root) {
		super(root);
	}

	public EquationsSystem(Calculator root, Function value) {
		super(root, new Function[] { value });
	}

	public EquationsSystem(Calculator root, Function[] value) {
		super(root, value);
	}

	@Override
	public String getSymbol() {
		return null;
	}

	@Override
	protected boolean isSolvable() {
		if (functions.length >= 1) {
			return true;
		}
		return false;
	}

	@Override
	public List<Function> solveOneStep() throws Error {
		final List<Function> ret = new ArrayList<>();
		if (functions.length == 1) {
			if (functions[0].isSolved()) {
				ret.add(functions[0]);
				return ret;
			} else {
				final List<Function> l = functions[0].solveOneStep();
				for (final Function f : l) {
					if (f instanceof Number) {
						ret.add(f);
					} else {
						ret.add(new Expression(root, new Function[] { f }));
					}
				}
				return ret;
			}
		} else {
			for (final Function f : functions) {
				if (f.isSolved() == false) {
					final List<Function> partial = f.solveOneStep();
					for (final Function fnc : partial) {
						ret.add(new Expression(root, new Function[] { fnc }));
					}
				}
			}
			return ret;
		}
	}

	@Override
	public void generateGraphics() {
		for (final Function f : functions) {
			f.setSmall(false);
			f.generateGraphics();
		}

		width = 0;
		for (final Function f : functions) {
			if (f.getWidth() > width) {
				width = f.getWidth();
			}
		}
		width += 5;

		height = 3;
		for (final Function f : functions) {
			height += f.getHeight() + spacing;
		}
		height = height - spacing + 2;

		line = height / 2;
	}

	@Override
	public void draw(int x, int y) {

		final int h = getHeight() - 1;
		final int marginTop = 3;
		final int marginBottom = (h - 3 - 2) / 2 + marginTop;
		final int spazioSopra = h - marginBottom;
		int dy = marginTop;
		for (final Function f : functions) {
			f.draw(x + 5, y + dy);
			dy += f.getHeight() + spacing;
		}

		DisplayManager.renderer.glDrawLine(x + 2, y + 0, x + 3, y + 0);
		DisplayManager.renderer.glDrawLine(x + 1, y + 1, x + 1, y + marginBottom / 2);
		DisplayManager.renderer.glDrawLine(x + 2, y + marginBottom / 2 + 1, x + 2, y + marginBottom - 1);
		DisplayManager.renderer.glDrawLine(x + 0, y + marginBottom, x + 1, y + marginBottom);
		DisplayManager.renderer.glDrawLine(x + 2, y + marginBottom + 1, x + 2, y + marginBottom + spazioSopra / 2 - 1);
		DisplayManager.renderer.glDrawLine(x + 1, y + marginBottom + spazioSopra / 2, x + 1, y + h - 1);
		DisplayManager.renderer.glDrawLine(x + 2, y + h, x + 3, y + h);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getLine() {
		return line;
	}
}
