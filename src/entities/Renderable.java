package entities;

import renderEngine.Renderer;
import shaders.StaticShader;

public interface Renderable 
{
	public void render(Renderer renderer, StaticShader shader);
}
