package renderEngine;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Loader {

		private List<Integer> vaos = new ArrayList<Integer>();
		private List<Integer> vbos = new ArrayList<Integer>();
		
	
		public RawModel loadToVAO(float[] positions, float[] normals, int[] indices)
		{
			int vaoID = createVAO();
			//System.out.println("Created " + vaoID);
			bindIndicesBuffer(indices);
			vaos.add(vaoID);
			storeDataInAttrList(0, positions);
			storeDataInAttrList(1, normals);
			unbindVAO();
			return new RawModel(vaoID, indices.length);
		}
		
		public void cleanUP()
		{
			for (int vao : vaos)
			{
				GL30.glDeleteVertexArrays(vao);
			}
			
			for (int vbo : vbos)
			{
				GL15.glDeleteBuffers(vbo);
			}
				
		}
		
		private int createVAO() 
		{
			int vaoID = GL30.glGenVertexArrays();
			GL30.glBindVertexArray(vaoID);
			return vaoID;
		}
		
		private void storeDataInAttrList(int attrNum, float[] data)
		{
			int vboID = GL15.glGenBuffers();
			vbos.add(vboID);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
			FloatBuffer buffer = storeDataInFloatBuffer(data);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
			GL20.glVertexAttribPointer(attrNum, 3, GL11.GL_FLOAT, false, 0,0);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		}
		
		private void unbindVAO() 
		{
			GL30.glBindVertexArray(0);
		}
		
		public void bindIndicesBuffer(int[] indices)
		{
			int vboID = GL15.glGenBuffers();
			vbos.add(vboID);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
			IntBuffer buffer = storeDataInIntBuffer(indices);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
			
		}
		
		private IntBuffer storeDataInIntBuffer(int[] data)
		{
			IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
			buffer.put(data);
			buffer.flip();
			return buffer;
		}
		
		private FloatBuffer storeDataInFloatBuffer(float[] data)
		{
			FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
			buffer.put(data);
			buffer.flip();
			return buffer;
		}
}
