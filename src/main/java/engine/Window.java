package engine;

import engine.input.KeyListener;
import engine.input.MouseListener;
import engine.util.ErrorHandler;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private static Window instance;
    private long window;
    private int width, height;
    private String title;

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
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

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
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        int screenWidth = vidmode.width();
        int screenHeight = vidmode.height();

        // Set aspect ratio
        float aspect = 16f / 9f;

        // Sets the window size to half the screen size and maintains aspect ratio
        this.height = screenHeight;
        this.width = (int)(this.height * aspect);
        if(this.width > screenWidth) {
            this.width = screenWidth;
            this.height = (int)(this.width / aspect);
        }

        this.width /= 2;
        this.height /= 2;

        // Create the window
        window = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (window == NULL) {
            ErrorHandler.glfwWindowError();
        }

        // Setup a key callback. It will be called every time a key is pressed, repeated, or released.
        glfwSetKeyCallback(window, KeyListener::keyCallback);

        glfwSetMouseButtonCallback(window, MouseListener::mouseButtonCallback);
        glfwSetCursorPosCallback(window, MouseListener::mousePosCallback);
        glfwSetScrollCallback(window, MouseListener::mouseScrollCallback);

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Center the window
            glfwSetWindowPos(
                    window,
                    (screenWidth - pWidth.get(0)) / 2,
                    (screenHeight - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
    }

    private void loop() {
        // Run the rendering loop until the user has attempted to close the window
        while (!glfwWindowShouldClose(window)) {
            // Poll for window events. The key callback above will only be invoked during this call.
            glfwPollEvents();

            // The window should close when the Escape key is pressed
            if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
                glfwSetWindowShouldClose(window, true);
            }

            glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Set the clear color
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            glfwSwapBuffers(window); // swap the color buffers
        }
    }
}