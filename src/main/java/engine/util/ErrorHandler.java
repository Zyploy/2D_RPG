package engine.util;

public class ErrorHandler {
    // GLFW Errors
    public static void glfwInitError() {
        throw new IllegalStateException("Unable to initialize GLFW");
    }

    public static void glfwWindowError() {
        throw new RuntimeException("Failed to create the GLFW window");
    }
}