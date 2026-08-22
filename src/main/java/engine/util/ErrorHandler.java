package engine.util;

public class ErrorHandler {
    // GLFW Errors
    public static void glfwInitError() {
        throw new RuntimeException("Unable to initialize GLFW");
    }

    public static void glfwWindowError() {
        throw new RuntimeException("Failed to create the GLFW window");
    }

    // Texture Errors
    public static void textureLoadError(String filepath) {
        throw new RuntimeException("Failed to load texture: " + filepath);
    }

    // Shader Errors
    public static void shaderLoadError(String filepath) {
        throw new RuntimeException("Failed to load shader: " + filepath);
    }

    public static void shaderCompileError(String filepath) {
        throw new RuntimeException("Failed to compile shader: " + filepath);
    }

    public static void shaderLinkError(String vertexPath, String fragmentPath) {
        throw new RuntimeException("Failed to link shaders: " + vertexPath + " + " + fragmentPath);
    }
}