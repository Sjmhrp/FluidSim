package sjmhrp.opencl;

import static org.lwjgl.opencl.CL10.CL_PROGRAM_BUILD_LOG;
import static org.lwjgl.opencl.CL10.CL_SUCCESS;
import static org.lwjgl.opencl.CL10.CL_TRUE;
import static org.lwjgl.opencl.CL10.clBuildProgram;
import static org.lwjgl.opencl.CL10.clCreateKernel;
import static org.lwjgl.opencl.CL10.clCreateProgramWithSource;
import static org.lwjgl.opencl.CL10.clEnqueueReadBuffer;
import static org.lwjgl.opencl.CL10.clEnqueueWriteBuffer;
import static org.lwjgl.opencl.CL10.clReleaseProgram;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

import org.lwjgl.BufferUtils;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLKernel;
import org.lwjgl.opencl.CLMem;
import org.lwjgl.opencl.CLProgram;

import sjmhrp.io.Log;

public class CLProg {

	private CLProgram program;

	HashMap<String,CLKernel> kernels = new HashMap<String,CLKernel>();
	HashMap<String,CLMem> memory = new HashMap<String,CLMem>();

	public CLProg loadFromSource(String s) {
		program = clCreateProgramWithSource(CLHandler.context,s,null);
		int error = clBuildProgram(program,CLHandler.devices.get(0),"",null);
		if(error!=CL_SUCCESS) {
			Log.println(program.getBuildInfoString(CLHandler.devices.get(0),CL_PROGRAM_BUILD_LOG));
			System.exit(0);
		}
		CLHandler.addProgram(this);
		return this;
	}
	
	public CLProg loadFromFile(String... s) {
		return loadFromFile(null,s);
	}
	
	public CLProg loadFromFile(Function<String,String> preprocessor, String... s) {
		String prog = "";
		for(String name : s) {
			prog+=loadProg(name);
		}
		if(preprocessor!=null)prog=preprocessor.apply(prog);
		loadFromSource(prog);
		return this;
	}
	
	public void addKernel(String name, Object... args) {
		CLKernel kernel = clCreateKernel(program,name,null);
		for(int i = 0; i < args.length; i++) {
			Object o = args[i];
			if(o instanceof Byte)kernel.setArg(i,(Byte)o);
			if(o instanceof Double)kernel.setArg(i,(Double)o);
			if(o instanceof Float)kernel.setArg(i,(Float)o);
			if(o instanceof Integer)kernel.setArg(i,(Integer)o);
			if(o instanceof Long)kernel.setArg(i,(Long)o);
			if(o instanceof Short)kernel.setArg(i,(Short)o);
			if(o instanceof String)kernel.setArg(i,(CLMem)memory.get(o));
		}
		kernels.put(name,kernel);
	}
	
	public void addMemory(String name, int bytes) {
		addMemory(name,CLHandler.createMem(bytes));
	}
	
	public void addMemory(String name, float[] data) {
		addMemory(name,CLHandler.createMem(data));
	}
	
	public void addMemory(String name, int[] data) {
		addMemory(name,CLHandler.createMem(data));
	}
	
	public void addMemory(String name, ByteBuffer data) {
		addMemory(name,CLHandler.createMem(data));
	}
	
	public void addMemory(String name, CLMem mem) {
		memory.put(name,mem);
	}
	
	public void removeMemory(String mem) {
		memory.remove(mem);
	}
	
	public CLMem getMemory(String name) {
		return memory.get(name);
	}
	
	public void runKernel(String name, int... workItems) {
		CLHandler.runKernel(kernels.get(name),workItems);
	}
	
	public void queueKernel(String name, int... workItems) {
		CLHandler.queueKernel(kernels.get(name),workItems);
	}
	
	public void setArg(String name, int i, Object o) {
		CLKernel kernel = kernels.get(name);
		if(o instanceof Byte)kernel.setArg(i,(Byte)o);
		if(o instanceof Double)kernel.setArg(i,(Double)o);
		if(o instanceof Float)kernel.setArg(i,(Float)o);
		if(o instanceof Integer)kernel.setArg(i,(Integer)o);
		if(o instanceof Long)kernel.setArg(i,(Long)o);
		if(o instanceof Short)kernel.setArg(i,(Short)o);
		if(o instanceof String)kernel.setArg(i,(CLMem)memory.get(o));
	}
	
	public void writeMemory(String mem, FloatBuffer buffer) {
		buffer.rewind();
		clEnqueueWriteBuffer(CLHandler.queue,memory.get(mem),CL_TRUE,0,buffer,null,null);
	}
	
	public void writeMemory(String mem, IntBuffer buffer) {
		buffer.rewind();
		clEnqueueWriteBuffer(CLHandler.queue,memory.get(mem),CL_TRUE,0,buffer,null,null);
	}
	
	public void writeMemory(String mem, float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.rewind();
		clEnqueueWriteBuffer(CLHandler.queue,memory.get(mem),CL_TRUE,0,buffer,null,null);
	}
	
	public void writeMemory(String mem, int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.rewind();
		clEnqueueWriteBuffer(CLHandler.queue,memory.get(mem),CL_TRUE,0,buffer,null,null);
	}
	
	public FloatBuffer readMemoryFloat(String mem, int size) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(size);
		clEnqueueReadBuffer(CLHandler.queue,memory.get(mem),CL_TRUE,0,buffer,null,null);
		return buffer;
	}
	
	public IntBuffer readMemoryInt(String mem, int size) {
		IntBuffer buffer = BufferUtils.createIntBuffer(size);
		clEnqueueReadBuffer(CLHandler.queue,memory.get(mem),CL_TRUE,0,buffer,null,null);
		return buffer;
	}
	
	public ArrayList<CLKernel> getKernels() {
		return new ArrayList<CLKernel>(kernels.values());
	}

	public int memoryUnits() {
		return memory.size();
	}
	
	public CLKernel getKernel(String name) {
		return kernels.get(name);
	}
	
	public void cleanMem(String mem) {
		CL10.clReleaseMemObject(memory.get(mem));
		memory.remove(mem);
	}
	
	public void cleanMem() {
		memory.values().forEach(m->CL10.clReleaseMemObject(m));
		memory.clear();
	}
	
	public void cleanKernels() {
		kernels.values().forEach(CL10::clReleaseKernel);
		memory.values().forEach(CL10::clReleaseMemObject);
		kernels.clear();
		memory.clear();
	}
	
	public void cleanUp() {
		kernels.values().forEach(CL10::clReleaseKernel);
		kernels.clear();
		clReleaseProgram(program);
		memory.values().forEach(CL10::clReleaseMemObject);
		memory.clear();
		CLHandler.removeProgram(this);
	}
	
	public static String loadProg(String file) {
		StringBuilder source = new StringBuilder();
		try {
			InputStream in = Class.class.getResourceAsStream("/sjmhrp/"+file+".cls");
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while((line=reader.readLine())!=null){
				source.append(line).append("\n");
			}
			reader.close();
		} catch(Exception e) {
			Log.printError(e);
			System.exit(-1);
		}
		return source.toString();
	}
}