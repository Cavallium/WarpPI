package it.cavallium.warppi.gui.graphicengine.framebuffer;

import java.io.IOException;

import it.cavallium.warppi.gui.graphicengine.GraphicEngine;
import it.cavallium.warppi.gui.graphicengine.common.PngSkin;

public class FBSkin extends PngSkin {

	public FBSkin(String file) throws IOException {
		super(file);
	}

	@Override
	public void use(GraphicEngine d) {
		FBEngine dfb = (FBEngine) d;
		// TODO: implement
	}

}
