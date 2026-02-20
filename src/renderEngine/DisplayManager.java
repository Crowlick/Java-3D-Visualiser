package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {
	
	private static int WIDTH = 1280;
	private static int HEIGHT = 720;
	private static final int FPS_CAP = 60;
	
	
	public static void createDisplay()
	{
		ContextAttribs attribs = new ContextAttribs(3, 2)
		.withForwardCompatible(true)
		.withProfileCore(true);
		
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat().withSamples(4), attribs);
			Display.setTitle("Model");
			GL11.glEnable(GL13.GL_MULTISAMPLE);
			Display.setResizable(true);
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
	}
	
	public static void updateDisplay()
	{
		Display.sync(FPS_CAP);
		Display.update();
		if (Display.wasResized()) 
		{
            WIDTH = Display.getWidth();
            HEIGHT = Display.getHeight();
            GL11.glViewport(0, 0, WIDTH, HEIGHT);
        }
	}
	
	public static void closeDisplay()
	{
		Display.destroy();
	}
	
	public static int getHeight() {return HEIGHT;}
	public static int getWidth() {return WIDTH;}
	
	public static int getFPS( ) {return FPS_CAP;}
}
