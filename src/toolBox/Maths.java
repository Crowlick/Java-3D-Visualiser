package toolBox;

import java.util.ArrayList;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import renderEngine.Loader;
import renderEngine.RawModel;

public class Maths 
{
	public static Matrix4f createTransformationMatrix(Vector3f transl, Vector3f rotat, Vector3f scale)
	{
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		
		Matrix4f.translate(transl, matrix, matrix);
		
		/*Matrix4f.rotate((float)Math.toRadians(rotat.x), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate((float)Math.toRadians(rotat.y), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate((float)Math.toRadians(rotat.z), new Vector3f(0, 0, 1), matrix, matrix);*/
		
		Matrix4f.rotate(rotat.x, new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate(rotat.y, new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate(rotat.z, new Vector3f(0, 0, 1), matrix, matrix);
		
		/*Matrix4f.rotate(rotat.x, new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate(rotat.y, new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate(rotat.z, new Vector3f(0, 0, 1), matrix, matrix);*/
				
		
		Matrix4f.scale(scale, matrix, matrix);
		
		return matrix;
	}
	
	public static void eulerAnglesFormCos(Vector3f cos)
	{
		cos.x = (float)Math.asin(-cos.y);
		cos.y = (float)Math.atan2(cos.x, cos.z);
		cos.z = 0f;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f transl, Quaternion rotat, float scale)
	{
		Matrix4f matrix = new Matrix4f();
		
		matrix.setIdentity();
		
		Matrix4f.translate(transl, matrix, matrix);
		
		Matrix4f.mul(matrix, rotat.toMatrix4f(), matrix);
		
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
		
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f transl, Quaternion rotat, Vector3f scale)
	{
		Matrix4f matrix = new Matrix4f();
		
		matrix.setIdentity();
		
		Matrix4f.translate(transl, matrix, matrix);
		
		Matrix4f.mul(matrix, rotat.toMatrix4f(), matrix);
		
		Matrix4f.scale(scale, matrix, matrix);
		
		return matrix;
	}
	
	public static Matrix3f toMatrix3f(Matrix4f mat)
	{
		Matrix3f result = new Matrix3f();
		result.m00 = mat.m00;
		result.m01 = mat.m01;
		result.m02 = mat.m02;
		
		result.m10 = mat.m10;
		result.m11 = mat.m11;
		result.m12 = mat.m12;
		
		result.m20 = mat.m20;
		result.m21 = mat.m21;
		result.m22 = mat.m22;
		
		return result;
	}
	
	public static Matrix3f createNormalMatrix(Matrix4f transformationMatrix)
	{
		Matrix3f matrix = new Matrix3f();
		
		Matrix3f.transpose(toMatrix3f(Matrix4f.invert(transformationMatrix, null)), matrix);
		
		return matrix;
	}
	
	public static Matrix4f createViewMatrix(Camera camera)
	{
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		
		/*Vector3f incry = Maths.rotate(camera.getUp(), camera.getRight(), camera.getRot2().x);
		
		Vector3f incrx = Maths.rotate(camera.getRight(), camera.getUp(), camera.getRot2().y);
		System.out.println(incry + " " + incrx);
		
		incry.normalise();
		incrx.normalise();*/
		
		Matrix4f.rotate(camera.getRot2().x, camera.getRight(), viewMatrix, viewMatrix);
		Matrix4f.rotate(camera.getRot2().y, camera.getUp(), viewMatrix, viewMatrix);
		//Matrix4f.rotate(camera.getRot2().z, new Vector3f(0f,0f,1f), viewMatrix, viewMatrix);
		//	Matrix4f.mul(viewMatrix, camera.getRot().toMatrix4f(), viewMatrix);
		Vector3f camPos = camera.getPos();
		Vector3f negativeCameraPos = new Vector3f(-camPos.x, -camPos.y, -camPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}
	
	public static Matrix4f createViewMatrix2(Camera camera)
	{
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();

		Matrix4f.rotate(camera.getRot2().x, camera.getRight(), viewMatrix, viewMatrix);
		Matrix4f.rotate(camera.getRot2().y, camera.getUp(), viewMatrix, viewMatrix);
		return viewMatrix;
	}

	public static Vector3f rotate(Vector3f point, Vector3f axis, float angle)
	{
		angle /= 2;
		
		float cos = (float)Math.cos(angle);
		float sin = (float)Math.sin(angle);

		Quaternion qPoint = new Quaternion(point);
		
		axis.normalise(axis);
		
		Quaternion q = new Quaternion(cos, sin * axis.x, sin * axis.y, sin * axis.z);

		return q.dot(qPoint).dot(q.negate()).toVector3f();
	}
	
	public static Vector3f rotate(Vector3f point, Quaternion q)
	{
		Quaternion qPoint = new Quaternion(point);
		return q.dot(qPoint).dot(q.negate()).toVector3f();
	}
	
	public static Vector3f rotateAroundPoint(Vector3f point, Vector3f around, Vector3f axis, float angle)
	{
		Vector3f newPoint = new Vector3f(point);
		Vector3f.sub(newPoint, around, newPoint);
		
		newPoint = rotate(newPoint, axis, angle);
		
		Vector3f.add(around, newPoint, newPoint);
		
		return newPoint;
	}
	
	public static Vector3f matrixMulVector(Matrix3f mat, Vector3f vect)
	{
		float x = mat.m00 * vect.x + mat.m10 * vect.y + mat.m20 * vect.z;
		float y = mat.m01 * vect.x + mat.m11 * vect.y + mat.m21 * vect.z;
		float z = mat.m02 * vect.x + mat.m12 * vect.y + mat.m22 * vect.z;
		
		return new Vector3f(x, y, z);
	}
	
	public static float[] makePoints(ArrayList<Integer> indices)
	{
		int rows = 50;
		int cols = 50;
		int size = (rows * cols + 2) * 3;
		float[] vertices = new float[size];
		
		double[] u = new double[rows];
		double[] v = new double[cols];
		
	    for (int i = 0; i < rows; i++)
	    	u[i] = 2 * Math.PI / rows * i;
	    for (int i = 0; i < cols; i++)
	    	v[i] = -Math.PI / 2. + Math.PI / (cols + 1) * (i + 1);
	    
	    vertices[0] = 0f;
	    vertices[1] = 0f;
	    vertices[2] = -1f;
	    
	    vertices[size-3] = 0f;
	    vertices[size-2] = 0f;
	    vertices[size-1] = 1f;

	    for (int i = 0; i < v.length; i++)
	    {
	    	for (int j = 0; j < u.length; j++)
	    	{
	    		float x = (float)(Math.cos(v[i]) * Math.cos(u[j]));
	    		float y = (float)(Math.cos(v[i]) * Math.sin(u[j]));
	    		float z = (float)Math.sin(v[i]);
	    		
	    		vertices[i * 3 * rows + j * 3 + 3] = x;
	    		vertices[i * 3 * rows + j * 3 + 4] = y;
	    		vertices[i * 3 * rows + j * 3 + 5] = z;
	    	}
	    }
	   

	    for (int i = 0; i < rows; i++)
	    {
	    	indices.add(i + 1);
	    	indices.add(0);
	    	indices.add((i + 1) % rows + 1);
	    }
	    
	    for (int i = 0; i < cols - 1; i++)
	    {
	    	for (int j = 0; j < rows; j++)
	    	{
	    		int first = i * rows + j + 1;
	    		int second = first % rows + 1 + i * rows;
	    		
	    		int first2 = (i+1) * rows + j + 1;
	    		int second2 = first % rows + 1 + (i+1) * rows;
	    		int[] square = {first, second, first2, second2};
	    		
	    		for (int k = 0; k < 3; k++)
	    			indices.add(square[k]);
	    		
	    		for (int k = 3; k >= 1; k--)
	    			indices.add(square[k]);
	    	}
	    }
	    
	    int last = size / 3 - 1;
	    for (int i = 0; i < rows; i++)
	    {
	    	indices.add(last);
	    	indices.add(last - rows + i);
	    	indices.add(last - rows + (i + 1) % rows);
	    }
	    return vertices;
	}
	
	public static RawModel sphereModel(Loader loader)
	{
		ArrayList<Integer> indices = new ArrayList<Integer>();
		int rows = 10;
		int cols = 10;
		int size = (rows * cols + 2) * 3;
		float[] vertices = new float[size];
		float[] normals = new float[size];
		
		double[] u = new double[rows];
		double[] v = new double[cols];
		
	    for (int i = 0; i < rows; i++)
	    	u[i] = 2 * Math.PI / rows * i;
	    for (int i = 0; i < cols; i++)
	    	v[i] = -Math.PI / 2. + Math.PI / (cols + 1) * (i + 1);
	    
	    vertices[0] = 0f;
	    vertices[1] = 0f;
	    vertices[2] = -1f;
	    
	    vertices[size-3] = 0f;
	    vertices[size-2] = 0f;
	    vertices[size-1] = 1f;
	    
	    normals[0] = 0f;
	    normals[1] = 0f;
	    normals[2] = -1f;
	    
	    normals[size-3] = 0f;
	    normals[size-2] = 0f;
	    normals[size-1] = 1f;

	    for (int i = 0; i < v.length; i++)
	    {
	    	for (int j = 0; j < u.length; j++)
	    	{
	    		float x = (float)(Math.cos(v[i]) * Math.cos(u[j]));
	    		float y = (float)(Math.cos(v[i]) * Math.sin(u[j]));
	    		float z = (float)Math.sin(v[i]);
	    		
	    		vertices[i * 3 * rows + j * 3 + 3] = x;
	    		vertices[i * 3 * rows + j * 3 + 4] = y;
	    		vertices[i * 3 * rows + j * 3 + 5] = z;
	    		
	    		normals[i * 3 * rows + j * 3 + 3] = x;
	    		normals[i * 3 * rows + j * 3 + 4] = y;
	    		normals[i * 3 * rows + j * 3 + 5] = z;
	    	}
	    }
	   

	    for (int i = 0; i < rows; i++)
	    {
	    	indices.add(i + 1);
	    	indices.add(0);
	    	indices.add((i + 1) % rows + 1);
	    }
	    
	    for (int i = 0; i < cols - 1; i++)
	    {
	    	for (int j = 0; j < rows; j++)
	    	{
	    		int first = i * rows + j + 1;
	    		int second = first % rows + 1 + i * rows;
	    		
	    		int first2 = (i+1) * rows + j + 1;
	    		int second2 = first % rows + 1 + (i+1) * rows;
	    		int[] square = {first, second, first2, second2};
	    		
	    		for (int k = 0; k < 3; k++)
	    			indices.add(square[k]);
	    		
	    		for (int k = 3; k >= 1; k--)
	    			indices.add(square[k]);
	    	}
	    }
	    
	    int last = size / 3 - 1;
	    for (int i = 0; i < rows; i++)
	    {
	    	indices.add(last);
	    	indices.add(last - rows + i);
	    	indices.add(last - rows + (i + 1) % rows);
	    }
	    
	    int[] ind = indices.stream().mapToInt(i -> i).toArray(); 
	    
	    return loader.loadToVAO(vertices, vertices, ind);
	}
	
	public static float[] makeSquareVertices(Vector3f vect)
	{
		float x = vect.x;
		float y = vect.y;
		float z = vect.z;
		float[] vertices = {
				-0.5f + x, 0.5f + y, 0.5f + z,
				-0.5f + x, -0.5f + y, 0.5f + z,
				0.5f + x, -0.5f + y, 0.5f + z,
				0.5f + x, 0.5f + y, 0.5f + z,
				
				-0.5f + x, 0.5f + y, -0.5f + z,
				-0.5f + x, -0.5f + y, -0.5f + z,
				0.5f + x, -0.5f + y, -0.5f + z,
				0.5f + x, 0.5f + y, -0.5f + z
		};
		return vertices;
	}
	
	public static RawModel cubeModel(Loader loader, Vector3f vect)
	{
		float x = vect.x;
		float y = vect.y;
		float z = vect.z;
		
		float[] vertices = {
				//front
				-0.5f + x, -0.5f + y, 0.5f + z,
				-0.5f + x, 0.5f + y, 0.5f + z,
				0.5f + x, -0.5f + y, 0.5f + z,
				0.5f + x, 0.5f + y, 0.5f + z,
			    //back
				-0.5f + x, -0.5f + y, -0.5f + z,
				-0.5f + x, 0.5f + y, -0.5f + z,
				0.5f + x, -0.5f + y, -0.5f + z,
				0.5f + x, 0.5f + y, -0.5f + z,
				//left
				-0.5f + x, 0.5f + y, -0.5f + z,
				-0.5f + x, 0.5f + y, 0.5f + z,
				0.5f + x, 0.5f + y, -0.5f + z,
				0.5f + x, 0.5f + y, 0.5f + z,
				//right
				-0.5f + x, -0.5f + y, -0.5f + z,
				-0.5f + x, -0.5f + y, 0.5f + z,
				0.5f + x, -0.5f + y, -0.5f + z,
				0.5f + x, -0.5f + y, 0.5f + z,
				//up
				0.5f + x, -0.5f + y, -0.5f + z,
				0.5f + x, -0.5f + y, 0.5f + z,
				0.5f + x, 0.5f + y, -0.5f + z,
				0.5f + x, 0.5f + y, 0.5f + z,
				//bottom
				-0.5f + x, -0.5f + y, -0.5f + z,
				-0.5f + x, -0.5f + y, 0.5f + z,
				-0.5f + x, 0.5f + y, -0.5f + z,
				-0.5f + x, 0.5f + y, 0.5f + z
		};
		
		float[] normals = {
				//front
				0f, 0f, 1f,
				0f, 0f, 1f,
				0f, 0f, 1f,
				0f, 0f, 1f,
			    //back
				0f, 0f, -1f,
				0f, 0f, -1f,
				0f, 0f, -1f,
				0f, 0f, -1f,
				//left
				0f, 1f, 0f,
				0f, 1f, 0f,
				0f, 1f, 0f,
				0f, 1f, 0f,
				//right
				0f, -1f, 0f,
				0f, -1f, 0f,
				0f, -1f, 0f,
				0f, -1f, 0f,
				//up
				1f, 0f, 0f,
				1f, 0f, 0f,
				1f, 0f, 0f,
				1f, 0f, 0f,
				//bottom
				-1f, 0f, 0f,
				-1f, 0f, 0f,
				-1f, 0f, 0f,
				-1f, 0f, 0f			
		};
		
		int[] indices = {
				//front
				0, 2, 1,
				2, 3, 1,
				//back
				0 + 4, 2 + 4, 1 + 4,
				2 + 4, 3 + 4, 1 + 4,
				//left
				0 + 4 * 2, 2 + 4 * 2, 1 + 4 * 2,
				2 + 4 * 2, 3 + 4 * 2, 1 + 4 * 2,
				//right
				0 + 4 * 3, 2 + 4 * 3, 1 + 4 * 3,
				2 + 4 * 3, 3 + 4 * 3, 1 + 4 * 3,
				//up
				0 + 4 * 4, 2 + 4 * 4, 1 + 4 * 4,
				2 + 4 * 4, 3 + 4 * 4, 1 + 4 * 4,
				//bottom
				0 + 4 * 5, 2 + 4 * 5, 1 + 4 * 5,
				2 + 4 * 5, 3 + 4 * 5, 1 + 4 * 5
			};
		return loader.loadToVAO(vertices, normals, indices);
	}
	
	
	public static Vector3f objectRotation(Vector3f pos1, Vector3f pos2)
	{
		Vector3f vect = Vector3f.sub(pos2, pos1, null);
		Vector3f cos = vect.normalise(null);
		cos = new Vector3f((float)Math.asin(-cos.y), (float)Math.atan2(cos.x, cos.z), 0);
		return cos;
	}
}
