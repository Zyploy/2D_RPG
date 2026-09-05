package engine;

import engine.input.KeyListener;
import engine.input.MouseListener;
import engine.input.ResizeListener;
import engine.render.Renderer;
import engine.render.Texture;
import engine.util.ErrorHandler;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static engine.util.Constants.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private static Window instance;
    private int width, height;
    private String title;
    private boolean fullscreen;

    private long window;
    private long monitor;
    private GLFWVidMode vidmode;

    private int lastWidth, lastHeight, lastX, lastY;

    private Window() {
        this.width = 1280;
        this.height = 720;
        this.title = "2D RPG";
    }

    // If a window object is created, it returns that object. If not, it creates that object.
    // This paired with the private constructor makes it so only one window object can be made
    public static Window get() {
        if(Window.instance == null) {
            Window.instance = new Window();
        }
        return Window.instance;
    }

    public void run() {
        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(this.window);
        glfwDestroyWindow(this.window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if(!glfwInit()) {
            ErrorHandler.glfwInitError();
        }

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Get the resolution of the primary monitor
        this.monitor = glfwGetPrimaryMonitor();
        this.vidmode = glfwGetVideoMode(this.monitor);
        int screenWidth = this.vidmode.width();
        int screenHeight = this.vidmode.height();

        // Sets the window size to half the screen size and maintains aspect ratio
        this.height = screenHeight;
        this.width = (int)(this.height * ASPECT);
        if(this.width > screenWidth) {
            this.width = screenWidth;
            this.height = (int)(this.width / ASPECT);
        }

        this.width /= 2;
        this.height /= 2;

        this.fullscreen = false;

        // Create the window
        this.window = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if(this.window == NULL) {
            ErrorHandler.glfwWindowError();
        }

        // Setup a key callback. It will be called every time a key is pressed, repeated, or released.
        glfwSetKeyCallback(this.window, KeyListener::keyCallback);

        glfwSetMouseButtonCallback(this.window, MouseListener::mouseButtonCallback);
        glfwSetCursorPosCallback(this.window, MouseListener::mousePosCallback);
        glfwSetScrollCallback(this.window, MouseListener::mouseScrollCallback);

        glfwSetWindowSizeCallback(this.window, ResizeListener::resizeCallback);
        glfwSetWindowPosCallback(this.window, ResizeListener::moveCallback);

        // Get the thread stack and push a new frame
        try(MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(this.window, pWidth, pHeight);

            // Center the window
            glfwSetWindowPos(this.window, (screenWidth - pWidth.get(0)) / 2, (screenHeight - pHeight.get(0)) / 2);
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(this.window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(this.window);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private void loop() {
        Renderer renderer = new Renderer();

        // Run the rendering loop until the user has attempted to close the window
        while(!glfwWindowShouldClose(this.window)) {
            // Poll for window events. The key callback above will only be invoked during this call.
            glfwPollEvents();

            // The window should close when the Escape key is pressed
            if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
                glfwSetWindowShouldClose(this.window, true);
            }

            if(KeyListener.isKeyPressedOnce(GLFW_KEY_F11)) {
                this.fullscreen = !this.fullscreen;
                if(this.fullscreen) {
                    this.lastWidth = this.width;
                    this.lastHeight = this.height;
                    this.lastX = ResizeListener.getX();
                    this.lastY = ResizeListener.getY();

                    setSize(this.vidmode.width(), this.vidmode.height());
                    glfwSetWindowMonitor(this.window, this.monitor,
                            0, 0,
                            this.width, this.height,
                            this.vidmode.refreshRate());
                } else {
                    setSize(this.lastWidth, this.lastHeight);
                    glfwSetWindowMonitor(this.window, NULL,
                            this.lastX, this.lastY,
                            this.width, this.height,
                            this.vidmode.refreshRate());
                }
            }

            glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Set the clear color
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            for(int y = 0; y < VER_TILES; y++) {
                for(int x = 0; x < HOR_TILES; x++) {
                    renderer.render(x, y, 1, new Texture("src/main/resources/textures/tileset.png"));
                }
            }

            glfwSwapBuffers(this.window); // swap the color buffers

            endFrame();
        }
    }

    public void endFrame() {
        KeyListener.endFrame();
        MouseListener.endFrame();
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}