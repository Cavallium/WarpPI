package org.warp.picalculator.extra.mario;

public class MarioBlock {
	private final int x;
	private final int y;
	private final byte id;

	public MarioBlock(int x, int y, byte b) {
		this.x = x;
		this.y = y;
		id = b;
	}

	public boolean isSolid() {
		return MarioBlock.isSolid(id);
	}

	public byte getID() {
		return id;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public static boolean isSolid(byte id) {
		return id != 0b0;
	}
}
