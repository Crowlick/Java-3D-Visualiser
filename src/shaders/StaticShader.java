package shaders;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import toolBox.Maths;

public class StaticShader extends ShaderProgram
{
	
	private static final String VERTEX_FILE = "/shaders/vertexShader.txt";
	private static final String FRAGMENT_FILE = "/shaders/fragmentShader.txt";
	
	private int _transfMatrLoc;
	private int _projMatrLoc;
	private int _viewMatrLoc;
	private int _normalMatrix;
	private int _color;
	
	public StaticShader(String vertexFile, String FargmentFile)
	{
		super(vertexFile, FargmentFile);
	}
	
	public StaticShader()
	{
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	
	protected void bindAttribute()
	{
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "normalsinio");
	}
	
	@Override
	protected void getAllUnifLocs()
	{
		_transfMatrLoc = super.getUnifLoc("transformMatrix");
		_normalMatrix = super.getUnifLoc("normalMatrix");
		_projMatrLoc = super.getUnifLoc("projectionMatrix");
		_viewMatrLoc = super.getUnifLoc("viewMatrix");
		_color = super.getUnifLoc("col");
	}
	
	public void loadTransformationfMatrix(Matrix4f matrix)
	{
		super.loadMatrix(_transfMatrLoc, matrix);
	}
	
	public void loadNormalfMatrix(Matrix3f matrix)
	{
		super.loadMatrix(_normalMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix)
	{
		super.loadMatrix(_projMatrLoc, matrix);
	}
	
	public void loadViewMatrix(Camera camera)
	{
		super.loadMatrix(_viewMatrLoc, Maths.createViewMatrix(camera));
	}
	
	public void loadViewMatrix2(Camera camera)
	{
		super.loadMatrix(_viewMatrLoc, Maths.createViewMatrix2(camera));
	}
	
	public void loadVector(Vector3f vect)
	{
		super.loadVector(_color, vect);
	}
}
