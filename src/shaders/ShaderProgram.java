package shaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public abstract class ShaderProgram 
{
	private int _programID;
	private int _vertexShaderID;
	private int _fragmentShaderID;
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	private static FloatBuffer matrix3fBuffer = BufferUtils.createFloatBuffer(9);
	
	public ShaderProgram(String vertexFile, String fragmentFile)
	{
		_vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		_fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		_programID = GL20.glCreateProgram();
		GL20.glAttachShader(_programID, _vertexShaderID);
		GL20.glAttachShader(_programID, _fragmentShaderID);
		GL20.glLinkProgram(_programID);
		GL20.glValidateProgram(_programID);
		getAllUnifLocs();
	}
	
	protected abstract void getAllUnifLocs();
	
	protected int getUnifLoc(String uniformName)
	{
		return GL20.glGetUniformLocation(_programID, uniformName);
	}
	
	public void start()
	{
		GL20.glUseProgram(_programID);
	}
	
	public void stop()
	{
		GL20.glUseProgram(0);
	}
	
	public void cleanUP()
	{
		stop();
		GL20.glDetachShader(_programID, _vertexShaderID);
		GL20.glDetachShader(_programID, _fragmentShaderID);
		GL20.glDeleteShader(_vertexShaderID);
		GL20.glDeleteShader(_fragmentShaderID);
		GL20.glDeleteProgram(_programID);
	}
	
	protected void bindAttribute(int attr, String variable)
	{
		GL20.glBindAttribLocation(_programID, attr, variable);
	}
	
	protected void loadFloat(int loc, float val)
	{
		GL20.glUniform1f(loc, val);
	}
	
	protected void loadVector(int loc, Vector3f vect)
	{
		GL20.glUniform3f(loc, vect.x, vect.y, vect.z);
	}
	
	protected void loadBoolean(int loc, boolean val)
	{
		GL20.glUniform1f(loc, val ? 1f : 0f);
	}
	
	protected void loadMatrix(int loc, Matrix4f matr)
	{
		matr.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(loc, false, matrixBuffer);
	}
	
	protected void loadMatrix(int loc, Matrix3f matr)
	{
		matr.store(matrix3fBuffer);
		matrix3fBuffer.flip();
		GL20.glUniformMatrix3(loc, false, matrix3fBuffer);
	}
	
	private static int loadShader(String file, int type)
	{
		StringBuilder shaderSource = new StringBuilder();
		try
		{
			InputStream in = ShaderProgram.class.getResourceAsStream(file);
            		if (in == null)
                 		throw new IOException("No found resource: " + file);
            
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)))
			{
				String line;
				while ((line = reader.readLine()) != null)
				{
					shaderSource.append(line).append("\n");
				}
			}
		}
		catch (IOException e)
		{
			System.err.println("Could not read file!");
			e.printStackTrace();
			System.exit(-1);
		}
		
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
		{
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.out.println("Could not compile shader");
			System.exit(-2);
		}
		
		return shaderID;
	}

}
