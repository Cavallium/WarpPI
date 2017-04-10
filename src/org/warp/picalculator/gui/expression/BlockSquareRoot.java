package org.warp.picalculator.gui.expression;

import org.warp.picalculator.gui.graphicengine.GraphicEngine;
import org.warp.picalculator.gui.graphicengine.Renderer;

public class BlockSquareRoot extends Block {

	public static final int CLASS_ID = 0x00000003;

	private final BlockContainer containerNumber;

	private int h1;

	public BlockSquareRoot() {
		containerNumber = new BlockContainer(false);
		recomputeDimensions();
	}

	@Override
	public void draw(GraphicEngine ge, Renderer r, int x, int y, Caret caret) {
		BlockContainer.getDefaultFont(small).use(ge);
		r.glColor(BlockContainer.getDefaultColor());
		r.glDrawLine(x, y + height - 10 + 1, x, y + height - 10 + 2); // /
		r.glDrawLine(x + 1, y + height - 10, x + 1, y + height - 10 + 1); // /
		r.glDrawLine(x + 2, y + height - 10 + 2, x + 2, y + height - 10 + 6); // \
		r.glDrawLine(x + 3, y + height - 10 + 7, x + 3, y + height - 10 + 9); // \
		r.glDrawLine(x + 5, y + height - h1 - 1 - 2, x + width - 1, y + height - h1 - 1 - 2); // ----
		r.glDrawLine(x + 5, y + height - h1 - 1 - 2, x + 5, y + height - (h1 - 2) / 3f * 2f - 1); // |
		r.glDrawLine(x + 4, y + height - (h1 - 2) / 3f * 2f - 1, x + 4, y + height - (h1 - 2) / 3f - 1); // |
		r.glDrawLine(x + 3, y + height - (h1 - 2) / 3f - 1, x + 3, y + height - 1); // |
		containerNumber.draw(ge, r, x + 7, y + 3, caret);
	}

	@Override
	public boolean putBlock(Caret caret, Block newBlock) {
		boolean added = false;
		added = added | containerNumber.putBlock(caret, newBlock);
		if (added) {
			recomputeDimensions();
		}
		return added;
	}

	@Override
	public boolean delBlock(Caret caret) {
		boolean removed = false;
		removed = removed | containerNumber.delBlock(caret);
		if (removed) {
			recomputeDimensions();
		}
		return removed;
	}

	@Override
	public void recomputeDimensions() {
		final int w1 = containerNumber.getWidth();
		h1 = containerNumber.getHeight();
		final int l1 = containerNumber.getLine();
		width = 8 + w1 + 2;
		height = 3 + h1;
		line = 3 + l1;
		if (height < 9) {
			height = 9;
			line += (9 - (3 + h1));
		}
	}

	@Override
	public void setSmall(boolean small) {
		this.small = small;
		containerNumber.setSmall(small);
		recomputeDimensions();
	}

	public BlockContainer getNumberContainer() {
		return containerNumber;
	}

	@Override
	public int getClassID() {
		return CLASS_ID;
	}

	@Override
	public int computeCaretMaxBound() {
		return containerNumber.computeCaretMaxBound();
	}
}
