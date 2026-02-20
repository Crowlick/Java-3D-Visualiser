package toolBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.Loader;
import renderEngine.OBJLoader;
import renderEngine.RawModel;

public class ModelProvider 
{
	private static ArrayList<Model> _models = new ArrayList<Model>();
	private static Loader _loader;
	public static RawModel getModel(String name)
	{
		for (Model md : _models)
			if (md.name().equals(name))
			{
				if (md.model() == null)
					return findByPath(md.path());
				return md.model();
			}
		return null;
	}
	
	public static RawModel findByPath(String path)
	{
		for (Model md : _models)
			if (md.path() != null && md.path().equals(path))
			{
				if (md.model() == null) 
					md.init(_loader);
				return md.model();
			}
		return null;		
	}
	
	private static boolean has(String name)
	{
		for (Model md : _models)
			if (md.name().equals(name))
				return true;
		return false;
	}
	
	public static Vector3f getScaleOf(String name)
	{
		for (Model md : _models)
			if (md.name().equals(name))
			{
				return md.getScale();
			}
		return null;
	}
	
	public static Vector3f getDirectionOf(String name)
	{
		for (Model md : _models)
			if (md.name().equals(name))
			{
				return md.getDirection();
			}
		return null;
	}
	
	private static void processJSONArray(JSONArray jsonArray)
	{
		for (int i = 0; i < jsonArray.length(); i++)
		{
			JSONObject jsonModel = jsonArray.getJSONObject(i);
			Model model = null;
			String name = "";
			String filePath = "";
			if (!jsonModel.has("modelName"))
			{
				System.err.println("No model in config!");
				System.exit(-1);
			}
			name = jsonModel.getString("modelName");
			if (!has(name))
			{
				filePath = jsonModel.getString("filePath");
				model = new Model(name, filePath);
				_models.add(model);
			}
			
		}
	}
	
	public static void provide(String fileName, Loader loader)
	{
		_loader = loader;
		
		_models.add(new Model("Cube", Maths.cubeModel(loader, new Vector3f())));
		_models.add(new Model("Sphere", Maths.sphereModel(loader)));
		_models.add(new Model("Cylinder", "Cylinder"));
		StringBuilder jsonString = new StringBuilder();
		
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName)))
		{
			String line;
			while ((line = br.readLine()) != null)
			{
				jsonString.append(line).append('\n');
			}
		} catch (IOException e) {
			System.out.println("No found config file");
			e.printStackTrace();
			System.exit(-1);
		}
		
		try 
		{
			JSONObject jsonFile = new JSONObject(jsonString.toString());
			JSONObject jsonElements = jsonFile.getJSONObject("elements");
			
			processJSONArray(jsonElements.getJSONArray("Bodies"));
			processJSONArray(jsonElements.getJSONArray("Joints"));
			processJSONArray(jsonElements.getJSONArray("TwoPointConnectable"));
			processJSONArray(jsonElements.getJSONArray("BodyParts"));
			JSONArray twoBp = jsonElements.getJSONArray("TwoBodyParts");
			
			for (int i = 0; i < twoBp.length(); i++)
			{
				JSONObject tmp = twoBp.getJSONObject(i);
				processJSONArray(tmp.getJSONArray("subModels"));
			}
		} catch (JSONException e) { 
	            System.err.println("Error parsing JSON model configuration file: " + fileName);
	            e.printStackTrace();
	            System.exit(-1);
	    }
	}
	
	public static void clean()
	{
		_models.clear();
	}
}


class Model
{
	private String _name = null;
	private String _fileName = null;
	private RawModel _model = null;
	private Vector3f _baseScale = null;
	private Vector3f _baseDirection = null;

	public Model(String name, String fileName)
	{
		_fileName = new String(fileName);
		_name = new String(name);
	}
	
	public Model(String name, RawModel model)
	{
		_model = model;
		_name = new String(name);
	}
	
	public void init(Loader loader) {_model = OBJLoader.loadOBJModel(_fileName, loader);}
	public String name() {return _name;}
	public String path() {return _fileName;}
	public RawModel model() {return _model;}
	public void setModel(RawModel newModel) {_model = newModel;}
	public Vector3f setScale(Vector3f scale) {return _baseScale = new Vector3f(scale);}
	public Vector3f setScale(float x, float y, float z) {return _baseScale = new Vector3f(x, y, z);}
	public Vector3f setDirection(Vector3f direction) {return _baseDirection = new Vector3f(direction);}
	public Vector3f setDirection(float x, float y, float z) {return _baseDirection = new Vector3f(x, y, z);}
	public Vector3f getScale() {return _baseScale;}
	public Vector3f getDirection() {return _baseDirection;}
}