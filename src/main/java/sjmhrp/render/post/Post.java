package sjmhrp.render.post;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;

import sjmhrp.core.Globals;
import sjmhrp.render.RenderHandler;
import sjmhrp.render.post.Fbo.FboType;
import sjmhrp.render.shader.PostShaderProgram;

public class Post {

	static ArrayList<PostShaderProgram> post = new ArrayList<PostShaderProgram>();
	static ArrayList<Fbo> fboCache = new ArrayList<Fbo>();
	
	static ArrayList<Fbo> pipeline = new ArrayList<Fbo>();

	public static Fbo main = new Fbo(Globals.GRID_SIZE,Globals.GRID_SIZE);
	public static Fbo tempVelocity = new Fbo(Globals.GRID_SIZE,Globals.GRID_SIZE,FboType.DEPTH_TEXTURE);
	public static Fbo velocity = new Fbo(Globals.GRID_SIZE,Globals.GRID_SIZE,FboType.DEPTH_TEXTURE);
	public static Fbo colour = new Fbo(Globals.GRID_SIZE,Globals.GRID_SIZE,FboType.DEPTH_TEXTURE);
	public static Fbo pressure1 = new Fbo(Globals.GRID_SIZE,Globals.GRID_SIZE,FboType.DEPTH_TEXTURE);
	public static Fbo pressure2 = new Fbo(Globals.GRID_SIZE,Globals.GRID_SIZE,FboType.DEPTH_TEXTURE);
	public static Fbo rayData;

	public static void init() {
		for(int i = 0; i < 10; i++)fboCache.add(new Fbo(Display.getWidth(),Display.getHeight(),FboType.DEPTH_TEXTURE));
		rayData=new Fbo(Display.getWidth(),Display.getHeight(),FboType.DEPTH_TEXTURE);
	}

	public static void clear() {
		post.clear();
		pipeline.clear();
	}

	public static void addToPipeline(PostShaderProgram s) {
		pipeline.add(fboCache.get(post.size()));
		post.add(s);
	}

	public static void process(Fbo input, Fbo output) {
		process(input,output,false);
	}
	
	public static void process(Fbo input, Fbo output, boolean add) {
		process(input.getColourTexture(),output,add);
	}
	
	public static void process(int t, Fbo output, boolean add) {
		int texture = t;
		for(int i = 0; i < post.size()-1; i++) {;
			PostShaderProgram s = post.get(i);
			pipeline.get(i).bindFrameBuffer();
			RenderHandler.clear();
			s.start();
			RenderHandler.renderQuad(s.getTextures(texture));
			s.stop();
			pipeline.get(i).unbindFrameBuffer();
			texture = pipeline.get(i).getColourTexture();
		}
		output.bindFrameBuffer();
		if(add) {
			glEnable(GL_BLEND);
			glBlendFunc(GL_ONE,GL_ONE);
		} else {
			RenderHandler.clear();
		}
		post.get(post.size()-1).start();
		RenderHandler.renderQuad(post.get(post.size()-1).getTextures(texture));
		post.get(post.size()-1).stop();
		if(add)glDisable(GL_BLEND);
		output.unbindFrameBuffer();
	}

	public static void display(Fbo f) {
		int texture = f.getColourTexture();
		for(int i = 0; i < post.size()-1; i++) {;
			PostShaderProgram s = post.get(i);
			pipeline.get(i).bindFrameBuffer();
			RenderHandler.clear();
			s.start();
			RenderHandler.clear();
			RenderHandler.renderQuad(s.getTextures(texture));
			s.stop();
			pipeline.get(i).unbindFrameBuffer();
			texture = pipeline.get(i).getColourTexture();
		}
        glBindFramebuffer(GL_FRAMEBUFFER,0);
        glViewport(0,0,Display.getWidth(),Display.getHeight());
		RenderHandler.clear();
		post.get(post.size()-1).start();
		RenderHandler.renderQuad(texture);
		post.get(post.size()-1).stop();
	}

	public static void cleanUp() {
		main.cleanUp();
		fboCache.forEach(Fbo::cleanUp);
	}
}
