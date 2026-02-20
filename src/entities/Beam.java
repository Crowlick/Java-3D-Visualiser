package entities;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.RawModel;

public class Beam extends TwoPointConnectable
{
	public Beam(int[] pins, Vector3f pinPos1, Vector3f pinPos2, RawModel model) 
	{
		super(pins, pinPos1, pinPos2, new Vector3f(0.02f, 0.02f, 1f), model);
		setColor(new Vector3f(1f, 1f, 1f));
	}
	
	public Beam(int[] pins, Vector3f pinPos1, Vector3f pinPos2, Vector3f baseScale, RawModel model) 
	{
		super(pins, pinPos1, pinPos2, baseScale, model);
		setColor(new Vector3f(1f, 1f, 1f));
	}
}
