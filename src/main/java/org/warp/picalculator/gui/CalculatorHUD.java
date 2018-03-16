package org.warp.picalculator.gui;

import org.warp.picalculator.StaticVars;
import org.warp.picalculator.Utils;
import org.warp.picalculator.device.Keyboard;
import org.warp.picalculator.gui.graphicengine.GraphicEngine;
import org.warp.picalculator.gui.graphicengine.Renderer;
import org.warp.picalculator.gui.graphicengine.Skin;

public class CalculatorHUD extends HUD {

	@Override
	public void created() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialized() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renderTopmostBackground() {
		Renderer renderer = d.renderer;
		GraphicEngine engine = d.engine;
		Skin guiSkin = d.guiSkin;
		
		renderer.glColor(0xFFc5c2af);
		renderer.glFillColor(0, 0, engine.getWidth(), 20);
	}
	
	@Override
	public void renderTopmost() {
		Renderer renderer = d.renderer;
		GraphicEngine engine = d.engine;
		Skin guiSkin = d.guiSkin;
		
		//DRAW TOP
		renderer.glColor3i(0, 0, 0);
		renderer.glDrawLine(0, 20, engine.getWidth() - 1, 20);
		renderer.glColor3i(255, 255, 255);
		guiSkin.use(engine);
		if (Keyboard.shift) {
			renderer.glFillRect(2 + 18 * 0, 2, 16, 16, 16 * 2, 16 * 0, 16, 16);
		} else {
			renderer.glFillRect(2 + 18 * 0, 2, 16, 16, 16 * 3, 16 * 0, 16, 16);
		}
		if (Keyboard.alpha) {
			renderer.glFillRect(2 + 18 * 1, 2, 16, 16, 16 * 0, 16 * 0, 16, 16);
		} else {
			renderer.glFillRect(2 + 18 * 1, 2, 16, 16, 16 * 1, 16 * 0, 16, 16);
		}
		/*
		if (Calculator.angleMode == AngleMode.DEG) {
			drawSkinPart(8 + 18 * 2, 2, 16 * 4, 16 * 0, 16 + 16 * 4, 16 + 16 * 0);
			drawSkinPart(8 + 18 * 3, 2, 16 * 7, 16 * 0, 16 + 16 * 7, 16 + 16 * 0);
			drawSkinPart(8 + 18 * 4, 2, 16 * 9, 16 * 0, 16 + 16 * 9, 16 + 16 * 0);
		} else if (Calculator.angleMode == AngleMode.RAD) {
			drawSkinPart(8 + 18 * 2, 2, 16 * 5, 16 * 0, 16 + 16 * 5, 16 + 16 * 0);
			drawSkinPart(8 + 18 * 3, 2, 16 * 6, 16 * 0, 16 + 16 * 6, 16 + 16 * 0);
			drawSkinPart(8 + 18 * 4, 2, 16 * 9, 16 * 0, 16 + 16 * 9, 16 + 16 * 0);
		} else if (Calculator.angleMode == AngleMode.GRA) {
			drawSkinPart(8 + 18 * 2, 2, 16 * 5, 16 * 0, 16 + 16 * 5, 16 + 16 * 0);
			drawSkinPart(8 + 18 * 3, 2, 16 * 7, 16 * 0, 16 + 16 * 7, 16 + 16 * 0);
			drawSkinPart(8 + 18 * 4, 2, 16 * 8, 16 * 0, 16 + 16 * 8, 16 + 16 * 0);
		} else {
			drawSkinPart(8 + 18 * 2, 2, 16 * 5, 16 * 0, 16 + 16 * 5, 16 + 16 * 0);
			drawSkinPart(8 + 18 * 3, 2, 16 * 7, 16 * 0, 16 + 16 * 7, 16 + 16 * 0);
			drawSkinPart(8 + 18 * 4, 2, 16 * 9, 16 * 0, 16 + 16 * 9, 16 + 16 * 0);
		}*/

		int padding = 2;

		final int brightness = (int) (Math.ceil(DisplayManager.INSTANCE.getBrightness() * 9));
		if (brightness <= 10) {
			renderer.glFillRect(StaticVars.screenSize[0] - (padding + 16), 2, 16, 16, 16 * brightness, 16 * 1, 16, 16);
		} else {
			Utils.out.println(1, "Brightness error");
		}

		padding += 18 + 6;

		final boolean canGoBack = DisplayManager.INSTANCE.canGoBack();
		final boolean canGoForward = DisplayManager.INSTANCE.canGoForward();

		if (StaticVars.haxMode) {
			renderer.glFillRect(StaticVars.screenSize[0] - (padding + 16), 2, 16, 16, 16 * 18, 16 * 0, 16, 16);
			padding += 18 + 6;
		}

		if (canGoBack && canGoForward) {
			renderer.glFillRect(StaticVars.screenSize[0] - (padding + 16), 2, 16, 16, 16 * 14, 16 * 0, 16, 16);
		} else if (canGoBack) {
			renderer.glFillRect(StaticVars.screenSize[0] - (padding + 16), 2, 16, 16, 16 * 15, 16 * 0, 16, 16);
		} else if (canGoForward) {
			renderer.glFillRect(StaticVars.screenSize[0] - (padding + 16), 2, 16, 16, 16 * 16, 16 * 0, 16, 16);
		} else {
			renderer.glFillRect(StaticVars.screenSize[0] - (padding + 16), 2, 16, 16, 16 * 17, 16 * 0, 16, 16);
		}

		padding += 18;
		
		
		//DRAW BOTTOM
		this.d.renderer.glDrawStringLeft(2, 90, this.d.displayDebugString);

		Utils.getFont(true, false).use(DisplayManager.INSTANCE.engine);
		DisplayManager.INSTANCE.renderer.glColor4i(255, 0, 0, 40);
		DisplayManager.INSTANCE.renderer.glDrawStringLeft(1 + 1, StaticVars.screenSize[1] - 7 - 7 + 1, "WORK IN");
		DisplayManager.INSTANCE.renderer.glColor4i(255, 0, 0, 80);
		DisplayManager.INSTANCE.renderer.glDrawStringLeft(1, StaticVars.screenSize[1] - 7 - 7, "WORK IN");
		DisplayManager.INSTANCE.renderer.glColor4i(255, 0, 0, 40);
		DisplayManager.INSTANCE.renderer.glDrawStringLeft(1 + 1, StaticVars.screenSize[1] - 7 + 1, "PROGRESS.");
		DisplayManager.INSTANCE.renderer.glColor4i(255, 0, 0, 80);
		DisplayManager.INSTANCE.renderer.glDrawStringLeft(1, StaticVars.screenSize[1] - 7, "PROGRESS.");
	}

	@Override
	public void beforeRender(float dt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renderBackground() {
		// TODO Auto-generated method stub
		
	}

}
