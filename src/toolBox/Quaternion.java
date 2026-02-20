package toolBox;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Quaternion {
	
	private float _w;
	private float _x;
	private float _y;
	private float _z;
	
	public Quaternion(float w, float x, float y, float z)
	{
		_w = w;
		_x = x;
		_y = y;
		_z = z;
	}
	
	public Quaternion(float x, float y, float z)
	{
		_w = 0f;
		_x = x;
		_y = y;
		_z = z;
	}
	
	public Quaternion(Vector3f vect)
	{
		_w = 0f;
		_x = vect.x;
		_y = vect.y;
		_z = vect.z;
	}
	
	public Quaternion(Quaternion quat)
	{
		_w = quat._w;
		_x = quat._x;
		_y = quat._y;
		_z = quat._z;
	}
	
	public Quaternion() {}
	
	public void set(float w, float x, float y, float z) 
	{
		_w = w;
		_x = x;
		_y = y;
		_z = z;
	}
	
	public void setW(float w) {_w = w;}
	public void setX(float x) {_x = x;}
	public void setY(float y) {_y = y;}
	public void setZ(float z) {_z = z;}
	
	public float getW() {return _w;}
	public float getX() {return _x;}
	public float getY() {return _y;}
	public float getZ() {return _z;}
	
	public Quaternion dot(Quaternion q2)
	{
		float newW = _w * q2.getW() - _x * q2.getX() - _y * q2.getY() - _z * q2.getZ();
		float newX = _w * q2.getX() + _x * q2.getW() + _y * q2.getZ() - _z * q2.getY();
		float newY = _w * q2.getY() - _x * q2.getZ() + _y * q2.getW() + _z * q2.getX();
		float newZ = _w * q2.getZ() + _x * q2.getY() - _y * q2.getX() + _z * q2.getW();
		
		return new Quaternion(newW, newX, newY, newZ);
	}
	
	public static Quaternion dot(Quaternion q1, Quaternion q2)
	{
		float newW = q1.getW() * q2.getW() - q1.getX() * q2.getX() - q1.getY() * q2.getY() - q1.getZ() * q2.getZ();
		float newX = q1.getW() * q2.getX() + q1.getX() * q2.getW() + q1.getY() * q2.getZ() - q1.getZ() * q2.getY();
		float newY = q1.getW() * q2.getY() - q1.getX() * q2.getZ() + q1.getY() * q2.getW() + q1.getZ() * q2.getX();
		float newZ = q1.getW() * q2.getZ() + q1.getX() * q2.getY() - q1.getY() * q2.getX() + q1.getZ() * q2.getW();
		
		return new Quaternion(newW, newX, newY, newZ);
	}
	
	public static void dot(Quaternion q1, Quaternion q2, Quaternion dest)
	{
		float newW = q1.getW() * q2.getW() - q1.getX() * q2.getX() - q1.getY() * q2.getY() - q1.getZ() * q2.getZ();
		float newX = q1.getW() * q2.getX() + q1.getX() * q2.getW() + q1.getY() * q2.getZ() - q1.getZ() * q2.getY();
		float newY = q1.getW() * q2.getY() - q1.getX() * q2.getZ() + q1.getY() * q2.getW() + q1.getZ() * q2.getX();
		float newZ = q1.getW() * q2.getZ() + q1.getX() * q2.getY() - q1.getY() * q2.getX() + q1.getZ() * q2.getW();
		dest._w = newW;
		dest._x = newX;
		dest._y = newY;
		dest._z = newZ;
	}
	
	public static Quaternion sub(Quaternion q1, Quaternion q2)
	{
		return new Quaternion(q1._w - q2._w, q1._x - q2._x, q1._y - q2._y, q1._z - q2._z);
	}
	
	public Quaternion negate()
	{
		return new Quaternion(_w, -_x, -_y, -_z);
	}
	
	public Quaternion norm()
	{
		float len = length();
		return new Quaternion(_w / len, _x / len, _y / len, _z / len);
	}
	
	public void normalise()
	{
		float len = length();
		_w /= _w / len;
		_x /= len;
		_y /= len;
		_z /= len;
	}
	
	public float length()
	{
		return (float)Math.sqrt(_w * _w + _x * _x + _y * _y + _z * _z);
	}
	
	public String toString()
	{
		return "Quaternion [" + Float.toString(_w) + "\t" + Float.toString(_x) + "\t" + Float.toString(_y) + "\t" + Float.toString(_z) + "]";
	}
	
	public Vector3f toVector3f()
	{
		return new Vector3f(_x, _y, _z);
	}
	
	public Vector3f toEuler()
	{
		Vector3f result = new Vector3f();
		
		Quaternion q = norm();

		result.x = (float) Math.atan2(2 * (q._x * q._w + q._y * q._z), 1 - 2 * (q._x * q._x + q._y * q._y));
		result.y = (float) Math.asin(2 * (q._y * q._w - q._z * q._x));
		result.z = (float) Math.atan2(2 * (q._z * q._w + q._x * q._y), 1 - 2 * (q._y * q._y + q._z * q._z));
		return result;
	}
	
	public Matrix4f toMatrix4f()
	{
		Matrix4f result = new Matrix4f();
		
		Quaternion q = norm();
		
		result.m00 = q._w * q._w + q._x * q._x - q._y * q._y - q._z * q._z;
		result.m10 = 2f * (q._x * q._y - q._z * q._w);
		result.m20 = 2f * (q._x * q._z + q._y * q._w);
		
		result.m01 = 2f * (q._x * q._y + q._z * q._w);
		result.m11 = q._w * q._w - q._x * q._x + q._y * q._y - q._z * q._z;
		result.m21 = 2f * (q._y * q._z - q._x * q._w);
		
		result.m02 = 2f * (q._x * q._z - q._y * q._w);
		result.m12 = 2f * (q._y * q._z + q._x * q._w);
		result.m22 = q._w * q._w - q._x * q._x - q._y * q._y + q._z * q._z;
		
		return result;
	}
	
	public Matrix3f toMatrix3f()
	{
		Matrix3f result = new Matrix3f();
		
		Quaternion q = norm();
		
		result.m00 = q._w * q._w + q._x * q._x - q._y * q._y - q._z * q._z;
		result.m10 = 2f * (q._x * q._y - q._z * q._w);
		result.m20 = 2f * (q._x * q._z + q._y * q._w);
		
		result.m01 = 2f * (q._x * q._y + q._z * q._w);
		result.m11 = q._w * q._w - q._x * q._x + q._y * q._y - q._z * q._z;
		result.m21 = 2f * (q._y * q._z - q._x * q._w);
		
		result.m02 = 2f * (q._x * q._z - q._y * q._w);
		result.m12 = 2f * (q._y * q._z + q._x * q._w);
		result.m22 = q._w * q._w - q._x * q._x - q._y * q._y + q._z * q._z;
		return result;
	}
	
	public static void fromAxis(Vector3f axis, float angle, Quaternion dest)
	{
		float cos = (float)Math.cos(angle);
		float sin = (float)Math.sin(angle);
		
		axis.normalise(axis);
		dest._w = cos;
		dest._x = sin * axis.x;
		dest._y = sin * axis.y;
		dest._z = sin * axis.z;
	}
	
	public static Quaternion toQuaternion(double roll, double pitch, double yaw)
	{
		double cr = Math.cos(roll * 0.5);
		double sr = Math.sin(roll * 0.5);
		double cp = Math.cos(pitch * 0.5);
		double sp = Math.sin(pitch * 0.5);
		double cy = Math.cos(yaw * 0.5);
		double sy = Math.sin(yaw * 0.5);
		Quaternion q = new Quaternion();
		q._w = (float)(cr * cp * cy + sr * sp * sy);
		q._x = (float)(sr * cp * cy - cr * sp * sy);
		q._y = (float)(cr * sp * cy + sr * cp * sy);
		q._z = (float)(cr * cp * sy - sr * sp * cy);
		return q;
	}
	
	public static Quaternion toQuaternion(double roll, double pitch, double yaw, Quaternion dest)
	{
		double cr = Math.cos(roll * 0.5);
		double sr = Math.sin(roll * 0.5);
		double cp = Math.cos(pitch * 0.5);
		double sp = Math.sin(pitch * 0.5);
		double cy = Math.cos(yaw * 0.5);
		double sy = Math.sin(yaw * 0.5);
		dest._w = (float)(cr * cp * cy + sr * sp * sy);
		dest._x = (float)(sr * cp * cy - cr * sp * sy);
		dest._y = (float)(cr * sp * cy + sr * cp * sy);
		dest._z = (float)(cr * cp * sy - sr * sp * cy);
		return dest;
	}
	
	public void add(Quaternion quat)
	{
		_w += quat._w;
		_x += quat._x;
		_y += quat._y;
		_z += quat._z;
	}
}
