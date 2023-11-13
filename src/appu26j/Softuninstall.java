package appu26j;

import appu26j.assets.Assets;
import appu26j.gui.font.FontRenderer;
import appu26j.gui.screens.GuiScreen;
import appu26j.gui.screens.GuiSoftuninstall;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public enum Softuninstall
{
	INSTANCE;

	private final int width = 900, height = 700;
	private float mouseX = 0, mouseY = 0;
	private GuiScreen currentScreen;
	private long window = 0;

	public void start()
	{
		this.initializeWindow();
		this.setupOpenGL();
		this.loop();
	}

	private void initializeWindow()
	{
		Assets.loadAssets();
		GLFWErrorCallback.createPrint(System.err).set();

		if (!glfwInit())
		{
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		glfwWindowHint(GLFW_FOCUS_ON_SHOW, GLFW_TRUE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		this.window = glfwCreateWindow(this.width, this.height, "Softuninstall", 0, 0);
		glfwSetWindowPos(this.window, 50, 50);

		if (this.window == 0)
		{
			throw new IllegalStateException("Unable to create the GLFW window");
		}

		glfwMakeContextCurrent(this.window);
		glfwSwapInterval(1);

		glfwSetCharCallback(this.window, new GLFWCharCallback()
		{
			@Override
			public void invoke(long window, int codepoint)
			{
				currentScreen.charTyped((char) codepoint, Softuninstall.this.mouseX, Softuninstall.this.mouseY);
			}
		});

		glfwSetKeyCallback(this.window, new GLFWKeyCallback()
		{
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods)
			{
				if (action == 1)
				{
					currentScreen.keyPressed(key, Softuninstall.this.mouseX, Softuninstall.this.mouseY);
				}
			}
		});

		glfwSetMouseButtonCallback(this.window, new GLFWMouseButtonCallback()
		{
		    public void invoke(long window, int button, int action, int mods)
		    {
				if (action == 1)
				{
					currentScreen.mouseClicked(button, Softuninstall.this.mouseX, Softuninstall.this.mouseY);
				}

				else
				{
					currentScreen.mouseReleased(button, Softuninstall.this.mouseX, Softuninstall.this.mouseY);
				}
		    }
		});

		glfwSetScrollCallback(this.window, new GLFWScrollCallback()
		{
			@Override
			public void invoke(long window, double xOffset, double yOffset)
			{
				currentScreen.scroll((float) yOffset);
			}
		});
	}

	private void setupOpenGL()
	{
		GL.createCapabilities();
		glClearColor(0.875F, 0.875F, 0.875F, 1);
		glLoadIdentity();
		glViewport(0, 0, this.width, this.height);
		glOrtho(0, this.width, this.height, 0, 1, 0);
	}

	private void loop()
	{
		this.currentScreen = new GuiSoftuninstall();
		this.currentScreen.initGUI(this.width, this.height);
		glfwShowWindow(this.window);

		while (!glfwWindowShouldClose(this.window))
		{
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			try (MemoryStack memoryStack = MemoryStack.stackPush())
			{
				DoubleBuffer mouseX = memoryStack.mallocDouble(1);
				DoubleBuffer mouseY = memoryStack.mallocDouble(1);
				glfwGetCursorPos(this.window, mouseX, mouseY);
				this.mouseX = (float) mouseX.get();
				this.mouseY = (float) mouseY.get();
			}

			this.currentScreen.drawScreen(this.mouseX, this.mouseY);
			glfwSwapBuffers(this.window);
			glfwPollEvents();
		}

		this.currentScreen.getFontRenderers().forEach(FontRenderer::shutdown);
		glfwDestroyWindow(this.window);
		glfwTerminate();
		Objects.requireNonNull(glfwSetErrorCallback(null)).free();
	}

	public void displayGUIScreen(GuiScreen guiScreen)
	{
		if (guiScreen != null)
		{
			guiScreen.initGUI(this.width, this.height);
		}

		this.currentScreen = guiScreen;
	}

	public GuiScreen getCurrentScreen()
	{
		return this.currentScreen;
	}

	public long getWindowID()
	{
		return this.window;
	}
}
