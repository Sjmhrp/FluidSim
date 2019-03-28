package sjmhrp.core;

import org.lwjgl.opengl.Display;

import sjmhrp.render.RenderHandler;

public class Main {

	public static void main(String[] args) {
		RenderHandler.init();
		long time = System.nanoTime();
		while(!Display.isCloseRequested()) {
			double dt = System.nanoTime()-time;
			time=System.nanoTime();
			RenderHandler.render(dt/1e9);
		}
		RenderHandler.cleanUp();
	}
}