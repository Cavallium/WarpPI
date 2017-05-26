package org.warp.picalculator.gui.expression.containers;

import java.io.Serializable;

import org.warp.picalculator.Error;
import org.warp.picalculator.device.KeyboardEventListener;
import org.warp.picalculator.gui.GraphicalElement;
import org.warp.picalculator.gui.expression.Caret;
import org.warp.picalculator.gui.expression.CaretState;
import org.warp.picalculator.gui.expression.ExtraMenu;
import org.warp.picalculator.gui.expression.InputContext;
import org.warp.picalculator.gui.expression.blocks.Block;
import org.warp.picalculator.gui.expression.blocks.BlockContainer;
import org.warp.picalculator.gui.expression.layouts.InputLayout;
import org.warp.picalculator.gui.graphicengine.GraphicEngine;
import org.warp.picalculator.gui.graphicengine.Renderer;
import org.warp.picalculator.math.Function;
import org.warp.picalculator.math.MathContext;
import org.warp.picalculator.math.parser.MathParser;
import org.warp.picalculator.math.parser.features.interfaces.Feature;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public abstract class InputContainer implements GraphicalElement, InputLayout, Serializable {
	private static final long serialVersionUID = 923589369317765667L;
	private final BlockContainer root;
	private Caret caret;
	private static final float CARET_DURATION = 0.5f;
	private float caretTime;
	private int maxPosition = 0;
	private boolean parsed = false;
	private ExtraMenu<?> extra;
	protected InputContext inputContext;

	public synchronized InputContext getInputContext() {
		return inputContext;
	}
	@Deprecated()
	/**
	 * Use InputContainer(InputContext) instead
	 */
	public InputContainer() {
		this(new InputContext());
	}

	public InputContainer(InputContext ic) {
		this(ic, false);
	}

	public InputContainer(InputContext ic, boolean small) {
		this(ic, small, 0, 0);
	}

	public InputContainer(InputContext ic, boolean small, int minWidth, int minHeight) {
		inputContext = ic;
		caret = new Caret(CaretState.VISIBLE_ON, 0);
		root = new BlockContainer(small, false);
	}

	public void typeChar(char c) {
		final Block b = parseChar(c);
		if (b != null) {
			caret.resetRemaining();
			if (root.putBlock(caret, b)) {
				caret.setPosition(caret.getPosition() + 1);
				maxPosition = root.computeCaretMaxBound();
				root.recomputeDimensions();
			}
		}
		caretTime = 0;
		caret.turnOn();
		closeExtra();
	}

	public void typeChar(String c) {
		typeChar(c.charAt(0));
	}
	
	public void del() {
		caret.resetRemaining();
		if (root.delBlock(caret)) {
			root.recomputeDimensions();
		}
		if (caret.getPosition() > 0) {
			caret.setPosition(caret.getPosition() - 1);
			maxPosition = root.computeCaretMaxBound();
		}
		caret.turnOn();
		caretTime = 0;
		closeExtra();
	}

	
	public Block getSelectedBlock() {
		caret.resetRemaining();
		return root.getBlock(caret);
	}

	public void moveLeft() {
		final int curPos = caret.getPosition();
		if (curPos > 0) {
			caret.setPosition(curPos - 1);
		} else {
			caret.setPosition(maxPosition-1);
		}
		caret.turnOn();
		caretTime = 0;
		closeExtra();
	}

	public void moveRight() {
		final int curPos = caret.getPosition();
		if (curPos + 1 < maxPosition) {
			caret.setPosition(curPos + 1);
		} else {
			caret.setPosition(0);
		}
		caret.turnOn();
		caretTime = 0;
		closeExtra();
	}

	@Override
	public void recomputeDimensions() {
		root.recomputeDimensions();
	}

	@Override
	public int getWidth() {
		return root.getWidth();
	}

	@Override
	public int getHeight() {
		return root.getHeight();
	}

	@Override
	public int getLine() {
		return root.getLine();
	}

	/**
	 * 
	 * @param delta
	 *            Time, in seconds
	 * @return true if something changed
	 */
	public boolean beforeRender(float delta) {
		boolean somethingChanged = false;
		caretTime += delta;
		if (caretTime >= CARET_DURATION) {
			while (caretTime >= CARET_DURATION) {
				caretTime -= CARET_DURATION;
				caret.flipState();
				somethingChanged = true;
			}
		}
		
		if (extra != null) {
			somethingChanged = somethingChanged|extra.beforeRender(delta, caret);
		}

		return somethingChanged;
	}

	/**
	 * 
	 * @param ge
	 *            Graphic Engine class.
	 * @param r
	 *            Graphic Renderer class of <b>ge</b>.
	 * @param x
	 *            Position relative to the window.
	 * @param y
	 *            Position relative to the window.
	 */
	public void draw(GraphicEngine ge, Renderer r, int x, int y) {
		caret.resetRemaining();
		root.draw(ge, r, x, y, caret);
		if (extra != null) {
			extra.draw(ge, r, caret);
		}
	}

	public void clear() {
		caret = new Caret(CaretState.VISIBLE_ON, 0);
		root.clear();
		maxPosition = root.computeCaretMaxBound();
		recomputeDimensions();
	}

	public boolean isEmpty() {
		return maxPosition <= 1;
	}

	public int getCaretMaxPosition() {
		return maxPosition;
	}

	public void setCaretPosition(int pos) {
		if (pos > 0 && pos < maxPosition) {
			caret.setPosition(pos);
		}
		caret.turnOn();
		caretTime = 0;
		closeExtra();
	}

	public void setParsed(boolean parsed) {
		this.parsed = parsed;
	}

	public boolean isAlreadyParsed() {
		return parsed;
	}

	/**
	 * <strong>WARNING! DO NOT MODIFY THIS ARRAY!!!</strong>
	 * 
	 * @return an arraylist representing the content
	 */
	public ObjectArrayList<Block> getContent() {
		return root.getContent();
	}

	public void toggleExtra() {
		if (extra == null) {
			Block selectedBlock = getSelectedBlock();
			if (selectedBlock != null) {
				extra = selectedBlock.getExtraMenu();
				extra.open();
			}
		} else {
			extra.close();
			extra = null;
		}
	}
	
	public void closeExtra() {
		if (extra != null) {
			extra.close();
			extra = null;
		}
	}
	
	public boolean isExtraOpened() {
		return extra != null;
	}
	
	public KeyboardEventListener getExtraKeyboardEventListener() {
		return extra;
	}
	public Function toFunction(MathContext context) throws Error {
		return root.toFunction(context);
	}

}
