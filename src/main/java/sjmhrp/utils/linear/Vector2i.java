package sjmhrp.utils.linear;

import java.io.Serializable;
import java.util.Objects;

public class Vector2i implements Serializable{
	
	private static final long serialVersionUID = -3383228540145847036L;
	
	public int x,y;

	public Vector2i() {}

	public Vector2i(int d) {
		set(d,d);
	}

	public Vector2i(int x, int y) {
		set(x,y);
	}

	public Vector2i(Vector2i v) {
		set(v.x,v.y);
	}

	public Vector2i(Vector3i v) {
		set(v.x,v.y);
	}
	
	public Vector2i(Vector4i v) {
		set(v.x,v.y);
	}
	
	public Vector2i set(Vector2i v) {
		x=v.x;
		y=v.y;
		return this;
	}
	
	public Vector2i set(int x, int y) {
		this.x=x;
		this.y=y;
		return this;
	}

	public int get(int i) {
		switch(i) {
			case 0:return x;
			case 1:return y;
		}
		return 0;
	}
	
	public Vector2i zero() {
		return set(0,0);
	}

	public Vector2i add(Vector2i v) {
		x+=v.x;
		y+=v.y;
		return this;
	}

	public Vector2i sub(Vector2i v) {
		x-=v.x;
		y-=v.y;
		return this;
	}

	public Vector2i scale(int d) {
		x*=d;
		y*=d;
		return this;
	}

	public Vector2i scale(Vector2i v) {
		x*=v.x;
		y*=v.y;
		return this;
	}
	
	public Vector2i mod(int m) {
		x%=m;
		y%=m;
		return this;
	}
	
	public Vector2i abs() {
		x=Math.abs(x);
		y=Math.abs(y);
		return this;
	}

	public Vector2i getAbs() {
		return new Vector2i(this).abs();
	}
	
	public Vector2i negate() {
		return set(-x,-y);
	}

	public Vector2i getNegative() {
		return new Vector2i(this).negate();
	}
	
	public int dot(Vector2i v) {
		return x*v.x+y*v.y;
	}

	public int lengthSquared() {
		return dot(this);
	}

	public double length() {
		return Math.sqrt(lengthSquared());
	}

	public static Vector2i add(Vector2i u, Vector2i v) {
		return new Vector2i(u).add(v);
	}
	
	public static Vector2i sub(Vector2i u, Vector2i v) {
		return new Vector2i(u).sub(v);
	}

	public static Vector2i scale(int d, Vector2i v) {
		return new Vector2i(v).scale(d);
	}
	
	public static Vector2i scale(Vector2i u, Vector2i v) {
		return new Vector2i(u).scale(v);
	}
	
	public static int dot(Vector2i u, Vector2i v) {
		return u.dot(v);
	}
	
	@Override
	public String toString() {
		return "Vector2i["+x+", "+y+"]";
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Vector2i))return false;
		Vector2i v = (Vector2i)o;
		return x==v.x&&y==v.y;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x,y);
	}
}