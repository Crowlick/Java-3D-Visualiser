package entities;

import java.util.Arrays;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.RawModel;
import toolBox.Maths;
import toolBox.ModelProvider;
import toolBox.Quaternion;

public class Joint extends Entity implements Connectable
{
	private int[] _pins;
	private boolean _isFixed;
	private Body _connectedBody = null;
	private Vector3f _locPos = null;
	private RawModel _baseModel = ModelProvider.getModel("Sphere");
	public Joint(int[] pins, Vector3f pos, boolean isFix, RawModel model)
	{
		super(model, pos, new Vector3f(0f, 0f, 0f), 0.02f);
		Vector3f color = isFix ? new Vector3f(0.5f, 0f, 0f) : new Vector3f(1f, 1f, 1f);
		setColor(color);
		_pins = pins.clone();
		Arrays.sort(_pins);
		_isFixed = isFix;
	}
	
	public Joint(int[] pins, Vector3f pos, boolean isFix)
	{
		super(pos, new Vector3f(0f, 0f, 0f), 0.02f);
		setModel(_baseModel);
		Vector3f color = isFix ? new Vector3f(0.5f, 0f, 0f) : new Vector3f(1f, 1f, 1f);
		setColor(color);
		_pins = pins.clone();
		Arrays.sort(_pins);
		_isFixed = isFix;
	}
	
	public Joint(Vector3f pos, boolean isFix)
	{
		super(pos, new Vector3f(0f, 0f, 0f), 0.02f);
		setModel(_baseModel);
		Vector3f color = isFix ? new Vector3f(0.5f, 0f, 0f) : new Vector3f(1f, 1f, 1f);
		setColor(color);
		_pins = null;
		_isFixed = isFix;
	}

	public boolean isFixed() {return _isFixed;}
	public Body getBody() {return _connectedBody;}
	
	@Override
	public int[] getPins() {return _pins;}
	
	@Override
	public Joint attachBody(Body newBody) 
	{
		_connectedBody = newBody;
		_locPos = Vector3f.sub(getPos(), _connectedBody.getPos(), null);
		return this;
	} 
	
	@Override
	public void update()
	{
		if (_isFixed || _connectedBody == null)
			return;
		Matrix3f mul = _connectedBody.getRot2().toMatrix3f();
		Vector3f newLocPos = Maths.matrixMulVector(mul, _locPos);
		Vector3f.add(newLocPos, _connectedBody.getPos(), newLocPos);
		setPos(newLocPos);
	}
	
	@Override
	public void setBaseDirection(Vector3f baseDirection)
	{
		baseDirection.set((float)Math.toRadians(baseDirection.x), (float)Math.toRadians(baseDirection.y), (float)Math.toRadians(baseDirection.z));
		Quaternion.toQuaternion(baseDirection.x, baseDirection.y, baseDirection.z, _rot2);
	}
	
	@Override
	public void setBaseScale(Vector3f baseScale)
	{
		_scale.set(baseScale);
	}
}
