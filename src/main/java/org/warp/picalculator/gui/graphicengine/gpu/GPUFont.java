package org.warp.picalculator.gui.graphicengine.gpu;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.warp.picalculator.gui.graphicengine.BinaryFont;
import org.warp.picalculator.gui.graphicengine.GraphicEngine;
import org.warp.picalculator.gui.graphicengine.cpu.CPUFont;

import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.texture.Texture;

public class GPUFont implements BinaryFont {

	public Texture texture;
	public int textureW;
	public int textureH;
	public int charW;
	public int charH;
	public int minCharIndex;
	public int maxCharIndex;

	public int memoryWidth;
	public int memoryHeight;
	public int memoryWidthOfEachColumn;

	private boolean initialized = false;
	private BufferedImage tmpFont;

	GPUFont(GraphicEngine g, String file) throws IOException {
		load(file);
		((GPUEngine) g).registerFont(this);
	}

	@Override
	public void load(String file) throws IOException {
		CPUFont font = CPUFont.loadTemporaryFont(file);
		charW = font.charW;
		charH = font.charH;
		minCharIndex = font.minBound;
		maxCharIndex = font.maxBound;
		pregenTexture(font.rawchars);
		font = null;
	}

	public int[] getCharIndexes(String txt) {
		final int l = txt.length();
		final int[] indexes = new int[l];
		final char[] chars = txt.toCharArray();
		for (int i = 0; i < l; i++) {
			indexes[i] = getCharIndex(chars[i]);
		}
		return indexes;
	}

	public int getCharIndex(char ch) {
		return (ch & 0xFFFF) - minCharIndex;
	}

	private void pregenTexture(boolean[][] chars) {
		final double totalChars = maxCharIndex - minCharIndex;
		final int w = powerOf2((int) (Math.ceil(Math.sqrt(totalChars) * charW)));
		final int h = powerOf2((int) (Math.ceil(Math.sqrt(totalChars) * charH)));
		final int maxIndexW = (int) Math.floor(((double) w) / ((double) charW)) - 1;
		BufferedImage bfi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		int indexX = 0;
		int indexY = 0;
		for (int i = 0; i < totalChars; i++) {
			boolean[] currentChar = chars[i];
			if (currentChar != null && currentChar.length > 0) {
				for (int charY = 0; charY < charH; charY++) {
					for (int charX = 0; charX < charW; charX++) {
						if (currentChar[charY * charW + charX]) {
							bfi.setRGB(indexX * charW + charX, indexY * charH + charY, 0xFFFFFFFF);
						}
					}
				}
			}
			indexX++;
			if (indexX >= maxIndexW) {
				indexX = 0;
				indexY += 1;
			}
			currentChar = null;
		}
		try {
			memoryWidth = w;
			memoryHeight = h;
			memoryWidthOfEachColumn = maxIndexW;
			textureW = bfi.getWidth();
			textureH = bfi.getHeight();
			this.tmpFont = bfi;
		} catch (GLException e) {
			e.printStackTrace();
		}
	}
	
	private void genTexture() {
		try {
			texture = GPURenderer.importTexture(tmpFont);
		} catch (GLException | IOException e) {
			e.printStackTrace();
		}
	}

	private int powerOf2(int a) {
		return (int) (a == 0 ? 0 : Math.pow(2, 32 - Integer.numberOfLeadingZeros(a - 1)));
	}

	@Override
	public void initialize(GraphicEngine d) {
		genTexture();
		tmpFont = null;
		initialized = true;
	}

	@Override
	public void use(GraphicEngine d) {
		if (!initialized) {
			initialize(d);
		}
		final GPURenderer r = (GPURenderer) d.getRenderer();
		r.currentFont = this;
		r.useTexture(texture, textureW, textureH);
	}

	@Override
	public int getStringWidth(String text) {
		final int w = (charW) * text.length();
		if (text.length() > 0) {
			return w;
		} else {
			return 0;
		}
	}

	@Override
	public int getCharacterWidth() {
		return charW;
	}

	@Override
	public int getCharacterHeight() {
		return charH;
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}
}