package sjmhrp.utils.linear;

import java.io.Serializable;
import java.util.Objects;

public class Vector4i implements Serializable{
	
	private static final long serialVersionUID = 4055110419647812882L;
	
	public int x,y,z,w;
	
	public Vector4i() {}
	
	public Vector4i(int d) {
		set(d,d,d,d);
	}
	
	public Vector4i(int x, int y, int z, int w) {
		set(x,y,z,w);
	}
	
	public Vector4i(Vector2i v, int z, int w) {
		set(v.x,v.y,z,w);
	}
	
	public Vector4i(int x, Vector2i v, int w) {
		set(x,v.x,v.y,w);
	}
	
	public Vector4i(int x, int y, Vector2i v) {
		set(x,y,v.x,v.y);
	}
	
	public Vector4i(Vector2i a, Vector2i b) {
		set(a.x,a.y,b.x,b.y);
	}
	
	public Vector4i(Vector3i v, int w) {
		set(v.x,v.y,v.z,w);
	}
	
	public Vector4i(int x, Vector3i v) {
		set(x,v.x,v.y,v.z);
	}
	
	public Vector4i(Vector4i v) {
		set(v.x,v.y,v.z,v.w);
	}
	
	public Vector4i set(Vector4i v) {
		x=v.x;
		y=v.y;
		z=v.z;
		w=v.w;
		return this;
	}
	
	public Vector4i set(int x, int y, int z, int w) {
		this.x=x;
		this.y=y;
		this.z=z;
		this.w=w;
		return this;
	}
	
	public int get(int i) {
		switch(i) {
			case 0:return x;
			case 1:return y;
			case 2:return z;
			case 3:return w;
		}
		return 0;
	}
	
	public Vector4i zero() {
		return set(0,0,0,0);
	}
	
	public Vector4i add(Vector4i v) {
		x+=v.x;
		y+=v.y;
		z+=v.z;
		w+=v.w;
		return this;
	}
	
	public Vector4i sub(Vector4i v) {
		x-=v.x;
		y-=v.y;
		z-=v.z;
		w-=v.w;
		return this;
	}
	
	public Vector4i scale(int d) {
		x*=d;
		y*=d;
		z*=d;
		w*=d;
		return this;
	}
	
	public Vector4i scale(Vector4i v) {
		x*=v.x;
		y*=v.y;
		z*=v.z;
		w*=v.w;
		return this;
	}
	
	public Vector4i mod(int m) {
		x%=m;
		y%=m;
		z%=m;
		w%=m;
		return this;
	}
	
	public Vector4i abs() {
		x=Math.abs(x);
		y=Math.abs(y);
		z=Math.abs(z);
		w=Math.abs(w);
		return this;
	}
	
	public Vector4i getAbs() {
		return new Vector4i(this).abs();
	}
	
	public Vector4i negate() {
		return set(-x,-y,-z,-w);
	}
	
	public Vector4i getNegative() {
		return new Vector4i(this).negate();
	}
	
	public int dot(Vector4i v) {
		return x*v.x+y*v.y+z*v.z+w*v.w;
	}
	
	public int lengthSquared() {
		return dot(this);
	}
	
	public double length() {
		return Math.sqrt(lengthSquared());
	}

	public Vector2d xy() {
		return new Vector2d(x,y);
	}
	
	public Vector2d xz() {
		return new Vector2d(x,z);
	}
	
	public Vector2d yz() {
		return new Vector2d(y,z);
	}

	public Vector2d xw() {
		return new Vector2d(x,w);
	}
	
	public Vector2d yw() {
		return new Vector2d(y,w);
	}
	
	public Vector2d zw() {
		return new Vector2d(z,w);
	}

	public Vector3d xyz() {
		return new Vector3d(x,y,z);
	}
	
	public Vector3d yzw() {
		return new Vector3d(y,z,w);
	}
	
	public Vector3d xzw() {
		return new Vector3d(x,z,w);
	}
	
	public Vector3d xyw() {
		return new Vector3d(x,y,w);
	}
	
	public static Vector4i add(Vector4i u, Vector4i v) {
		return new Vector4i(u).add(v);
	}

	public static Vector4i sub(Vector4i u, Vector4i v) {
		return new Vector4i(u).sub(v);
	}
	
	public static Vector4i scale(int d, Vector4i v) {
		return new Vector4i(v).scale(d);
	}
	
	public static Vector4i scale(Vector4i u, Vector4i v) {
		return new Vector4i(u).scale(v);
	}
	
	public static Vector4i cross(Vector4i u, Vector4i v, Vector4i w) {
		int a = v.x*w.y-v.y*w.x;
		int b = v.x*w.z-v.z*w.x;
		int c = v.x*w.w-v.w*w.x;
		int d = v.y*w.z-v.z*w.y;
		int e = v.y*w.w-v.w*w.y;
		int f = v.z*w.w-v.w*w.z;
		return new Vector4i(u.y*f-u.z*e+u.w*d,-u.x*f+u.z*c-u.w*b,u.x*e-u.y*c+u.w*a,-u.x*d+u.y*b-u.z*a);
	}
	
	public static int dot(Vector4i u, Vector4i v) {
		return u.dot(v);
	}
	
	@Override
	public String toString() {
		return "Vector4d["+x+", "+y+", "+z+", "+w+"]";
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Vector4i))return false;
		Vector4i v = (Vector4i)o;
		return x==v.x&&y==v.y&&z==v.z&&w==v.w;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x,y,z,w);
	}
}