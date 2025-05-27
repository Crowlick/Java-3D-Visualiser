package entities;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.RawModel;
import renderEngine.Renderer;
import shaders.StaticShader;
import toolBox.Quaternion;

public class Entity implements Renderable
{
	private RawModel _model = null;
	protected Vector3f _pos = new Vector3f(0f, 0f, 0f);
	protected Quaternion _rot2 = Quaternion.toQuaternion(0f, 0f, 0f);
	private Vector3f _rot = new Vector3f(0f, 0f, 0f);;
	protected Vector3f _scale = new Vector3f(1f, 1f, 1f);
	protected Vector3f _color = new Vector3f(1f, 0f, 0f);
	public Entity(RawModel model, Vector3f pos, Vector3f rot, Vector3f scale)
	{
		_model = model;
		_pos = new Vector3f(pos);
		_rot = new Vector3f(rot);
		_scale = new Vector3f(scale);
		_rot2 = Quaternion.toQuaternion(rot.x, rot.y, rot.z);
	}
	
	public Entity() {}
	
	public Entity(Vector3f pos, Vector3f rot, Vector3f scale)
	{
		_pos = new Vector3f(pos);
		_rot = new Vector3f(rot);
		_scale = new Vector3f(scale);
		_rot2 = Quaternion.toQuaternion(rot.x, rot.y, rot.z);
	}
	
	public Entity(Vector3f pos, Vector3f rot, float scale)
	{
		_pos = new Vector3f(pos);
		_rot = new Vector3f(rot);
		_scale = new Vector3f(scale, scale, scale);
		_rot2 = Quaternion.toQuaternion(rot.x, rot.y, rot.z);
	}
	
	public Entity(Vector3f pos) {_pos = new Vector3f(pos);}
	
	public void setModel(RawModel model) {_model = model;}
	
	public Entity(RawModel model)
	{
		_model = model;
	}
	
	public Entity(RawModel model, Vector3f pos, Vector3f rot, float scale)
	{
		_model = model;
		_pos = new Vector3f(pos);
		_rot = new Vector3f(rot);
		_scale = new Vector3f(scale, scale, scale);
		_rot2 = Quaternion.toQuaternion(rot.x, rot.y, rot.z);
	}
	
	
	public void increasePos(Vector3f dv)
	{
		Vector3f.add(_pos, dv, _pos);
	}
	
	public void increaseRot(Vector3f dr)
	{
		Vector3f.add(_rot, dr, _rot);
	}
	
	public void increaseRot2(Quaternion dq)
	{
		Quaternion.dot(_rot2, dq, _rot2);
	}
	
	public void setPos(Vector3f newPos) {_pos = new Vector3f(newPos);}
	
	public void setRot(Vector3f newRot) {_rot = new Vector3f(newRot);}
	
	public void setRot(Quaternion newRot) {_rot2 = new Quaternion(newRot);}
	
	public void setScale(Vector3f newScale) {_scale =new Vector3f(newScale);}
	
	public void setColor(Vector3f newColor) {_color = new Vector3f(newColor);}
	
	public RawModel getModel() {return _model;}
	public Vector3f getPos() {return _pos;}
	public Vector3f getRot() {return _rot;}
	public Quaternion getRot2() {return _rot2;}
	public Vector3f getScale() {return _scale;}
	public Vector3f getColor() {return _color;}

	@Override
	public void render(Renderer renderer, StaticShader shader) 
	{
		renderer.render(this, shader);
	}
	
}
