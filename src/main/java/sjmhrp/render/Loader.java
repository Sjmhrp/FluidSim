package sjmhrp.render;

import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import sjmhrp.io.Log;
import sjmhrp.render.models.MeshData;
import sjmhrp.render.models.RawModel;

public class Loader {

	private static ArrayList<Integer> vaos = new ArrayList<Integer>();
	private static ArrayList<Integer> vbos = new ArrayList<Integer>();
	private static ArrayList<Integer> textures = new ArrayList<Integer>();
	
	public static RawModel load(double[] pos, int[] indices, double[] normals, double[] uv) {
		int id = create();
		int i = bindIndices(indices);
		int v = store(0,3,pos);
		int u = store(1,2,uv);
		int n = store(2,3,normals);
		MeshData m = new MeshData(pos,v,uv,u,normals,n,indices,i);
		return new RawModel(id,indices.length,m);
	}

	public static RawModel load(double[] pos, int[] indices, double[] normals) {
		int id = create();
		int i = bindIndices(indices);
		int v = store(0,3,pos);
		int n = store(1,3,normals);
		unbind();
		MeshData m = new MeshData(pos,v,normals,n,indices,i);
		return new RawModel(id,indices.length,m);
	}

	public static RawModel load(double[] pos, int dim) {
		int id = create();
		int vbo = store(0,dim,pos);
		unbind();
		MeshData m = new MeshData(pos,vbo);
		return new RawModel(id,pos.length/dim,m);
	}
	
	public static int loadTexture(String file) {
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG",Class.class.getResourceAsStream("/res/textures/"+file+".png"));
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MIN_FILTER,GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D,GL14.GL_TEXTURE_LOD_BIAS,0);
	        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
	        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
	        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_CLAMP);
	        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_CLAMP);
			if(GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
				float amount = Math.min(4,GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
			} else {
				Log.println("Anisotropic Filtering is not supported");
			}
		} catch (Exception e) {
			Log.println("Tried to load "+file+".png");
			Log.printError(e);
		}
		int id = texture.getTextureID();
		textures.add(id);
		return id;
	}

	private static int create() {
		int id = GL30.glGenVertexArrays();
		vaos.add(id);
		GL30.glBindVertexArray(id);
		return id;
	}
	
	private static int store(int n, int size, double[] data) {
		int id = GL15.glGenBuffers();
		vbos.add(id);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
		FloatBuffer f = convertFloats(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER,f,GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(n,size,GL11.GL_FLOAT,false,0,0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,0);
		return id;
	}

	private static void unbind() {
		GL30.glBindVertexArray(0);
	}

	private static int bindIndices(int[] indices) {
		int id = GL15.glGenBuffers();
		vbos.add(id);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, id);
		IntBuffer i = convertInts(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, i, GL15.GL_STATIC_DRAW);
		return id;
	}

	public static IntBuffer convertInts(int[] data) {
		IntBuffer i = BufferUtils.createIntBuffer(data.length);
		i.put(data);
		i.flip();
		return i;
	}

	public static FloatBuffer convertFloats(double[] data) {
		FloatBuffer f = BufferUtils.createFloatBuffer(data.length);
		for(double d : data) {
			f.put((float)d);
		}
		f.flip();
		return f;
	}
	
	public static void cleanUp() {
		for(int i : vaos) {
			GL30.glDeleteVertexArrays(i);
		}
		for(int i : vbos) {
			GL15.glDeleteBuffers(i);
		}
		for(int i : textures) {
			GL11.glDeleteTextures(i);
		}
		vaos.clear();
		vbos.clear();
		textures.clear();
	}
}