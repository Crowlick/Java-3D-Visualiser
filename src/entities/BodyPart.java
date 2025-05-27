package entities;


import org.lwjgl.util.vector.Vector3f;

import renderEngine.RawModel;
import toolBox.Quaternion;

public class BodyPart extends Entity implements Connectable 
{
	protected Joint _pin;
	
	protected Vector3f _tempMiddle = new Vector3f();
	protected Vector3f _tempCos = new Vector3f();
	
	protected Quaternion _baseDirection = Quaternion.toQuaternion(0f, 0f, 0f);
	
	protected int[] _pins;
	public BodyPart(int[] pins, Vector3f pinPos, Vector3f baseDirection, Vector3f baseScale, RawModel model)
	{
		super(model, new Vector3f(), new Vector3f(), baseScale);
		_pins = pins.clone();
		_pin = new Joint(pinPos, false);
		baseDirection.normalise(baseDirection);
		Quaternion.toQuaternion((float)Math.asin(-baseDirection.y), (float)Math.atan2(baseDirection.x, baseDirection.z), 0f, _baseDirection);
	}

	public void update()
	{
		_pin.update();
		setPos(_pin.getPos());
		Quaternion.dot(_pin.getBody().getRot2(), _baseDirection, _rot2);
	}

	public int[] getPins() {return _pins.clone();}

	public Joint attachBody(Body bd) 
	{
		return _pin.attachBody(bd);
	}
	
	@Override
	public void setBaseDirection(Vector3f baseDirection)
	{
		baseDirection.set((float)Math.toRadians(baseDirection.x), (float)Math.toRadians(baseDirection.y), (float)Math.toRadians(baseDirection.z));
		Quaternion.toQuaternion(baseDirection.x, baseDirection.y, baseDirection.z, _baseDirection);
	}
	
	@Override
	public void setBaseScale(Vector3f baseScale)
	{
		_scale.set(baseScale);
	}

}
