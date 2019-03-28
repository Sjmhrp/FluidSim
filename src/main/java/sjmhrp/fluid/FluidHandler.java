package sjmhrp.fluid;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;

import java.nio.ByteBuffer;

import org.lwjgl.input.Mouse;
import org.lwjgl.opencl.CLMem;
import org.lwjgl.opengl.Display;

import sjmhrp.core.Globals;
import sjmhrp.opencl.CLHandler;
import sjmhrp.opencl.CLProg;
import sjmhrp.utils.linear.Vector2d;

public class FluidHandler {

	static CLProg fluid;
	static int velocity1Texture;
	public static int velocity2Texture;
	static boolean colourSwitch = true;
	static int colour1Texture;
	static int colour2Texture;
	static boolean pressureSwitch = true;
	static int pressure1Texture;
	static int pressure2Texture;
	static CLMem velocity1;
	static CLMem velocity2;
	static CLMem colour1;
	static CLMem colour2;
	static CLMem pressure1;
	static CLMem pressure2;
	static boolean pressed = false;
	static Vector2d prevMouse = new Vector2d();
	static Vector2d mouseDir = new Vector2d();
	
	public static void init() {
		fluid = new CLProg().loadFromFile("fluid/fluid");
		fluid.addKernel("initialize");
		fluid.addKernel("advect");
		fluid.addKernel("divergence");
		fluid.addKernel("jacobiPressure");
		fluid.addKernel("subtractPressure");
		velocity1Texture=create();
		velocity2Texture=create();
		colour1Texture=create();
		colour2Texture=create();
		pressure1Texture=create();
		pressure2Texture=create();
		velocity1=CLHandler.create2DTexture(velocity1Texture);
		velocity2=CLHandler.create2DTexture(velocity2Texture);
		colour1=CLHandler.create2DTexture(colour1Texture);
		colour2=CLHandler.create2DTexture(colour2Texture);
		pressure1=CLHandler.create2DTexture(pressure1Texture);
		pressure2=CLHandler.create2DTexture(pressure2Texture);
		fluid.addMemory("velocity1",velocity1);
		fluid.addMemory("velocity2",velocity2);
		fluid.addMemory("colour1",colour1);
		fluid.addMemory("colour2",colour2);
		fluid.addMemory("pressure1",pressure1);
		fluid.addMemory("pressure2",pressure2);
		fluid.setArg("initialize",0,"velocity1");
		fluid.setArg("initialize",1,"colour1");
		fluid.setArg("initialize",2,Globals.GRID_SIZE);
		fluid.runKernel("initialize",Globals.GRID_SIZE,Globals.GRID_SIZE);
	}
	
	public static void reset() {
		fluid.setArg("initialize",0,"velocity1");
		fluid.setArg("initialize",1,"colour1");
		fluid.setArg("initialize",2,Globals.GRID_SIZE);
		fluid.runKernel("initialize",Globals.GRID_SIZE,Globals.GRID_SIZE);
	}
	
	static int create() {
		int id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D,id);
        glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA32F,Globals.GRID_SIZE,Globals.GRID_SIZE,0,GL_RGBA,GL_FLOAT,(ByteBuffer)null);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_REPEAT);
        glFramebufferTexture2D(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT0,GL_TEXTURE_2D,id,0);
        return id;
	}
	
	static void handleMouse() {
		mouseDir.zero();
		if(Mouse.isButtonDown(0)) {
			if(!pressed) {
				pressed=true;
			} else {
				mouseDir.set(Mouse.getX()/(float)Display.getWidth(),Mouse.getY()/(float)Display.getHeight()).sub(prevMouse);
			}
			prevMouse.set(Mouse.getX()/(float)Display.getWidth(),Mouse.getY()/(float)Display.getHeight());
		} else {
			pressed=false;
		}
	}
	
	public static void step(double dt) {
		handleMouse();
		fluid.setArg("advect",0,"velocity1");
		fluid.setArg("advect",1,"velocity2");
		fluid.setArg("advect",2,colourSwitch?"colour1":"colour2");
		colourSwitch=!colourSwitch;
		fluid.setArg("advect",3,colourSwitch?"colour1":"colour2");
		fluid.setArg("advect",4,(float)1./Globals.GRID_SIZE);
		fluid.setArg("advect",5,(float)dt);
		fluid.setArg("advect",6,(float)mouseDir.x);
		fluid.setArg("advect",7,(float)mouseDir.y);
		fluid.setArg("advect",8,(float)Mouse.getX()/Display.getWidth());
		fluid.setArg("advect",9,(float)Mouse.getY()/Display.getHeight());
		fluid.queueKernel("advect",Globals.GRID_SIZE,Globals.GRID_SIZE);
		fluid.setArg("divergence",0,"velocity2");
		fluid.setArg("divergence",1,pressureSwitch?"pressure1":"pressure2");
		fluid.setArg("divergence",2,(float)1./Globals.GRID_SIZE);
		fluid.queueKernel("divergence",Globals.GRID_SIZE,Globals.GRID_SIZE);
		for(int i = 0; i < Globals.JACOBI_ITERATIONS; i++) {
			fluid.setArg("jacobiPressure",0,pressureSwitch?"pressure1":"pressure2");
			pressureSwitch=!pressureSwitch;
			fluid.setArg("jacobiPressure",1,pressureSwitch?"pressure1":"pressure2");
			fluid.setArg("jacobiPressure",2,(float)1./Globals.GRID_SIZE);
			fluid.queueKernel("jacobiPressure",Globals.GRID_SIZE,Globals.GRID_SIZE);
		}
		fluid.setArg("subtractPressure",0,"velocity2");
		fluid.setArg("subtractPressure",1,pressureSwitch?"pressure1":"pressure2");
		fluid.setArg("subtractPressure",2,"velocity1");
		fluid.setArg("subtractPressure",3,(float)1./Globals.GRID_SIZE);
		fluid.queueKernel("subtractPressure",Globals.GRID_SIZE,Globals.GRID_SIZE);
		CLHandler.finishQueue();
	}
	
	public static int getColourTexture() {
		return colourSwitch?colour1Texture:colour2Texture;
	}
}