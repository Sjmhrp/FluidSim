package sjmhrp.render.shader;

import java.util.HashMap;
import java.util.Map.Entry;

public class Shader {
	
	private static HashMap<String,ShaderProgram> shaders;

	public static void init() {
		shaders = new HashMap<String,ShaderProgram>();
		addShader("Generic",new PostShaderProgram("render/post/Generic","render/post/Generic"));
		connectTextures();
	}

	static void addShader(String name, ShaderProgram shader) {
		shaders.put(name,shader);
	}

	public static void connectTextures() {
		for(Entry<String,ShaderProgram> e : shaders.entrySet()) {
			if(!(e.getValue() instanceof MultiTextureShader))continue;
			ShaderProgram s = e.getValue();
			s.start();
			((MultiTextureShader)s).connectTextures();
			s.stop();
		}
	}

	public static void cleanUp() {
		for(Entry<String,ShaderProgram> e : shaders.entrySet()) {
			e.getValue().cleanUp();
		}
	}

	public static ShaderProgram getShader(String name) {
		return shaders.get(name);
	}

	public static PostShaderProgram getGenericShader() {
		return (PostShaderProgram)getShader("Generic");
	}
}