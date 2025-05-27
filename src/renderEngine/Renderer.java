package renderEngine;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;

import entities.Axes;
import entities.Entity;
import shaders.StaticShader;
import toolBox.Maths;

public class Renderer {

	private static final float _FOV = 70f;
	private static final float _NEAR_PLANE = 0.1f;
	private static final float _FAR_PLANE = 100f;
	
	private Matrix4f _projMatr;
	
	public void prepare()
	{
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	public Renderer(StaticShader shader)
	{
		GL11.glClearColor(1f, 1f, 1f, 1f);
		createProjectionMatrix();
		shader.start();
		shader.loadProjectionMatrix(_projMatr);
		shader.stop();
	}
	
	public void onWindowResize(StaticShader shader) 
	{
        createProjectionMatrix();
    	shader.start();
    	shader.loadProjectionMatrix(_projMatr);
    	shader.stop();
    }
	
	public void render(Entity entity, StaticShader shader)
	{
		RawModel model = entity.getModel();
		GL30.glBindVertexArray(model.getVaoId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		Matrix4f transfMatr = Maths.createTransformationMatrix(entity.getPos(), entity.getRot2(), entity.getScale());
		Matrix3f normalMatrix = Maths.createNormalMatrix(transfMatr);
		shader.loadTransformationfMatrix(transfMatr);
		shader.loadNormalfMatrix(normalMatrix);
		shader.loadVector(entity.getColor());
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}
	
	public void render2(Entity entity, StaticShader shader)
	{
		RawModel model = entity.getModel();
		GL30.glBindVertexArray(model.getVaoId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		Matrix4f transfMatr = Maths.createTransformationMatrix(entity.getPos(), entity.getRot(), entity.getScale());
		//Matrix4f transfMatr = Maths.createTransformationMatrix(entity.getPos(), entity.getRot2(), entity.getScale());
		Matrix3f normalMatrix = Maths.createNormalMatrix(transfMatr);
		shader.loadTransformationfMatrix(transfMatr);
		shader.loadNormalfMatrix(normalMatrix);
		shader.loadVector(entity.getColor());
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	private void createProjectionMatrix()
	{
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float)(1f / Math.tan(Math.toRadians(_FOV / 2f))) * aspectRatio;
		float x_scale = y_scale / aspectRatio;
		float frustum_length = _FAR_PLANE - _NEAR_PLANE;
		float plane_sum = _FAR_PLANE + _NEAR_PLANE;
		float plane_prod = _FAR_PLANE * _NEAR_PLANE;
		
		_projMatr = new Matrix4f();
		_projMatr.m00 = x_scale;
		_projMatr.m11 = y_scale;
		_projMatr.m22 = -plane_sum / frustum_length;
		_projMatr.m23 = -1f;
		_projMatr.m32 = -2f * plane_prod / frustum_length;
	}
	
	/*public void render(Body body, StaticShader shader)
	{
		//render(body, shader);
		/*for (Entity ent : body.getEntities())
		{
			render(ent, shader);
		}
	}*/
	
	public void render(Axes axes, StaticShader shader)
	{
		for (Entity ent : axes.getEntities())
		{
			render2(ent, shader);
		}
	}
	
	public static float getFOV() {return _FOV;}
	
	/*public void render(Joint joint, StaticShader shader)
	{
		render(joint.getEntity(), shader);
	}*/
}
