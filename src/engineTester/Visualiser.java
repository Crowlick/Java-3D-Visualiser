package engineTester;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Axes;
import entities.Beam;
import entities.Body;
import entities.BodyPart;
import entities.Camera;
import entities.Connectable;
import entities.Joint;
import entities.Plunger;
import entities.Renderable;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.RawModel;
import renderEngine.Renderer;
import shaders.StaticShader;
import toolBox.ModelProvider;
import toolBox.Quaternion;

public class Visualiser implements Runnable {

	public String config;
	public String data;
	public AniManager an;
	public Camera _camera = null;
	private Animator animator = null;
	public float dt;
	private float curT = 0f;
	private static String CONFIG_PATH = "./config.json";
	private boolean _isPaused = false;
	private boolean _isCameraMoving = false;
	private int _cameraMoveDirection = 0;
	
	public static void setConfig(String config_path) {CONFIG_PATH = new String(config_path + "config.json");}
	
	public void run()
	{
		DisplayManager.createDisplay();
		dt /= DisplayManager.getFPS();
		
		Loader loader = new Loader();
		
		ModelProvider.provide(CONFIG_PATH, loader);
		
		StaticShader shader = new StaticShader();
		StaticShader shader2 = new StaticShader("/shaders/vertexShader3.txt", "/shaders/fragmentShader.txt");
		Renderer renderer = new Renderer(shader, CONFIG_PATH);
	    
		Axes axes = new Axes(loader);
		ArrayList<Body> bodies = new ArrayList<Body>();
		ArrayList<Joint> joints = new ArrayList<Joint>();

		ArrayList<Connectable> bebras = new ArrayList<Connectable>();
		
		readSystem(bodies, joints, bebras, config);
		

		animator = new Animator(data, bodies.size());

		
		an.setAnimationSliderMax(animator.getMax() - 1);
		
		Vector3f center = new Vector3f(0f, 0f, 0f);
		
		for (Body bd : bodies)
		{
			Vector3f.add(bd.getPos(), center, center);
		}

		for (Joint jt : joints)
		{
			Vector3f.add(jt.getPos(), center, center);
		}
		
		center.scale(1.f / (bodies.size() + joints.size()));	
		
		_camera = new Camera(center, bodies, joints, CONFIG_PATH);
		
		for (Body bd : bodies)
			for (Connectable br : bebras)
				attach(br, bd, joints);
		for (Body bd : bodies)
		{
			for (Joint jt : joints)
			{
				jt.refreshScale();
				if (jt.getPins() != null)
					for (int pin = 0; pin < bd.getPins().length; pin++)
					{
						if (Arrays.binarySearch(jt.getPins(),  bd.getPins()[pin]) > 0)
						{
							bd.addJoint(jt);
							pin = bd.getPins().length;
						}
					}
				else if(jt.getBody() == bd)
					bd.addJoint(jt);
				bebras.add(jt);
			}
		}
		_camera.reset();
		
		boolean showAxes = true;
			
		curT = 0f;
		animator.findClosestLeast(curT);
		
		for (Connectable b : bebras)
			b.update();
		while (!Display.isCloseRequested())
		{
			_camera.move();
			_camera.rotate();
			renderer.prepare();
			
			shader.start();
			shader.loadViewMatrix(_camera);

			for (Renderable ren : bebras)
				ren.render(renderer, shader);
			for (Body bd : bodies)
				bd.render(renderer, shader);
			shader.stop();
			
			if (showAxes)
			{
				axes.rotate(_camera.getRot2());
				shader2.start();
				renderer.render(axes, shader2);
				shader2.stop();
			}
			
			DisplayManager.updateDisplay();
			if (Display.wasResized())
				renderer.onWindowResize(shader);
			for (int i = 0; i < bodies.size(); i++)
			{
				bodies.get(i).setPos(animator.getNextFrame().get(i).getPos());
				bodies.get(i).setRot(animator.getNextFrame().get(i).getRot());
			}
			
			if (!_isPaused)
			{
				curT += dt;
				animator.findClosestLeast(curT);
				an.updSlider(animator.getCurInder());
				dt = speed(dt);
				if ((curT + dt) < 0)
					dt = 0f;
			}
			
			for (Connectable b : bebras)
				b.update();
			
			_isPaused = setPause(_isPaused);

			showAxes = setAxes(showAxes);
			
			if (Keyboard.isKeyDown(Keyboard.KEY_HOME))
				curT = 0.f;
			if (_isCameraMoving)
				moveCamera();
		}
		ModelProvider.clean();
		shader.cleanUP();
		shader2.cleanUP();
		loader.cleanUP();
		an.close();
		DisplayManager.closeDisplay();
	}
	
	public static void attach(Connectable con, Body to, ArrayList<Joint> joints)
	{
		for (int pin = 0; pin < to.getPins().length; pin++)
		{
			int i = 0;
			while (i < con.getPins().length && con.getPins()[i] != to.getPins()[pin])
				i++;
			if (i < con.getPins().length)
			{
				joints.add(con.attachBody(to));
			}
			pin = to.getPins().length;
		}
	}
	
	public static boolean setPause(boolean p)
	{
		if (Keyboard.isKeyDown(Keyboard.KEY_P))
			return true;
		if (Keyboard.isKeyDown(Keyboard.KEY_O))
			return false;
		return p;
	}
	
	public void restart() {animator.setFrame(0); curT = 0f; an.updSlider(0);}
	
	public boolean isPaused() {return _isPaused;}
	
	public void setPauseTrue() {_isPaused = true;}
	
	public void setPauseFalse() {_isPaused = false;}
	
	public void multDt(float m) {dt *= m;}
	
	public float getDt() {return dt;}
	
	public void moveCamera()
	{
		switch (_cameraMoveDirection)
		{
			case 1:
				_camera.moveUp();
				break;
			case 2:
				_camera.moveLeft();
				break;
			case 3:
				_camera.moveRight();
				break;
			case 4:
				_camera.moveDown();
				break;
			case 5:
				_camera.moveForward();
				break;
			case 6:
				_camera.moveBackward();
				break;
			default:
				break;
		}
	}
	
	public void setCameraMoveTrue(int direction) {_isCameraMoving = true; _cameraMoveDirection = direction;}
	
	public void setCameraMoveFalse() {_isCameraMoving = false;}
	
	public void resetCamera() {_camera.reset();}
	
	public static boolean setAxes(boolean p)
	{
		if (Keyboard.isKeyDown(Keyboard.KEY_J))
			return true;
		if (Keyboard.isKeyDown(Keyboard.KEY_K))
			return false;
		return p;
	}
	
	public static float speed(float cur)
	{
		float add = 0.1f / DisplayManager.getFPS();
		if (Keyboard.isKeyDown(Keyboard.KEY_SUBTRACT) && cur > -add * 50f)
			return cur -= add;
		if (Keyboard.isKeyDown(Keyboard.KEY_ADD) && cur < add * 50f)
			return cur += add;
		return cur;
	}
	
	public static String[] processLine(String line)
	{
		return line.replaceAll("[,;]", "").split(" ");
	}
	
	public static int findDataStart(String[] vals)
	{
		int i = 0;
		while (!vals[i++].equals("="));
		return i;
	}
	
	public static int[] findPins(String[] vals)
	{
		int end = 1;
		
		int i = findDataStart(vals);
			
		int[] pins = new int[i - end - 1];
		
		for (int j = 0; j < i - end - 1; j++)
			pins[j] = Integer.parseInt(vals[j+end]);
		return pins;
	}
	
	
	public void setFrame(int frame)
	{
		animator.setFrame(frame);
		curT = animator.getCurT();
	}
	
	public static void processConnectable(ArrayList<Connectable> con, int type, String line, JSONObject jsonModel)
	{
		String[] vals = processLine(line);
		int start = findDataStart(vals) + jsonModel.getInt("coordinateStart");
		Vector3f pos1 = new Vector3f(Float.parseFloat(vals[start++]), Float.parseFloat(vals[start++]), Float.parseFloat(vals[start++]));
		Vector3f pos2 = null;
		if (type == 0)
			pos2 = new Vector3f(Float.parseFloat(vals[start++]), Float.parseFloat(vals[start++]), Float.parseFloat(vals[start++]));
		RawModel model = ModelProvider.getModel(jsonModel.getString("modelName"));
		Connectable beam = null;
		if (type == 0)
			beam = new Beam(findPins(vals), pos1, pos2, model);
		else
			beam = new BodyPart(findPins(vals), pos1, new Vector3f(0f, 0f, 1f), new Vector3f(1f, 1f, 1f), model);
		Vector3f parameter = null;
		if (jsonModel.has("baseScale"))
		{
			JSONArray jsonScale = jsonModel.getJSONArray("baseScale");
			parameter = new Vector3f(jsonScale.getFloat(0), jsonScale.getFloat(1), jsonScale.getFloat(2));
			beam.setBaseScale(parameter);
		}
		if (jsonModel.has("baseDirection"))
		{
			JSONArray jsonScale = jsonModel.getJSONArray("baseDirection");
			parameter = new Vector3f(jsonScale.getFloat(0), jsonScale.getFloat(1), jsonScale.getFloat(2));
			beam.setBaseDirection(parameter);
		}
		if (jsonModel.has("color"))
		{
			JSONArray jsonColor = jsonModel.getJSONArray("color");
			parameter = new Vector3f(jsonColor.getFloat(0), jsonColor.getFloat(1), jsonColor.getFloat(2));
			beam.setColor(parameter);
		}
		con.add(beam);
	}
	
	public static void readSystem(ArrayList<Body> bodies, ArrayList<Joint> joints,
			ArrayList<Connectable> con, String pth)
	{

		StringBuilder jsonString = new StringBuilder();
		
		try (BufferedReader br = Files.newBufferedReader(Paths.get(CONFIG_PATH)))
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
		
		
		//try (BufferedReader br = Files.newBufferedReader(Paths.get(pth)))
		try (BufferedReader br = new BufferedReader(new StringReader(pth)))
		{
			String line;
			
			while ((line = br.readLine()) != null)
			{
				int i = 0;
				while (i < line.length() && line.charAt(i++) != ':');
				line = line.substring(i);
				if (line.length() > 1)
					line = line.substring(1);
					
				try
				{
					JSONObject jsonFile = new JSONObject(jsonString.toString());
					JSONObject jsonElements = jsonFile.getJSONObject("elements");
					JSONArray jsonModels = jsonElements.getJSONArray("Bodies");
					String[] vals = null;
					Vector3f pos1 = new Vector3f(0f, 0f, 0f);
					Vector3f pos2 = new Vector3f(0f, 0f, 0f);
					for (i = 0; i < jsonModels.length(); i++)
					{
						JSONObject jsonElement = jsonModels.getJSONObject(i);
						if (!line.startsWith(jsonElement.getString("nameInConfig")))
							continue;
						vals = processLine(line);
						int start = findDataStart(vals) + jsonElement.getInt("coordinateStart");
						pos1.set(Float.parseFloat(vals[start++]), Float.parseFloat(vals[start++]), Float.parseFloat(vals[start++]));
						Body newBody = new Body(ModelProvider.getModel("Cube"), findPins(vals), pos1);
						if (jsonElement.has("baseScale"))
						{
							Object jsonScale = jsonElement.get("baseScale");
							if (jsonScale instanceof JSONArray)
							{ 
								JSONArray jsScale = (JSONArray)jsonScale;
								Vector3f parameter = new Vector3f(jsScale.getFloat(0), jsScale.getFloat(1), jsScale.getFloat(2));
								newBody.setScale(parameter);
							}
							else
							{
								float scale = jsonElement.getFloat("baseScale");
								Vector3f parameter = new Vector3f(scale, scale, scale);
								newBody.setScale(parameter);
							}
						}
						bodies.add(newBody);
					}
					jsonModels = jsonElements.getJSONArray("Joints");

					for (i = 0; i < jsonModels.length(); i++)
					{
						JSONObject jsonElement = jsonModels.getJSONObject(i);
						if (!line.startsWith(jsonElement.getString("nameInConfig")))
							continue;
						vals = processLine(line);
						int start = findDataStart(vals) + jsonElement.getInt("coordinateStart");
						pos1.set(Float.parseFloat(vals[start++]), Float.parseFloat(vals[start++]), Float.parseFloat(vals[start++]));
						Joint newJoint = new Joint(findPins(vals), pos1, jsonElement.getBoolean("isFixed"), ModelProvider.getModel(jsonElement.getString("modelName")));
						if (jsonElement.has("baseScale"))
						{
							Object jsonScale = jsonElement.get("baseScale");
							if (jsonScale instanceof JSONArray)
							{ 
								JSONArray jsScale = (JSONArray)jsonScale;
								Vector3f parameter = new Vector3f(jsScale.getFloat(0), jsScale.getFloat(1), jsScale.getFloat(2));
		
								newJoint.setBaseScale(parameter);
							}
							else
							{
								float scale = jsonElement.getFloat("baseScale");

								Vector3f parameter = new Vector3f(scale, scale, scale);
								newJoint.setBaseScale(parameter);
							}
						}

						joints.add(newJoint);
					}
					
					jsonModels = jsonElements.getJSONArray("TwoPointConnectable");
					for (i = 0; i < jsonModels.length(); i++)
					{
						JSONObject jsonElement = jsonModels.getJSONObject(i);
						if (!line.startsWith(jsonElement.getString("nameInConfig")))
							continue;
						processConnectable(con, 0, line, jsonElement);
					}
					
					jsonModels = jsonElements.getJSONArray("BodyParts");
					for (i = 0; i < jsonModels.length(); i++)
					{
						JSONObject jsonElement = jsonModels.getJSONObject(i);
						if (!line.startsWith(jsonElement.getString("nameInConfig")))
							continue;
						processConnectable(con, 1, line, jsonElement);
					}
					
					jsonModels = jsonElements.getJSONArray("TwoBodyParts");
					for (i = 0; i < jsonModels.length(); i++)
					{
						JSONObject jsonElement = jsonModels.getJSONObject(i);
						if (!line.startsWith(jsonElement.getString("nameInConfig")))
							continue;
						vals = processLine(line);
						int start = findDataStart(vals) + jsonElement.getInt("coordinateStart");
						pos1.set(Float.parseFloat(vals[start++]), Float.parseFloat(vals[start++]), Float.parseFloat(vals[start++]));
						pos2.set(Float.parseFloat(vals[start++]), Float.parseFloat(vals[start++]), Float.parseFloat(vals[start++]));
						JSONArray jsonModel = jsonElement.getJSONArray("subModels");
						Plunger plg = new Plunger(findPins(vals), pos1, pos2);
						for (int j = 0; j < jsonModel.length(); j++)
						{
							JSONObject jsonSubModel = jsonModel.getJSONObject(j);
							JSONArray jsonParam = jsonSubModel.getJSONArray("color");
							Vector3f param = new Vector3f(jsonParam.getFloat(0), jsonParam.getFloat(1), jsonParam.getFloat(2));
							plg.setC(param, j);
							jsonParam = jsonSubModel.getJSONArray("baseScale");
							param = new Vector3f(jsonParam.getFloat(0), jsonParam.getFloat(1), jsonParam.getFloat(2));
							plg.setS(param, j);
						}
						con.add(plg);
					}
				} catch (JSONException e) { 
		            System.err.println("Error parsing JSON model configuration file: " + CONFIG_PATH);
		            e.printStackTrace();
		            System.exit(-1);
				}
			}
		}
		catch (IOException e)
		{
			System.err.println("Error while reading file " + pth);
			e.printStackTrace();
			System.exit(-1);
		}
	}

}

class BodyState
{
	private Vector3f _position = new Vector3f();
	private Quaternion _rotation = new Quaternion();
	
	public Vector3f getPos() {return _position;}
	public Quaternion getRot() {return _rotation;}
	
	public BodyState() {}
	
	public BodyState(BodyState bs)
	{
		_position = new Vector3f(bs.getPos());
		_rotation = new Quaternion(bs.getRot());
	}
	
	public void setPosition(float x, float y, float z)
	{
		_position.set(x, y, z);
	}
	
	public void setRotation(float w, float x, float y, float z)
	{
		_rotation.set(w, x, y, z);
	}
	
	public static void copy(BodyState src, BodyState dest)
	{
		dest._position.set(src._position.x, src._position.y, src._position.z);
		dest._rotation.set(src._rotation.getW(), src._rotation.getX(), src._rotation.getY(), src._rotation.getZ());
	}
}

class AnimationFrame
{
	private float _time;
	private ArrayList<BodyState> _bodyStates = new ArrayList<BodyState>();
	public AnimationFrame(float time)
	{
		_time = time;
	}
	
	public float getTime() {return _time;}
	
	public void setBodyState(int index, BodyState bs)
	{
		BodyState.copy(bs, _bodyStates.get(index));
	}
	
	public void addBodyState(BodyState bodyState) {_bodyStates.add(new BodyState(bodyState));}
	
	public ArrayList<BodyState> getBodyStates() {return _bodyStates;}
	
	public int size() {return _bodyStates.size();}
}

class Animator
{
	private int _index = 0;
	
	ArrayList<AnimationFrame> _animation = new ArrayList<AnimationFrame>();
	
	public Animator(String path, int bodyCount)
	{
		try (BufferedReader br = Files.newBufferedReader(Paths.get(path)))
		{
			String line;
			BodyState bs = new BodyState();
			int currentFrame = -1;
			String currentTime = "";
			while ((line = br.readLine()) != null)
			{
				String[] parts = line.trim().split(" +");
				if (parts.length > 7)
				{
					String time = parts[0];
					if (!currentTime.equals(time))
					{
						_animation.add(new AnimationFrame(Float.parseFloat(time)));
						currentFrame++;
						currentTime = time;
					}
					bs.setPosition(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
					bs.setRotation(Float.parseFloat(parts[4]), Float.parseFloat(parts[5]), Float.parseFloat(parts[6]), Float.parseFloat(parts[7]));
					_animation.get(currentFrame).addBodyState(bs);
				}
			}
			if (_animation.get(currentFrame).size() != bodyCount)
			{
				System.out.println("Not enough data for all bodies...Setting previous values for last frame");
				for (int i = _animation.get(currentFrame).size(); i < bodyCount; i++)
					_animation.get(currentFrame).addBodyState(new BodyState(_animation.get(currentFrame - 1).getBodyStates().get(i)));
			}
		}
		catch (IOException e)
		{
			System.out.println("No file Found");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void findClosestLeast(float time)
	{
		if (time > _animation.get(_index).getTime())
		{
			while (_index < _animation.size() && time > _animation.get(_index).getTime())
				_index++;
			_index--;
		}
		else if (time < _animation.get(_index).getTime())
		{
			while (_index > 0 && time < _animation.get(_index).getTime())
				_index--;
		}
	}
	
	public ArrayList<BodyState> getNextFrame() {return _animation.get(_index).getBodyStates();}
	
	public AnimationFrame getFirst() {return _animation.get(0);}
	public AnimationFrame getLast() {return _animation.get(_animation.size() - 1);}
	public int getMax() {return _animation.size();}
	
	public void setFrame(int frame) {_index = frame;}
	
	public float getCurT() {return _animation.get(_index).getTime();}
	public int getCurInder() {return _index;}
}
