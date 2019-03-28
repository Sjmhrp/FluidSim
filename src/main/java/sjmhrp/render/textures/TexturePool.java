package sjmhrp.render.textures;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import sjmhrp.render.Loader;
import sjmhrp.utils.linear.Vector3d;

public class TexturePool {
	
	static HashMap<String,Integer> pool = new HashMap<String,Integer>();
	
	public static int getTexture(String name) {
		Integer t = pool.get(name);
		if(t==null){
			t = Loader.loadTexture(name);
			pool.put(name,t);
		}
		return t;
	}

	public static int getColour(Vector3d colour) {
		String c = colour.x+";"+colour.y+";"+colour.z;
		Integer t = pool.get(c);
		if(t==null) {
			FloatBuffer buffer = ByteBuffer.allocateDirect(4*Float.SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
			buffer.put((float)colour.x).put((float)colour.y).put((float)colour.z).put(1);
			buffer.flip();
			t = GL11.glGenTextures();
			glBindTexture(GL_TEXTURE_2D,t);
	        GL11.glTexImage2D(GL11.GL_TEXTURE_2D,0,GL30.GL_RGBA32F,1,1,0,GL11.GL_RGBA,GL11.GL_FLOAT,buffer);
	        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
	        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
	        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_REPEAT);
	        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_REPEAT);
			pool.put(c,t);
		}
		return t;
	}
}