package entities;


import org.lwjgl.util.vector.Vector3f;

import renderEngine.Renderer;
import shaders.StaticShader;
import toolBox.ModelProvider;

public class Plunger implements Connectable
{
	private BodyPart _shtock;
	private BodyPart _frame;
	private float _baseLen = 1f;
	public Plunger(int[] pins, Vector3f pos1, Vector3f pos2)
	{
		Vector3f middle = Vector3f.add(pos1, pos2, null);
		middle.scale(0.5f);
		Vector3f vect = Vector3f.sub(pos2, pos1, null);
		_baseLen = vect.length() / 20f;
		_shtock = new BodyPart(pins, pos1, Vector3f.sub(pos1, pos2, null), new Vector3f(0.01f, 0.01f, _baseLen), ModelProvider.getModel("Shtock"));
		_frame = new BodyPart(pins, pos2, Vector3f.sub(pos2, pos1, null), new Vector3f(0.01f, 0.01f, _baseLen), ModelProvider.getModel("Frame"));
		_shtock.setColor(new Vector3f(0f, 0.5f, 0.5f));
		_frame.setColor(new Vector3f(0f, 0.4f, 0.4f));
	}
	
	public Plunger(int[] pins, Vector3f pos1, Vector3f pos2, String one, String two)
	{
		Vector3f middle = Vector3f.add(pos1, pos2, null);
		middle.scale(0.5f);
		Vector3f vect = Vector3f.sub(pos2, pos1, null);
		float len = vect.length();
		_shtock = new BodyPart(pins, pos1, Vector3f.sub(pos1, pos2, null), new Vector3f(0.01f, 0.01f, len), ModelProvider.getModel(one));
		_frame = new BodyPart(pins, pos2, Vector3f.sub(pos2, pos1, null), new Vector3f(0.01f, 0.01f, len), ModelProvider.getModel(two));
		/*_shtock.setColor(new Vector3f(0f, 0.5f, 0.5f));
		_frame.setColor(new Vector3f(0f, 0.4f, 0.4f));*/
	}
	
	public void setS(Vector3f s1, int i)
	{
		if (i == 0)
		{
			_shtock._scale.x = s1.x;
			_shtock._scale.y = s1.y;
			_shtock._scale.z *= s1.z;
			
		}
		if (i == 1)
		{
			_frame._scale.x = s1.x;
			_frame._scale.y = s1.y;
			_frame._scale.z *= s1.z;
		}
	}
	
	public void setC(Vector3f s1, int i)
	{
		if (i == 0)
		{
			_shtock.setColor(s1);
		}
		if (i == 1)
		{
			_frame.setColor(s1);
		}
	}
	
	@Override
	public int[] getPins() {return _shtock.getPins();}
	
	@Override
	public void update() 
	{
		_shtock.update();
		_frame.update();
	}

	@Override
	public Joint attachBody(Body body) 
	{
		for (int pin = 0; pin < body.getPins().length; pin++)
		{
			int jpin = 0;
			while (jpin < getPins().length && body.getPins()[pin] != getPins()[jpin])
				jpin++;
			if (jpin < getPins().length)
			{
				if (jpin == 0)
				{
					return _shtock.attachBody(body);
				}
				else
				{
					return _frame.attachBody(body);
				}
			}
		}
		return null;
	}

	@Override
	public void render(Renderer renderer, StaticShader shader) 
	{
		renderer.render(_shtock, shader);
		renderer.render(_frame, shader);
	}

	@Override
	public void setBaseScale(Vector3f baseScale) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBaseDirection(Vector3f baseDirection) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setColor(Vector3f color) {
		// TODO Auto-generated method stub
		
	}
	
}
