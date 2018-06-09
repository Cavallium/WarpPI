package org.warp.picalculator.device;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.warp.picalculator.StaticVars;

public class CacheFile {
	private String path;
	private ObjectOutputStream lastOOS;
	private FileOutputStream lastFOS;
	private ObjectInputStream lastOIS;
	private FileInputStream lastFIS;

	public CacheFile() {
		do {
			path = UUID.randomUUID().toString() + ".ser";
		} while (Files.exists(Paths.get(path)));
		try {
			Files.createTempFile(StaticVars.calculatorNameLOWER, "");
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public ObjectOutputStream getObjectOutputStram() {
		if (lastOOS == null) {
			try {
				return new ObjectOutputStream(new FileOutputStream(path));
			} catch (final IOException e) {
				e.printStackTrace();
				return lastOOS;
			}
		} else {
			return lastOOS;
		}
	}

	public ObjectInputStream getObjectInputStram() {
		if (lastOIS == null) {
			try {
				return new ObjectInputStream(new FileInputStream(path));
			} catch (final IOException e) {
				return lastOIS;
			}
		} else {
			return lastOIS;
		}
	}

	public void closeStreams() {
		try {
			if (lastOOS != null) {
				lastOOS.close();
				lastOOS = null;
			}
			if (lastFOS != null) {
				lastFOS.close();
				lastFOS = null;
			}
			if (lastOIS != null) {
				lastOIS.close();
				lastOIS = null;
			}
			if (lastFIS != null) {
				lastFIS.close();
				lastFIS = null;
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void dispose() {
		closeStreams();
		try {
			Files.deleteIfExists(Paths.get(path));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
