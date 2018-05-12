package org.warp.picalculator.gui.expression.blocks;

import org.warp.picalculator.Error;
import org.warp.picalculator.gui.expression.Caret;
import org.warp.picalculator.gui.graphicengine.GraphicEngine;
import org.warp.picalculator.gui.graphicengine.Renderer;
import org.warp.picalculator.math.Function;
import org.warp.picalculator.math.MathContext;
import org.warp.picalculator.math.parser.features.FeatureLogarithm;
import org.warp.picalculator.math.parser.features.interfaces.Feature;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class BlockLogarithm extends Block {

	private final BlockContainer containerBase;
	private final BlockContainer containerNumber;

	private final String prefix = "log";
	private int prw;
	private int bw;
	private int bh;
	private int bl;
	private int chw;
	private int chh;
	private int schh;
	private int nmbh;
	private int toph;

	public BlockLogarithm() {
		containerBase = new BlockContainer(true);
		containerNumber = new BlockContainer(false);
		recomputeDimensions();
	}

	public BlockLogarithm(ObjectArrayList<Block> blocks) {
		containerBase = new BlockContainer(true);
		containerNumber = new BlockContainer(false, blocks);
		recomputeDimensions();
	}

	@Override
	public void draw(GraphicEngine ge, Renderer r, int x, int y, Caret caret) {
		BlockContainer.getDefaultFont(small).use(ge);
		r.glColor(BlockContainer.getDefaultColor());
		if (prefix != null) {
			r.glDrawStringLeft(x + 1, y + line - chh / 2, prefix);
		}
		r.glDrawCharLeft(x + bw + prw, y + toph, '╭');
		r.glDrawCharLeft(x + bw + prw, y + toph + nmbh - chh, '╰');
		if (small) {
			r.glFillColor(x + bw + prw + 1, y + toph + 5, 1, nmbh - 4 * 2);
			r.glFillColor(x + width - 3, y + toph + 5, 1, nmbh - 4 * 2);
		} else {
			r.glFillColor(x + bw + prw + 3, y + toph + 6, 2, nmbh - 6 * 2);
			r.glFillColor(x + width - 5, y + toph + 6, 2, nmbh - 6 * 2);
		}
		r.glDrawCharLeft(x + width - chw, y + toph, '╮');
		r.glDrawCharLeft(x + width - chw, y + toph + nmbh - chh, '╯');
		r.glColor(BlockContainer.getDefaultColor());
		containerBase.draw(ge, r, x + prw, y + line + chh / 2 - bl, caret);
		r.glColor(BlockContainer.getDefaultColor());
		containerNumber.draw(ge, r, x + bw + prw + chw, y + toph, caret);
	}

	@Override
	public boolean putBlock(Caret caret, Block newBlock) {
		boolean added = false;
		added = added | containerBase.putBlock(caret, newBlock);
		added = added | containerNumber.putBlock(caret, newBlock);
		if (added) {
			recomputeDimensions();
		}
		return added;
	}

	@Override
	public boolean delBlock(Caret caret) {
		boolean removed = false;
		removed = removed | containerBase.delBlock(caret);
		removed = removed | containerNumber.delBlock(caret);
		if (removed) {
			recomputeDimensions();
		}
		return removed;
	}

	@Override
	public BlockReference<?> getBlock(Caret caret) {
		BlockReference<?> bl = null;
		bl = containerBase.getBlock(caret);
		if (bl != null) {
			return bl;
		}
		bl = containerNumber.getBlock(caret);
		return bl;
	}

	@Override
	public void recomputeDimensions() {
		if (prefix == null) {
			prw = 0;
		} else {
			prw = 1 + BlockContainer.getDefaultCharWidth(small) * prefix.length();
		}
		bw = containerBase.getWidth();
		bh = containerBase.getHeight();
		bl = containerBase.getLine();
		chw = BlockContainer.getDefaultCharWidth(small);
		chh = BlockContainer.getDefaultCharHeight(small);
		schh = BlockContainer.getDefaultCharHeight(true);
		width = prw + bw + chw + containerNumber.getWidth() + chw + 3;
		nmbh = containerNumber.getHeight();
		final int nl = containerNumber.getLine();
		if (bl > nmbh) {
			toph = bl - nmbh;
			line = toph + nl;
			if (bl + (bh - bl) > toph + nmbh) {
				height = bl + (bh - bl);
			} else {
				height = toph + nmbh;
			}
		} else {
			System.out.println("b");
			toph = 0;
			line = toph + nl;
			if (nmbh + bh - bl > toph + nmbh) {
				height = nmbh + (bh - bl);
			} else {
				height = toph + nmbh;
			}
		}
	}

	@Override
	public void setSmall(boolean small) {
		this.small = small;
		containerBase.setSmall(small);
		containerNumber.setSmall(small);
		recomputeDimensions();
	}

	public BlockContainer getBaseContainer() {
		return containerBase;
	}

	public BlockContainer getNumberContainer() {
		return containerNumber;
	}

	@Override
	public int computeCaretMaxBound() {
		return containerBase.computeCaretMaxBound() + containerNumber.computeCaretMaxBound();
	}

	@Override
	public Feature toFeature(MathContext context) throws Error {
		final Function base = getBaseContainer().toFunction(context);
		final Function number = getNumberContainer().toFunction(context);
		return new FeatureLogarithm(base, number);
	}

}
