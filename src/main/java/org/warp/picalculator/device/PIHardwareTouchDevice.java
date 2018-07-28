package org.warp.picalculator.device;

import org.warp.picalculator.event.TouchCancelEvent;
import org.warp.picalculator.event.TouchEndEvent;
import org.warp.picalculator.event.TouchMoveEvent;
import org.warp.picalculator.event.TouchPoint;
import org.warp.picalculator.event.TouchStartEvent;
import org.warp.picalculator.gui.screens.Screen;

public class PIHardwareTouchDevice implements HardwareTouchDevice {

	private final boolean invertXY, invertX, invertY;
	
	public PIHardwareTouchDevice(boolean invertXY, boolean invertX, boolean invertY) {
		this.invertXY = invertXY;
		this.invertX = invertX;
		this.invertY = invertY;
	}
	
	@Override
	public boolean onTouchStart(TouchStartEvent e) {
		final Screen scr = HardwareDevice.INSTANCE.getDisplayManager().getScreen();
		boolean refresh = false;
		if (scr != null && scr.initialized && scr.onTouchStart(e)) {
			refresh = true;
		} else {
			//Default behavior
		}
		if (refresh) {
			HardwareDevice.INSTANCE.getDisplayManager().forceRefresh = true;
		}
		return true;
	}

	@Override
	public boolean onTouchEnd(TouchEndEvent e) {
		final Screen scr = HardwareDevice.INSTANCE.getDisplayManager().getScreen();
		boolean refresh = false;
		if (scr != null && scr.initialized && scr.onTouchEnd(e)) {
			refresh = true;
		} else {
			//Default behavior
		}
		if (refresh) {
			HardwareDevice.INSTANCE.getDisplayManager().forceRefresh = true;
		}
		return true;
	}

	@Override
	public boolean onTouchCancel(TouchCancelEvent e) {
		final Screen scr = HardwareDevice.INSTANCE.getDisplayManager().getScreen();
		boolean refresh = false;
		if (scr != null && scr.initialized && scr.onTouchCancel(e)) {
			refresh = true;
		} else {
			//Default behavior
		}
		if (refresh) {
			HardwareDevice.INSTANCE.getDisplayManager().forceRefresh = true;
		}
		return true;
	}

	@Override
	public boolean onTouchMove(TouchMoveEvent e) {
		final Screen scr = HardwareDevice.INSTANCE.getDisplayManager().getScreen();
		boolean refresh = false;
		if (scr != null && scr.initialized && scr.onTouchMove(e)) {
			refresh = true;
		} else {
			//Default behavior
		}
		if (refresh) {
			HardwareDevice.INSTANCE.getDisplayManager().forceRefresh = true;
		}
		return true;
	}

	@Override
	public boolean getInvertedXY() {
		return invertXY;
	}

	@Override
	public boolean getInvertedX() {
		return invertX;
	}

	@Override
	public boolean getInvertedY() {
		return invertY;
	}

	@Override
	public TouchPoint makePoint(long id, float x, float y, int screenWidth, int screenHeight, float radiusX, float radiusY, float force, float rotationAngle) {
		if (getInvertedXY()) {
			double oldX = x;
			double oldY = y;
			x = (float) (oldY * ((double)screenWidth)/((double)screenHeight));
			y = (float) (oldX * ((double)screenHeight)/((double)screenWidth));
		}
		if (getInvertedX()) {
			x = screenWidth - x;
		}
		if (getInvertedY()) {
			y = screenHeight - y;
		}
		return new TouchPoint(id, x, y, radiusX, radiusY, force, rotationAngle);
	}
}
