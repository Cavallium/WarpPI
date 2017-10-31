package org.warp.picalculator.device.chip;

import com.pi4j.wiringpi.Gpio;

public class ParallelToSerial {

	private final int SH_LD;
	private final int CLK_INH;
	private final int QH;
	private final int CLK;

	public ParallelToSerial(int SH_LD_pin, int CLK_INH_pin, int QH_pin, int CLK_pin) {
		SH_LD = SH_LD_pin;
		CLK_INH = CLK_INH_pin;
		QH = QH_pin;
		CLK = CLK_pin;
	}

	public boolean[] read() {
		final boolean[] data = new boolean[8];
		Gpio.digitalWrite(CLK_INH, Gpio.HIGH);
		Gpio.digitalWrite(SH_LD, Gpio.LOW);
		Gpio.delayMicroseconds(1);
		Gpio.digitalWrite(SH_LD, Gpio.HIGH);
		Gpio.digitalWrite(CLK_INH, Gpio.LOW);

		for (int i = 7; i >= 0; i--) {
			Gpio.digitalWrite(CLK, Gpio.HIGH);
			Gpio.digitalWrite(CLK, Gpio.LOW);
			data[i] = Gpio.digitalRead(QH) == Gpio.HIGH ? true : false;
		}

		return data;
	}
}
