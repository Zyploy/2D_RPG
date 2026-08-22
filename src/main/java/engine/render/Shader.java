package engine.render;

import engine.util.ErrorHandler;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int shaderID;
    private int vertexID, fragmentID;
    private String vertexSource, fragmentSource;
    private String vertexPath, fragmentPath;

    public Shader(String vertexPath, String fragmentPath) {
        this.vertexPath = vertexPath;
        this.fragmentPath = fragmentPath;

        this.vertexSource = loadFile(vertexPath);
        this.fragmentSource = loadFile(fragmentPath);

        this.vertexID = compile(vertexSource, GL_VERTEX_SHADER);
        this.fragmentID = compile(fragmentSource, GL_FRAGMENT_SHADER);

        link();

        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);
    }

    public String loadFile(String filepath) {
        try {
            return Files.readString(Path.of(filepath));
        } catch (IOException e) {
            ErrorHandler.shaderLoadError(filepath);
        }
        return null;
    }

    public int compile(String source, int type) {
        int id = glCreateShader(type);
        glShaderSource(id, source);
        glCompileShader(id);

        if(glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
            ErrorHandler.shaderCompileError(source);
        }

        return id;
    }

    public void link() {
        shaderID = glCreateProgram();
        glAttachShader(shaderID, vertexID);
        glAttachShader(shaderID, fragmentID);
        glLinkProgram(shaderID);

        if(glGetProgrami(shaderID, GL_LINK_STATUS) == GL_FALSE) {
            ErrorHandler.shaderLinkError(vertexPath, fragmentPath);
        }
    }

    public void bind() {
        glUseProgram(shaderID);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void uploadMat4f(String name, Matrix4f mat) {
        int location = glGetUniformLocation(shaderID, name);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        mat.get(buffer);
        glUniformMatrix4fv(location, false, buffer);
    }

    public void uploadInt(String name, int value) {
        int location = glGetUniformLocation(shaderID, name);
        glUniform1i(location, value);
    }
}