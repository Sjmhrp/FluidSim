package sjmhrp.utils.linear;

import java.io.Serializable;
import java.util.Objects;

public class Vector3i implements Serializable{
	
	private static final long serialVersionUID = -2600152262788331002L;
	
	public int x,y,z;

	public Vector3i() {}

	public Vector3i(int d) {
		set(d,d,d);
	}

	public Vector3i(int x, int y, int z) {
		set(x,y,z);
	}

	public Vector3i(int x, Vector2i v) {
		set(x,v.x,v.y);
	}
	
	public Vector3i(Vector2i v, int z) {
		set(v.x,v.y,z);
	}
	
	public Vector3i(Vector3i v) {
		set(v.x,v.y,v.z);
	}

	public Vector3i(Vector4i v) {
		set(v.x,v.y,v.z);
	}
	
	public Vector3i set(Vector3i v) {
		x=v.x;
		y=v.y;
		z=v.z;
		return this;
	}
	
	public Vector3i set(int x, int y, int z) {
		this.x=x;
		this.y=y;
		this.z=z;
		return this;
	}
	
	public int get(int i) {
		switch(i) {
			case 0:return x;
			case 1:return y;
			case 2:return z;
		}
		return 0;
	}
	
	public Vector3i zero() {
		return set(0,0,0);
	}

	public Vector3i add(Vector3i v) {
		x+=v.x;
		y+=v.y;
		z+=v.z;
		return this;
	}

	public Vector3i sub(Vector3i v) {
		x-=v.x;
		y-=v.y;
		z-=v.z;
		return this;
	}

	public Vector3i scale(int d) {
		x*=d;
		y*=d;
		z*=d;
		return this;
	}

	public Vector3i scale(Vector3i v) {
		x*=v.x;
		y*=v.y;
		z*=v.z;
		return this;
	}
	
	public Vector3i mod(int m) {
		x%=m;
		y%=m;
		z%=m;
		return this;
	}
	
	public Vector3i abs() {
		x=Math.abs(x);
		y=Math.abs(y);
		z=Math.abs(z);
		return this;
	}
	
	public Vector3i getAbs() {
		return new Vector3i(this).abs();
	}
	
	public Vector3i negate() {
		return set(-x,-y,-z);
	}
	
	public Vector3i getNegative() {
		return new Vector3i(this).negate();
	}
	
	public int dot(Vector3i v) {
		return x*v.x+y*v.y+z*v.z;
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
	
	public static Vector3i add(Vector3i u, Vector3i v) {
		return new Vector3i(u).add(v);
	}
	
	public static Vector3i sub(Vector3i u, Vector3i v) {
		return new Vector3i(u).sub(v);
	}
	
	public static Vector3i scale(int d, Vector3i v) {
		return new Vector3i(v).scale(d);
	}
	
	public static Vector3i scale(Vector3i u, Vector3i v) {
		return new Vector3i(u).scale(v);
	}
	
	public static Vector3i cross(Vector3i u, Vector3i v) {
		return new Vector3i(u.y*v.z-u.z*v.y,v.x*u.z-v.z*u.x,u.x*v.y-u.y*v.x);
	}
	
	public static int dot(Vector3i u, Vector3i v) {
		return u.dot(v);
	}
	
	@Override
	public String toString() {
		return "Vector3d["+x+", "+y+", "+z+"]";
	}
	
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Vector3i))return false;
		Vector3i v = (Vector3i)o;
		return x==v.x&&y==v.y&&z==v.z;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x,y,z);
	}
}