package entities;


import org.lwjgl.util.vector.Vector3f;

import renderEngine.Loader;
import renderEngine.OBJLoader;
import renderEngine.RawModel;

public class Axes 
{
	private static RawModel _xModel;
	private static RawModel _yModel;
	private static RawModel _zModel;
	private Entity _xAxis;
	private Entity _yAxis;
	private Entity _zAxis;
	public Axes(Loader loader)
	{
		_xModel = OBJLoader.loadOBJModel("Axes/X", loader);
		_yModel = OBJLoader.loadOBJModel("Axes/Y", loader);
		_zModel = OBJLoader.loadOBJModel("Axes/Z", loader);
		
		float size = 0.0015f;
		
		Vector3f zero = new Vector3f(-0.8f, -0.8f, 0f);
		_xAxis = new Entity(_xModel, zero, new Vector3f(0f, 0f, 0f), size);
		_xAxis.setColor(new Vector3f(1f, 0f, 0f));
	
		_yAxis = new Entity(_yModel, zero, new Vector3f(0f, 0f, 0f), size);
		_yAxis.setColor(new Vector3f(0f, 1f, 0f));
		
		_zAxis = new Entity(_zModel, zero, new Vector3f(0f, 0f, 0f), size);
		_zAxis.setColor(new Vector3f(0f, 0f, 1f));
	}
	
	public Entity[] getEntities()
	{
		Entity[] bebra = {_xAxis, _yAxis, _zAxis};
		//Entity[] bebra = {_xAxis};
		
		return bebra;
	}
	
	
	public void rotate(Vector3f angle)
	{
		Vector3f ang = new Vector3f(angle);

		ang.x = -angle.x;
		ang.y = -angle.y;
		ang.z = -angle.z;
		_xAxis.setRot(ang);
		_zAxis.setRot(ang);
		ang.setY(0f);
		_yAxis.setRot(ang);
	}
}
