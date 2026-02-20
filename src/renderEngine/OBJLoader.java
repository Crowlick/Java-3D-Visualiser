package renderEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;/*
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.InputStreamReader;*/

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class OBJLoader {

	private static String CONFIG_PATH = "";
	
	public static void init (String config_path) {CONFIG_PATH = new String(config_path);}
	
	public static RawModel loadOBJModel(String fileName, Loader loader)
	{
		/*InputStream in = null;
		try
		{
			in = OBJLoader.class.getResourceAsStream("/models/" + fileName + ".obj");
			if (in == null)
                 		throw new IOException("No found resource: " + fileName);
		} catch (IOException e)
		{
			System.err.println("Could not read file!");
			e.printStackTrace();
			System.exit(-1);
		}*/
		
		FileReader fr = null;
		try
		{
			fr = new FileReader(new File(CONFIG_PATH + "/models/" + fileName + ".obj"));
		} catch (FileNotFoundException e)
		{
			System.err.println("Couldn't load file!");
			e.printStackTrace();
		}
		
		String line;

		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		float[] vertArr = null;
		float[] normArr = null;
		float[] texArr = null;
		int[] indicesArray = null;
		//try(BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)))
		try(BufferedReader br = new BufferedReader(fr))
		{
			while (true)
			{
				line = br.readLine();
				String[] curLine = line.split(" ");
				if (line.startsWith("v "))
				{
					float x = Float.parseFloat(curLine[1]);
					float y = Float.parseFloat(curLine[2]);
					float z = Float.parseFloat(curLine[3]);
					vertices.add(new Vector3f(x, y, z));
				}
				else if (line.startsWith("vt "))
				{
					float x = Float.parseFloat(curLine[1]);
					float y = Float.parseFloat(curLine[2]);
					textures.add(new Vector2f(x, y));
				}
				else if (line.startsWith("vn "))
				{
					float x = Float.parseFloat(curLine[1]);
					float y = Float.parseFloat(curLine[2]);
					float z = Float.parseFloat(curLine[3]);
					normals.add(new Vector3f(x, y, z));
				}
				else if (line.startsWith("f "))
				{
					int size = vertices.size();
					texArr = new float[size * 2];
					normArr = new float[size * 3];
					break;
				}
			}
			while (line != null)
			{
				if (!line.startsWith("f "))
				{
					line = br.readLine();
					continue;
				}
				String[] curLine = line.split(" ");
				String[] vert1 = curLine[1].split("/");
				String[] vert2 = curLine[2].split("/");
				String[] vert3 = curLine[3].split("/");
				processVecrtex(vert1, indices, textures, normals, texArr, normArr);
				processVecrtex(vert2, indices, textures, normals, texArr, normArr);
				processVecrtex(vert3, indices, textures, normals, texArr, normArr);
				line = br.readLine();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		vertArr = new float[vertices.size() * 3];
		indicesArray = new int[indices.size()];
		
		int vertPos = 0;
		for (Vector3f vert : vertices)
		{
			vertArr[vertPos++] = vert.x;
			vertArr[vertPos++] = vert.y;
			vertArr[vertPos++] = vert.z;
		}
		
		vertPos = 0;

		for (int i = 0; i < indices.size(); i++)
			indicesArray[i] = indices.get(i);
		return loader.loadToVAO(vertArr, normArr, indicesArray);
	}
	
	private static void processVecrtex(String[] vertData, List<Integer> indices,
			List<Vector2f> textures, List<Vector3f> normals, float[] texArr,
			float[] normArr)
	{
		int curVertPos = Integer.parseInt(vertData[0]) - 1;
		indices.add(curVertPos);
		if (vertData[1].length() > 0)
		{
			Vector2f curTex = textures.get(Integer.parseInt(vertData[1]) - 1);
			texArr[curVertPos * 2] = curTex.x;
			texArr[curVertPos * 2 + 1] = curTex.y;
		}
		Vector3f curNorm = normals.get(Integer.parseInt(vertData[2]) - 1);
		normArr[curVertPos * 3] = curNorm.x;
		normArr[curVertPos * 3 + 1] = curNorm.y;
		normArr[curVertPos * 3 + 2] = curNorm.z;
	}
}
