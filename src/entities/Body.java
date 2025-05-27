package entities;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.RawModel;
import renderEngine.Renderer;
import shaders.StaticShader;
import toolBox.Maths;
import toolBox.Quaternion;

public class Body extends Entity
{
	private ArrayList<Entity> _joints = new ArrayList<Entity>();
	private ArrayList<Joint> _jointReal = new ArrayList<Joint>();
	private int[] _pins;
	
	public Body(Vector3f pos) 
	{
		super(pos);
	}
	
	public Body(RawModel model) 
	{
		super(model, new Vector3f(0,0,0), new Vector3f(0,0,0), new Vector3f(0.05f, 0.05f, 0.05f));
	}
	
	public Body(RawModel model, int[] pins, Vector3f pos) 
	{
		super(model, pos, new Vector3f(0,0,0), new Vector3f(0.05f, 0.05f, 0.05f));
		_pins = pins.clone();
		setColor(new Vector3f((float)Math.random(), (float)Math.random(), (float)Math.random()));
	}
	
	public void addJoint(Vector3f start, Vector3f end)
	{
		Vector3f middle = Vector3f.add(start, end, null);
		middle.scale(0.5f);
		Vector3f vect = Vector3f.sub(end, start, null);
		float len = vect.length();
		Vector3f cos = Maths.objectRotation(start, end);
		_joints.add(new Entity(getModel(), middle, cos, new Vector3f(0.1f, 0.1f, len)));
	}
	
	public void addJoint(Joint end)
	{
		if (end.getBody() == null)
			end.attachBody(this);
		Vector3f vect = Vector3f.sub(end.getPos(), getPos(), null);
		float len = vect.length();
		if (len > 1e-3f)
		{
			Vector3f middle = Vector3f.add(getPos(), end.getPos(), null);
			middle.scale(0.5f);
			
			Vector3f cos = Maths.objectRotation(getPos(), end.getPos());
			
			_joints.add(new Entity(getModel(), middle, cos, new Vector3f(0.01f, 0.01f, len)));
			
			_joints.get(_joints.size() - 1).setColor(_color);
			
			_jointReal.add(end);
		}
	}
	
	public void update()
	{
		Vector3f middle = new Vector3f();
		Vector3f cos = new Vector3f();
		float len = 1f;
		for (int i = 0; i < _jointReal.size(); i++)
		{
			Vector3f.add(_pos, _jointReal.get(i).getPos(), middle);
			middle.scale(0.5f);
			_joints.get(i).setPos(middle);
			
			len = Vector3f.sub(_jointReal.get(i).getPos(), _pos, null).length();
			_joints.get(i).setScale(new Vector3f(0.01f, 0.01f, len));
			
			cos = Maths.objectRotation(_pos, _jointReal.get(i).getPos());
			_joints.get(i).setRot(Quaternion.toQuaternion(cos.x, cos.y, cos.z));
		}
	}
	
	public int[] getPins() {return _pins;}
	public ArrayList<Entity> getEntities() {return _joints;}

	
	public ArrayList<Joint> getJoints() {return _jointReal;}
	
	@Override
	public void render(Renderer renderer, StaticShader shader)
	{
		update();
		renderer.render(this, shader);
		for (Entity jt : _joints)
			renderer.render(jt, shader);
	}
}
/*public class Body extends Entity
{
	//private ArrayList<Entity> _joints = new ArrayList<Entity>();
	private ArrayList<BodyPart> _joints = new ArrayList<BodyPart>();
	private ArrayList<Joint> _jointReal = new ArrayList<Joint>();
	private int[] _pins;
	private Vector3f _color = new Vector3f((float)Math.random(), (float)Math.random(), (float)Math.random());
	
	public Body(Vector3f pos) 
	{
		super(pos);
	}
	
	public Body(RawModel model) 
	{
		super(model, new Vector3f(0,0,0), new Vector3f(0,0,0), new Vector3f(0.05f, 0.05f, 0.05f));
	}
	
	public Body(RawModel model, int[] pins, Vector3f pos) 
	{
		super(model, pos, new Vector3f(0,0,0), new Vector3f(0.05f, 0.05f, 0.05f));
		_pins = pins.clone();
		setColor(_color);
	}

	public void addJoint(Joint end)
	{
		if (end.getBody() == null)
			end.attachBody(this);
		Vector3f middle = Vector3f.add(getPos(), end.getPos(), null);
		middle.scale(0.5f);
		Vector3f vect = Vector3f.sub(end.getPos(), getPos(), null);
		float len = vect.length();
		if (len > 1e-3f)
		{
			_joints.add(new BodyPart(_pins, _pos, Vector3f.sub(end.getPos(), _pos, null), new Vector3f(0.01f, 0.01f, len), ModelProvider.getModel("Cube2")));
			_joints.get(_joints.size() - 1).setColor(_color);
			_joints.get(_joints.size() - 1).attachBody(this);
			_jointReal.add(end);
		}
	}
	
	public void update()
	{
		for (int i = 0; i < _jointReal.size(); i++)
		{
			_joints.get(i).update();
		}
	}
	
	public int[] getPins() {return _pins;}
	public ArrayList<BodyPart> getEntities() {return _joints;}

	
	public ArrayList<Joint> getJoints() {return _jointReal;}
}*/