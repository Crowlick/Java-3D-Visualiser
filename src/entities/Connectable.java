package entities;

import org.lwjgl.util.vector.Vector3f;

public interface Connectable extends Renderable
{
	public int[] getPins();
	public Joint attachBody(Body bd);
	public void update();
	public void setBaseScale(Vector3f baseScale);
	public void setBaseDirection(Vector3f baseDirection);
	public void setColor(Vector3f color);
}
