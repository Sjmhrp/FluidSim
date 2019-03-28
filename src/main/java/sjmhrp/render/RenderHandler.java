package sjmhrp.render;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthRange;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import sjmhrp.core.Globals;
import sjmhrp.fluid.FluidHandler;
import sjmhrp.io.KeyHandler;
import sjmhrp.io.Log;
import sjmhrp.opencl.CLHandler;
import sjmhrp.render.models.ModelPool;
import sjmhrp.render.post.Post;
import sjmhrp.render.shader.Shader;

public class RenderHandler {

	public static boolean paused = false;
	static double timeStep;
	static int tick = 0;
	
	public static void init() {
		try {
			Display.setTitle("FluidSim");
			Display.setResizable(false);
			Display.setDisplayMode(new DisplayMode(Globals.SCREEN_WIDTH,Globals.SCREEN_HEIGHT));
//			Display.setVSyncEnabled(true);
			Display.create();
			glEnable(GL_DEPTH_TEST);
			glViewport(0,0,Display.getWidth(),Display.getHeight());
			glDepthRange(0,1);
			enableCulling();
			KeyHandler.init();
			Post.init();
			Shader.init();
			ModelPool.init();
			CLHandler.init();
			FluidHandler.init();
		} catch(Exception e) {
			Log.printError(e);
		}
	}
	
	public static void render(double dt) {
		timeStep=dt;
		if(Keyboard.isKeyDown(Keyboard.KEY_R))FluidHandler.reset();
		if(!paused&&!Keyboard.isKeyDown(Keyboard.KEY_SPACE))FluidHandler.step(dt);
		clear();
		Shader.getGenericShader().start();
		renderQuad(FluidHandler.getColourTexture());
		Shader.getGenericShader().stop();
		Display.sync(60);
		Display.update();
		tick++;
	}
	
	public static void renderQuad(int... textures) {
		glBindVertexArray(ModelPool.getModel("quad").getVaoId());
		glEnableVertexAttribArray(0);
		for(int i = 0; i < textures.length; i++) {
			glActiveTexture(GL_TEXTURE0+i);
			glBindTexture(GL_TEXTURE_2D,textures[i]);
		}
		glDrawArrays(GL_TRIANGLE_STRIP,0,4);
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
	}
	
	public static void enableCulling() {
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
	}

	public static void disableCulling() {
		glDisable(GL_CULL_FACE);
	}
	
	public static void clear() {
		glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT|GL_STENCIL_BUFFER_BIT);		
	}
	
	public static double getTimeStep() {
		return timeStep;
	}
	
	public static void cleanUp() {
		Loader.cleanUp();
		Shader.cleanUp();
	}
}