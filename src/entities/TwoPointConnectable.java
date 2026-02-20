package entities;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.RawModel;
import toolBox.Maths;
import toolBox.Quaternion;



public abstract class TwoPointConnectable extends Entity implements Connectable
{
	protected Joint _pin1;
	protected Joint _pin2;
	protected Vector3f _baseScale;
	
	protected Vector3f _tempMiddle = new Vector3f();
	protected Vector3f _tempCos = new Vector3f();
	
	protected Quaternion _baseDirection = Quaternion.toQuaternion(0f, 0f, 0f);
	
	protected boolean _isResizable = true;
	
	protected int[] _pins;
	protected float _len;
	
	TwoPointConnectable(int[] pins, Vector3f pinPos1, Vector3f pinPos2, Vector3f baseScale, RawModel model)
	{
		super(model, new Vector3f(), new Vector3f(), baseScale);
		_pins = pins.clone();
		_pin1 = new Joint(pinPos1, false);
		_pin2 = new Joint(pinPos2, false);
		_baseScale = new Vector3f(baseScale);
	//	attachBody(new Body(pinPos1));
		//attachBody(new Body(pinPos2));
	}
	
	public void setResisable(boolean state) {_isResizable = state;}
	
	public void update()
	{
		_pin1.update();
		_pin2.update();
		Vector3f.add(_pin1.getPos(), _pin2.getPos(), _tempMiddle);
		_tempMiddle.scale(0.5f);
		setPos(_tempMiddle);
		Vector3f.sub(_pin2.getPos(), _pin1.getPos(), _tempCos);
		_len = _tempCos.length();
		_tempCos.normalise(_tempCos);
		Quaternion.toQuaternion((float)Math.asin(-_tempCos.y), (float)Math.atan2(_tempCos.x, _tempCos.z), 0f, _rot2);
		Quaternion.dot(_rot2, _baseDirection, _rot2);

		setScale(_baseScale);
		if (_isResizable)
		{
			_scale.z *= _len;
			_scale = Maths.rotate(_scale, _baseDirection);
		}
	}
	
	@Override
	public void setBaseDirection(Vector3f baseDirection)
	{
		baseDirection.set((float)Math.toRadians(baseDirection.x), (float)Math.toRadians(baseDirection.y), (float)Math.toRadians(baseDirection.z));
		Quaternion.toQuaternion(baseDirection.x, baseDirection.y, baseDirection.z, _baseDirection);
	//	_scale = Maths.rotate(_scale, _baseDirection);
	}
	
	@Override
	public void setBaseScale(Vector3f baseScale)
	{
		_baseScale.set(baseScale);
	}
	
	public Joint attachBody(Body body)
	{
		for (int pin = 0; pin < body.getPins().length; pin++)
		{
			int jpin = 0;
			while (jpin < _pins.length && body.getPins()[pin] != _pins[jpin])
				jpin++;
			if (jpin < _pins.length)
			{
				if (jpin == 0)
				{
					return _pin1.attachBody(body);
				}
				else
				{
					return _pin2.attachBody(body);
				}
			}
		}
		return null;
	}
	
	public Joint getPos1() {return _pin1;}
	public Joint getPos2() {return _pin2;}
	
	public int[] getPins() {return _pins.clone();}
}
