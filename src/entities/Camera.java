package entities;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import toolBox.Maths;
import toolBox.Quaternion;

public class Camera 
{
	private Vector3f _pos = new Vector3f(0f, 0f, 0f);
	private Vector3f _startPos = new Vector3f(0f, 0f, 0f);
	private Vector3f _rot2 = new Vector3f(0, 0, 0);
	private Quaternion _rot = Quaternion.toQuaternion(0f, 0f, 0f);
	private Vector3f _dir = new Vector3f(0f, 0f, -1f);
	private Vector3f _dirRight = new Vector3f(1f, 0f, 0f);
	private Vector3f _dirUp = new Vector3f(0f, 1f, 0f);
	private Vector3f _incrz = new Vector3f();
	private Vector3f _incrx = new Vector3f();
	private Vector3f _incry = new Vector3f();
	private float _camSpeed = 0.03f;
	private float _camSens = 0.02f;
	
	private ArrayList<Body> _bodies;
	private ArrayList<Joint> _joints;
	public Camera(Vector3f pos, ArrayList<Body> bd, ArrayList<Joint> jt) 
	{
		_startPos = new Vector3f(pos);
		_pos = new Vector3f(pos);
		_bodies = bd; _joints = jt;
	}
	public Camera() {}
	
	public void move()
	{		
		int wheel = Mouse.getDWheel();
		
		if (wheel < 0)
		{
			_incrz.normalise();
			_incrz.scale(_camSpeed * 2);
			Vector3f.sub(_pos, _incrz, _pos);
		}
		if (wheel > 0)
		{
			_incrz.normalise();
			_incrz.scale(_camSpeed * 2);
			Vector3f.add(_pos, _incrz, _pos);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S))
			Vector3f.sub(_pos, _incrz, _pos);
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
			Vector3f.add(_pos, _incrz, _pos);
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
			Vector3f.sub(_pos, _incrx, _pos);
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			Vector3f.add(_pos, _incrx, _pos);
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			Vector3f.add(_pos, _incry, _pos);
		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_C))
			Vector3f.sub(_pos, _incry, _pos);
		
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			reset();
	//	System.out.println(_pos);
	}
	
	public void moveUp()
	{
		Vector3f.add(_pos, _incry, _pos);
	}
	
	public void moveDown()
	{
		Vector3f.sub(_pos, _incry, _pos);
	}
	
	public void moveLeft()
	{
		Vector3f.sub(_pos, _incrx, _pos);
	}
	
	public void moveRight()
	{
		Vector3f.add(_pos, _incrx, _pos);
	}
	
	public void moveForward()
	{
		Vector3f.add(_pos, _incrz, _pos);
	}
	
	public void moveBackward()
	{
		Vector3f.sub(_pos, _incrz, _pos);
	}
	
	public void rotate()
	{		
		if (Mouse.isButtonDown(0))
		{
			float incy = Mouse.getDX() * 0.01f;
			float incx = Mouse.getDY() * 0.01f;
			_rot2.y += incy;
			_rot2.x -= incx;
			_pos = Maths.rotateAroundPoint(_pos, _startPos, _dirUp, -incy);
			_pos = Maths.rotateAroundPoint(_pos, _startPos, Maths.rotate(_dirRight, _dirUp, -_rot2.y), incx);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_E) || Keyboard.isKeyDown(Keyboard.KEY_LEFT))
		{
			_rot2.y += _camSens;
			_pos = Maths.rotateAroundPoint(_pos, _startPos, _dirUp, -_camSens);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_Q) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
		{
			_rot2.y -= _camSens;
			_pos = Maths.rotateAroundPoint(_pos, _startPos, _dirUp, _camSens);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
		{
			_rot2.x -= _camSens;
			_pos = Maths.rotateAroundPoint(_pos, _startPos, Maths.rotate(_dirRight, _dirUp, -_rot2.y), _camSens);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_UP))
		{
			_rot2.x += _camSens;
			_pos = Maths.rotateAroundPoint(_pos, _startPos, Maths.rotate(_dirRight, _dirUp, -_rot2.y), -_camSens);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_N))
		{
			_rot2.z -= _camSens;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_M))
		{
			_rot2.z += _camSens;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			reset();
		_incrz = Maths.rotate(_dir, _dirRight, -_rot2.x);
		_incrz = Maths.rotate(_incrz, _dirUp, -_rot2.y);
		
		_incrx = Maths.rotate(_dirRight, _dirUp, -_rot2.y);
		
		Vector3f.cross(_incrx, _incrz, _incry);
		_incrx.normalise();
		_incry.normalise();
		_incrz.normalise();
		
		_incrz.scale(_camSpeed);
		_incrx.scale(_camSpeed);
		_incry.scale(_camSpeed);
	}
	
	public void reset()
	{
		Vector3f center = new Vector3f();
		for (Body bd : _bodies)
			Vector3f.add(bd.getPos(), center, center);

		for (Joint jt : _joints)
			Vector3f.add(jt.getPos(), center, center);
		
		center.scale(1.f / (_bodies.size() + _joints.size()));

		float len = Vector3f.sub(_bodies.get(0).getPos(), center, null).length();
		for (Body bd : _bodies)
			len = Math.max(len, Vector3f.sub(bd.getPos(), center, null).length());

		for (Joint jt : _joints)
			len = Math.max(len, Vector3f.sub(jt.getPos(), center, null).length());
		
		
		len *= 5f;
		
		Vector3f incrz = Maths.rotate(_dir, _dirRight, -_rot2.x);
		incrz = Maths.rotate(incrz, _dirUp, -_rot2.y);
		incrz.normalise();
		incrz.scale(len);
		_pos = new Vector3f(_startPos);
		Vector3f.sub(_pos, incrz, _pos);
	}

	
	public void incrPos(Vector3f dr) {Vector3f.add(dr, _pos, _pos);}
	
	public Vector3f getPos() {return _pos;}
	public Quaternion getRot() {return _rot;}
	public Vector3f getUp() {return _dirUp;}
	public Vector3f getRight() {return _dirRight;}
	public Vector3f getDir() {return _dir;}
	public Vector3f getRot2() {return new Vector3f(_rot2);}
	public Vector3f getStart() {return _startPos;}
	public float getSens() {return _camSens;}
	public float getSpeed() {return _camSpeed;}
	public void setPos(Vector3f newPos) {_pos = new Vector3f(newPos);}
}
