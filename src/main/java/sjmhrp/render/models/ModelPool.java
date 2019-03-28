package sjmhrp.render.models;

import java.util.HashMap;

import sjmhrp.io.Log;
import sjmhrp.render.Loader;

public class ModelPool {

	static final String RES_LOC = "/res/models/";
	static HashMap<String,RawModel> pool = new HashMap<String,RawModel>();
	
	public static void init() {
		pool.put("quad",Loader.load(new double[] {-1,1,-1,-1,1,1,1,-1},2));
		pool.put("3quad",Loader.load(new double[]{1,1,0,1,-1,0,-1,-1,0,-1,1,0},new int[]{2,0,3,1,0,2},new double[]{0,0,1,0,0,1,0,0,1,0,0,1},new double[]{1,0,1,1,0,1,0,0}));
		pool.put("cube",Loader.load(new double[]{-1,1,-1,-1,-1,-1,1,-1,-1,1,-1,-1,1,1,-1,-1,1,-1,-1,-1,1,-1,-1,-1,-1,1,-1,-1,1,-1,-1,1,1,-1,-1,1,1,-1,-1,1,-1,1,1,1,1,1,1,1,1,1,-1,1,-1,-1,-1,-1,1,-1,1,1,1,1,1,1,1,1,1,-1,1,-1,-1,1,-1,1,-1,1,1,-1,1,1,1,1,1,1,-1,1,1,-1,1,-1,-1,-1,-1,-1,-1,1,1,-1,-1,1,-1,-1,-1,-1,1,1,-1,1},3));
	}	

	public static RawModel getModel(String name) {
		RawModel m = pool.get(name);
		if(m==null) {
			Log.printError(new Exception("Model "+name+" Not Found"));
			System.exit(0);
		}
		return m;
	}
}