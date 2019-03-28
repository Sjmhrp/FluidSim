package sjmhrp.opencl;

import static org.lwjgl.opencl.CL10.CL_DEVICE_TYPE_GPU;
import static org.lwjgl.opencl.CL10.CL_MEM_COPY_HOST_PTR;
import static org.lwjgl.opencl.CL10.CL_MEM_READ_ONLY;
import static org.lwjgl.opencl.CL10.CL_MEM_READ_WRITE;
import static org.lwjgl.opencl.CL10.CL_QUEUE_PROFILING_ENABLE;
import static org.lwjgl.opencl.CL10.clCreateBuffer;
import static org.lwjgl.opencl.CL10.clCreateCommandQueue;
import static org.lwjgl.opencl.CL10.clEnqueueNDRangeKernel;
import static org.lwjgl.opencl.CL10.clFinish;
import static org.lwjgl.opencl.CL10.clReleaseCommandQueue;
import static org.lwjgl.opencl.CL10.clReleaseContext;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CL10GL;
import org.lwjgl.opencl.CLCommandQueue;
import org.lwjgl.opencl.CLContext;
import org.lwjgl.opencl.CLDevice;
import org.lwjgl.opencl.CLKernel;
import org.lwjgl.opencl.CLMem;
import org.lwjgl.opencl.CLPlatform;
import org.lwjgl.opencl.Util;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import sjmhrp.io.Log;

public class CLHandler {

	public static CLContext context;
	public static CLPlatform platform;
	public static List<CLDevice> devices;
	public static CLCommandQueue queue;
	
	static List<CLProg> programs = new ArrayList<CLProg>();
	
	public static void init() {
		try {
			IntBuffer error = BufferUtils.createIntBuffer(1);
			CL.create();
			platform = CLPlatform.getPlatforms().get(0);
			devices = platform.getDevices(CL_DEVICE_TYPE_GPU);
			context = CLContext.create(platform,devices,null,Display.getDrawable(),error);
			Util.checkCLError(error.get(0));
			queue = clCreateCommandQueue(context,devices.get(0),CL_QUEUE_PROFILING_ENABLE,error);
			Util.checkCLError(error.get(0));
		} catch(Exception e) {
			Log.printError(e);
			System.exit(0);
		}
	}

	public static void addProgram(CLProg p) {
		programs.add(p);
	}
	
	public static void removeProgram(CLProg p) {
		programs.remove(p);
	}
	
	public static CLMem createMem(int bytes) {
		IntBuffer errorBuffer = BufferUtils.createIntBuffer(1);
		CLMem mem = clCreateBuffer(context,CL_MEM_READ_WRITE,bytes,errorBuffer);
		Util.checkCLError(errorBuffer.get(0));
		return mem;
	}
	
	public static CLMem createMem(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		IntBuffer errorBuffer = BufferUtils.createIntBuffer(1);
		buffer.put(data);
		buffer.rewind();
		CLMem mem = clCreateBuffer(context,CL_MEM_READ_WRITE|CL_MEM_COPY_HOST_PTR,buffer,errorBuffer);
		Util.checkCLError(errorBuffer.get(0));
		return mem;
	}
	
	public static CLMem createMem(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		IntBuffer errorBuffer = BufferUtils.createIntBuffer(1);
		buffer.put(data);
		buffer.rewind();
		CLMem mem = clCreateBuffer(context,CL_MEM_READ_WRITE|CL_MEM_COPY_HOST_PTR,buffer,errorBuffer);
		Util.checkCLError(errorBuffer.get(0));
		return mem;
	}
	
	public static CLMem createMem(ByteBuffer buffer) {
		IntBuffer errorBuffer = BufferUtils.createIntBuffer(1);
		buffer.rewind();
		CLMem mem = clCreateBuffer(context,CL_MEM_READ_WRITE|CL_MEM_COPY_HOST_PTR,buffer,errorBuffer);
		Util.checkCLError(errorBuffer.get(0));
		return mem;
	}
	
	public static CLMem create2DTexture(int texture) {
		IntBuffer errorBuffer = BufferUtils.createIntBuffer(1);
		CLMem mem = CL10GL.clCreateFromGLTexture2D(context,CL_MEM_READ_WRITE,GL11.GL_TEXTURE_2D,0,texture,errorBuffer);
		Util.checkCLError(errorBuffer.get(0));
		return mem;
	}
	
	public static CLMem create3DTextureInput(int texture) {
		IntBuffer errorBuffer = BufferUtils.createIntBuffer(1);
		CLMem mem = CL10GL.clCreateFromGLTexture3D(context,CL_MEM_READ_ONLY,GL12.GL_TEXTURE_3D,0,texture,errorBuffer);
		Util.checkCLError(errorBuffer.get(0));
		return mem;
	}
	
	public static void runKernel(CLKernel kernel, int... workItems) {
		queueKernel(kernel,workItems);
		clFinish(queue);
	}
	
	public static void queueKernel(CLKernel kernel, int... workItems) {
		if(workItems.length==0)return;
		PointerBuffer buffer = BufferUtils.createPointerBuffer(workItems.length);
		for(int i = 0; i < workItems.length; i++) {
			buffer.put(i,workItems[i]);
		}
		queueKernel(kernel,buffer,null);
	}
	
	public static void queueKernel(CLKernel kernel, PointerBuffer globalWork, PointerBuffer localWork) {
		queueKernel(kernel,null,globalWork,localWork);
	}
	
	public static void queueKernel(CLKernel kernel, PointerBuffer globalWorkOffset, PointerBuffer globalWork, PointerBuffer localWork) {
		clEnqueueNDRangeKernel(queue,kernel,globalWork.capacity(),null,globalWork,localWork,null,null);
	}
	
	public static void finishQueue() {
		clFinish(queue);
	}

	static int blockSize(int count) {
		if(count == 0){return 0;}
		else if(count <= 1){return 1;}
		else if(count <= 2){return 2;}
		else if(count <= 4){return 4;}
		else if(count <= 8){return 8;}
		else if(count <= 16){return 16;}
		else if(count <= 32){return 32;}
		else if(count <= 64){return 64;}
		else if(count <= 128){return 128;}
		else {return 256;}
	}
	
	public static void cleanUp() {
		new ArrayList<CLProg>(programs).forEach(CLProg::cleanUp);
		clReleaseCommandQueue(queue);
		clReleaseContext(context);
		CL.destroy();
	}
}