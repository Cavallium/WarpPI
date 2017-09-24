package org.warp.picalculator.gui.expression.blocks;

import org.warp.picalculator.Error;
import org.warp.picalculator.device.Keyboard.Key;
import org.warp.picalculator.gui.DisplayManager;
import org.warp.picalculator.gui.expression.Caret;
import org.warp.picalculator.gui.expression.ExtraMenu;
import org.warp.picalculator.gui.expression.InputContext;
import org.warp.picalculator.gui.graphicengine.BinaryFont;
import org.warp.picalculator.gui.graphicengine.GraphicEngine;
import org.warp.picalculator.gui.graphicengine.Renderer;
import org.warp.picalculator.math.MathContext;
import org.warp.picalculator.math.functions.Variable.V_TYPE;
import org.warp.picalculator.math.parser.features.FeatureVariable;
import org.warp.picalculator.math.parser.features.interfaces.Feature;

public class BlockVariable extends Block {

	private InputContext ic;
	private final char ch;
	private final VariableMenu menu;
	private V_TYPE type;
	private int color;
	private boolean mustRefresh = true;
	private BlockVariable typeDirtyID;
	private final boolean typeLocked;

	public BlockVariable(InputContext ic, char ch) {
		this(ic, ch, false);
	}
	public BlockVariable(InputContext ic, char ch, boolean typeLocked) {
		this.ic = ic;
		this.ch = ch;
		this.type = V_TYPE.VARIABLE;
		this.color = 0xFF304ffe;
		this.typeDirtyID = this;
		this.typeLocked = typeLocked;
		this.menu = typeLocked ? null : new VariableMenu(this);
		retrieveValue();
		recomputeDimensions();
	}

	private void retrieveValue() {
		type = ic.variableTypes.getOrDefault(ch, V_TYPE.VARIABLE);
		typeDirtyID = ic.variableTypeDirtyID;
		if (menu != null) {
			menu.mustRefreshMenu = true;
		}
		mustRefresh = true;
		System.out.println("retrieve:"+type.toString());
	}

	public void pushValue() {
		if (ic.variableTypeDirtyID != this) {
			typeDirtyID = this;
			ic.variableTypeDirtyID = this;
		} else {
			typeDirtyID = null;
			ic.variableTypeDirtyID = null;
		}
		ic.variableTypes.put(ch, type);
		System.out.println("push:"+type.toString());
	}

	@Override
	public void draw(GraphicEngine ge, Renderer r, int x, int y, Caret caret) {
		if(ic.variableTypeDirtyID != typeDirtyID) {
			retrieveValue();
		}
		if (mustRefresh) {
			mustRefresh = false;
			switch (type) {
				case VARIABLE:
					color = 0xFF304ffe;
					break;
				case CONSTANT:
					color = typeLocked ? 0xFF000000 : 0xFF35913F;
					break;
				case SOLUTION:
				default:
					color = 0xFFf50057;
					break;
			}
		}
		
		BlockContainer.getDefaultFont(small).use(ge);
		r.glColor(color);
		r.glDrawCharLeft(x, y, ch);
	}

	@Override
	public boolean putBlock(Caret caret, Block newBlock) {
		return false;
	}

	@Override
	public boolean delBlock(Caret caret) {
		return false;
	}

	@Override
	public Block getBlock(Caret caret) {
		return null;
	}

	@Override
	public void recomputeDimensions() {
		width = BlockContainer.getDefaultCharWidth(small);
		height = BlockContainer.getDefaultCharHeight(small);
		line = height / 2;
	}

	@Override
	public void setSmall(boolean small) {
		this.small = small;
		recomputeDimensions();
	}

	public char getChar() {
		return ch;
	}

	@Override
	public int computeCaretMaxBound() {
		return 0;
	}
	
	@Override
	public ExtraMenu<?> getExtraMenu() {
		return menu;
	}

	public class VariableMenu extends ExtraMenu<BlockVariable> {

		String text = "";
		boolean mustRefreshMenu = true;
		
		public VariableMenu(BlockVariable var) {
			super(var);
		}

		private static final long serialVersionUID = 3941994107852212764L;

		@Override
		public void open() {
			
		}

		@Override
		public void close() {
		}

		@Override
		public boolean keyPressed(Key k) {
			switch (k) {
				case LEFT:
				case UP:
					switch (block.type) {
						case VARIABLE:
							block.type = V_TYPE.SOLUTION;
							break;
						case CONSTANT:
							block.type = V_TYPE.VARIABLE;
							break;
						case SOLUTION:
						default:
							block.type = V_TYPE.CONSTANT;
							break;
					}
					break;
				case RIGHT:
				case DOWN:
				case EQUAL:
				case SIMPLIFY:
					switch (block.type) {
						case VARIABLE:
							block.type = V_TYPE.CONSTANT;
							break;
						case CONSTANT:
							block.type = V_TYPE.SOLUTION;
							break;
						case SOLUTION:
						default:
							block.type = V_TYPE.VARIABLE;
							break;
					}
					break;
				default:
					return false;
			}

			block.pushValue();
			mustRefresh = true;
			mustRefreshMenu = true;
			return true;
		}

		@Override
		public boolean keyReleased(Key k) {
			return false;
		}

		@Override
		public boolean beforeRender(float delta, Caret caret) {
			if (mustRefreshMenu) {
				mustRefreshMenu = false;
				text = block.type.toString();
				BinaryFont f = BlockContainer.getDefaultFont(true);
				width = 7+f.getStringWidth(text)+7;
				height = 2+f.getCharacterHeight()+2;
				
				super.beforeRender(delta, caret);
				return true;
			}
			return false;
		}

		@Override
		public void draw(GraphicEngine ge, Renderer r, Caret caret) {
			r.glColor3f(1.0f, 1.0f, 1.0f);
			DisplayManager.guiSkin.use(ge);
			int popupX = location[0];
			int popupY = location[1];
			if (popupX < 0) {
				popupX = 0;
			}
			if (popupY < 0) {
				popupY = 0;
			}
			int[] screenSize = ge.getSize();
			if (popupX+width >= screenSize[0]) {
				popupX=screenSize[0]-width-1;
			}
			if (popupY+height >= screenSize[1]) {
				popupY=screenSize[1]-height-1;
			}
			r.glFillRect(location[0]+width/2-5, popupY+1, 10, 5, 163, 16, 10, 5);
			r.glFillColor(popupX, popupY+5, width, height);
			r.glFillColor(popupX+2, popupY+4, width-4, height+2);
			r.glFillColor(popupX-1, popupY+7, width+2, height-4);
			r.glFillRect(popupX+2, popupY+5+height/2-7/2, 4, 7, 160, 21, 4, 7);
			r.glFillRect(popupX+width-2-4, popupY+5+height/2-7/2, 4, 7, 172, 21, 4, 7);
			r.glColor(color);
			BlockContainer.getDefaultFont(true).use(ge);
			r.glDrawStringCenter(popupX+width/2, popupY+2+5, text);
		}
		
	}

	@Override
	public Feature toFeature(MathContext context) throws Error {
		return new FeatureVariable(ch, type);
	}
}
